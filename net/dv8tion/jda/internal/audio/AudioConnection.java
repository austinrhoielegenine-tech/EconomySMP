package net.dv8tion.jda.internal.audio;

import com.neovisionaries.ws.client.WebSocket;
import com.sun.jna.ptr.PointerByReference;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.audio.AudioNatives;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.OpusPacket;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.audio.dave.DaveSession;
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory;
import net.dv8tion.jda.api.audio.factory.IAudioSendSystem;
import net.dv8tion.jda.api.audio.factory.IPacketProvider;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.managers.AudioManagerImpl;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.ResizingByteBuffer;
import org.slf4j.Logger;
import tomp2p.opuswrapper.Opus;

public class AudioConnection {
   public static final Logger LOG = JDALogger.getLog(AudioConnection.class);
   public static final long MAX_UINT_32 = 4294967295L;
   static final ByteBuffer silenceBytes = ByteBuffer.wrap(new byte[]{-8, -1, -2});
   private static boolean printedError = false;
   protected volatile DatagramSocket udpSocket;
   private final TIntLongMap ssrcMap = new TIntLongHashMap();
   private final TIntObjectMap<Decoder> opusDecoders = new TIntObjectHashMap<Decoder>();
   private final HashMap<User, Queue<AudioData>> combinedQueue = new HashMap();
   private final String threadIdentifier;
   private final AudioWebSocket webSocket;
   private final JDAImpl api;
   protected final ReentrantLock readyLock = new ReentrantLock();
   protected final Condition readyCondvar;
   private AudioChannel channel;
   private PointerByReference opusEncoder;
   private ScheduledExecutorService combinedAudioExecutor;
   private IAudioSendSystem sendSystem;
   private Thread receiveThread;
   private long queueTimeout;
   private boolean shutdown;
   private volatile AudioSendHandler sendHandler;
   private volatile AudioReceiveHandler receiveHandler;
   private volatile boolean couldReceive;
   private volatile int speakingMode;

   public AudioConnection(AudioManagerImpl manager, String endpoint, String sessionId, String token, AudioChannel channel) {
      this.readyCondvar = this.readyLock.newCondition();
      this.shutdown = false;
      this.sendHandler = null;
      this.receiveHandler = null;
      this.couldReceive = false;
      this.speakingMode = SpeakingMode.VOICE.getRaw();
      this.api = (JDAImpl)channel.getJDA();
      this.channel = channel;
      JDAImpl api = (JDAImpl)channel.getJDA();
      this.threadIdentifier = api.getIdentifierString() + " AudioConnection Guild: " + channel.getGuild().getId();
      this.webSocket = new AudioWebSocket(this, manager.getListenerProxy(), endpoint, channel.getGuild(), sessionId, token, manager.isAutoReconnect());
      DaveSession daveSession = manager.getJDA().getAudioModuleConfig().getDaveSessionFactory().createDaveSession(this.webSocket, manager.getJDA().getSelfUser().getIdLong(), channel.getIdLong());
      this.webSocket.setDaveSession(daveSession);
   }

   public void startConnection() {
      this.webSocket.startConnection();
   }

   public ConnectionStatus getConnectionStatus() {
      return this.webSocket.getConnectionStatus();
   }

   public void setAutoReconnect(boolean shouldReconnect) {
      this.webSocket.setAutoReconnect(shouldReconnect);
   }

   public void setSendingHandler(AudioSendHandler handler) {
      this.sendHandler = handler;
      if (this.webSocket.isReady()) {
         this.setupSendSystem();
      }

   }

   public void setReceivingHandler(AudioReceiveHandler handler) {
      this.receiveHandler = handler;
      if (this.webSocket.isReady()) {
         this.setupReceiveSystem();
      }

   }

   public void setSpeakingMode(EnumSet<SpeakingMode> mode) {
      int raw = SpeakingMode.getRaw((Collection)mode);
      if (raw != this.speakingMode && this.webSocket.isReady()) {
         this.setSpeaking(raw);
      }

      this.speakingMode = raw;
   }

   public void setQueueTimeout(long queueTimeout) {
      this.queueTimeout = queueTimeout;
   }

   public AudioChannel getChannel() {
      return this.channel;
   }

   public void setChannel(AudioChannel channel) {
      this.channel = channel;
   }

   public JDAImpl getJDA() {
      return this.api;
   }

   public Guild getGuild() {
      return this.getChannel().getGuild();
   }

   public void close(ConnectionStatus closeStatus) {
      this.shutdown();
      this.webSocket.close(closeStatus);
   }

   public synchronized void shutdown() {
      this.shutdown = true;
      if (this.sendSystem != null) {
         this.sendSystem.shutdown();
         this.sendSystem = null;
      }

      if (this.receiveThread != null) {
         this.receiveThread.interrupt();
         this.receiveThread = null;
      }

      if (this.combinedAudioExecutor != null) {
         this.combinedAudioExecutor.shutdownNow();
         this.combinedAudioExecutor = null;
      }

      if (this.opusEncoder != null) {
         Opus.INSTANCE.opus_encoder_destroy(this.opusEncoder);
         this.opusEncoder = null;
      }

      this.opusDecoders.valueCollection().forEach(Decoder::close);
      this.opusDecoders.clear();
      ReentrantLock var10000 = this.readyLock;
      Condition var10001 = this.readyCondvar;
      Objects.requireNonNull(var10001);
      MiscUtil.locked(var10000, var10001::signalAll);
   }

   public WebSocket getWebSocket() {
      return this.webSocket.socket;
   }

   protected void prepareReady() {
      Thread readyThread = new Thread(() -> {
         this.getJDA().setContext();
         boolean ready = (Boolean)MiscUtil.locked(this.readyLock, (Supplier)(() -> {
            long timeout = this.getGuild().getAudioManager().getConnectTimeout();

            while(!this.webSocket.isReady()) {
               try {
                  boolean activated = this.readyCondvar.await(timeout, TimeUnit.MILLISECONDS);
                  if (!activated) {
                     this.webSocket.close(ConnectionStatus.ERROR_CONNECTION_TIMEOUT);
                     this.shutdown = true;
                  }

                  if (this.shutdown) {
                     return false;
                  }
               } catch (InterruptedException e) {
                  LOG.error((String)"AudioConnection ready thread got interrupted while sleeping", (Throwable)e);
                  return false;
               }
            }

            return true;
         }));
         if (ready) {
            this.setupSendSystem();
            this.setupReceiveSystem();
         }

      });
      readyThread.setUncaughtExceptionHandler((thread, throwable) -> {
         LOG.error("Uncaught exception in Audio ready-thread", throwable);
         JDAImpl api = this.getJDA();
         api.handleEvent(new ExceptionEvent(api, throwable, true));
      });
      readyThread.setDaemon(true);
      readyThread.setName(this.threadIdentifier + " Ready Thread");
      readyThread.start();
   }

   protected void removeUserSSRC(long userId) {
      AtomicInteger ssrcRef = new AtomicInteger(0);
      boolean modified = this.ssrcMap.retainEntries((ssrc, id) -> {
         boolean isEntry = id == userId;
         if (isEntry) {
            ssrcRef.set(ssrc);
         }

         return !isEntry;
      });
      if (modified) {
         Decoder decoder = this.opusDecoders.remove(ssrcRef.get());
         if (decoder != null) {
            decoder.close();
         }

      }
   }

   protected void updateUserSSRC(int ssrc, long userId) {
      if (this.ssrcMap.containsKey(ssrc)) {
         long previousId = this.ssrcMap.get(ssrc);
         if (previousId != userId) {
            LOG.error("Yeah.. So.. JDA received a UserSSRC update for an ssrc that already had a User set. Inform devs.\nChannelId: {} SSRC: {} oldId: {} newId: {}", this.channel.getId(), ssrc, previousId, userId);
         }
      } else {
         this.ssrcMap.put(ssrc, userId);
         if (this.receiveThread != null && AudioNatives.ensureOpus()) {
            this.opusDecoders.put(ssrc, new Decoder(ssrc));
         }
      }

   }

   private synchronized void setupSendSystem() {
      if (this.udpSocket != null && !this.udpSocket.isClosed() && this.sendHandler != null && this.sendSystem == null) {
         this.setSpeaking(this.speakingMode);
         IAudioSendFactory factory = this.getJDA().getAudioSendFactory();
         this.sendSystem = factory.createSendSystem(new PacketProvider());
         this.sendSystem.setContextMap(this.getJDA().getContextMap());
         this.sendSystem.start();
      } else if (this.sendHandler == null && this.sendSystem != null) {
         this.sendSystem.shutdown();
         this.sendSystem = null;
         if (this.opusEncoder != null) {
            Opus.INSTANCE.opus_encoder_destroy(this.opusEncoder);
            this.opusEncoder = null;
         }
      }

   }

   private synchronized void setupReceiveSystem() {
      if (this.udpSocket != null && !this.udpSocket.isClosed() && this.receiveHandler != null && this.receiveThread == null) {
         this.setupReceiveThread();
      } else if (this.receiveHandler == null && this.receiveThread != null) {
         this.receiveThread.interrupt();
         this.receiveThread = null;
         if (this.combinedAudioExecutor != null) {
            this.combinedAudioExecutor.shutdownNow();
            this.combinedAudioExecutor = null;
         }

         this.opusDecoders.valueCollection().forEach(Decoder::close);
         this.opusDecoders.clear();
      } else if (this.receiveHandler != null && !this.receiveHandler.canReceiveCombined() && this.combinedAudioExecutor != null) {
         this.combinedAudioExecutor.shutdownNow();
         this.combinedAudioExecutor = null;
      }

   }

   private synchronized void setupReceiveThread() {
      if (this.receiveThread == null) {
         this.receiveThread = new Thread(() -> {
            this.getJDA().setContext();

            try {
               this.udpSocket.setSoTimeout(1000);
            } catch (SocketException e) {
               LOG.error((String)"Couldn't set SO_TIMEOUT for UDP socket", (Throwable)e);
            }

            byte[] buffer = new byte[4096];
            ResizingByteBuffer decryptBuffer = new ResizingByteBuffer(ByteBuffer.allocateDirect(1024));

            while(!this.udpSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
               DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

               try {
                  this.udpSocket.receive(receivedPacket);
                  boolean shouldDecode = this.receiveHandler != null && (this.receiveHandler.canReceiveUser() || this.receiveHandler.canReceiveCombined());
                  boolean canReceive = this.receiveHandler != null && (this.receiveHandler.canReceiveUser() || this.receiveHandler.canReceiveCombined() || this.receiveHandler.canReceiveEncoded());
                  if (canReceive && this.webSocket.getSecretKey() != null) {
                     this.couldReceive = true;
                     AudioPacket audioPacket = new AudioPacket(receivedPacket);
                     int ssrc = audioPacket.getSSRC();
                     long userId = this.ssrcMap.containsKey(ssrc) ? this.ssrcMap.get(ssrc) : 0L;
                     if (userId != 0L) {
                        AudioPacket decryptedPacket = audioPacket.asDecryptAudioPacket(this.webSocket.crypto, userId, decryptBuffer);
                        if (decryptedPacket != null) {
                           Decoder decoder = this.opusDecoders.get(ssrc);
                           if (decoder == null) {
                              if (AudioNatives.ensureOpus()) {
                                 this.opusDecoders.put(ssrc, decoder = new Decoder(ssrc));
                              } else if (!this.receiveHandler.canReceiveEncoded()) {
                                 LOG.error("Unable to decode audio due to missing opus binaries!");
                                 break;
                              }
                           }

                           OpusPacket opusPacket = new OpusPacket(decryptedPacket, userId, decoder);
                           if (this.receiveHandler.canReceiveEncoded()) {
                              this.receiveHandler.handleEncodedAudio(opusPacket);
                           }

                           if (shouldDecode && opusPacket.canDecode()) {
                              User user = this.getJDA().getUserById(userId);
                              if (user == null) {
                                 LOG.warn("Received audio data with a known SSRC, but the userId associate with the SSRC is unknown to JDA! You likely need to cache members.");
                              } else {
                                 short[] decodedAudio = opusPacket.decode();
                                 if (decodedAudio != null) {
                                    if (this.receiveHandler.canReceiveUser()) {
                                       this.receiveHandler.handleUserAudio(new UserAudio(user, decodedAudio));
                                    }

                                    if (this.receiveHandler.canReceiveCombined() && this.receiveHandler.includeUserInCombinedAudio(user)) {
                                       Queue<AudioData> queue = (Queue)this.combinedQueue.get(user);
                                       if (queue == null) {
                                          queue = new ConcurrentLinkedQueue();
                                          this.combinedQueue.put(user, queue);
                                       }

                                       queue.add(new AudioData(decodedAudio));
                                    }
                                 }
                              }
                           }
                        }
                     }
                  } else {
                     this.couldReceive = false;
                  }
               } catch (SocketTimeoutException var17) {
               } catch (SocketException var18) {
               } catch (Exception e) {
                  LOG.error((String)"There was some random exception while waiting for udp packets", (Throwable)e);
               }
            }

         });
         this.receiveThread.setUncaughtExceptionHandler((thread, throwable) -> {
            LOG.error("There was some uncaught exception in the audio receive thread", throwable);
            JDAImpl api = this.getJDA();
            api.handleEvent(new ExceptionEvent(api, throwable, true));
         });
         this.receiveThread.setDaemon(true);
         this.receiveThread.setName(this.threadIdentifier + " Receiving Thread");
         this.receiveThread.start();
      }

      if (this.receiveHandler.canReceiveCombined()) {
         this.setupCombinedExecutor();
      }

   }

   private synchronized void setupCombinedExecutor() {
      if (this.combinedAudioExecutor == null) {
         this.combinedAudioExecutor = Executors.newSingleThreadScheduledExecutor((task) -> {
            Thread t = new Thread(task, this.threadIdentifier + " Combined Thread");
            t.setDaemon(true);
            t.setUncaughtExceptionHandler((thread, throwable) -> {
               LOG.error("I have no idea how, but there was an uncaught exception in the combinedAudioExecutor", throwable);
               JDAImpl api = this.getJDA();
               api.handleEvent(new ExceptionEvent(api, throwable, true));
            });
            return t;
         });
         this.combinedAudioExecutor.scheduleAtFixedRate(() -> {
            this.getJDA().setContext();

            try {
               List<User> users = new ArrayList();
               List<short[]> audioParts = new ArrayList();
               if (this.receiveHandler != null && this.receiveHandler.canReceiveCombined()) {
                  long currentTime = System.currentTimeMillis();

                  for(Map.Entry<User, Queue<AudioData>> entry : this.combinedQueue.entrySet()) {
                     User user = (User)entry.getKey();
                     Queue<AudioData> queue = (Queue)entry.getValue();
                     if (!queue.isEmpty()) {
                        AudioData audioData;
                        for(audioData = (AudioData)queue.poll(); audioData != null && currentTime - audioData.time > this.queueTimeout; audioData = (AudioData)queue.poll()) {
                        }

                        if (audioData != null) {
                           users.add(user);
                           audioParts.add(audioData.data);
                        }
                     }
                  }

                  if (!audioParts.isEmpty()) {
                     int audioLength = audioParts.stream().mapToInt((it) -> it.length).max().getAsInt();
                     short[] mix = new short[1920];

                     for(int i = 0; i < audioLength; ++i) {
                        int sample = 0;
                        Iterator<short[]> iterator = audioParts.iterator();

                        while(iterator.hasNext()) {
                           short[] audio = (short[])iterator.next();
                           if (i < audio.length) {
                              sample += audio[i];
                           } else {
                              iterator.remove();
                           }
                        }

                        if (sample > 32767) {
                           mix[i] = 32767;
                        } else if (sample < -32768) {
                           mix[i] = Short.MIN_VALUE;
                        } else {
                           mix[i] = (short)sample;
                        }
                     }

                     this.receiveHandler.handleCombinedAudio(new CombinedAudio(users, mix));
                  } else {
                     this.receiveHandler.handleCombinedAudio(new CombinedAudio(Collections.emptyList(), new short[1920]));
                  }
               }
            } catch (Exception e) {
               LOG.error((String)"There was some unexpected exception in the combinedAudioExecutor!", (Throwable)e);
            }

         }, 0L, 20L, TimeUnit.MILLISECONDS);
      }

   }

   private ByteBuffer encodeToOpus(ByteBuffer rawAudio) {
      ShortBuffer nonEncodedBuffer = ShortBuffer.allocate(rawAudio.remaining() / 2);
      ByteBuffer encoded = ByteBuffer.allocateDirect(4096);

      for(int i = rawAudio.position(); i < rawAudio.limit(); i += 2) {
         int firstByte = 255 & rawAudio.get(i);
         int secondByte = 255 & rawAudio.get(i + 1);
         short toShort = (short)(firstByte << 8 | secondByte);
         nonEncodedBuffer.put(toShort);
      }

      ((Buffer)nonEncodedBuffer).flip();
      int result = Opus.INSTANCE.opus_encode(this.opusEncoder, nonEncodedBuffer, 960, encoded, encoded.capacity());
      if (result <= 0) {
         LOG.error((String)"Received error code from opus_encode(...): {}", (Object)result);
         return null;
      } else {
         ((Buffer)encoded).position(0).limit(result);
         return encoded;
      }
   }

   private void setSpeaking(int raw) {
      DataObject obj = DataObject.empty().put("speaking", raw).put("ssrc", this.webSocket.getSSRC()).put("delay", 0);
      this.webSocket.send(5, obj);
   }

   protected void finalize() {
      this.shutdown();
   }

   private class PacketProvider implements IPacketProvider {
      private char seq;
      private int timestamp;
      private ResizingByteBuffer buffer;
      private ByteBuffer temporaryDirectBuffer;
      private ByteBuffer datagramBuffer;

      private PacketProvider() {
         this.seq = 0;
         this.timestamp = 0;
         this.buffer = new ResizingByteBuffer(ByteBuffer.allocateDirect(2048));
         this.temporaryDirectBuffer = null;
         this.datagramBuffer = null;
      }

      @Nonnull
      public String getIdentifier() {
         return AudioConnection.this.threadIdentifier;
      }

      @Nonnull
      public AudioChannel getConnectedChannel() {
         return AudioConnection.this.getChannel();
      }

      @Nonnull
      public DatagramSocket getUdpSocket() {
         return AudioConnection.this.udpSocket;
      }

      @Nonnull
      public InetSocketAddress getSocketAddress() {
         return AudioConnection.this.webSocket.getAddress();
      }

      public DatagramPacket getNextPacket(boolean unused) {
         ByteBuffer buffer = this.getNextPacketRaw(unused);
         return buffer == null ? null : this.getDatagramPacket(buffer);
      }

      public ByteBuffer getNextPacketRaw(boolean unused) {
         try {
            if (AudioConnection.this.sendHandler != null && AudioConnection.this.sendHandler.canProvide()) {
               ByteBuffer rawAudio = AudioConnection.this.sendHandler.provide20MsAudio();
               if (rawAudio != null && rawAudio.hasRemaining()) {
                  if (!AudioConnection.this.sendHandler.isOpus()) {
                     rawAudio = this.encodeAudio(rawAudio);
                     if (rawAudio == null) {
                        return null;
                     }
                  }

                  this.loadEncryptedPacketData(this.ensureDirect(rawAudio));
                  if (this.seq + 1 > 65535) {
                     this.seq = 0;
                  } else {
                     ++this.seq;
                  }
               }
            }
         } catch (Exception e) {
            AudioConnection.LOG.error((String)"There was an error while getting next audio packet", (Throwable)e);
         }

         this.timestamp += 960;
         return this.buffer.buffer();
      }

      private ByteBuffer ensureDirect(ByteBuffer buffer) {
         if (buffer.isDirect()) {
            return buffer;
         } else {
            if (this.temporaryDirectBuffer == null) {
               this.temporaryDirectBuffer = ByteBuffer.allocateDirect(buffer.remaining());
            }

            this.temporaryDirectBuffer = IOUtil.replace(this.temporaryDirectBuffer, buffer);
            return this.temporaryDirectBuffer;
         }
      }

      private ByteBuffer encodeAudio(ByteBuffer rawAudio) {
         if (AudioConnection.this.opusEncoder == null) {
            if (!AudioNatives.ensureOpus()) {
               if (!AudioConnection.printedError) {
                  AudioConnection.LOG.error("Unable to process PCM audio without opus binaries!");
               }

               AudioConnection.printedError = true;
               return null;
            }

            IntBuffer error = IntBuffer.allocate(1);
            AudioConnection.this.opusEncoder = Opus.INSTANCE.opus_encoder_create(48000, 2, 2049, error);
            if (error.get() != 0 && AudioConnection.this.opusEncoder == null) {
               AudioConnection.LOG.error((String)"Received error status from opus_encoder_create(...): {}", (Object)error.get());
               return null;
            }
         }

         return AudioConnection.this.encodeToOpus(rawAudio);
      }

      private DatagramPacket getDatagramPacket(ByteBuffer b) {
         if (this.datagramBuffer == null) {
            this.datagramBuffer = ByteBuffer.allocate(b.remaining());
         }

         this.datagramBuffer = IOUtil.replace(this.datagramBuffer, b);
         byte[] data = this.datagramBuffer.array();
         int offset = this.datagramBuffer.arrayOffset() + this.datagramBuffer.position();
         int length = this.datagramBuffer.remaining();
         return new DatagramPacket(data, offset, length, AudioConnection.this.webSocket.getAddress());
      }

      private void loadEncryptedPacketData(ByteBuffer rawAudio) {
         AudioPacket packet = new AudioPacket(this.seq, this.timestamp, AudioConnection.this.webSocket.getSSRC(), rawAudio);
         packet.asEncryptedPacket(AudioConnection.this.webSocket.crypto, this.buffer);
      }

      public void onConnectionError(@Nonnull ConnectionStatus status) {
         AudioConnection.LOG.warn((String)"IAudioSendSystem reported a connection error of: {}", (Object)status);
         AudioConnection.LOG.warn("Shutting down AudioConnection.");
         AudioConnection.this.webSocket.close(status);
      }

      public void onConnectionLost() {
         AudioConnection.LOG.warn("Closing AudioConnection due to inability to send audio packets.");
         AudioConnection.LOG.warn("Cannot send audio packet because JDA cannot navigate the route to Discord.\nAre you sure you have internet connection? It is likely that you've lost connection.");
         AudioConnection.this.webSocket.close(ConnectionStatus.ERROR_LOST_CONNECTION);
      }
   }

   private static class AudioData {
      private final long time;
      private final short[] data;

      private AudioData(short[] data) {
         this.time = System.currentTimeMillis();
         this.data = data;
      }
   }
}
