package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.Lockable;
import okhttp3.internal.concurrent.Task;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.connection.BufferedSocket;
import okhttp3.internal.connection.BufferedSocketKt;
import okhttp3.internal.connection.RealCall;
import okio.ByteString;
import okio.Sink;
import okio.Socket;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000²\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 f2\u00020\u00012\u00020\u00022\u00020\u0003:\u0004cdefBI\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u0012\u0006\u0010\u0010\u001a\u00020\r\u0012\u0006\u0010\u0011\u001a\u00020\r¢\u0006\u0004\b\u0012\u0010\u0013J\b\u00109\u001a\u00020\u0007H\u0016J\b\u0010.\u001a\u00020\rH\u0016J\b\u0010:\u001a\u00020;H\u0016J\u000e\u0010<\u001a\u00020;2\u0006\u0010=\u001a\u00020>J\f\u0010?\u001a\u000200*\u00020\u000fH\u0002J\u0015\u0010@\u001a\u00020(2\u0006\u0010A\u001a\u00020BH\u0000¢\u0006\u0002\bCJ\u001e\u0010D\u001a\u00020;2\u0006\u0010&\u001a\u00020\u00172\u0006\u0010'\u001a\u00020E2\u0006\u0010=\u001a\u000200J\u000e\u0010F\u001a\u00020;2\u0006\u0010A\u001a\u00020BJ\u0006\u0010G\u001a\u000200J\u0006\u0010H\u001a\u00020;J\u0006\u0010I\u001a\u00020;J\u0006\u00105\u001a\u000202J\u0006\u00106\u001a\u000202J\u0006\u00107\u001a\u000202J\u0010\u0010J\u001a\u00020;2\u0006\u0010K\u001a\u00020\u0017H\u0016J\u0010\u0010J\u001a\u00020;2\u0006\u0010L\u001a\u00020+H\u0016J\u0010\u0010M\u001a\u00020;2\u0006\u0010N\u001a\u00020+H\u0016J\u0010\u0010O\u001a\u00020;2\u0006\u0010N\u001a\u00020+H\u0016J\u0018\u0010P\u001a\u00020;2\u0006\u0010Q\u001a\u0002022\u0006\u0010R\u001a\u00020\u0017H\u0016J\u0010\u0010S\u001a\u0002002\u0006\u0010K\u001a\u00020\u0017H\u0016J\u0010\u0010S\u001a\u0002002\u0006\u0010L\u001a\u00020+H\u0016J\u0018\u0010S\u001a\u0002002\u0006\u0010T\u001a\u00020+2\u0006\u0010U\u001a\u000202H\u0002J\u000e\u0010V\u001a\u0002002\u0006\u0010N\u001a\u00020+J\u001a\u0010W\u001a\u0002002\u0006\u0010Q\u001a\u0002022\b\u0010R\u001a\u0004\u0018\u00010\u0017H\u0016J \u0010W\u001a\u0002002\u0006\u0010Q\u001a\u0002022\b\u0010R\u001a\u0004\u0018\u00010\u00172\u0006\u0010X\u001a\u00020\rJ\b\u0010Y\u001a\u00020;H\u0002J\r\u0010Z\u001a\u000200H\u0000¢\u0006\u0002\b[J\r\u0010\\\u001a\u00020;H\u0000¢\u0006\u0002\b]J(\u0010^\u001a\u00020;2\n\u0010_\u001a\u00060`j\u0002`a2\n\b\u0002\u0010A\u001a\u0004\u0018\u00010B2\b\b\u0002\u0010b\u001a\u000200R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\u00020\tX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u001fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\"\u001a\u0004\u0018\u00010#X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020%X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010&\u001a\u0004\u0018\u00010\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010'\u001a\u0004\u0018\u00010(X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010)\u001a\b\u0012\u0004\u0012\u00020+0*X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010,\u001a\b\u0012\u0004\u0012\u00020-0*X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u000200X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00101\u001a\u000202X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00103\u001a\u0004\u0018\u00010\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00104\u001a\u000200X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00105\u001a\u000202X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00106\u001a\u000202X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00107\u001a\u000202X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00108\u001a\u000200X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006g"},
   d2 = {"Lokhttp3/internal/ws/RealWebSocket;", "Lokhttp3/WebSocket;", "Lokhttp3/internal/ws/WebSocketReader$FrameCallback;", "Lokhttp3/internal/concurrent/Lockable;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "originalRequest", "Lokhttp3/Request;", "listener", "Lokhttp3/WebSocketListener;", "random", "Ljava/util/Random;", "pingIntervalMillis", "", "extensions", "Lokhttp3/internal/ws/WebSocketExtensions;", "minimumDeflateSize", "webSocketCloseTimeout", "<init>", "(Lokhttp3/internal/concurrent/TaskRunner;Lokhttp3/Request;Lokhttp3/WebSocketListener;Ljava/util/Random;JLokhttp3/internal/ws/WebSocketExtensions;JJ)V", "getListener$okhttp", "()Lokhttp3/WebSocketListener;", "key", "", "call", "Lokhttp3/Call;", "getCall$okhttp", "()Lokhttp3/Call;", "setCall$okhttp", "(Lokhttp3/Call;)V", "writerTask", "Lokhttp3/internal/concurrent/Task;", "reader", "Lokhttp3/internal/ws/WebSocketReader;", "writer", "Lokhttp3/internal/ws/WebSocketWriter;", "taskQueue", "Lokhttp3/internal/concurrent/TaskQueue;", "name", "socket", "Lokio/Socket;", "pongQueue", "Ljava/util/ArrayDeque;", "Lokio/ByteString;", "messageAndCloseQueue", "", "queueSize", "enqueuedClose", "", "receivedCloseCode", "", "receivedCloseReason", "failed", "sentPingCount", "receivedPingCount", "receivedPongCount", "awaitingPong", "request", "cancel", "", "connect", "client", "Lokhttp3/OkHttpClient;", "isValid", "checkUpgradeSuccess", "response", "Lokhttp3/Response;", "checkUpgradeSuccess$okhttp", "initReaderAndWriter", "Lokhttp3/internal/connection/BufferedSocket;", "loopReader", "processNextFrame", "finishReader", "tearDown", "onReadMessage", "text", "bytes", "onReadPing", "payload", "onReadPong", "onReadClose", "code", "reason", "send", "data", "formatOpcode", "pong", "close", "cancelAfterCloseMillis", "runWriter", "writeOneFrame", "writeOneFrame$okhttp", "writePingFrame", "writePingFrame$okhttp", "failWebSocket", "e", "Ljava/lang/Exception;", "Lkotlin/Exception;", "isWriter", "Message", "Close", "WriterTask", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nRealWebSocket.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealWebSocket.kt\nokhttp3/internal/ws/RealWebSocket\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,703:1\n1#2:704\n55#3,4:705\n*S KotlinDebug\n*F\n+ 1 RealWebSocket.kt\nokhttp3/internal/ws/RealWebSocket\n*L\n499#1:705,4\n*E\n"})
public final class RealWebSocket implements WebSocket, WebSocketReader.FrameCallback, Lockable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Request originalRequest;
   @NotNull
   private final WebSocketListener listener;
   @NotNull
   private final Random random;
   private final long pingIntervalMillis;
   @Nullable
   private WebSocketExtensions extensions;
   private long minimumDeflateSize;
   private final long webSocketCloseTimeout;
   @NotNull
   private final String key;
   @Nullable
   private Call call;
   @Nullable
   private Task writerTask;
   @Nullable
   private WebSocketReader reader;
   @Nullable
   private WebSocketWriter writer;
   @NotNull
   private TaskQueue taskQueue;
   @Nullable
   private String name;
   @Nullable
   private Socket socket;
   @NotNull
   private final ArrayDeque<ByteString> pongQueue;
   @NotNull
   private final ArrayDeque<Object> messageAndCloseQueue;
   private long queueSize;
   private boolean enqueuedClose;
   private int receivedCloseCode;
   @Nullable
   private String receivedCloseReason;
   private boolean failed;
   private int sentPingCount;
   private int receivedPingCount;
   private int receivedPongCount;
   private boolean awaitingPong;
   @NotNull
   private static final List<Protocol> ONLY_HTTP1;
   private static final long MAX_QUEUE_SIZE = 16777216L;
   public static final long CANCEL_AFTER_CLOSE_MILLIS = 60000L;
   public static final long DEFAULT_MINIMUM_DEFLATE_SIZE = 1024L;

   public RealWebSocket(@NotNull TaskRunner taskRunner, @NotNull Request originalRequest, @NotNull WebSocketListener listener, @NotNull Random random, long pingIntervalMillis, @Nullable WebSocketExtensions extensions, long minimumDeflateSize, long webSocketCloseTimeout) {
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      Intrinsics.checkNotNullParameter(originalRequest, "originalRequest");
      Intrinsics.checkNotNullParameter(listener, "listener");
      Intrinsics.checkNotNullParameter(random, "random");
      super();
      this.originalRequest = originalRequest;
      this.listener = listener;
      this.random = random;
      this.pingIntervalMillis = pingIntervalMillis;
      this.extensions = extensions;
      this.minimumDeflateSize = minimumDeflateSize;
      this.webSocketCloseTimeout = webSocketCloseTimeout;
      this.taskQueue = taskRunner.newQueue();
      this.pongQueue = new ArrayDeque();
      this.messageAndCloseQueue = new ArrayDeque();
      this.receivedCloseCode = -1;
      if (!Intrinsics.areEqual((Object)"GET", (Object)this.originalRequest.method())) {
         int var13 = 0;
         String $this$_init__u24lambda_u241 = "Request must be GET: " + this.originalRequest.method();
         throw new IllegalArgumentException($this$_init__u24lambda_u241.toString());
      } else {
         byte[] var12 = new byte[16];
         ByteString.Companion var16 = ByteString.Companion;
         int var14 = 0;
         this.random.nextBytes(var12);
         Unit var17 = Unit.INSTANCE;
         this.key = ByteString.Companion.of$default(var16, var12, 0, 0, 3, (Object)null).base64();
      }
   }

   @NotNull
   public final WebSocketListener getListener$okhttp() {
      return this.listener;
   }

   @Nullable
   public final Call getCall$okhttp() {
      return this.call;
   }

   public final void setCall$okhttp(@Nullable Call var1) {
      this.call = var1;
   }

   @NotNull
   public Request request() {
      return this.originalRequest;
   }

   public synchronized long queueSize() {
      return this.queueSize;
   }

   public void cancel() {
      Call var10000 = this.call;
      Intrinsics.checkNotNull(var10000);
      var10000.cancel();
   }

   public final void connect(@NotNull OkHttpClient client) {
      Intrinsics.checkNotNullParameter(client, "client");
      if (this.originalRequest.header("Sec-WebSocket-Extensions") != null) {
         failWebSocket$default(this, (Exception)(new ProtocolException("Request header not permitted: 'Sec-WebSocket-Extensions'")), (Response)null, false, 6, (Object)null);
      } else {
         OkHttpClient webSocketClient = client.newBuilder().eventListener(EventListener.NONE).protocols(ONLY_HTTP1).build();
         final Request request = this.originalRequest.newBuilder().header("Upgrade", "websocket").header("Connection", "Upgrade").header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").header("Sec-WebSocket-Extensions", "permessage-deflate").build();
         this.call = new RealCall(webSocketClient, request, true);
         Call var10000 = this.call;
         Intrinsics.checkNotNull(var10000);
         var10000.enqueue(new Callback() {
            public void onResponse(Call call, Response response) {
               Intrinsics.checkNotNullParameter(call, "call");
               Intrinsics.checkNotNullParameter(response, "response");

               Socket var4;
               try {
                  var4 = RealWebSocket.this.checkUpgradeSuccess$okhttp(response);
               } catch (IOException e) {
                  RealWebSocket.failWebSocket$default(RealWebSocket.this, (Exception)e, response, false, 4, (Object)null);
                  _UtilCommonKt.closeQuietly(response);
                  Socket var10000 = response.socket();
                  if (var10000 != null) {
                     Sink var15 = var10000.getSink();
                     if (var15 != null) {
                        _UtilCommonKt.closeQuietly(var15);
                     }
                  }

                  var10000 = response.socket();
                  if (var10000 != null) {
                     Source var17 = var10000.getSource();
                     if (var17 != null) {
                        _UtilCommonKt.closeQuietly(var17);
                     }
                  }

                  return;
               }

               Socket socket = var4;
               WebSocketExtensions extensions = WebSocketExtensions.Companion.parse(response.headers());
               RealWebSocket.this.extensions = extensions;
               if (!RealWebSocket.this.isValid(extensions)) {
                  RealWebSocket e = RealWebSocket.this;
                  RealWebSocket var6 = RealWebSocket.this;
                  synchronized(e) {
                     int var8 = 0;
                     var6.messageAndCloseQueue.clear();
                     var8 = var6.close(1010, "unexpected Sec-WebSocket-Extensions in response header");
                  }
               }

               String name = _UtilJvmKt.okHttpName + " WebSocket " + request.url().redact();
               RealWebSocket.this.initReaderAndWriter(name, BufferedSocketKt.asBufferedSocket(socket), true);
               RealWebSocket.this.loopReader(response);
            }

            public void onFailure(Call call, IOException e) {
               Intrinsics.checkNotNullParameter(call, "call");
               Intrinsics.checkNotNullParameter(e, "e");
               RealWebSocket.failWebSocket$default(RealWebSocket.this, (Exception)e, (Response)null, false, 6, (Object)null);
            }
         });
      }
   }

   private final boolean isValid(WebSocketExtensions $this$isValid) {
      if ($this$isValid.unknownValues) {
         return false;
      } else if ($this$isValid.clientMaxWindowBits != null) {
         return false;
      } else {
         if ($this$isValid.serverMaxWindowBits != null) {
            int var2 = $this$isValid.serverMaxWindowBits;
            if (!(8 <= var2 ? var2 < 16 : false)) {
               return false;
            }
         }

         return true;
      }
   }

   @NotNull
   public final Socket checkUpgradeSuccess$okhttp(@NotNull Response response) throws IOException {
      Intrinsics.checkNotNullParameter(response, "response");
      if (response.code() != 101) {
         throw new ProtocolException("Expected HTTP 101 response but was '" + response.code() + ' ' + response.message() + '\'');
      } else {
         String headerConnection = Response.header$default(response, "Connection", (String)null, 2, (Object)null);
         if (!StringsKt.equals("Upgrade", headerConnection, true)) {
            throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '" + headerConnection + '\'');
         } else {
            String headerUpgrade = Response.header$default(response, "Upgrade", (String)null, 2, (Object)null);
            if (!StringsKt.equals("websocket", headerUpgrade, true)) {
               throw new ProtocolException("Expected 'Upgrade' header value 'websocket' but was '" + headerUpgrade + '\'');
            } else {
               String headerAccept = Response.header$default(response, "Sec-WebSocket-Accept", (String)null, 2, (Object)null);
               String acceptExpected = ByteString.Companion.encodeUtf8(this.key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").sha1().base64();
               if (!Intrinsics.areEqual((Object)acceptExpected, (Object)headerAccept)) {
                  throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '" + acceptExpected + "' but was '" + headerAccept + '\'');
               } else {
                  return response.socket();
               }
            }
         }
      }
   }

   public final void initReaderAndWriter(@NotNull String name, @NotNull BufferedSocket socket, boolean client) {
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(socket, "socket");
      WebSocketExtensions var10000 = this.extensions;
      Intrinsics.checkNotNull(var10000);
      WebSocketExtensions extensions = var10000;
      synchronized(this) {
         int var6 = 0;
         this.name = name;
         this.socket = socket;
         this.writer = new WebSocketWriter(client, socket.getSink(), this.random, extensions.perMessageDeflate, extensions.noContextTakeover(client), this.minimumDeflateSize);
         this.writerTask = new WriterTask();
         if (this.pingIntervalMillis != 0L) {
            long pingIntervalNanos = TimeUnit.MILLISECONDS.toNanos(this.pingIntervalMillis);
            this.taskQueue.schedule(name + " ping", pingIntervalNanos, RealWebSocket::initReaderAndWriter$lambda$0$0);
         }

         if (!((Collection)this.messageAndCloseQueue).isEmpty()) {
            this.runWriter();
         }

         Unit var11 = Unit.INSTANCE;
      }

      this.reader = new WebSocketReader(client, socket.getSource(), this, extensions.perMessageDeflate, extensions.noContextTakeover(!client));
   }

   public final void loopReader(@NotNull Response response) throws IOException {
      Intrinsics.checkNotNullParameter(response, "response");

      try {
         this.listener.onOpen(this, response);

         while(this.receivedCloseCode == -1) {
            WebSocketReader var10000 = this.reader;
            Intrinsics.checkNotNull(var10000);
            var10000.processNextFrame();
         }
      } catch (Exception e) {
         failWebSocket$default(this, e, (Response)null, false, 6, (Object)null);
      } finally {
         this.finishReader();
      }

   }

   public final boolean processNextFrame() throws IOException {
      boolean var1;
      try {
         WebSocketReader var10000 = this.reader;
         Intrinsics.checkNotNull(var10000);
         var10000.processNextFrame();
         var1 = this.receivedCloseCode == -1;
      } catch (Exception e) {
         failWebSocket$default(this, e, (Response)null, false, 6, (Object)null);
         var1 = false;
      }

      return var1;
   }

   public final void finishReader() {
      int code = 0;
      Object reason = null;
      boolean sendOnClosed = false;
      Object readerToClose = null;
      String var10;
      WebSocketReader var12;
      synchronized(this) {
         int var6 = 0;
         code = this.receivedCloseCode;
         var10 = this.receivedCloseReason;
         var12 = this.reader;
         this.reader = null;
         if (this.enqueuedClose && this.messageAndCloseQueue.isEmpty()) {
            WebSocketWriter writerToClose = this.writer;
            if (writerToClose != null) {
               this.writer = null;
               TaskQueue.execute$default(this.taskQueue, this.name + " writer close", 0L, false, RealWebSocket::finishReader$lambda$0$0, 2, (Object)null);
            }

            this.taskQueue.shutdown();
         }

         sendOnClosed = !this.failed && this.writer == null && this.receivedCloseCode != -1;
         Unit var13 = Unit.INSTANCE;
      }

      if (sendOnClosed) {
         WebSocketListener var10000 = this.listener;
         WebSocket var10001 = this;
         Intrinsics.checkNotNull(var10);
         var10000.onClosed(var10001, code, var10);
      }

      if (var12 != null) {
         _UtilCommonKt.closeQuietly(var12);
      }

   }

   public final void tearDown() throws InterruptedException {
      this.taskQueue.shutdown();
      this.taskQueue.idleLatch().await(10L, TimeUnit.SECONDS);
   }

   public final synchronized int sentPingCount() {
      return this.sentPingCount;
   }

   public final synchronized int receivedPingCount() {
      return this.receivedPingCount;
   }

   public final synchronized int receivedPongCount() {
      return this.receivedPongCount;
   }

   public void onReadMessage(@NotNull String text) throws IOException {
      Intrinsics.checkNotNullParameter(text, "text");
      this.listener.onMessage(this, text);
   }

   public void onReadMessage(@NotNull ByteString bytes) throws IOException {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      this.listener.onMessage(this, bytes);
   }

   public synchronized void onReadPing(@NotNull ByteString payload) {
      Intrinsics.checkNotNullParameter(payload, "payload");
      if (!this.failed && (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty())) {
         this.pongQueue.add(payload);
         this.runWriter();
         int var2 = this.receivedPingCount++;
      }
   }

   public synchronized void onReadPong(@NotNull ByteString payload) {
      Intrinsics.checkNotNullParameter(payload, "payload");
      int var2 = this.receivedPongCount++;
      this.awaitingPong = false;
   }

   public void onReadClose(int code, @NotNull String reason) {
      Intrinsics.checkNotNullParameter(reason, "reason");
      if (code == -1) {
         String var9 = "Failed requirement.";
         throw new IllegalArgumentException(var9.toString());
      } else {
         synchronized(this){}

         try {
            int var4 = 0;
            if (this.receivedCloseCode != -1) {
               int var5 = 0;
               String var10 = "already closed";
               throw new IllegalStateException(var10.toString());
            }

            this.receivedCloseCode = code;
            this.receivedCloseReason = reason;
            Unit var8 = Unit.INSTANCE;
         } finally {
            ;
         }

         this.listener.onClosing(this, code, reason);
      }
   }

   public boolean send(@NotNull String text) {
      Intrinsics.checkNotNullParameter(text, "text");
      return this.send(ByteString.Companion.encodeUtf8(text), 1);
   }

   public boolean send(@NotNull ByteString bytes) {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      return this.send(bytes, 2);
   }

   private final synchronized boolean send(ByteString data, int formatOpcode) {
      if (!this.failed && !this.enqueuedClose) {
         if (this.queueSize + (long)data.size() > 16777216L) {
            this.close(1001, (String)null);
            return false;
         } else {
            this.queueSize += (long)data.size();
            this.messageAndCloseQueue.add(new Message(formatOpcode, data));
            this.runWriter();
            return true;
         }
      } else {
         return false;
      }
   }

   public final synchronized boolean pong(@NotNull ByteString payload) {
      Intrinsics.checkNotNullParameter(payload, "payload");
      if (!this.failed && (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty())) {
         this.pongQueue.add(payload);
         this.runWriter();
         return true;
      } else {
         return false;
      }
   }

   public boolean close(int code, @Nullable String reason) {
      return this.close(code, reason, this.webSocketCloseTimeout);
   }

   public final synchronized boolean close(int code, @Nullable String reason, long cancelAfterCloseMillis) {
      WebSocketProtocol.INSTANCE.validateCloseCode(code);
      ByteString reasonBytes = null;
      if (reason != null) {
         reasonBytes = ByteString.Companion.encodeUtf8(reason);
         if ((long)reasonBytes.size() > 123L) {
            int var6 = 0;
            String var7 = "reason.size() > 123: " + reason;
            throw new IllegalArgumentException(var7.toString());
         }
      }

      if (!this.failed && !this.enqueuedClose) {
         this.enqueuedClose = true;
         this.messageAndCloseQueue.add(new Close(code, reasonBytes, cancelAfterCloseMillis));
         this.runWriter();
         return true;
      } else {
         return false;
      }
   }

   private final void runWriter() {
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         Task writerTask = this.writerTask;
         if (writerTask != null) {
            TaskQueue.schedule$default(this.taskQueue, writerTask, 0L, 2, (Object)null);
         }

      }
   }

   public final boolean writeOneFrame$okhttp() throws IOException {
      Object writer = null;
      Object pong = null;
      Object messageOrClose = null;
      int receivedCloseCode = 0;
      receivedCloseCode = -1;
      Object receivedCloseReason = null;
      boolean sendOnClosed = false;
      Object writerToClose = null;
      synchronized(this){}

      try {
         int var9 = 0;
         if (this.failed) {
            boolean var14 = false;
            return var14;
         }

         var25 = this.writer;
         pong = this.pongQueue.poll();
         if (pong == null) {
            messageOrClose = this.messageAndCloseQueue.poll();
            if (messageOrClose instanceof Close) {
               receivedCloseCode = this.receivedCloseCode;
               receivedCloseReason = this.receivedCloseReason;
               if (receivedCloseCode == -1) {
                  long cancelAfterCloseMillis = ((Close)messageOrClose).getCancelAfterCloseMillis();
                  TaskQueue.execute$default(this.taskQueue, this.name + " cancel", TimeUnit.MILLISECONDS.toNanos(cancelAfterCloseMillis), false, RealWebSocket::writeOneFrame$lambda$0$0, 4, (Object)null);
               } else {
                  writerToClose = this.writer;
                  this.writer = null;
                  sendOnClosed = writerToClose != null && this.reader == null;
                  this.taskQueue.shutdown();
               }
            } else if (messageOrClose == null) {
               boolean var13 = false;
               return var13;
            }
         }

         Unit var29 = Unit.INSTANCE;
      } finally {
         ;
      }

      boolean var18;
      boolean close;
      try {
         var18 = false;
         var18 = true;
         if (pong != null) {
            Intrinsics.checkNotNull(var25);
            var25.writePong((ByteString)pong);
         } else if (messageOrClose instanceof Message) {
            Object message = messageOrClose;
            Intrinsics.checkNotNull(var25);
            var25.writeMessageFrame(((Message)messageOrClose).getFormatOpcode(), ((Message)messageOrClose).getData());
            synchronized(this) {
               int var30 = 0;
               this.queueSize -= (long)((Message)message).getData().size();
               Unit var31 = Unit.INSTANCE;
            }
         } else {
            if (!(messageOrClose instanceof Close)) {
               throw new AssertionError();
            }

            Intrinsics.checkNotNull(var25);
            var25.writeClose(((Close)messageOrClose).getCode(), ((Close)messageOrClose).getReason());
            if (sendOnClosed) {
               WebSocketListener var10000 = this.listener;
               WebSocket var10001 = this;
               Intrinsics.checkNotNull(receivedCloseReason);
               var10000.onClosed(var10001, receivedCloseCode, (String)receivedCloseReason);
            }
         }

         close = true;
         var18 = false;
      } finally {
         if (var18) {
            WebSocketWriter var34 = (WebSocketWriter)writerToClose;
            if ((WebSocketWriter)writerToClose != null) {
               _UtilCommonKt.closeQuietly(var34);
            }

         }
      }

      if (writerToClose != null) {
         _UtilCommonKt.closeQuietly((Closeable)writerToClose);
      }

      return close;
   }

   public final void writePingFrame$okhttp() {
      Object writer = null;
      int failedPing = 0;
      synchronized(this){}

      try {
         int var4 = 0;
         if (this.failed) {
            return;
         }

         WebSocketWriter var10000 = this.writer;
         if (var10000 == null) {
            return;
         }

         var10 = var10000;
         failedPing = this.awaitingPong ? this.sentPingCount : -1;
         int var5 = this.sentPingCount++;
         this.awaitingPong = true;
         Unit var12 = Unit.INSTANCE;
      } finally {
         ;
      }

      if (failedPing != -1) {
         failWebSocket$default(this, (Exception)(new SocketTimeoutException("sent ping but didn't receive pong within " + this.pingIntervalMillis + "ms (after " + (failedPing - 1) + " successful ping/pongs)")), (Response)null, true, 2, (Object)null);
      } else {
         try {
            var10.writePing(ByteString.EMPTY);
         } catch (IOException e) {
            failWebSocket$default(this, (Exception)e, (Response)null, true, 2, (Object)null);
         }

      }
   }

   public final void failWebSocket(@NotNull Exception param1, @Nullable Response param2, boolean param3) {
      // $FF: Couldn't be decompiled
   }

   // $FF: synthetic method
   public static void failWebSocket$default(RealWebSocket var0, Exception var1, Response var2, boolean var3, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = null;
      }

      if ((var4 & 4) != 0) {
         var3 = false;
      }

      var0.failWebSocket(var1, var2, var3);
   }

   private static final long initReaderAndWriter$lambda$0$0(RealWebSocket this$0, long $pingIntervalNanos) {
      this$0.writePingFrame$okhttp();
      return $pingIntervalNanos;
   }

   private static final Unit finishReader$lambda$0$0(WebSocketWriter $writerToClose) {
      _UtilCommonKt.closeQuietly($writerToClose);
      return Unit.INSTANCE;
   }

   private static final Unit writeOneFrame$lambda$0$0(RealWebSocket this$0) {
      this$0.cancel();
      return Unit.INSTANCE;
   }

   private static final Unit failWebSocket$lambda$0$0(Ref.ObjectRef $writerToClose) {
      _UtilCommonKt.closeQuietly((Closeable)$writerToClose.element);
      return Unit.INSTANCE;
   }

   static {
      ONLY_HTTP1 = CollectionsKt.listOf(Protocol.HTTP_1_1);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\f"},
      d2 = {"Lokhttp3/internal/ws/RealWebSocket$Message;", "", "formatOpcode", "", "data", "Lokio/ByteString;", "<init>", "(ILokio/ByteString;)V", "getFormatOpcode", "()I", "getData", "()Lokio/ByteString;", "okhttp"}
   )
   public static final class Message {
      private final int formatOpcode;
      @NotNull
      private final ByteString data;

      public Message(int formatOpcode, @NotNull ByteString data) {
         Intrinsics.checkNotNullParameter(data, "data");
         super();
         this.formatOpcode = formatOpcode;
         this.data = data;
      }

      public final int getFormatOpcode() {
         return this.formatOpcode;
      }

      @NotNull
      public final ByteString getData() {
         return this.data;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\t\b\u0000\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0010"},
      d2 = {"Lokhttp3/internal/ws/RealWebSocket$Close;", "", "code", "", "reason", "Lokio/ByteString;", "cancelAfterCloseMillis", "", "<init>", "(ILokio/ByteString;J)V", "getCode", "()I", "getReason", "()Lokio/ByteString;", "getCancelAfterCloseMillis", "()J", "okhttp"}
   )
   public static final class Close {
      private final int code;
      @Nullable
      private final ByteString reason;
      private final long cancelAfterCloseMillis;

      public Close(int code, @Nullable ByteString reason, long cancelAfterCloseMillis) {
         this.code = code;
         this.reason = reason;
         this.cancelAfterCloseMillis = cancelAfterCloseMillis;
      }

      public final int getCode() {
         return this.code;
      }

      @Nullable
      public final ByteString getReason() {
         return this.reason;
      }

      public final long getCancelAfterCloseMillis() {
         return this.cancelAfterCloseMillis;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0006"},
      d2 = {"Lokhttp3/internal/ws/RealWebSocket$WriterTask;", "Lokhttp3/internal/concurrent/Task;", "<init>", "(Lokhttp3/internal/ws/RealWebSocket;)V", "runOnce", "", "okhttp"}
   )
   private final class WriterTask extends Task {
      public WriterTask() {
         super(RealWebSocket.this.name + " writer", false, 2, (DefaultConstructorMarker)null);
      }

      public long runOnce() {
         try {
            if (RealWebSocket.this.writeOneFrame$okhttp()) {
               return 0L;
            }
         } catch (IOException e) {
            RealWebSocket.failWebSocket$default(RealWebSocket.this, (Exception)e, (Response)null, true, 2, (Object)null);
         }

         return -1L;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0086T¢\u0006\u0002\n\u0000¨\u0006\u000b"},
      d2 = {"Lokhttp3/internal/ws/RealWebSocket$Companion;", "", "<init>", "()V", "ONLY_HTTP1", "", "Lokhttp3/Protocol;", "MAX_QUEUE_SIZE", "", "CANCEL_AFTER_CLOSE_MILLIS", "DEFAULT_MINIMUM_DEFLATE_SIZE", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
