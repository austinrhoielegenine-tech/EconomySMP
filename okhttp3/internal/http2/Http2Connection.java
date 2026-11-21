package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Headers;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.Lockable;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.connection.BufferedSocket;
import okhttp3.internal.http2.flowcontrol.WindowCounter;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000Æ\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 ¡\u00012\u00020\u00012\u00020\u0002:\b\u009e\u0001\u009f\u0001 \u0001¡\u0001B\u0011\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0006\u0010V\u001a\u00020\u0011J\u0010\u0010W\u001a\u0004\u0018\u00010\u00122\u0006\u0010X\u001a\u00020\u0011J\u0017\u0010Y\u001a\u0004\u0018\u00010\u00122\u0006\u0010Z\u001a\u00020\u0011H\u0000¢\u0006\u0002\b[J\u0015\u0010\\\u001a\u00020]2\u0006\u0010^\u001a\u00020+H\u0000¢\u0006\u0002\b_J$\u0010`\u001a\u00020\u00122\u0006\u0010a\u001a\u00020\u00112\f\u0010b\u001a\b\u0012\u0004\u0012\u00020d0c2\u0006\u0010e\u001a\u00020\bJ\u001c\u0010f\u001a\u00020\u00122\f\u0010b\u001a\b\u0012\u0004\u0012\u00020d0c2\u0006\u0010e\u001a\u00020\bJ&\u0010f\u001a\u00020\u00122\u0006\u0010a\u001a\u00020\u00112\f\u0010b\u001a\b\u0012\u0004\u0012\u00020d0c2\u0006\u0010e\u001a\u00020\bH\u0002J+\u0010g\u001a\u00020]2\u0006\u0010Z\u001a\u00020\u00112\u0006\u0010h\u001a\u00020\b2\f\u0010i\u001a\b\u0012\u0004\u0012\u00020d0cH\u0000¢\u0006\u0002\bjJ(\u0010k\u001a\u00020]2\u0006\u0010Z\u001a\u00020\u00112\u0006\u0010h\u001a\u00020\b2\b\u0010l\u001a\u0004\u0018\u00010m2\u0006\u0010n\u001a\u00020+J\u001d\u0010o\u001a\u00020]2\u0006\u0010Z\u001a\u00020\u00112\u0006\u0010p\u001a\u00020qH\u0000¢\u0006\u0002\brJ\u001d\u0010s\u001a\u00020]2\u0006\u0010Z\u001a\u00020\u00112\u0006\u0010t\u001a\u00020qH\u0000¢\u0006\u0002\buJ\u001d\u0010v\u001a\u00020]2\u0006\u0010Z\u001a\u00020\u00112\u0006\u0010w\u001a\u00020+H\u0000¢\u0006\u0002\bxJ\u001e\u0010y\u001a\u00020]2\u0006\u0010z\u001a\u00020\b2\u0006\u0010{\u001a\u00020\u00112\u0006\u0010|\u001a\u00020\u0011J\u0006\u0010}\u001a\u00020]J\u0006\u0010y\u001a\u00020]J\u0006\u0010~\u001a\u00020]J\u0006\u0010\u007f\u001a\u00020]J\u000f\u0010\u0080\u0001\u001a\u00020]2\u0006\u0010t\u001a\u00020qJ\t\u0010\u0081\u0001\u001a\u00020]H\u0016J-\u0010\u0081\u0001\u001a\u00020]2\u0007\u0010\u0082\u0001\u001a\u00020q2\u0007\u0010\u0083\u0001\u001a\u00020q2\n\u0010\u0084\u0001\u001a\u0005\u0018\u00010\u0085\u0001H\u0000¢\u0006\u0003\b\u0086\u0001J\u0015\u0010\u0087\u0001\u001a\u00020]2\n\u0010\u0088\u0001\u001a\u0005\u0018\u00010\u0085\u0001H\u0002J\u0014\u0010\u0089\u0001\u001a\u00020]2\t\b\u0002\u0010\u008a\u0001\u001a\u00020\bH\u0007J\u0010\u0010\u008b\u0001\u001a\u00020]2\u0007\u0010\u008c\u0001\u001a\u000207J\u0010\u0010\u008d\u0001\u001a\u00020\b2\u0007\u0010\u008e\u0001\u001a\u00020+J\u000f\u0010\u008f\u0001\u001a\u00020]H\u0000¢\u0006\u0003\b\u0090\u0001J\u0017\u0010\u0091\u0001\u001a\u00020\b2\u0006\u0010Z\u001a\u00020\u0011H\u0000¢\u0006\u0003\b\u0092\u0001J%\u0010\u0093\u0001\u001a\u00020]2\u0006\u0010Z\u001a\u00020\u00112\f\u0010b\u001a\b\u0012\u0004\u0012\u00020d0cH\u0000¢\u0006\u0003\b\u0094\u0001J.\u0010\u0095\u0001\u001a\u00020]2\u0006\u0010Z\u001a\u00020\u00112\f\u0010b\u001a\b\u0012\u0004\u0012\u00020d0c2\u0007\u0010\u0096\u0001\u001a\u00020\bH\u0000¢\u0006\u0003\b\u0097\u0001J2\u0010\u0098\u0001\u001a\u00020]2\u0006\u0010Z\u001a\u00020\u00112\b\u0010\u0099\u0001\u001a\u00030\u009a\u00012\u0006\u0010n\u001a\u00020\u00112\u0007\u0010\u0096\u0001\u001a\u00020\bH\u0000¢\u0006\u0003\b\u009b\u0001J\u001f\u0010\u009c\u0001\u001a\u00020]2\u0006\u0010Z\u001a\u00020\u00112\u0006\u0010p\u001a\u00020qH\u0000¢\u0006\u0003\b\u009d\u0001R\u0014\u0010\u0007\u001a\u00020\bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u000b\u001a\u00020\fX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR \u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00120\u0010X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0015\u001a\u00020\u0016X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0019\u001a\u00020\u0011X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u001a\u0010\u001e\u001a\u00020\u0011X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u001b\"\u0004\b \u0010\u001dR\u000e\u0010!\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020%X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020%X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020%X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020)X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u00102\u001a\u000203X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b4\u00105R\u0011\u00106\u001a\u000207¢\u0006\b\n\u0000\u001a\u0004\b8\u00109R\u001a\u0010:\u001a\u000207X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b;\u00109\"\u0004\b<\u0010=R\u0011\u0010>\u001a\u00020?¢\u0006\b\n\u0000\u001a\u0004\b@\u0010AR\u001e\u0010C\u001a\u00020+2\u0006\u0010B\u001a\u00020+@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bD\u0010ER\u001e\u0010F\u001a\u00020+2\u0006\u0010B\u001a\u00020+@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bG\u0010ER\u0014\u0010H\u001a\u00020IX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\bJ\u0010KR\u0011\u0010L\u001a\u00020M¢\u0006\b\n\u0000\u001a\u0004\bN\u0010OR\u0015\u0010P\u001a\u00060QR\u00020\u0000¢\u0006\b\n\u0000\u001a\u0004\bR\u0010SR\u0014\u0010T\u001a\b\u0012\u0004\u0012\u00020\u00110UX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006¢\u0001"},
   d2 = {"Lokhttp3/internal/http2/Http2Connection;", "Ljava/io/Closeable;", "Lokhttp3/internal/concurrent/Lockable;", "builder", "Lokhttp3/internal/http2/Http2Connection$Builder;", "<init>", "(Lokhttp3/internal/http2/Http2Connection$Builder;)V", "client", "", "getClient$okhttp", "()Z", "listener", "Lokhttp3/internal/http2/Http2Connection$Listener;", "getListener$okhttp", "()Lokhttp3/internal/http2/Http2Connection$Listener;", "streams", "", "", "Lokhttp3/internal/http2/Http2Stream;", "getStreams$okhttp", "()Ljava/util/Map;", "connectionName", "", "getConnectionName$okhttp", "()Ljava/lang/String;", "lastGoodStreamId", "getLastGoodStreamId$okhttp", "()I", "setLastGoodStreamId$okhttp", "(I)V", "nextStreamId", "getNextStreamId$okhttp", "setNextStreamId$okhttp", "isShutdown", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "writerQueue", "Lokhttp3/internal/concurrent/TaskQueue;", "pushQueue", "settingsListenerQueue", "pushObserver", "Lokhttp3/internal/http2/PushObserver;", "intervalPingsSent", "", "intervalPongsReceived", "degradedPingsSent", "degradedPongsReceived", "awaitPingsSent", "awaitPongsReceived", "degradedPongDeadlineNs", "flowControlListener", "Lokhttp3/internal/http2/FlowControlListener;", "getFlowControlListener$okhttp", "()Lokhttp3/internal/http2/FlowControlListener;", "okHttpSettings", "Lokhttp3/internal/http2/Settings;", "getOkHttpSettings", "()Lokhttp3/internal/http2/Settings;", "peerSettings", "getPeerSettings", "setPeerSettings", "(Lokhttp3/internal/http2/Settings;)V", "readBytes", "Lokhttp3/internal/http2/flowcontrol/WindowCounter;", "getReadBytes", "()Lokhttp3/internal/http2/flowcontrol/WindowCounter;", "value", "writeBytesTotal", "getWriteBytesTotal", "()J", "writeBytesMaximum", "getWriteBytesMaximum", "socket", "Lokhttp3/internal/connection/BufferedSocket;", "getSocket$okhttp", "()Lokhttp3/internal/connection/BufferedSocket;", "writer", "Lokhttp3/internal/http2/Http2Writer;", "getWriter", "()Lokhttp3/internal/http2/Http2Writer;", "readerRunnable", "Lokhttp3/internal/http2/Http2Connection$ReaderRunnable;", "getReaderRunnable", "()Lokhttp3/internal/http2/Http2Connection$ReaderRunnable;", "currentPushRequests", "", "openStreamCount", "getStream", "id", "removeStream", "streamId", "removeStream$okhttp", "updateConnectionFlowControl", "", "read", "updateConnectionFlowControl$okhttp", "pushStream", "associatedStreamId", "requestHeaders", "", "Lokhttp3/internal/http2/Header;", "out", "newStream", "writeHeaders", "outFinished", "alternating", "writeHeaders$okhttp", "writeData", "buffer", "Lokio/Buffer;", "byteCount", "writeSynResetLater", "errorCode", "Lokhttp3/internal/http2/ErrorCode;", "writeSynResetLater$okhttp", "writeSynReset", "statusCode", "writeSynReset$okhttp", "writeWindowUpdateLater", "unacknowledgedBytesRead", "writeWindowUpdateLater$okhttp", "writePing", "reply", "payload1", "payload2", "writePingAndAwaitPong", "awaitPong", "flush", "shutdown", "close", "connectionCode", "streamCode", "cause", "Ljava/io/IOException;", "close$okhttp", "failConnection", "e", "start", "sendConnectionPreface", "setSettings", "settings", "isHealthy", "nowNs", "sendDegradedPingLater", "sendDegradedPingLater$okhttp", "pushedStream", "pushedStream$okhttp", "pushRequestLater", "pushRequestLater$okhttp", "pushHeadersLater", "inFinished", "pushHeadersLater$okhttp", "pushDataLater", "source", "Lokio/BufferedSource;", "pushDataLater$okhttp", "pushResetLater", "pushResetLater$okhttp", "Builder", "ReaderRunnable", "Listener", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nHttp2Connection.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 -UtilCommon.kt\nokhttp3/internal/_UtilCommonKt\n+ 5 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 6 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,1042:1\n63#2:1043\n63#2:1044\n63#2:1045\n38#2:1046\n63#2:1047\n63#2:1049\n63#2:1050\n63#2:1051\n34#2:1052\n63#2:1053\n63#2:1054\n34#2:1055\n63#2:1056\n63#2:1057\n49#2,4:1058\n63#2:1067\n63#2:1087\n63#2:1088\n63#2:1089\n63#2:1090\n63#2:1091\n63#2:1092\n63#2:1095\n63#2:1101\n63#2:1107\n63#2:1111\n1#3:1048\n228#4,5:1062\n228#4,5:1071\n228#4,5:1077\n228#4,5:1082\n228#4,2:1093\n230#4,3:1096\n228#4,2:1099\n230#4,3:1102\n228#4,2:1105\n230#4,3:1108\n37#5,2:1068\n13805#6:1070\n13806#6:1076\n*S KotlinDebug\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection\n*L\n174#1:1043\n176#1:1044\n179#1:1045\n183#1:1046\n190#1:1047\n242#1:1049\n243#1:1050\n313#1:1051\n321#1:1052\n394#1:1053\n405#1:1054\n407#1:1055\n424#1:1056\n426#1:1057\n452#1:1058,4\n459#1:1067\n519#1:1087\n520#1:1088\n531#1:1089\n557#1:1090\n914#1:1091\n152#1:1092\n926#1:1095\n944#1:1101\n971#1:1107\n985#1:1111\n454#1:1062,5\n467#1:1071,5\n473#1:1077,5\n478#1:1082,5\n923#1:1093,2\n923#1:1096,3\n941#1:1099,2\n941#1:1102,3\n967#1:1105,2\n967#1:1108,3\n461#1:1068,2\n466#1:1070\n466#1:1076\n*E\n"})
public final class Http2Connection implements Closeable, Lockable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   private final boolean client;
   @NotNull
   private final Listener listener;
   @NotNull
   private final Map<Integer, Http2Stream> streams;
   @NotNull
   private final String connectionName;
   private int lastGoodStreamId;
   private int nextStreamId;
   private boolean isShutdown;
   @NotNull
   private final TaskRunner taskRunner;
   @NotNull
   private final TaskQueue writerQueue;
   @NotNull
   private final TaskQueue pushQueue;
   @NotNull
   private final TaskQueue settingsListenerQueue;
   @NotNull
   private final PushObserver pushObserver;
   private long intervalPingsSent;
   private long intervalPongsReceived;
   private long degradedPingsSent;
   private long degradedPongsReceived;
   private long awaitPingsSent;
   private long awaitPongsReceived;
   private long degradedPongDeadlineNs;
   @NotNull
   private final FlowControlListener flowControlListener;
   @NotNull
   private final Settings okHttpSettings;
   @NotNull
   private Settings peerSettings;
   @NotNull
   private final WindowCounter readBytes;
   private long writeBytesTotal;
   private long writeBytesMaximum;
   @NotNull
   private final BufferedSocket socket;
   @NotNull
   private final Http2Writer writer;
   @NotNull
   private final ReaderRunnable readerRunnable;
   @NotNull
   private final Set<Integer> currentPushRequests;
   public static final int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
   @NotNull
   private static final Settings DEFAULT_SETTINGS;
   public static final int INTERVAL_PING = 1;
   public static final int DEGRADED_PING = 2;
   public static final int AWAIT_PING = 3;
   public static final int DEGRADED_PONG_TIMEOUT_NS = 1000000000;

   public Http2Connection(@NotNull Builder builder) {
      Intrinsics.checkNotNullParameter(builder, "builder");
      super();
      this.client = builder.getClient$okhttp();
      this.listener = builder.getListener$okhttp();
      this.streams = (Map)(new LinkedHashMap());
      this.connectionName = builder.getConnectionName$okhttp();
      this.nextStreamId = builder.getClient$okhttp() ? 3 : 2;
      this.taskRunner = builder.getTaskRunner$okhttp();
      this.writerQueue = this.taskRunner.newQueue();
      this.pushQueue = this.taskRunner.newQueue();
      this.settingsListenerQueue = this.taskRunner.newQueue();
      this.pushObserver = builder.getPushObserver$okhttp();
      this.flowControlListener = builder.getFlowControlListener$okhttp();
      Settings var2 = new Settings();
      int var4 = 0;
      if (builder.getClient$okhttp()) {
         var2.set(4, 16777216);
      }

      this.okHttpSettings = var2;
      this.peerSettings = DEFAULT_SETTINGS;
      this.readBytes = new WindowCounter(0);
      this.writeBytesMaximum = (long)this.peerSettings.getInitialWindowSize();
      this.socket = builder.getSocket$okhttp();
      this.writer = new Http2Writer(this.socket.getSink(), this.client);
      this.readerRunnable = new ReaderRunnable(new Http2Reader(this.socket.getSource(), this.client));
      this.currentPushRequests = (Set)(new LinkedHashSet());
      if (builder.getPingIntervalMillis$okhttp() != 0) {
         long pingIntervalNanos = TimeUnit.MILLISECONDS.toNanos((long)builder.getPingIntervalMillis$okhttp());
         this.writerQueue.schedule(this.connectionName + " ping", pingIntervalNanos, Http2Connection::_init_$lambda$0);
      }

   }

   public final boolean getClient$okhttp() {
      return this.client;
   }

   @NotNull
   public final Listener getListener$okhttp() {
      return this.listener;
   }

   @NotNull
   public final Map<Integer, Http2Stream> getStreams$okhttp() {
      return this.streams;
   }

   @NotNull
   public final String getConnectionName$okhttp() {
      return this.connectionName;
   }

   public final int getLastGoodStreamId$okhttp() {
      return this.lastGoodStreamId;
   }

   public final void setLastGoodStreamId$okhttp(int var1) {
      this.lastGoodStreamId = var1;
   }

   public final int getNextStreamId$okhttp() {
      return this.nextStreamId;
   }

   public final void setNextStreamId$okhttp(int var1) {
      this.nextStreamId = var1;
   }

   @NotNull
   public final FlowControlListener getFlowControlListener$okhttp() {
      return this.flowControlListener;
   }

   @NotNull
   public final Settings getOkHttpSettings() {
      return this.okHttpSettings;
   }

   @NotNull
   public final Settings getPeerSettings() {
      return this.peerSettings;
   }

   public final void setPeerSettings(@NotNull Settings var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.peerSettings = var1;
   }

   @NotNull
   public final WindowCounter getReadBytes() {
      return this.readBytes;
   }

   public final long getWriteBytesTotal() {
      return this.writeBytesTotal;
   }

   public final long getWriteBytesMaximum() {
      return this.writeBytesMaximum;
   }

   @NotNull
   public final BufferedSocket getSocket$okhttp() {
      return this.socket;
   }

   @NotNull
   public final Http2Writer getWriter() {
      return this.writer;
   }

   @NotNull
   public final ReaderRunnable getReaderRunnable() {
      return this.readerRunnable;
   }

   public final int openStreamCount() {
      // $FF: Couldn't be decompiled
   }

   @Nullable
   public final Http2Stream getStream(int param1) {
      // $FF: Couldn't be decompiled
   }

   @Nullable
   public final Http2Stream removeStream$okhttp(int param1) {
      // $FF: Couldn't be decompiled
   }

   public final void updateConnectionFlowControl$okhttp(long read) {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var6 = 0;
         WindowCounter.update$default(this.readBytes, read, 0L, 2, (Object)null);
         long readBytesToAcknowledge = this.readBytes.getUnacknowledged();
         if (readBytesToAcknowledge >= (long)(this.okHttpSettings.getInitialWindowSize() / 2)) {
            this.writeWindowUpdateLater$okhttp(0, readBytesToAcknowledge);
            WindowCounter.update$default(this.readBytes, 0L, readBytesToAcknowledge, 1, (Object)null);
         }

         this.flowControlListener.receivingConnectionWindowChanged(this.readBytes);
         Unit var9 = Unit.INSTANCE;
      }

   }

   @NotNull
   public final Http2Stream pushStream(int associatedStreamId, @NotNull List<Header> requestHeaders, boolean out) throws IOException {
      Intrinsics.checkNotNullParameter(requestHeaders, "requestHeaders");
      if (this.client) {
         int var4 = 0;
         String var5 = "Client cannot push requests.";
         throw new IllegalStateException(var5.toString());
      } else {
         return this.newStream(associatedStreamId, requestHeaders, out);
      }
   }

   @NotNull
   public final Http2Stream newStream(@NotNull List<Header> requestHeaders, boolean out) throws IOException {
      Intrinsics.checkNotNullParameter(requestHeaders, "requestHeaders");
      return this.newStream(0, requestHeaders, out);
   }

   private final Http2Stream newStream(int associatedStreamId, List<Header> requestHeaders, boolean out) throws IOException {
      boolean outFinished = !out;
      boolean inFinished = false;
      boolean flushHeaders = false;
      Object stream = null;
      int streamId = 0;
      Lockable $this$withLock$iv = this.writer;
      int $i$f$withLock = 0;
      Http2Stream var26;
      synchronized($this$withLock$iv) {
         int var12 = 0;
         Lockable $this$withLock$iv = this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv){}

         try {
            int var16 = 0;
            if (this.nextStreamId > 1073741823) {
               this.shutdown(ErrorCode.REFUSED_STREAM);
            }

            if (this.isShutdown) {
               throw new ConnectionShutdownException();
            }

            streamId = this.nextStreamId;
            this.nextStreamId += 2;
            var26 = new Http2Stream(streamId, this, outFinished, inFinished, (Headers)null);
            flushHeaders = !out || this.writeBytesTotal >= this.writeBytesMaximum || var26.getWriteBytesTotal() >= var26.getWriteBytesMaximum();
            if (var26.isOpen()) {
               this.streams.put(streamId, var26);
            }

            Unit var17 = Unit.INSTANCE;
         } finally {
            ;
         }

         if (associatedStreamId == 0) {
            this.writer.headers(outFinished, streamId, requestHeaders);
         } else {
            if (this.client) {
               $i$f$withLock = 0;
               String var29 = "client streams shouldn't have associated stream IDs";
               throw new IllegalArgumentException(var29.toString());
            }

            this.writer.pushPromise(associatedStreamId, streamId, requestHeaders);
         }

         Unit var19 = Unit.INSTANCE;
      }

      if (flushHeaders) {
         this.writer.flush();
      }

      return var26;
   }

   public final void writeHeaders$okhttp(int streamId, boolean outFinished, @NotNull List<Header> alternating) throws IOException {
      Intrinsics.checkNotNullParameter(alternating, "alternating");
      this.writer.headers(outFinished, streamId, alternating);
   }

   public final void writeData(int streamId, boolean outFinished, @Nullable Buffer buffer, long byteCount) throws IOException {
      if (byteCount == 0L) {
         this.writer.data(outFinished, streamId, buffer, 0);
      } else {
         long byteCount = 0L;
         byteCount = byteCount;

         while(byteCount > 0L) {
            int toWrite = 0;
            Lockable $this$withLock$iv = this;
            int $i$f$withLock = 0;
            synchronized($this$withLock$iv){}

            try {
               int var10 = 0;

               try {
                  while(this.writeBytesTotal >= this.writeBytesMaximum) {
                     if (!this.streams.containsKey(streamId)) {
                        throw new IOException("stream closed");
                     }

                     Lockable $this$wait$iv = this;
                     int $i$f$wait = 0;
                     Intrinsics.checkNotNull($this$wait$iv, "null cannot be cast to non-null type java.lang.Object");
                     ((Object)$this$wait$iv).wait();
                  }
               } catch (InterruptedException var19) {
                  Thread.currentThread().interrupt();
                  throw new InterruptedIOException();
               }

               int var21 = (int)Math.min(byteCount, this.writeBytesMaximum - this.writeBytesTotal);
               toWrite = Math.min(var21, this.writer.maxDataLength());
               this.writeBytesTotal += (long)toWrite;
               Unit var13 = Unit.INSTANCE;
            } finally {
               ;
            }

            byteCount -= (long)toWrite;
            this.writer.data(outFinished && byteCount == 0L, streamId, buffer, toWrite);
         }

      }
   }

   public final void writeSynResetLater$okhttp(int streamId, @NotNull ErrorCode errorCode) {
      Intrinsics.checkNotNullParameter(errorCode, "errorCode");
      TaskQueue.execute$default(this.writerQueue, this.connectionName + '[' + streamId + "] writeSynReset", 0L, false, Http2Connection::writeSynResetLater$lambda$0, 6, (Object)null);
   }

   public final void writeSynReset$okhttp(int streamId, @NotNull ErrorCode statusCode) throws IOException {
      Intrinsics.checkNotNullParameter(statusCode, "statusCode");
      this.writer.rstStream(streamId, statusCode);
   }

   public final void writeWindowUpdateLater$okhttp(int streamId, long unacknowledgedBytesRead) {
      TaskQueue.execute$default(this.writerQueue, this.connectionName + '[' + streamId + "] windowUpdate", 0L, false, Http2Connection::writeWindowUpdateLater$lambda$0, 6, (Object)null);
   }

   public final void writePing(boolean reply, int payload1, int payload2) {
      try {
         this.writer.ping(reply, payload1, payload2);
      } catch (IOException e) {
         this.failConnection(e);
      }

   }

   public final void writePingAndAwaitPong() throws InterruptedException {
      this.writePing();
      this.awaitPong();
   }

   public final void writePing() throws InterruptedException {
      // $FF: Couldn't be decompiled
   }

   public final void awaitPong() throws InterruptedException {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var4 = 0;

         while(this.awaitPongsReceived < this.awaitPingsSent) {
            Lockable $this$wait$iv = this;
            int $i$f$wait = 0;
            Intrinsics.checkNotNull($this$wait$iv, "null cannot be cast to non-null type java.lang.Object");
            ((Object)$this$wait$iv).wait();
         }

         Unit var8 = Unit.INSTANCE;
      }

   }

   public final void flush() throws IOException {
      this.writer.flush();
   }

   public final void shutdown(@NotNull ErrorCode statusCode) throws IOException {
      Intrinsics.checkNotNullParameter(statusCode, "statusCode");
      Lockable $this$withLock$iv = this.writer;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var5 = 0;
         int lastGoodStreamId = 0;
         Lockable $this$withLock$iv = this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv){}

         try {
            int var10 = 0;
            if (this.isShutdown) {
               return;
            }

            this.isShutdown = true;
            lastGoodStreamId = this.lastGoodStreamId;
            Unit var11 = Unit.INSTANCE;
         } finally {
            ;
         }

         this.writer.goAway(lastGoodStreamId, statusCode, _UtilCommonKt.EMPTY_BYTE_ARRAY);
         Unit var12 = Unit.INSTANCE;
      }

   }

   public void close() {
      this.close$okhttp(ErrorCode.NO_ERROR, ErrorCode.CANCEL, (IOException)null);
   }

   public final void close$okhttp(@NotNull ErrorCode connectionCode, @NotNull ErrorCode streamCode, @Nullable IOException cause) {
      Intrinsics.checkNotNullParameter(connectionCode, "connectionCode");
      Intrinsics.checkNotNullParameter(streamCode, "streamCode");
      Lockable $this$assertLockNotHeld$iv = this;
      int $i$f$assertLockNotHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
      } else {
         int $i$f$ignoreIoExceptions = 0;

         try {
            $i$f$assertLockNotHeld = 0;
            this.shutdown(connectionCode);
         } catch (IOException var19) {
         }

         Object streamsToClose = null;
         Lockable $this$withLock$iv = this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv) {
            int var8 = 0;
            if (!this.streams.isEmpty()) {
               Collection $this$toTypedArray$iv = this.streams.values();
               int $i$f$toTypedArray = 0;
               streamsToClose = $this$toTypedArray$iv.toArray(new Http2Stream[0]);
               this.streams.clear();
            }

            Unit var12 = Unit.INSTANCE;
         }

         Http2Stream[] var10000 = (Http2Stream[])streamsToClose;
         if ((Http2Stream[])streamsToClose != null) {
            Object[] $this$forEach$iv = var10000;
            int $i$f$forEach = 0;
            int var30 = 0;

            for(int thisCollection$iv = $this$forEach$iv.length; var30 < thisCollection$iv; ++var30) {
               Object element$iv = $this$forEach$iv[var30];
               Http2Stream stream = (Http2Stream)element$iv;
               int var33 = 0;
               int $i$f$ignoreIoExceptions = 0;

               try {
                  int var14 = 0;
                  stream.close(streamCode, cause);
               } catch (IOException var18) {
               }
            }
         }

         int $i$f$ignoreIoExceptions = 0;

         try {
            $i$f$withLock = 0;
            this.writer.close();
         } catch (IOException var17) {
         }

         $i$f$ignoreIoExceptions = 0;

         try {
            $i$f$withLock = 0;
            this.socket.cancel();
         } catch (IOException var16) {
         }

         this.writerQueue.shutdown();
         this.pushQueue.shutdown();
         this.settingsListenerQueue.shutdown();
      }
   }

   private final void failConnection(IOException e) {
      this.close$okhttp(ErrorCode.PROTOCOL_ERROR, ErrorCode.PROTOCOL_ERROR, e);
   }

   @JvmOverloads
   public final void start(boolean sendConnectionPreface) throws IOException {
      if (sendConnectionPreface) {
         this.writer.connectionPreface();
         this.writer.settings(this.okHttpSettings);
         int windowSize = this.okHttpSettings.getInitialWindowSize();
         if (windowSize != 65535) {
            this.writer.windowUpdate(0, (long)(windowSize - '\uffff'));
         }
      }

      TaskQueue.execute$default(this.taskRunner.newQueue(), this.connectionName, 0L, false, this.readerRunnable, 6, (Object)null);
   }

   // $FF: synthetic method
   public static void start$default(Http2Connection var0, boolean var1, int var2, Object var3) throws IOException {
      if ((var2 & 1) != 0) {
         var1 = true;
      }

      var0.start(var1);
   }

   public final void setSettings(@NotNull Settings settings) throws IOException {
      Intrinsics.checkNotNullParameter(settings, "settings");
      Lockable $this$withLock$iv = this.writer;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var5 = 0;
         Lockable $this$withLock$iv = this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv){}

         try {
            int var9 = 0;
            if (this.isShutdown) {
               throw new ConnectionShutdownException();
            }

            this.okHttpSettings.merge(settings);
            Unit var10 = Unit.INSTANCE;
         } finally {
            ;
         }

         this.writer.settings(settings);
         Unit var11 = Unit.INSTANCE;
      }

   }

   public final boolean isHealthy(long nowNs) {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      boolean var10;
      try {
         int var6 = 0;
         if (!this.isShutdown) {
            if (this.degradedPongsReceived < this.degradedPingsSent && nowNs >= this.degradedPongDeadlineNs) {
               boolean var9 = false;
               return var9;
            }

            boolean var8 = true;
            return var8;
         }

         var10 = false;
      } finally {
         ;
      }

      return var10;
   }

   public final void sendDegradedPingLater$okhttp() {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      label39: {
         try {
            int var4 = 0;
            if (this.degradedPongsReceived >= this.degradedPingsSent) {
               int var5 = this.degradedPingsSent++;
               this.degradedPongDeadlineNs = System.nanoTime() + (long)1000000000;
               Unit var10 = Unit.INSTANCE;
               break label39;
            }
         } finally {
            ;
         }

         return;
      }

      TaskQueue.execute$default(this.writerQueue, this.connectionName + " ping", 0L, false, Http2Connection::sendDegradedPingLater$lambda$1, 6, (Object)null);
   }

   public final boolean pushedStream$okhttp(int streamId) {
      return streamId != 0 && (streamId & 1) == 0;
   }

   public final void pushRequestLater$okhttp(int streamId, @NotNull List<Header> requestHeaders) {
      Intrinsics.checkNotNullParameter(requestHeaders, "requestHeaders");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      label44: {
         try {
            int var6 = 0;
            if (!this.currentPushRequests.contains(streamId)) {
               boolean var7 = this.currentPushRequests.add(streamId);
               break label44;
            }

            this.writeSynResetLater$okhttp(streamId, ErrorCode.PROTOCOL_ERROR);
         } finally {
            ;
         }

         return;
      }

      TaskQueue.execute$default(this.pushQueue, this.connectionName + '[' + streamId + "] onRequest", 0L, false, Http2Connection::pushRequestLater$lambda$1, 6, (Object)null);
   }

   public final void pushHeadersLater$okhttp(int streamId, @NotNull List<Header> requestHeaders, boolean inFinished) {
      Intrinsics.checkNotNullParameter(requestHeaders, "requestHeaders");
      TaskQueue.execute$default(this.pushQueue, this.connectionName + '[' + streamId + "] onHeaders", 0L, false, Http2Connection::pushHeadersLater$lambda$0, 6, (Object)null);
   }

   public final void pushDataLater$okhttp(int streamId, @NotNull BufferedSource source, int byteCount, boolean inFinished) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      Buffer buffer = new Buffer();
      source.require((long)byteCount);
      source.read(buffer, (long)byteCount);
      TaskQueue.execute$default(this.pushQueue, this.connectionName + '[' + streamId + "] onData", 0L, false, Http2Connection::pushDataLater$lambda$0, 6, (Object)null);
   }

   public final void pushResetLater$okhttp(int streamId, @NotNull ErrorCode errorCode) {
      Intrinsics.checkNotNullParameter(errorCode, "errorCode");
      TaskQueue.execute$default(this.pushQueue, this.connectionName + '[' + streamId + "] onReset", 0L, false, Http2Connection::pushResetLater$lambda$0, 6, (Object)null);
   }

   @JvmOverloads
   public final void start() throws IOException {
      start$default(this, false, 1, (Object)null);
   }

   private static final long _init_$lambda$0(Http2Connection param0, long param1) {
      // $FF: Couldn't be decompiled
   }

   private static final Unit writeSynResetLater$lambda$0(Http2Connection this$0, int $streamId, ErrorCode $errorCode) {
      try {
         this$0.writeSynReset$okhttp($streamId, $errorCode);
      } catch (IOException e) {
         this$0.failConnection(e);
      }

      return Unit.INSTANCE;
   }

   private static final Unit writeWindowUpdateLater$lambda$0(Http2Connection this$0, int $streamId, long $unacknowledgedBytesRead) {
      try {
         this$0.writer.windowUpdate($streamId, $unacknowledgedBytesRead);
      } catch (IOException e) {
         this$0.failConnection(e);
      }

      return Unit.INSTANCE;
   }

   private static final Unit sendDegradedPingLater$lambda$1(Http2Connection this$0) {
      this$0.writePing(false, 2, 0);
      return Unit.INSTANCE;
   }

   private static final Unit pushRequestLater$lambda$1(Http2Connection this$0, int $streamId, List $requestHeaders) {
      boolean cancel = this$0.pushObserver.onRequest($streamId, $requestHeaders);
      int $i$f$ignoreIoExceptions = 0;

      try {
         int var5 = 0;
         if (cancel) {
            this$0.writer.rstStream($streamId, ErrorCode.CANCEL);
            Lockable $this$withLock$iv = this$0;
            int $i$f$withLock = 0;
            synchronized($this$withLock$iv) {
               int var9 = 0;
               this$0.currentPushRequests.remove($streamId);
               Unit var10 = Unit.INSTANCE;
            }
         }
      } catch (IOException var13) {
      }

      return Unit.INSTANCE;
   }

   private static final Unit pushHeadersLater$lambda$0(Http2Connection this$0, int $streamId, List $requestHeaders, boolean $inFinished) {
      boolean cancel = this$0.pushObserver.onHeaders($streamId, $requestHeaders, $inFinished);
      int $i$f$ignoreIoExceptions = 0;

      try {
         int var6 = 0;
         if (cancel) {
            this$0.writer.rstStream($streamId, ErrorCode.CANCEL);
         }

         if (cancel || $inFinished) {
            Lockable $this$withLock$iv = this$0;
            int $i$f$withLock = 0;
            synchronized($this$withLock$iv) {
               int var10 = 0;
               this$0.currentPushRequests.remove($streamId);
               Unit var11 = Unit.INSTANCE;
            }
         }
      } catch (IOException var14) {
      }

      return Unit.INSTANCE;
   }

   private static final Unit pushDataLater$lambda$0(Http2Connection this$0, int $streamId, Buffer $buffer, int $byteCount, boolean $inFinished) {
      int $i$f$ignoreIoExceptions = 0;

      try {
         int var6 = 0;
         boolean cancel = this$0.pushObserver.onData($streamId, $buffer, $byteCount, $inFinished);
         if (cancel) {
            this$0.writer.rstStream($streamId, ErrorCode.CANCEL);
         }

         if (cancel || $inFinished) {
            Lockable $this$withLock$iv = this$0;
            int $i$f$withLock = 0;
            synchronized($this$withLock$iv) {
               int var11 = 0;
               this$0.currentPushRequests.remove($streamId);
               Unit var12 = Unit.INSTANCE;
            }
         }
      } catch (IOException var15) {
      }

      return Unit.INSTANCE;
   }

   private static final Unit pushResetLater$lambda$0(Http2Connection this$0, int $streamId, ErrorCode $errorCode) {
      this$0.pushObserver.onReset($streamId, $errorCode);
      Lockable $this$withLock$iv = this$0;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var6 = 0;
         this$0.currentPushRequests.remove($streamId);
         Unit var7 = Unit.INSTANCE;
      }

      return Unit.INSTANCE;
   }

   static {
      Settings $this$DEFAULT_SETTINGS_u24lambda_u240 = new Settings();
      int var2 = 0;
      $this$DEFAULT_SETTINGS_u24lambda_u240.set(4, 65535);
      $this$DEFAULT_SETTINGS_u24lambda_u240.set(5, 16384);
      DEFAULT_SETTINGS = $this$DEFAULT_SETTINGS_u24lambda_u240;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0016\u0010\u000e\u001a\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u00102\u001a\u00020\u0015J\u000e\u0010\u001a\u001a\u00020\u00002\u0006\u0010\u001a\u001a\u00020\u001bJ\u000e\u0010 \u001a\u00020\u00002\u0006\u0010 \u001a\u00020!J\u000e\u0010&\u001a\u00020\u00002\u0006\u0010&\u001a\u00020'J\u000e\u0010,\u001a\u00020\u00002\u0006\u0010,\u001a\u00020-J\u0006\u00103\u001a\u000204R\u001a\u0010\u0002\u001a\u00020\u0003X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u0014\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\u00020\u000fX\u0080.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0014\u001a\u00020\u0015X\u0080.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001a\u001a\u00020\u001bX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001a\u0010 \u001a\u00020!X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u001a\u0010&\u001a\u00020'X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b(\u0010)\"\u0004\b*\u0010+R\u001a\u0010,\u001a\u00020-X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b.\u0010/\"\u0004\b0\u00101¨\u00065"},
      d2 = {"Lokhttp3/internal/http2/Http2Connection$Builder;", "", "client", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "<init>", "(ZLokhttp3/internal/concurrent/TaskRunner;)V", "getClient$okhttp", "()Z", "setClient$okhttp", "(Z)V", "getTaskRunner$okhttp", "()Lokhttp3/internal/concurrent/TaskRunner;", "socket", "Lokhttp3/internal/connection/BufferedSocket;", "getSocket$okhttp", "()Lokhttp3/internal/connection/BufferedSocket;", "setSocket$okhttp", "(Lokhttp3/internal/connection/BufferedSocket;)V", "connectionName", "", "getConnectionName$okhttp", "()Ljava/lang/String;", "setConnectionName$okhttp", "(Ljava/lang/String;)V", "listener", "Lokhttp3/internal/http2/Http2Connection$Listener;", "getListener$okhttp", "()Lokhttp3/internal/http2/Http2Connection$Listener;", "setListener$okhttp", "(Lokhttp3/internal/http2/Http2Connection$Listener;)V", "pushObserver", "Lokhttp3/internal/http2/PushObserver;", "getPushObserver$okhttp", "()Lokhttp3/internal/http2/PushObserver;", "setPushObserver$okhttp", "(Lokhttp3/internal/http2/PushObserver;)V", "pingIntervalMillis", "", "getPingIntervalMillis$okhttp", "()I", "setPingIntervalMillis$okhttp", "(I)V", "flowControlListener", "Lokhttp3/internal/http2/FlowControlListener;", "getFlowControlListener$okhttp", "()Lokhttp3/internal/http2/FlowControlListener;", "setFlowControlListener$okhttp", "(Lokhttp3/internal/http2/FlowControlListener;)V", "peerName", "build", "Lokhttp3/internal/http2/Http2Connection;", "okhttp"}
   )
   public static final class Builder {
      private boolean client;
      @NotNull
      private final TaskRunner taskRunner;
      public BufferedSocket socket;
      public String connectionName;
      @NotNull
      private Listener listener;
      @NotNull
      private PushObserver pushObserver;
      private int pingIntervalMillis;
      @NotNull
      private FlowControlListener flowControlListener;

      public Builder(boolean client, @NotNull TaskRunner taskRunner) {
         Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
         super();
         this.client = client;
         this.taskRunner = taskRunner;
         this.listener = Http2Connection.Listener.REFUSE_INCOMING_STREAMS;
         this.pushObserver = PushObserver.CANCEL;
         this.flowControlListener = FlowControlListener.None.INSTANCE;
      }

      public final boolean getClient$okhttp() {
         return this.client;
      }

      public final void setClient$okhttp(boolean var1) {
         this.client = var1;
      }

      @NotNull
      public final TaskRunner getTaskRunner$okhttp() {
         return this.taskRunner;
      }

      @NotNull
      public final BufferedSocket getSocket$okhttp() {
         BufferedSocket var10000 = this.socket;
         if (var10000 != null) {
            return var10000;
         } else {
            Intrinsics.throwUninitializedPropertyAccessException("socket");
            return null;
         }
      }

      public final void setSocket$okhttp(@NotNull BufferedSocket var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.socket = var1;
      }

      @NotNull
      public final String getConnectionName$okhttp() {
         String var10000 = this.connectionName;
         if (var10000 != null) {
            return var10000;
         } else {
            Intrinsics.throwUninitializedPropertyAccessException("connectionName");
            return null;
         }
      }

      public final void setConnectionName$okhttp(@NotNull String var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.connectionName = var1;
      }

      @NotNull
      public final Listener getListener$okhttp() {
         return this.listener;
      }

      public final void setListener$okhttp(@NotNull Listener var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.listener = var1;
      }

      @NotNull
      public final PushObserver getPushObserver$okhttp() {
         return this.pushObserver;
      }

      public final void setPushObserver$okhttp(@NotNull PushObserver var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.pushObserver = var1;
      }

      public final int getPingIntervalMillis$okhttp() {
         return this.pingIntervalMillis;
      }

      public final void setPingIntervalMillis$okhttp(int var1) {
         this.pingIntervalMillis = var1;
      }

      @NotNull
      public final FlowControlListener getFlowControlListener$okhttp() {
         return this.flowControlListener;
      }

      public final void setFlowControlListener$okhttp(@NotNull FlowControlListener var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.flowControlListener = var1;
      }

      @NotNull
      public final Builder socket(@NotNull BufferedSocket socket, @NotNull String peerName) throws IOException {
         Intrinsics.checkNotNullParameter(socket, "socket");
         Intrinsics.checkNotNullParameter(peerName, "peerName");
         Builder $this$socket_u24lambda_u240 = this;
         int var5 = 0;
         $this$socket_u24lambda_u240.setSocket$okhttp(socket);
         $this$socket_u24lambda_u240.setConnectionName$okhttp($this$socket_u24lambda_u240.client ? _UtilJvmKt.okHttpName + ' ' + peerName : "MockWebServer " + peerName);
         return this;
      }

      @NotNull
      public final Builder listener(@NotNull Listener listener) {
         Intrinsics.checkNotNullParameter(listener, "listener");
         Builder $this$listener_u24lambda_u240 = this;
         int var4 = 0;
         $this$listener_u24lambda_u240.listener = listener;
         return this;
      }

      @NotNull
      public final Builder pushObserver(@NotNull PushObserver pushObserver) {
         Intrinsics.checkNotNullParameter(pushObserver, "pushObserver");
         Builder $this$pushObserver_u24lambda_u240 = this;
         int var4 = 0;
         $this$pushObserver_u24lambda_u240.pushObserver = pushObserver;
         return this;
      }

      @NotNull
      public final Builder pingIntervalMillis(int pingIntervalMillis) {
         Builder $this$pingIntervalMillis_u24lambda_u240 = this;
         int var4 = 0;
         $this$pingIntervalMillis_u24lambda_u240.pingIntervalMillis = pingIntervalMillis;
         return this;
      }

      @NotNull
      public final Builder flowControlListener(@NotNull FlowControlListener flowControlListener) {
         Intrinsics.checkNotNullParameter(flowControlListener, "flowControlListener");
         Builder $this$flowControlListener_u24lambda_u240 = this;
         int var4 = 0;
         $this$flowControlListener_u24lambda_u240.flowControlListener = flowControlListener;
         return this;
      }

      @NotNull
      public final Http2Connection build() {
         return new Http2Connection(this);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0086\u0004\u0018\u00002\u00020\u00012\b\u0012\u0004\u0012\u00020\u00030\u0002B\u0011\b\u0000\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\n\u001a\u00020\u0003H\u0096\u0002J(\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000fH\u0016J.\u0010\u0013\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\u000f2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016H\u0016J\u0018\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0018\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\u001dH\u0016J\u0016\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\u001dJ\b\u0010\u001f\u001a\u00020\u0003H\u0016J \u0010 \u001a\u00020\u00032\u0006\u0010!\u001a\u00020\r2\u0006\u0010\"\u001a\u00020\u000f2\u0006\u0010#\u001a\u00020\u000fH\u0016J \u0010$\u001a\u00020\u00032\u0006\u0010%\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010&\u001a\u00020'H\u0016J\u0018\u0010(\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010)\u001a\u00020*H\u0016J(\u0010+\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010,\u001a\u00020\u000f2\u0006\u0010-\u001a\u00020\u000f2\u0006\u0010.\u001a\u00020\rH\u0016J&\u0010/\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u00100\u001a\u00020\u000f2\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016H\u0016J8\u00102\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u00103\u001a\u0002042\u0006\u00105\u001a\u00020'2\u0006\u00106\u001a\u0002042\u0006\u00107\u001a\u00020\u000f2\u0006\u00108\u001a\u00020*H\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u00069"},
      d2 = {"Lokhttp3/internal/http2/Http2Connection$ReaderRunnable;", "Lokhttp3/internal/http2/Http2Reader$Handler;", "Lkotlin/Function0;", "", "reader", "Lokhttp3/internal/http2/Http2Reader;", "<init>", "(Lokhttp3/internal/http2/Http2Connection;Lokhttp3/internal/http2/Http2Reader;)V", "getReader$okhttp", "()Lokhttp3/internal/http2/Http2Reader;", "invoke", "data", "inFinished", "", "streamId", "", "source", "Lokio/BufferedSource;", "length", "headers", "associatedStreamId", "headerBlock", "", "Lokhttp3/internal/http2/Header;", "rstStream", "errorCode", "Lokhttp3/internal/http2/ErrorCode;", "settings", "clearPrevious", "Lokhttp3/internal/http2/Settings;", "applyAndAckSettings", "ackSettings", "ping", "ack", "payload1", "payload2", "goAway", "lastGoodStreamId", "debugData", "Lokio/ByteString;", "windowUpdate", "windowSizeIncrement", "", "priority", "streamDependency", "weight", "exclusive", "pushPromise", "promisedStreamId", "requestHeaders", "alternateService", "origin", "", "protocol", "host", "port", "maxAge", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHttp2Connection.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection$ReaderRunnable\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 -UtilCommon.kt\nokhttp3/internal/_UtilCommonKt\n*L\n1#1,1042:1\n63#2:1043\n63#2:1044\n63#2:1045\n63#2:1048\n63#2:1049\n38#2:1050\n63#2:1051\n63#2:1054\n38#2:1055\n63#2:1056\n37#3,2:1046\n37#3,2:1052\n228#4,5:1057\n*S KotlinDebug\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection$ReaderRunnable\n*L\n678#1:1043\n757#1:1044\n758#1:1045\n792#1:1048\n809#1:1049\n819#1:1050\n845#1:1051\n864#1:1054\n866#1:1055\n871#1:1056\n775#1:1046,2\n846#1:1052,2\n703#1:1057,5\n*E\n"})
   public final class ReaderRunnable implements Http2Reader.Handler, Function0<Unit> {
      @NotNull
      private final Http2Reader reader;

      public ReaderRunnable(@NotNull Http2Reader reader) {
         Intrinsics.checkNotNullParameter(reader, "reader");
         super();
         this.reader = reader;
      }

      @NotNull
      public final Http2Reader getReader$okhttp() {
         return this.reader;
      }

      public void invoke() {
         ErrorCode connectionErrorCode = ErrorCode.INTERNAL_ERROR;
         ErrorCode streamErrorCode = ErrorCode.INTERNAL_ERROR;
         IOException errorException = null;

         try {
            this.reader.readConnectionPreface(this);

            while(this.reader.nextFrame(false, this)) {
            }

            connectionErrorCode = ErrorCode.NO_ERROR;
            streamErrorCode = ErrorCode.CANCEL;
         } catch (IOException e) {
            errorException = e;
            connectionErrorCode = ErrorCode.PROTOCOL_ERROR;
            streamErrorCode = ErrorCode.PROTOCOL_ERROR;
         } finally {
            Http2Connection.this.close$okhttp(connectionErrorCode, streamErrorCode, errorException);
            _UtilCommonKt.closeQuietly(this.reader);
         }

      }

      public void data(boolean inFinished, int streamId, @NotNull BufferedSource source, int length) throws IOException {
         Intrinsics.checkNotNullParameter(source, "source");
         if (Http2Connection.this.pushedStream$okhttp(streamId)) {
            Http2Connection.this.pushDataLater$okhttp(streamId, source, length, inFinished);
         } else {
            Http2Stream dataStream = Http2Connection.this.getStream(streamId);
            if (dataStream == null) {
               Http2Connection.this.writeSynResetLater$okhttp(streamId, ErrorCode.PROTOCOL_ERROR);
               Http2Connection.this.updateConnectionFlowControl$okhttp((long)length);
               source.skip((long)length);
            } else {
               dataStream.receiveData(source, length);
               if (inFinished) {
                  dataStream.receiveHeaders(Headers.EMPTY, true);
               }

            }
         }
      }

      public void headers(boolean inFinished, int streamId, int associatedStreamId, @NotNull List<Header> headerBlock) {
         Intrinsics.checkNotNullParameter(headerBlock, "headerBlock");
         if (Http2Connection.this.pushedStream$okhttp(streamId)) {
            Http2Connection.this.pushHeadersLater$okhttp(streamId, headerBlock, inFinished);
         } else {
            Object stream = null;
            Lockable $this$withLock$iv = (Lockable)Http2Connection.this;
            Http2Connection var7 = Http2Connection.this;
            int $i$f$withLock = 0;
            synchronized($this$withLock$iv){}

            label107: {
               try {
                  int var10 = 0;
                  var17 = var7.getStream(streamId);
                  if (var17 != null) {
                     Unit var13 = Unit.INSTANCE;
                     break label107;
                  }

                  if (!var7.isShutdown) {
                     if (streamId <= var7.getLastGoodStreamId$okhttp()) {
                        return;
                     }

                     if (streamId % 2 == var7.getNextStreamId$okhttp() % 2) {
                        return;
                     }

                     Headers headers = _UtilJvmKt.toHeaders(headerBlock);
                     Http2Stream newStream = new Http2Stream(streamId, var7, false, inFinished, headers);
                     var7.setLastGoodStreamId$okhttp(streamId);
                     var7.getStreams$okhttp().put(streamId, newStream);
                     TaskQueue.execute$default(var7.taskRunner.newQueue(), var7.getConnectionName$okhttp() + '[' + streamId + "] onStream", 0L, false, ReaderRunnable::headers$lambda$0$0, 6, (Object)null);
                     return;
                  }
               } finally {
                  ;
               }

               return;
            }

            var17.receiveHeaders(_UtilJvmKt.toHeaders(headerBlock), inFinished);
         }
      }

      public void rstStream(int streamId, @NotNull ErrorCode errorCode) {
         Intrinsics.checkNotNullParameter(errorCode, "errorCode");
         if (Http2Connection.this.pushedStream$okhttp(streamId)) {
            Http2Connection.this.pushResetLater$okhttp(streamId, errorCode);
         } else {
            Http2Stream rstStream = Http2Connection.this.removeStream$okhttp(streamId);
            if (rstStream != null) {
               rstStream.receiveRstStream(errorCode);
            }

         }
      }

      public void settings(boolean clearPrevious, @NotNull Settings settings) {
         Intrinsics.checkNotNullParameter(settings, "settings");
         TaskQueue.execute$default(Http2Connection.this.writerQueue, Http2Connection.this.getConnectionName$okhttp() + " applyAndAckSettings", 0L, false, ReaderRunnable::settings$lambda$0, 6, (Object)null);
      }

      public final void applyAndAckSettings(boolean clearPrevious, @NotNull Settings settings) {
         Intrinsics.checkNotNullParameter(settings, "settings");
         long delta = 0L;
         Object streamsToNotify = null;
         Ref.ObjectRef newPeerSettings = new Ref.ObjectRef();
         Http2Stream[] $this$withLock$iv = Http2Connection.this.getWriter();
         Http2Connection var6 = Http2Connection.this;
         int $i$f$withLock = 0;
         Http2Stream[] var33;
         synchronized($this$withLock$iv) {
            int var9 = 0;
            Lockable $this$withLock$iv = var6;
            int $i$f$withLock = 0;
            synchronized($this$withLock$iv) {
               int var13 = 0;
               Settings previousPeerSettings = var6.getPeerSettings();
               Ref.ObjectRef var10000 = newPeerSettings;
               Settings var10001;
               if (clearPrevious) {
                  var10001 = settings;
               } else {
                  Settings var15 = new Settings();
                  int thisCollection$iv = 0;
                  var15.merge(previousPeerSettings);
                  var15.merge(settings);
                  var10000 = newPeerSettings;
                  var10001 = var15;
               }

               var10000.element = (T)var10001;
               long peerInitialWindowSize = (long)((Settings)newPeerSettings.element).getInitialWindowSize();
               delta = peerInitialWindowSize - (long)previousPeerSettings.getInitialWindowSize();
               Http2Stream[] var42;
               if (delta != 0L && !var6.getStreams$okhttp().isEmpty()) {
                  Collection $this$toTypedArray$iv = var6.getStreams$okhttp().values();
                  int $i$f$toTypedArray = 0;
                  var42 = (Http2Stream[])$this$toTypedArray$iv.toArray(new Http2Stream[0]);
               } else {
                  var42 = null;
               }

               var33 = var42;
               var6.setPeerSettings((Settings)newPeerSettings.element);
               TaskQueue.execute$default(var6.settingsListenerQueue, var6.getConnectionName$okhttp() + " onSettings", 0L, false, ReaderRunnable::applyAndAckSettings$lambda$0$0$1, 6, (Object)null);
               Unit var23 = Unit.INSTANCE;
            }

            try {
               var6.getWriter().applyAndAckSettings((Settings)newPeerSettings.element);
            } catch (IOException e) {
               var6.failConnection(e);
            }

            Unit var25 = Unit.INSTANCE;
         }

         if (var33 != null) {
            $this$withLock$iv = var33;
            int var35 = 0;

            for(int var36 = var33.length; var35 < var36; ++var35) {
               Http2Stream stream = $this$withLock$iv[var35];
               Lockable $this$withLock$iv = stream;
               int $i$f$withLock = 0;
               synchronized($this$withLock$iv) {
                  int var12 = 0;
                  stream.addBytesToWriteWindow(delta);
                  Unit var39 = Unit.INSTANCE;
               }
            }
         }

      }

      public void ackSettings() {
      }

      public void ping(boolean ack, int payload1, int payload2) {
         if (ack) {
            Lockable $this$withLock$iv = (Lockable)Http2Connection.this;
            Http2Connection var5 = Http2Connection.this;
            int $i$f$withLock = 0;
            synchronized($this$withLock$iv) {
               int var8 = 0;
               switch (payload1) {
                  case 1:
                     long var16 = var5.intervalPongsReceived;
                     var5.intervalPongsReceived = var16 + 1L;
                     Long var18 = var16;
                     break;
                  case 2:
                     long var15 = var5.degradedPongsReceived;
                     var5.degradedPongsReceived = var15 + 1L;
                     Long var17 = var15;
                     break;
                  case 3:
                     long var9 = var5.awaitPongsReceived;
                     var5.awaitPongsReceived = var9 + 1L;
                     Lockable $this$notifyAll$iv = var5;
                     int $i$f$notifyAll = 0;
                     Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
                     ((Object)$this$notifyAll$iv).notifyAll();
                     Unit var10000 = Unit.INSTANCE;
                     break;
                  default:
                     Unit var19 = Unit.INSTANCE;
               }
            }
         } else {
            TaskQueue.execute$default(Http2Connection.this.writerQueue, Http2Connection.this.getConnectionName$okhttp() + " ping", 0L, false, ReaderRunnable::ping$lambda$1, 6, (Object)null);
         }

      }

      public void goAway(int lastGoodStreamId, @NotNull ErrorCode errorCode, @NotNull ByteString debugData) {
         Intrinsics.checkNotNullParameter(errorCode, "errorCode");
         Intrinsics.checkNotNullParameter(debugData, "debugData");
         if (debugData.size() > 0) {
         }

         Object streamsCopy = null;
         Http2Stream[] $this$withLock$iv = (Lockable)Http2Connection.this;
         Http2Connection var6 = Http2Connection.this;
         int $i$f$withLock = 0;
         Object[] var15;
         synchronized($this$withLock$iv) {
            int var9 = 0;
            Collection $this$toTypedArray$iv = var6.getStreams$okhttp().values();
            int $i$f$toTypedArray = 0;
            var15 = $this$toTypedArray$iv.toArray(new Http2Stream[0]);
            var6.isShutdown = true;
            Unit var13 = Unit.INSTANCE;
         }

         $this$withLock$iv = (Http2Stream[])var15;
         int var17 = 0;

         for(int var18 = $this$withLock$iv.length; var17 < var18; ++var17) {
            Http2Stream http2Stream = $this$withLock$iv[var17];
            if (http2Stream.getId() > lastGoodStreamId && http2Stream.isLocallyInitiated()) {
               http2Stream.receiveRstStream(ErrorCode.REFUSED_STREAM);
               Http2Connection.this.removeStream$okhttp(http2Stream.getId());
            }
         }

      }

      public void windowUpdate(int streamId, long windowSizeIncrement) {
         if (streamId == 0) {
            Lockable $this$withLock$iv = (Lockable)Http2Connection.this;
            Http2Connection var5 = Http2Connection.this;
            int $i$f$withLock = 0;
            synchronized($this$withLock$iv) {
               int var8 = 0;
               var5.writeBytesMaximum = var5.getWriteBytesMaximum() + windowSizeIncrement;
               Lockable $this$notifyAll$iv = var5;
               int $i$f$notifyAll = 0;
               Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
               ((Object)$this$notifyAll$iv).notifyAll();
               Unit var11 = Unit.INSTANCE;
            }
         } else {
            Http2Stream stream = Http2Connection.this.getStream(streamId);
            if (stream != null) {
               Lockable $this$withLock$iv = stream;
               int $i$f$withLock = 0;
               synchronized($this$withLock$iv) {
                  int var18 = 0;
                  stream.addBytesToWriteWindow(windowSizeIncrement);
                  Unit var19 = Unit.INSTANCE;
               }
            }
         }

      }

      public void priority(int streamId, int streamDependency, int weight, boolean exclusive) {
      }

      public void pushPromise(int streamId, int promisedStreamId, @NotNull List<Header> requestHeaders) {
         Intrinsics.checkNotNullParameter(requestHeaders, "requestHeaders");
         Http2Connection.this.pushRequestLater$okhttp(promisedStreamId, requestHeaders);
      }

      public void alternateService(int streamId, @NotNull String origin, @NotNull ByteString protocol, @NotNull String host, int port, long maxAge) {
         Intrinsics.checkNotNullParameter(origin, "origin");
         Intrinsics.checkNotNullParameter(protocol, "protocol");
         Intrinsics.checkNotNullParameter(host, "host");
      }

      private static final Unit headers$lambda$0$0(Http2Connection this$0, Http2Stream $newStream) {
         try {
            this$0.getListener$okhttp().onStream($newStream);
         } catch (IOException var7) {
            IOException e = var7;
            Platform.Companion.get().log("Http2Connection.Listener failure for " + this$0.getConnectionName$okhttp(), 4, (Throwable)var7);
            int $i$f$ignoreIoExceptions = 0;

            try {
               int var4 = 0;
               $newStream.close(ErrorCode.PROTOCOL_ERROR, e);
            } catch (IOException var6) {
            }
         }

         return Unit.INSTANCE;
      }

      private static final Unit settings$lambda$0(ReaderRunnable this$0, boolean $clearPrevious, Settings $settings) {
         this$0.applyAndAckSettings($clearPrevious, $settings);
         return Unit.INSTANCE;
      }

      private static final Unit applyAndAckSettings$lambda$0$0$1(Http2Connection this$0, Ref.ObjectRef $newPeerSettings) {
         this$0.getListener$okhttp().onSettings(this$0, (Settings)$newPeerSettings.element);
         return Unit.INSTANCE;
      }

      private static final Unit ping$lambda$1(Http2Connection this$0, int $payload1, int $payload2) {
         this$0.writePing(true, $payload1, $payload2);
         return Unit.INSTANCE;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0018\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016¨\u0006\u000e"},
      d2 = {"Lokhttp3/internal/http2/Http2Connection$Listener;", "", "<init>", "()V", "onStream", "", "stream", "Lokhttp3/internal/http2/Http2Stream;", "onSettings", "connection", "Lokhttp3/internal/http2/Http2Connection;", "settings", "Lokhttp3/internal/http2/Settings;", "Companion", "okhttp"}
   )
   public abstract static class Listener {
      @NotNull
      public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
      @JvmField
      @NotNull
      public static final Listener REFUSE_INCOMING_STREAMS = new Listener() {
         public void onStream(Http2Stream stream) throws IOException {
            Intrinsics.checkNotNullParameter(stream, "stream");
            stream.close(ErrorCode.REFUSED_STREAM, (IOException)null);
         }
      };

      public abstract void onStream(@NotNull Http2Stream var1) throws IOException;

      public void onSettings(@NotNull Http2Connection connection, @NotNull Settings settings) {
         Intrinsics.checkNotNullParameter(connection, "connection");
         Intrinsics.checkNotNullParameter(settings, "settings");
      }

      @Metadata(
         mv = {2, 2, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006"},
         d2 = {"Lokhttp3/internal/http2/Http2Connection$Listener$Companion;", "", "<init>", "()V", "REFUSE_INCOMING_STREAMS", "Lokhttp3/internal/http2/Http2Connection$Listener;", "okhttp"}
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

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u000e"},
      d2 = {"Lokhttp3/internal/http2/Http2Connection$Companion;", "", "<init>", "()V", "OKHTTP_CLIENT_WINDOW_SIZE", "", "DEFAULT_SETTINGS", "Lokhttp3/internal/http2/Settings;", "getDEFAULT_SETTINGS", "()Lokhttp3/internal/http2/Settings;", "INTERVAL_PING", "DEGRADED_PING", "AWAIT_PING", "DEGRADED_PONG_TIMEOUT_NS", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Settings getDEFAULT_SETTINGS() {
         return Http2Connection.DEFAULT_SETTINGS;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
