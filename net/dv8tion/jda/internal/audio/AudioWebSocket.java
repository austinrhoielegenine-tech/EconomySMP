package net.dv8tion.jda.internal.audio;

import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.audio.dave.DaveProtocolCallbacks;
import net.dv8tion.jda.api.audio.dave.DaveSession;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.managers.AudioManagerImpl;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

class AudioWebSocket extends WebSocketAdapter implements DaveProtocolCallbacks {
   public static final Logger LOG = JDALogger.getLog(AudioWebSocket.class);
   public static final int DISCORD_SECRET_KEY_LENGTH = 32;
   private static final byte[] UDP_KEEP_ALIVE = new byte[]{-55, 0, 0, 0, 0, 0, 0, 0, 0};
   protected volatile AudioEncryption encryption;
   protected volatile CryptoAdapter crypto;
   protected WebSocket socket;
   private DaveSession daveSession;
   private final AudioConnection audioConnection;
   private final ConnectionListener listener;
   private final ScheduledExecutorService keepAlivePool;
   private final Guild guild;
   private final String sessionId;
   private final String token;
   private final String wssEndpoint;
   private volatile ConnectionStatus connectionStatus;
   private boolean ready;
   private boolean reconnecting;
   private boolean shouldReconnect;
   private int ssrc;
   private byte[] secretKey;
   private Future<?> keepAliveHandle;
   private InetSocketAddress address;
   private long sequence;
   private volatile boolean shutdown;

   protected AudioWebSocket(AudioConnection audioConnection, ConnectionListener listener, String endpoint, Guild guild, String sessionId, String token, boolean shouldReconnect) {
      this.connectionStatus = ConnectionStatus.NOT_CONNECTED;
      this.ready = false;
      this.reconnecting = false;
      this.shutdown = false;
      this.audioConnection = audioConnection;
      this.listener = listener;
      this.guild = guild;
      this.sessionId = sessionId;
      this.token = token;
      this.shouldReconnect = shouldReconnect;
      this.keepAlivePool = this.getJDA().getAudioLifeCyclePool();
      String url = IOUtil.addQuery(endpoint, "v", 8);
      if (url.startsWith("wss://")) {
         this.wssEndpoint = url;
      } else {
         this.wssEndpoint = "wss://" + url;
      }

      if (sessionId != null && !sessionId.isEmpty()) {
         if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a audio websocket connection using a null/empty token!");
         }
      } else {
         throw new IllegalArgumentException("Cannot create a audio websocket connection using a null/empty sessionId!");
      }
   }

   void setDaveSession(DaveSession daveSession) {
      this.daveSession = daveSession;
   }

   protected void send(String message) {
      LOG.trace((String)"<- {}", (Object)message);
      this.socket.sendText(message);
   }

   protected void send(int op, Object data) {
      this.send(DataObject.empty().put("op", op).put("d", data).toString());
   }

   protected void startConnection() {
      if (!this.reconnecting && this.socket != null) {
         throw new IllegalStateException("Somehow, someway, this AudioWebSocket has already attempted to start a connection!");
      } else {
         try {
            WebSocketFactory socketFactory = new WebSocketFactory(this.getJDA().getWebSocketFactory());
            IOUtil.setServerName(socketFactory, this.wssEndpoint);
            if (socketFactory.getSocketTimeout() > 0) {
               socketFactory.setSocketTimeout(Math.max(1000, socketFactory.getSocketTimeout()));
            } else {
               socketFactory.setSocketTimeout(10000);
            }

            this.socket = socketFactory.createSocket(this.wssEndpoint);
            this.socket.setDirectTextMessage(true);
            this.socket.addListener(this);
            this.changeStatus(ConnectionStatus.CONNECTING_AWAITING_WEBSOCKET_CONNECT);
            this.socket.connectAsynchronously();
         } catch (IOException e) {
            LOG.warn((String)"Encountered IOException while attempting to connect to {}: {}\nClosing connection and attempting to reconnect.", (Object)this.wssEndpoint, (Object)e.getMessage());
            this.close(ConnectionStatus.ERROR_WEBSOCKET_UNABLE_TO_CONNECT);
         }

      }
   }

   protected void close(ConnectionStatus closeStatus) {
      if (!this.shutdown) {
         this.locked((manager) -> {
            if (!this.shutdown) {
               ConnectionStatus status = closeStatus;
               this.ready = false;
               this.shutdown = true;
               this.stopKeepAlive();
               if (this.audioConnection.udpSocket != null) {
                  this.audioConnection.udpSocket.close();
               }

               if (this.socket != null) {
                  this.socket.sendClose();
               }

               this.audioConnection.shutdown();
               this.daveSession.destroy();
               AudioChannel disconnectedChannel = manager.getConnectedChannel();
               manager.setAudioConnection((AudioConnection)null);
               JDAImpl api = this.getJDA();
               if (closeStatus != ConnectionStatus.DISCONNECTED_KICKED_FROM_CHANNEL || api.getClient().isSession() && api.getClient().isConnected()) {
                  if (closeStatus == ConnectionStatus.ERROR_LOST_CONNECTION || closeStatus == ConnectionStatus.DISCONNECTED_KICKED_FROM_CHANNEL) {
                     Guild connGuild = api.getGuildById(this.guild.getIdLong());
                     if (connGuild != null) {
                        AudioChannel channel = (AudioChannel)connGuild.getGuildChannelById(this.audioConnection.getChannel().getIdLong());
                        if (channel == null) {
                           status = ConnectionStatus.DISCONNECTED_CHANNEL_DELETED;
                        }
                     }
                  }
               } else {
                  LOG.debug("Connection was closed due to session invalidate!");
                  status = ConnectionStatus.ERROR_CANNOT_RESUME;
               }

               this.changeStatus(status);
               if (this.shouldReconnect && status.shouldReconnect() && status != ConnectionStatus.AUDIO_REGION_CHANGE) {
                  if (disconnectedChannel == null) {
                     LOG.debug("Cannot reconnect due to null audio channel");
                     return;
                  }

                  api.getDirectAudioController().reconnect(disconnectedChannel);
               } else if (status == ConnectionStatus.DISCONNECTED_REMOVED_FROM_GUILD) {
                  api.getAudioManagersView().remove(this.guild.getIdLong());
               } else if (status != ConnectionStatus.AUDIO_REGION_CHANGE && status != ConnectionStatus.DISCONNECTED_KICKED_FROM_CHANNEL) {
                  api.getDirectAudioController().disconnect(this.guild);
               }

            }
         });
      }
   }

   protected void changeStatus(ConnectionStatus newStatus) {
      this.connectionStatus = newStatus;
      this.listener.onStatusChange(newStatus);
   }

   protected void setAutoReconnect(boolean shouldReconnect) {
      this.shouldReconnect = shouldReconnect;
   }

   protected ConnectionStatus getConnectionStatus() {
      return this.connectionStatus;
   }

   protected InetSocketAddress getAddress() {
      return this.address;
   }

   protected byte[] getSecretKey() {
      return this.secretKey;
   }

   protected int getSSRC() {
      return this.ssrc;
   }

   protected boolean isReady() {
      return this.ready;
   }

   public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) {
      this.getJDA().setContext();
   }

   public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
      if (this.shutdown) {
         this.socket.sendClose(1000);
      } else {
         if (this.reconnecting) {
            this.resume();
         } else {
            this.identify();
         }

         this.changeStatus(ConnectionStatus.CONNECTING_AWAITING_AUTHENTICATION);
         this.audioConnection.prepareReady();
         this.reconnecting = false;
      }
   }

   public void onTextMessage(WebSocket websocket, byte[] data) {
      try {
         this.handleEvent(DataObject.fromJson(data));
      } catch (Exception ex) {
         String message = "malformed";

         try {
            message = new String(data, StandardCharsets.UTF_8);
         } catch (Exception var6) {
         }

         LOG.error((String)"Encountered exception trying to handle an event message: {}", (Object)message, (Object)ex);
      }

   }

   public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
      if (!this.shutdown) {
         LOG.debug((String)"The Audio connection was closed!\nBy remote? {}", (Object)closedByServer);
         if (serverCloseFrame != null) {
            LOG.debug((String)"Reason: {}\nClose code: {}", (Object)serverCloseFrame.getCloseReason(), (Object)serverCloseFrame.getCloseCode());
            int code = serverCloseFrame.getCloseCode();
            VoiceCode.Close closeCode = VoiceCode.Close.from(code);
            switch (closeCode) {
               case RATE_LIMIT_EXCEEDED:
               case SERVER_NOT_FOUND:
               case SERVER_CRASH:
               case INVALID_SESSION:
                  this.close(ConnectionStatus.ERROR_CANNOT_RESUME);
                  break;
               case AUTHENTICATION_FAILED:
                  this.close(ConnectionStatus.DISCONNECTED_AUTHENTICATION_FAILURE);
                  break;
               case DISCONNECTED_ALL_CLIENTS:
               case DISCONNECTED:
                  this.close(ConnectionStatus.DISCONNECTED_KICKED_FROM_CHANNEL);
                  break;
               default:
                  this.reconnect();
            }

         } else {
            if (clientCloseFrame != null) {
               LOG.debug((String)"ClientReason: {}\nClientCode: {}", (Object)clientCloseFrame.getCloseReason(), (Object)clientCloseFrame.getCloseCode());
               if (clientCloseFrame.getCloseCode() != 1000) {
                  this.reconnect();
                  return;
               }
            }

            this.close(ConnectionStatus.NOT_CONNECTED);
         }
      }
   }

   public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
      this.handleCallbackError(websocket, cause);
   }

   public void handleCallbackError(WebSocket websocket, Throwable cause) {
      LOG.error("There was some audio websocket error", cause);
      JDAImpl api = this.getJDA();
      api.handleEvent(new ExceptionEvent(api, cause, true));
   }

   public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) {
      String identifier = this.getJDA().getIdentifierString();
      String guildId = this.guild.getId();
      switch (threadType) {
         case CONNECT_THREAD:
            thread.setName(identifier + " AudioWS-ConnectThread (guildId: " + guildId + ')');
            break;
         case FINISH_THREAD:
            thread.setName(identifier + " AudioWS-FinishThread (guildId: " + guildId + ')');
            break;
         case WRITING_THREAD:
            thread.setName(identifier + " AudioWS-WriteThread (guildId: " + guildId + ')');
            break;
         case READING_THREAD:
            thread.setName(identifier + " AudioWS-ReadThread (guildId: " + guildId + ')');
            break;
         default:
            thread.setName(identifier + " AudioWS-" + threadType + " (guildId: " + guildId + ')');
      }

   }

   public void onConnectError(WebSocket webSocket, WebSocketException e) {
      LOG.warn("Failed to establish websocket connection to {}: {} - {}\nClosing connection and attempting to reconnect.", this.wssEndpoint, e.getError(), e.getMessage());
      this.close(ConnectionStatus.ERROR_WEBSOCKET_UNABLE_TO_CONNECT);
   }

   public DaveSession getDaveSession() {
      return this.daveSession;
   }

   private void sendBinary(int opcode, ByteBuffer payload) {
      ByteBuffer buffer = ByteBuffer.allocate(1 + payload.remaining()).put((byte)opcode).put(payload);
      buffer.flip();
      this.socket.sendBinary(buffer.array());
   }

   public void onBinaryMessage(WebSocket websocket, byte[] binary) {
      ByteBuffer message = ByteBuffer.allocateDirect(binary.length);
      message.put(binary);
      message.flip();
      short sequence = message.getShort();
      this.sequence = (long)sequence & 65535L;
      int opcode = message.get() & 255;
      switch (opcode) {
         case 25:
            LOG.trace("-> MLS_EXTERNAL_SENDER");
            this.daveSession.onDaveProtocolMLSExternalSenderPackage(message);
            break;
         case 26:
         case 28:
         default:
            LOG.trace((String)"-> UNKNOWN OP {}", (Object)opcode);
            break;
         case 27:
            LOG.trace("-> MLS_PROPOSALS");
            this.daveSession.onMLSProposals(message);
            break;
         case 29:
            LOG.trace("-> MLS_ANNOUNCE_COMMIT_TRANSITION");
            int transitionId = message.getShort() & '\uffff';
            this.daveSession.onMLSPrepareCommitTransition(transitionId, message);
            break;
         case 30:
            LOG.trace("-> MLS_WELCOME");
            int transitionId = message.getShort() & '\uffff';
            this.daveSession.onMLSWelcome(transitionId, message);
      }

   }

   public void sendMLSKeyPackage(@Nonnull ByteBuffer mlsKeyPackage) {
      LOG.trace("<- MLS_KEY_PACKAGE");
      this.sendBinary(26, mlsKeyPackage);
   }

   public void sendDaveProtocolReadyForTransition(int transitionId) {
      LOG.trace("<- DAVE_TRANSITION_READY");
      this.send(23, DataObject.empty().put("transition_id", transitionId));
   }

   public void sendMLSCommitWelcome(@Nonnull ByteBuffer commitWelcomeMessage) {
      LOG.trace("<- MLS_COMMIT_WELCOME");
      this.sendBinary(28, commitWelcomeMessage);
   }

   public void sendMLSInvalidCommitWelcome(int transitionId) {
      LOG.trace("<- MLS_INVALID_COMMIT_WELCOME");
      this.send(31, DataObject.empty().put("transition_id", transitionId));
   }

   private void handleEvent(DataObject contentAll) {
      int opCode = contentAll.getInt("op");
      this.sequence = contentAll.getLong("seq", this.sequence);
      switch (opCode) {
         case 2:
            LOG.trace((String)"-> READY {}", (Object)contentAll);
            DataObject content = contentAll.getObject("d");
            this.ssrc = content.getInt("ssrc");
            int port = content.getInt("port");
            String ip = content.getString("ip");
            DataArray modes = content.getArray("modes");
            this.encryption = CryptoAdapter.negotiate(AudioEncryption.fromArray(modes));
            if (this.encryption == null) {
               this.close(ConnectionStatus.ERROR_UNSUPPORTED_ENCRYPTION_MODES);
               LOG.error((String)"None of the provided encryption modes are supported: {}", (Object)modes);
               return;
            }

            LOG.debug("Using encryption mode " + this.encryption.getKey());
            this.changeStatus(ConnectionStatus.CONNECTING_ATTEMPTING_UDP_DISCOVERY);
            int tries = 0;

            InetSocketAddress externalIpAndPort;
            do {
               externalIpAndPort = this.handleUdpDiscovery(new InetSocketAddress(ip, port), this.ssrc);
               ++tries;
               if (externalIpAndPort == null && tries > 5) {
                  this.close(ConnectionStatus.ERROR_UDP_UNABLE_TO_CONNECT);
                  return;
               }
            } while(externalIpAndPort == null);

            this.daveSession.assignSsrcToCodec(DaveSession.Codec.OPUS, this.ssrc);
            DataObject object = DataObject.empty().put("protocol", "udp").put("data", DataObject.empty().put("address", externalIpAndPort.getHostString()).put("port", externalIpAndPort.getPort()).put("mode", this.encryption.getKey()));
            this.send(1, object);
            this.changeStatus(ConnectionStatus.CONNECTING_AWAITING_READY);
            break;
         case 3:
            LOG.trace((String)"-> HEARTBEAT {}", (Object)contentAll);
            this.send(3, System.currentTimeMillis());
            break;
         case 4:
            LOG.trace((String)"-> SESSION_DESCRIPTION {}", (Object)contentAll);
            this.send(5, DataObject.empty().put("delay", 0).put("speaking", 0).put("ssrc", this.ssrc));
            DataArray keyArray = contentAll.getObject("d").getArray("secret_key");
            this.secretKey = new byte[32];

            for(int i = 0; i < keyArray.length(); ++i) {
               this.secretKey[i] = (byte)keyArray.getInt(i);
            }

            this.crypto = new DaveCryptoAdapter(CryptoAdapter.getAdapter(this.encryption, this.secretKey), this.daveSession, this.ssrc);
            this.daveSession.onSelectProtocolAck(contentAll.getObject("d").getInt("dave_protocol_version"));
            LOG.debug("Audio connection has finished connecting!");
            this.ready = true;
            ReentrantLock var29 = this.audioConnection.readyLock;
            Condition var30 = this.audioConnection.readyCondvar;
            Objects.requireNonNull(var30);
            MiscUtil.locked(var29, var30::signalAll);
            this.changeStatus(ConnectionStatus.CONNECTED);
            break;
         case 5:
            LOG.trace((String)"-> USER_SPEAKING_UPDATE {}", (Object)contentAll);
            DataObject content = contentAll.getObject("d");
            int ssrc = content.getInt("ssrc");
            long userId = content.getUnsignedLong("user_id");
            this.audioConnection.updateUserSSRC(ssrc, userId);
            this.daveSession.addUser(userId);
            EnumSet<SpeakingMode> speaking = SpeakingMode.getModes(content.getInt("speaking"));
            User user = this.getUser(userId);
            if (user == null) {
               LOG.trace((String)"Got an Audio USER_SPEAKING_UPDATE for a non-existent User. JSON: {}", (Object)contentAll);
               this.listener.onUserSpeakingModeUpdate(UserSnowflake.fromId(userId), speaking);
            } else {
               this.listener.onUserSpeakingModeUpdate((UserSnowflake)user, speaking);
            }
            break;
         case 6:
            LOG.trace((String)"-> HEARTBEAT_ACK {}", (Object)contentAll);
            long ping = System.currentTimeMillis() - contentAll.getObject("d").getLong("t");
            this.listener.onPing(ping);
            break;
         case 7:
         case 10:
         case 12:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 23:
         default:
            LOG.trace((String)"-> UNKNOWN OP {}: {}", (Object)opCode, (Object)contentAll);
            break;
         case 8:
            LOG.trace((String)"-> HELLO {}", (Object)contentAll);
            DataObject payload = contentAll.getObject("d");
            int interval = payload.getInt("heartbeat_interval");
            this.stopKeepAlive();
            this.setupKeepAlive(interval);
            this.daveSession.initialize();
            break;
         case 9:
            LOG.trace((String)"-> RESUMED {}", (Object)contentAll);
            LOG.debug("Successfully resumed session!");
            this.changeStatus(ConnectionStatus.CONNECTED);
            this.ready = true;
            ReentrantLock var10000 = this.audioConnection.readyLock;
            Condition var10001 = this.audioConnection.readyCondvar;
            Objects.requireNonNull(var10001);
            MiscUtil.locked(var10000, var10001::signalAll);
            break;
         case 11:
            LOG.trace((String)"-> USER_BULK_CONNECT {}", (Object)contentAll);
            DataObject payload = contentAll.getObject("d");
            DataArray userIds = payload.getArray("user_ids");

            for(int i = 0; i < userIds.length(); ++i) {
               long userId = userIds.getUnsignedLong(i);
               this.daveSession.addUser(userId);
            }
            break;
         case 13:
            LOG.trace((String)"-> USER_DISCONNECT {}", (Object)contentAll);
            DataObject payload = contentAll.getObject("d");
            long userId = payload.getUnsignedLong("user_id");
            this.audioConnection.removeUserSSRC(userId);
            this.daveSession.removeUser(userId);
            break;
         case 21:
            LOG.trace((String)"-> DAVE_PREPARE_TRANSITION {}", (Object)contentAll);
            DataObject payload = contentAll.getObject("d");
            this.daveSession.onDaveProtocolPrepareTransition(payload.getInt("transition_id"), payload.getInt("protocol_version"));
            break;
         case 22:
            LOG.trace((String)"-> DAVE_EXECUTE_TRANSITION {}", (Object)contentAll);
            DataObject payload = contentAll.getObject("d");
            this.daveSession.onDaveProtocolExecuteTransition(payload.getInt("transition_id"));
            break;
         case 24:
            LOG.trace((String)"-> DAVE_PREPARE_EPOCH {}", (Object)contentAll);
            DataObject payload = contentAll.getObject("d");
            this.daveSession.onDaveProtocolPrepareEpoch(payload.getUnsignedLong("epoch"), payload.getInt("protocol_version"));
      }

   }

   private void identify() {
      this.sequence = 0L;
      int maxDaveProtocolVersion = this.daveSession.getMaxProtocolVersion();
      if (maxDaveProtocolVersion == 0) {
         LOG.warn("Maximum Dave Protocol Version is 0. This means your connection does not properly support encryption. This will fail to work in the future.");
      }

      DataObject connectObj = DataObject.empty().put("server_id", this.guild.getId()).put("user_id", this.getJDA().getSelfUser().getId()).put("session_id", this.sessionId).put("token", this.token).put("max_dave_protocol_version", maxDaveProtocolVersion);
      this.send(0, connectObj);
   }

   private void resume() {
      LOG.debug("Sending resume payload...");
      DataObject resumeObj = DataObject.empty().put("server_id", this.guild.getId()).put("session_id", this.sessionId).put("token", this.token).put("seq_ack", this.sequence);
      this.send(7, resumeObj);
   }

   private JDAImpl getJDA() {
      return this.audioConnection.getJDA();
   }

   private void locked(Consumer<AudioManagerImpl> consumer) {
      AudioManagerImpl manager = (AudioManagerImpl)this.guild.getAudioManager();
      MiscUtil.locked(manager.CONNECTION_LOCK, (Runnable)(() -> consumer.accept(manager)));
   }

   private void reconnect() {
      if (!this.shutdown) {
         this.locked((unused) -> {
            if (!this.shutdown) {
               this.ready = false;
               this.reconnecting = true;
               this.changeStatus(ConnectionStatus.ERROR_LOST_CONNECTION);
               this.startConnection();
            }
         });
      }
   }

   private InetSocketAddress handleUdpDiscovery(InetSocketAddress address, int ssrc) {
      try {
         if (this.audioConnection.udpSocket != null) {
            this.audioConnection.udpSocket.close();
         }

         this.audioConnection.udpSocket = new DatagramSocket();
         ByteBuffer buffer = ByteBuffer.allocate(74);
         buffer.putShort((short)1);
         buffer.putShort((short)70);
         buffer.putInt(ssrc);
         DatagramPacket discoveryPacket = new DatagramPacket(buffer.array(), buffer.array().length, address);
         this.audioConnection.udpSocket.send(discoveryPacket);
         DatagramPacket receivedPacket = new DatagramPacket(new byte[74], 74);
         this.audioConnection.udpSocket.setSoTimeout(1000);
         this.audioConnection.udpSocket.receive(receivedPacket);
         byte[] received = receivedPacket.getData();
         String ourIP = new String(received, 8, received.length - 10, StandardCharsets.UTF_8);
         ourIP = ourIP.trim();
         int ourPort = IOUtil.getShortBigEndian(received, received.length - 2) & '\uffff';
         this.address = address;
         return new InetSocketAddress(ourIP, ourPort);
      } catch (IOException var9) {
         return null;
      }
   }

   private void stopKeepAlive() {
      if (this.keepAliveHandle != null) {
         this.keepAliveHandle.cancel(true);
      }

      this.keepAliveHandle = null;
   }

   private void setupKeepAlive(int keepAliveInterval) {
      if (this.keepAliveHandle != null) {
         LOG.error("Setting up a KeepAlive runnable while the previous one seems to still be active!!");
      }

      try {
         if (this.socket != null) {
            Socket rawSocket = this.socket.getSocket();
            if (rawSocket != null) {
               rawSocket.setSoTimeout(keepAliveInterval + 10000);
            }
         }
      } catch (SocketException ex) {
         LOG.warn((String)"Failed to setup timeout for socket", (Throwable)ex);
      }

      Runnable keepAliveRunnable = () -> {
         this.getJDA().setContext();
         if (this.socket != null && this.socket.isOpen()) {
            DataObject packet = DataObject.empty().put("t", System.currentTimeMillis());
            if (this.sequence > 0L) {
               packet.put("seq_ack", this.sequence);
            }

            this.send(3, packet);
         }

         if (this.audioConnection.udpSocket != null && !this.audioConnection.udpSocket.isClosed()) {
            try {
               DatagramPacket keepAlivePacket = new DatagramPacket(UDP_KEEP_ALIVE, UDP_KEEP_ALIVE.length, this.address);
               this.audioConnection.udpSocket.send(keepAlivePacket);
            } catch (NoRouteToHostException var2) {
               LOG.warn("Closing AudioConnection due to inability to ping audio packets.");
               LOG.warn("Cannot send audio packet because JDA navigate the route to Discord.\nAre you sure you have internet connection? It is likely that you've lost connection.");
               this.close(ConnectionStatus.ERROR_LOST_CONNECTION);
            } catch (IOException e) {
               LOG.error((String)"There was some error sending an audio keepalive packet", (Throwable)e);
            }
         }

      };

      try {
         this.keepAliveHandle = this.keepAlivePool.scheduleAtFixedRate(keepAliveRunnable, 0L, (long)keepAliveInterval, TimeUnit.MILLISECONDS);
      } catch (RejectedExecutionException var4) {
      }

   }

   private User getUser(long userId) {
      return this.getJDA().getUserById(userId);
   }

   protected void finalize() {
      if (!this.shutdown) {
         LOG.error("Finalization hook of AudioWebSocket was triggered without properly shutting down");
         this.close(ConnectionStatus.NOT_CONNECTED);
      }

   }
}
