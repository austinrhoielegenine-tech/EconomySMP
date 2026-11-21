package okhttp3.internal.http2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Headers;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.Lockable;
import okhttp3.internal.http2.flowcontrol.WindowCounter;
import okio.AsyncTimeout;
import okio.Buffer;
import okio.BufferedSource;
import okio.Sink;
import okio.Socket;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0011\u0018\u0000 b2\u00020\u00012\u00020\u0002:\u0004`abcB3\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\u0004\b\f\u0010\rJ\u0010\u0010A\u001a\u00020\u000b2\b\b\u0002\u0010B\u001a\u00020\bJ\b\u0010C\u001a\u0004\u0018\u00010\u000bJ$\u0010D\u001a\u00020E2\f\u0010F\u001a\b\u0012\u0004\u0012\u00020H0G2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010I\u001a\u00020\bJ\u000e\u0010J\u001a\u00020E2\u0006\u0010K\u001a\u00020\u000bJ\u0006\u0010+\u001a\u00020LJ\u0006\u0010/\u001a\u00020LJ\u0018\u0010M\u001a\u00020E2\u0006\u0010N\u001a\u0002022\b\u00107\u001a\u0004\u0018\u000108J\b\u0010O\u001a\u00020EH\u0016J\u000e\u0010P\u001a\u00020E2\u0006\u00101\u001a\u000202J\u001a\u0010Q\u001a\u00020\b2\u0006\u00101\u001a\u0002022\b\u00107\u001a\u0004\u0018\u000108H\u0002J\u0016\u0010R\u001a\u00020E2\u0006\u0010#\u001a\u00020S2\u0006\u0010T\u001a\u00020\u0004J\u0016\u0010U\u001a\u00020E2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010V\u001a\u00020E2\u0006\u00101\u001a\u000202J\b\u0010W\u001a\u00020\bH\u0002J\r\u0010X\u001a\u00020EH\u0000¢\u0006\u0002\bYJ\u000e\u0010Z\u001a\u00020E2\u0006\u0010[\u001a\u00020\u0017J\r\u0010\\\u001a\u00020EH\u0000¢\u0006\u0002\b]J\r\u0010^\u001a\u00020EH\u0000¢\u0006\u0002\b_R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R$\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0016\u001a\u00020\u0017@@X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR$\u0010\u001d\u001a\u00020\u00172\u0006\u0010\u0016\u001a\u00020\u0017@@X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001a\"\u0004\b\u001f\u0010\u001cR\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020\u000b0!X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0018\u0010#\u001a\u00060$R\u00020\u0000X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0018\u0010'\u001a\u00060(R\u00020\u0000X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0018\u0010+\u001a\u00060,R\u00020\u0000X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0018\u0010/\u001a\u00060,R\u00020\u0000X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b0\u0010.R\u001e\u00101\u001a\u0004\u0018\u0001028@X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b3\u00104\"\u0004\b5\u00106R\u001c\u00107\u001a\u0004\u0018\u000108X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b9\u0010:\"\u0004\b;\u0010<R\u0011\u0010=\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b=\u0010>R\u0011\u0010?\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b?\u0010>R\u0011\u0010@\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b@\u0010>¨\u0006d"},
   d2 = {"Lokhttp3/internal/http2/Http2Stream;", "Lokhttp3/internal/concurrent/Lockable;", "Lokio/Socket;", "id", "", "connection", "Lokhttp3/internal/http2/Http2Connection;", "outFinished", "", "inFinished", "headers", "Lokhttp3/Headers;", "<init>", "(ILokhttp3/internal/http2/Http2Connection;ZZLokhttp3/Headers;)V", "getId", "()I", "getConnection", "()Lokhttp3/internal/http2/Http2Connection;", "readBytes", "Lokhttp3/internal/http2/flowcontrol/WindowCounter;", "getReadBytes", "()Lokhttp3/internal/http2/flowcontrol/WindowCounter;", "value", "", "writeBytesTotal", "getWriteBytesTotal", "()J", "setWriteBytesTotal$okhttp", "(J)V", "writeBytesMaximum", "getWriteBytesMaximum", "setWriteBytesMaximum$okhttp", "headersQueue", "Ljava/util/ArrayDeque;", "hasResponseHeaders", "source", "Lokhttp3/internal/http2/Http2Stream$FramingSource;", "getSource", "()Lokhttp3/internal/http2/Http2Stream$FramingSource;", "sink", "Lokhttp3/internal/http2/Http2Stream$FramingSink;", "getSink", "()Lokhttp3/internal/http2/Http2Stream$FramingSink;", "readTimeout", "Lokhttp3/internal/http2/Http2Stream$StreamTimeout;", "getReadTimeout$okhttp", "()Lokhttp3/internal/http2/Http2Stream$StreamTimeout;", "writeTimeout", "getWriteTimeout$okhttp", "errorCode", "Lokhttp3/internal/http2/ErrorCode;", "getErrorCode$okhttp", "()Lokhttp3/internal/http2/ErrorCode;", "setErrorCode$okhttp", "(Lokhttp3/internal/http2/ErrorCode;)V", "errorException", "Ljava/io/IOException;", "getErrorException$okhttp", "()Ljava/io/IOException;", "setErrorException$okhttp", "(Ljava/io/IOException;)V", "isOpen", "()Z", "isLocallyInitiated", "isSourceComplete", "takeHeaders", "callerIsIdle", "peekTrailers", "writeHeaders", "", "responseHeaders", "", "Lokhttp3/internal/http2/Header;", "flushHeaders", "enqueueTrailers", "trailers", "Lokio/Timeout;", "close", "rstStatusCode", "cancel", "closeLater", "closeInternal", "receiveData", "Lokio/BufferedSource;", "length", "receiveHeaders", "receiveRstStream", "doReadTimeout", "cancelStreamIfNecessary", "cancelStreamIfNecessary$okhttp", "addBytesToWriteWindow", "delta", "checkOutNotClosed", "checkOutNotClosed$okhttp", "waitForIo", "waitForIo$okhttp", "FramingSource", "FramingSink", "Companion", "StreamTimeout", "okhttp"}
)
@SourceDebugExtension({"SMAP\nHttp2Stream.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,743:1\n1#2:744\n63#3:745\n63#3:746\n63#3:747\n63#3:748\n63#3:749\n49#3,4:750\n63#3:754\n38#3:755\n63#3:756\n63#3:757\n49#3,4:758\n63#3:762\n38#3:763\n49#3,4:764\n49#3,4:768\n63#3:772\n38#3:773\n63#3:774\n38#3:775\n49#3,4:776\n63#3:780\n38#3:781\n34#3:782\n*S KotlinDebug\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream\n*L\n89#1:745\n114#1:746\n136#1:747\n149#1:748\n175#1:749\n200#1:750,4\n203#1:754\n207#1:755\n214#1:756\n227#1:757\n272#1:758,4\n274#1:762\n280#1:763\n294#1:764,4\n304#1:768,4\n307#1:772\n321#1:773\n329#1:774\n332#1:775\n543#1:776,4\n547#1:780\n693#1:781\n713#1:782\n*E\n"})
public final class Http2Stream implements Lockable, Socket {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   private final int id;
   @NotNull
   private final Http2Connection connection;
   @NotNull
   private final WindowCounter readBytes;
   private long writeBytesTotal;
   private long writeBytesMaximum;
   @NotNull
   private final ArrayDeque<Headers> headersQueue;
   private boolean hasResponseHeaders;
   @NotNull
   private final FramingSource source;
   @NotNull
   private final FramingSink sink;
   @NotNull
   private final StreamTimeout readTimeout;
   @NotNull
   private final StreamTimeout writeTimeout;
   @Nullable
   private ErrorCode errorCode;
   @Nullable
   private IOException errorException;
   public static final long EMIT_BUFFER_SIZE = 16384L;

   public Http2Stream(int id, @NotNull Http2Connection connection, boolean outFinished, boolean inFinished, @Nullable Headers headers) {
      Intrinsics.checkNotNullParameter(connection, "connection");
      super();
      this.id = id;
      this.connection = connection;
      this.readBytes = new WindowCounter(this.id);
      this.writeBytesMaximum = (long)this.connection.getPeerSettings().getInitialWindowSize();
      this.headersQueue = new ArrayDeque();
      this.source = new FramingSource((long)this.connection.getOkHttpSettings().getInitialWindowSize(), inFinished);
      this.sink = new FramingSink(outFinished);
      this.readTimeout = new StreamTimeout();
      this.writeTimeout = new StreamTimeout();
      if (headers != null) {
         if (this.isLocallyInitiated()) {
            int var6 = 0;
            String var7 = "locally-initiated streams shouldn't have headers yet";
            throw new IllegalStateException(var7.toString());
         }

         ((Collection)this.headersQueue).add(headers);
      } else if (!this.isLocallyInitiated()) {
         int var8 = 0;
         String var9 = "remotely-initiated streams should have headers";
         throw new IllegalStateException(var9.toString());
      }

   }

   public final int getId() {
      return this.id;
   }

   @NotNull
   public final Http2Connection getConnection() {
      return this.connection;
   }

   @NotNull
   public final WindowCounter getReadBytes() {
      return this.readBytes;
   }

   public final long getWriteBytesTotal() {
      return this.writeBytesTotal;
   }

   public final void setWriteBytesTotal$okhttp(long var1) {
      this.writeBytesTotal = var1;
   }

   public final long getWriteBytesMaximum() {
      return this.writeBytesMaximum;
   }

   public final void setWriteBytesMaximum$okhttp(long var1) {
      this.writeBytesMaximum = var1;
   }

   @NotNull
   public FramingSource getSource() {
      return this.source;
   }

   @NotNull
   public FramingSink getSink() {
      return this.sink;
   }

   @NotNull
   public final StreamTimeout getReadTimeout$okhttp() {
      return this.readTimeout;
   }

   @NotNull
   public final StreamTimeout getWriteTimeout$okhttp() {
      return this.writeTimeout;
   }

   @Nullable
   public final ErrorCode getErrorCode$okhttp() {
      // $FF: Couldn't be decompiled
   }

   public final void setErrorCode$okhttp(@Nullable ErrorCode var1) {
      this.errorCode = var1;
   }

   @Nullable
   public final IOException getErrorException$okhttp() {
      return this.errorException;
   }

   public final void setErrorException$okhttp(@Nullable IOException var1) {
      this.errorException = var1;
   }

   public final boolean isOpen() {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      boolean var8;
      try {
         int var4 = 0;
         if (this.getErrorCode$okhttp() == null) {
            if ((this.getSource().getFinished$okhttp() || this.getSource().getClosed$okhttp()) && (this.getSink().getFinished() || this.getSink().getClosed()) && this.hasResponseHeaders) {
               boolean var7 = false;
               return var7;
            }

            boolean var6 = true;
            return var6;
         }

         var8 = false;
      } finally {
         ;
      }

      return var8;
   }

   public final boolean isLocallyInitiated() {
      boolean streamIsClient = (this.id & 1) == 1;
      return this.connection.getClient$okhttp() == streamIsClient;
   }

   public final boolean isSourceComplete() {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public final Headers takeHeaders(boolean callerIsIdle) throws IOException {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      Headers var8;
      try {
         int var5 = 0;

         while(this.headersQueue.isEmpty() && this.getErrorCode$okhttp() == null) {
            boolean doReadTimeout = callerIsIdle || this.doReadTimeout();
            if (doReadTimeout) {
               this.readTimeout.enter();
            }

            try {
               this.waitForIo$okhttp();
            } finally {
               if (doReadTimeout) {
                  this.readTimeout.exitAndThrowIfTimedOut();
               }

            }
         }

         if (((Collection)this.headersQueue).isEmpty()) {
            IOException var15 = this.errorException;
            Throwable var16;
            if (var15 != null) {
               var16 = (Throwable)var15;
            } else {
               ErrorCode var10002 = this.getErrorCode$okhttp();
               Intrinsics.checkNotNull(var10002);
               var16 = (Throwable)(new StreamResetException(var10002));
            }

            throw var16;
         }

         Object var10000 = this.headersQueue.removeFirst();
         Intrinsics.checkNotNullExpressionValue(var10000, "removeFirst(...)");
         var8 = (Headers)var10000;
      } finally {
         ;
      }

      return var8;
   }

   // $FF: synthetic method
   public static Headers takeHeaders$default(Http2Stream var0, boolean var1, int var2, Object var3) throws IOException {
      if ((var2 & 1) != 0) {
         var1 = false;
      }

      return var0.takeHeaders(var1);
   }

   @Nullable
   public final Headers peekTrailers() throws IOException {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      Headers var7;
      try {
         int var4 = 0;
         if (!this.getSource().getFinished$okhttp() || !this.getSource().getReceiveBuffer().exhausted() || !this.getSource().getReadBuffer().exhausted()) {
            if (this.getErrorCode$okhttp() != null) {
               IOException var10 = this.errorException;
               Throwable var11;
               if (var10 != null) {
                  var11 = (Throwable)var10;
               } else {
                  ErrorCode var10002 = this.getErrorCode$okhttp();
                  Intrinsics.checkNotNull(var10002);
                  var11 = (Throwable)(new StreamResetException(var10002));
               }

               throw var11;
            }

            Object var6 = null;
            return (Headers)var6;
         }

         Headers var10000 = this.getSource().getTrailers();
         if (var10000 == null) {
            var10000 = Headers.EMPTY;
         }

         var7 = var10000;
      } finally {
         ;
      }

      return var7;
   }

   public final void writeHeaders(@NotNull List<Header> responseHeaders, boolean outFinished, boolean flushHeaders) throws IOException {
      Intrinsics.checkNotNullParameter(responseHeaders, "responseHeaders");
      Lockable $this$assertLockNotHeld$iv = this;
      int $i$f$assertLockNotHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
      } else {
         boolean flushHeaders = false;
         flushHeaders = flushHeaders;
         Lockable $this$withLock$iv = this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv) {
            int var8 = 0;
            this.hasResponseHeaders = true;
            if (outFinished) {
               this.getSink().setFinished(true);
               Lockable $this$notifyAll$iv = this;
               int $i$f$notifyAll = 0;
               Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
               ((Object)$this$notifyAll$iv).notifyAll();
            }

            Unit var11 = Unit.INSTANCE;
         }

         if (!flushHeaders) {
            $this$withLock$iv = this;
            $i$f$withLock = 0;
            synchronized($this$withLock$iv) {
               int var19 = 0;
               flushHeaders = this.connection.getWriteBytesTotal() >= this.connection.getWriteBytesMaximum();
               Unit var20 = Unit.INSTANCE;
            }
         }

         this.connection.writeHeaders$okhttp(this.id, outFinished, responseHeaders);
         if (flushHeaders) {
            this.connection.flush();
         }

      }
   }

   public final void enqueueTrailers(@NotNull Headers trailers) {
      Intrinsics.checkNotNullParameter(trailers, "trailers");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var5 = 0;
         if (this.getSink().getFinished()) {
            int var11 = 0;
            String var12 = "already finished";
            throw new IllegalStateException(var12.toString());
         }

         if (trailers.size() == 0) {
            int var6 = 0;
            String var10 = "trailers.size() == 0";
            throw new IllegalArgumentException(var10.toString());
         }

         this.getSink().setTrailers(trailers);
         Unit var7 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   @NotNull
   public final Timeout readTimeout() {
      return this.readTimeout;
   }

   @NotNull
   public final Timeout writeTimeout() {
      return this.writeTimeout;
   }

   public final void close(@NotNull ErrorCode rstStatusCode, @Nullable IOException errorException) throws IOException {
      Intrinsics.checkNotNullParameter(rstStatusCode, "rstStatusCode");
      if (this.closeInternal(rstStatusCode, errorException)) {
         this.connection.writeSynReset$okhttp(this.id, rstStatusCode);
      }
   }

   public void cancel() {
      this.closeLater(ErrorCode.CANCEL);
   }

   public final void closeLater(@NotNull ErrorCode errorCode) {
      Intrinsics.checkNotNullParameter(errorCode, "errorCode");
      if (this.closeInternal(errorCode, (IOException)null)) {
         this.connection.writeSynResetLater$okhttp(this.id, errorCode);
      }
   }

   private final boolean closeInternal(ErrorCode errorCode, IOException errorException) {
      Lockable $this$assertLockNotHeld$iv = this;
      int $i$f$assertLockNotHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
      } else {
         $this$assertLockNotHeld$iv = this;
         $i$f$assertLockNotHeld = 0;
         synchronized($this$assertLockNotHeld$iv){}

         label87: {
            boolean var12;
            try {
               int var6 = 0;
               if (this.getErrorCode$okhttp() == null) {
                  this.errorCode = errorCode;
                  this.errorException = errorException;
                  Lockable $this$notifyAll$iv = this;
                  int $i$f$notifyAll = 0;
                  Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
                  ((Object)$this$notifyAll$iv).notifyAll();
                  if (this.getSource().getFinished$okhttp() && this.getSink().getFinished()) {
                     boolean var11 = false;
                     return var11;
                  }

                  Unit var9 = Unit.INSTANCE;
                  break label87;
               }

               var12 = false;
            } finally {
               ;
            }

            return var12;
         }

         this.connection.removeStream$okhttp(this.id);
         return true;
      }
   }

   public final void receiveData(@NotNull BufferedSource source, int length) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      Lockable $this$assertLockNotHeld$iv = this;
      int $i$f$assertLockNotHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
      } else {
         this.getSource().receive$okhttp(source, (long)length);
      }
   }

   public final void receiveHeaders(@NotNull Headers headers, boolean inFinished) {
      Intrinsics.checkNotNullParameter(headers, "headers");
      Lockable $this$assertLockNotHeld$iv = this;
      int $i$f$assertLockNotHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
      } else {
         boolean open = false;
         Lockable $this$withLock$iv = this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv) {
            int var7 = 0;
            if (this.hasResponseHeaders && headers.get(":status") == null && headers.get(":method") == null) {
               this.getSource().setTrailers(headers);
            } else {
               this.hasResponseHeaders = true;
               ((Collection)this.headersQueue).add(headers);
            }

            if (inFinished) {
               this.getSource().setFinished$okhttp(true);
            }

            open = this.isOpen();
            Lockable $this$notifyAll$iv = this;
            int $i$f$notifyAll = 0;
            Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
            ((Object)$this$notifyAll$iv).notifyAll();
            Unit var10 = Unit.INSTANCE;
         }

         if (!open) {
            this.connection.removeStream$okhttp(this.id);
         }

      }
   }

   public final void receiveRstStream(@NotNull ErrorCode errorCode) {
      Intrinsics.checkNotNullParameter(errorCode, "errorCode");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var5 = 0;
         if (this.getErrorCode$okhttp() == null) {
            this.errorCode = errorCode;
            Lockable $this$notifyAll$iv = this;
            int $i$f$notifyAll = 0;
            Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
            ((Object)$this$notifyAll$iv).notifyAll();
         }

         Unit var8 = Unit.INSTANCE;
      }

   }

   private final boolean doReadTimeout() {
      return !this.connection.getClient$okhttp() || this.getSink().getClosed() || this.getSink().getFinished();
   }

   public final void cancelStreamIfNecessary$okhttp() throws IOException {
      Lockable $this$assertLockNotHeld$iv = this;
      int $i$f$assertLockNotHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
      } else {
         boolean open = false;
         $i$f$assertLockNotHeld = 0;
         Lockable $this$withLock$iv = this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv) {
            int var6 = 0;
            $i$f$assertLockNotHeld = !this.getSource().getFinished$okhttp() && this.getSource().getClosed$okhttp() && (this.getSink().getFinished() || this.getSink().getClosed());
            open = this.isOpen();
            Unit var7 = Unit.INSTANCE;
         }

         if ($i$f$assertLockNotHeld) {
            this.close(ErrorCode.CANCEL, (IOException)null);
         } else if (!open) {
            this.connection.removeStream$okhttp(this.id);
         }

      }
   }

   public final void addBytesToWriteWindow(long delta) {
      this.writeBytesMaximum += delta;
      if (delta > 0L) {
         Lockable $this$notifyAll$iv = this;
         int $i$f$notifyAll = 0;
         Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
         ((Object)$this$notifyAll$iv).notifyAll();
      }

   }

   public final void checkOutNotClosed$okhttp() throws IOException {
      if (this.getSink().getClosed()) {
         throw new IOException("stream closed");
      } else if (this.getSink().getFinished()) {
         throw new IOException("stream finished");
      } else if (this.getErrorCode$okhttp() != null) {
         IOException var10000 = this.errorException;
         Throwable var1;
         if (var10000 != null) {
            var1 = (Throwable)var10000;
         } else {
            ErrorCode var10002 = this.getErrorCode$okhttp();
            Intrinsics.checkNotNull(var10002);
            var1 = (Throwable)(new StreamResetException(var10002));
         }

         throw var1;
      }
   }

   public final void waitForIo$okhttp() throws InterruptedIOException {
      try {
         Lockable $this$wait$iv = this;
         int $i$f$wait = 0;
         Intrinsics.checkNotNull($this$wait$iv, "null cannot be cast to non-null type java.lang.Object");
         ((Object)$this$wait$iv).wait();
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
         throw new InterruptedIOException();
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0019\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u0003H\u0016J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u001b\u001a\u00020\u0003H\u0002J\u001d\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\"2\u0006\u0010\u001d\u001a\u00020\u0003H\u0000¢\u0006\u0002\b#J\b\u0010$\u001a\u00020%H\u0016J\b\u0010&\u001a\u00020\u001fH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0004\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u001c\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0018\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\t\"\u0004\b\u001a\u0010\u000b¨\u0006'"},
      d2 = {"Lokhttp3/internal/http2/Http2Stream$FramingSource;", "Lokio/Source;", "maxByteCount", "", "finished", "", "<init>", "(Lokhttp3/internal/http2/Http2Stream;JZ)V", "getFinished$okhttp", "()Z", "setFinished$okhttp", "(Z)V", "receiveBuffer", "Lokio/Buffer;", "getReceiveBuffer", "()Lokio/Buffer;", "readBuffer", "getReadBuffer", "trailers", "Lokhttp3/Headers;", "getTrailers", "()Lokhttp3/Headers;", "setTrailers", "(Lokhttp3/Headers;)V", "closed", "getClosed$okhttp", "setClosed$okhttp", "read", "sink", "byteCount", "updateConnectionFlowControl", "", "receive", "source", "Lokio/BufferedSource;", "receive$okhttp", "timeout", "Lokio/Timeout;", "close", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHttp2Stream.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,743:1\n1#2:744\n63#3:745\n49#3,4:746\n49#3,4:750\n63#3:754\n63#3:755\n38#3:756\n63#3:757\n38#3:758\n*S KotlinDebug\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSource\n*L\n390#1:745\n453#1:746,4\n467#1:750,4\n474#1:754\n500#1:755\n507#1:756\n528#1:757\n532#1:758\n*E\n"})
   public final class FramingSource implements Source {
      private final long maxByteCount;
      private boolean finished;
      @NotNull
      private final Buffer receiveBuffer;
      @NotNull
      private final Buffer readBuffer;
      @Nullable
      private Headers trailers;
      private boolean closed;

      public FramingSource(long maxByteCount, boolean finished) {
         this.maxByteCount = maxByteCount;
         this.finished = finished;
         this.receiveBuffer = new Buffer();
         this.readBuffer = new Buffer();
      }

      public final boolean getFinished$okhttp() {
         return this.finished;
      }

      public final void setFinished$okhttp(boolean var1) {
         this.finished = var1;
      }

      @NotNull
      public final Buffer getReceiveBuffer() {
         return this.receiveBuffer;
      }

      @NotNull
      public final Buffer getReadBuffer() {
         return this.readBuffer;
      }

      @Nullable
      public final Headers getTrailers() {
         return this.trailers;
      }

      public final void setTrailers(@Nullable Headers var1) {
         this.trailers = var1;
      }

      public final boolean getClosed$okhttp() {
         return this.closed;
      }

      public final void setClosed$okhttp(boolean var1) {
         this.closed = var1;
      }

      public long read(@NotNull Buffer sink, long byteCount) throws IOException {
         Intrinsics.checkNotNullParameter(sink, "sink");
         if (byteCount < 0L) {
            int var5 = 0;
            String var26 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException(var26.toString());
         } else {
            boolean tryAgain;
            Object errorExceptionToDeliver;
            long readBytesDelivered;
            do {
               tryAgain = false;
               readBytesDelivered = 0L;
               readBytesDelivered = -1L;
               errorExceptionToDeliver = null;
               Lockable $this$withLock$iv = (Lockable)Http2Stream.this;
               Http2Stream var8 = Http2Stream.this;
               int $i$f$withLock = 0;
               synchronized($this$withLock$iv){}

               try {
                  int var11 = 0;
                  boolean doReadTimeout = var8.doReadTimeout();
                  if (doReadTimeout) {
                     var8.getReadTimeout$okhttp().enter();
                  }

                  try {
                     if (var8.getErrorCode$okhttp() != null && !this.finished) {
                        IOException var10000 = var8.getErrorException$okhttp();
                        if (var10000 == null) {
                           ErrorCode var10002 = var8.getErrorCode$okhttp();
                           Intrinsics.checkNotNull(var10002);
                           var10000 = new StreamResetException(var10002);
                        }

                        errorExceptionToDeliver = var10000;
                     }

                     if (this.closed) {
                        throw new IOException("stream closed");
                     }

                     if (this.readBuffer.size() > 0L) {
                        readBytesDelivered = this.readBuffer.read(sink, Math.min(byteCount, this.readBuffer.size()));
                        WindowCounter.update$default(var8.getReadBytes(), readBytesDelivered, 0L, 2, (Object)null);
                        long unacknowledgedBytesRead = var8.getReadBytes().getUnacknowledged();
                        if (errorExceptionToDeliver == null && unacknowledgedBytesRead >= (long)(var8.getConnection().getOkHttpSettings().getInitialWindowSize() / 2)) {
                           var8.getConnection().writeWindowUpdateLater$okhttp(var8.getId(), unacknowledgedBytesRead);
                           WindowCounter.update$default(var8.getReadBytes(), 0L, unacknowledgedBytesRead, 1, (Object)null);
                        }
                     } else if (!this.finished && errorExceptionToDeliver == null) {
                        var8.waitForIo$okhttp();
                        tryAgain = true;
                     }
                  } finally {
                     if (doReadTimeout) {
                        var8.getReadTimeout$okhttp().exitAndThrowIfTimedOut();
                     }

                  }

                  Unit var16 = Unit.INSTANCE;
               } finally {
                  ;
               }

               Http2Stream.this.getConnection().getFlowControlListener$okhttp().receivingStreamWindowChanged(Http2Stream.this.getId(), Http2Stream.this.getReadBytes(), this.readBuffer.size());
            } while(tryAgain);

            if (readBytesDelivered != -1L) {
               return readBytesDelivered;
            } else if (errorExceptionToDeliver != null) {
               throw (Throwable)errorExceptionToDeliver;
            } else {
               return -1L;
            }
         }
      }

      private final void updateConnectionFlowControl(long read) {
         Lockable $this$assertLockNotHeld$iv = (Lockable)Http2Stream.this;
         int $i$f$assertLockNotHeld = 0;
         if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
         } else {
            Http2Stream.this.getConnection().updateConnectionFlowControl$okhttp(read);
         }
      }

      public final void receive$okhttp(@NotNull BufferedSource source, long byteCount) throws IOException {
         Intrinsics.checkNotNullParameter(source, "source");
         Lockable $this$assertLockNotHeld$iv = (Lockable)Http2Stream.this;
         int $i$f$assertLockNotHeld = 0;
         if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
         } else {
            long remainingByteCount = 0L;

            Unit var17;
            for(long var28 = byteCount; var28 > 0L; var17 = Unit.INSTANCE) {
               $i$f$assertLockNotHeld = 0;
               boolean flowControlError = false;
               Lockable $this$withLock$iv = (Lockable)Http2Stream.this;
               int $i$f$withLock = 0;
               synchronized($this$withLock$iv) {
                  int var10 = 0;
                  $i$f$assertLockNotHeld = this.finished;
                  flowControlError = var28 + this.readBuffer.size() > this.maxByteCount;
                  Unit var11 = Unit.INSTANCE;
               }

               if (flowControlError) {
                  source.skip(var28);
                  Http2Stream.this.closeLater(ErrorCode.FLOW_CONTROL_ERROR);
                  return;
               }

               if ($i$f$assertLockNotHeld) {
                  source.skip(var28);
                  return;
               }

               long read = source.read(this.receiveBuffer, var28);
               if (read == -1L) {
                  throw new EOFException();
               }

               var28 -= read;
               Lockable $this$withLock$ivx = (Lockable)Http2Stream.this;
               Http2Stream var26 = Http2Stream.this;
               int $i$f$withLock = 0;
               synchronized($this$withLock$ivx) {
                  int var13 = 0;
                  if (this.closed) {
                     this.receiveBuffer.clear();
                  } else {
                     boolean wasEmpty = this.readBuffer.size() == 0L;
                     this.readBuffer.writeAll(this.receiveBuffer);
                     if (wasEmpty) {
                        Lockable $this$notifyAll$iv = var26;
                        int $i$f$notifyAll = 0;
                        Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
                        ((Object)$this$notifyAll$iv).notifyAll();
                     }
                  }
               }
            }

            this.updateConnectionFlowControl(byteCount);
            Http2Stream.this.getConnection().getFlowControlListener$okhttp().receivingStreamWindowChanged(Http2Stream.this.getId(), Http2Stream.this.getReadBytes(), this.readBuffer.size());
         }
      }

      @NotNull
      public Timeout timeout() {
         return Http2Stream.this.getReadTimeout$okhttp();
      }

      public void close() throws IOException {
         long bytesDiscarded = 0L;
         Lockable $this$withLock$iv = (Lockable)Http2Stream.this;
         Http2Stream var2 = Http2Stream.this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv) {
            int var5 = 0;
            this.closed = true;
            bytesDiscarded = this.readBuffer.size();
            this.readBuffer.clear();
            Lockable $this$notifyAll$iv = var2;
            int $i$f$notifyAll = 0;
            Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
            ((Object)$this$notifyAll$iv).notifyAll();
            Unit var8 = Unit.INSTANCE;
         }

         if (bytesDiscarded > 0L) {
            this.updateConnectionFlowControl(bytesDiscarded);
         }

         Http2Stream.this.cancelStreamIfNecessary$okhttp();
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0018\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u0003H\u0002J\b\u0010\u001c\u001a\u00020\u0016H\u0016J\b\u0010\u001d\u001a\u00020\u001eH\u0016J\b\u0010\u001f\u001a\u00020\u0016H\u0016R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\f\u001a\u0004\u0018\u00010\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0007\"\u0004\b\u0014\u0010\t¨\u0006 "},
      d2 = {"Lokhttp3/internal/http2/Http2Stream$FramingSink;", "Lokio/Sink;", "finished", "", "<init>", "(Lokhttp3/internal/http2/Http2Stream;Z)V", "getFinished", "()Z", "setFinished", "(Z)V", "sendBuffer", "Lokio/Buffer;", "trailers", "Lokhttp3/Headers;", "getTrailers", "()Lokhttp3/Headers;", "setTrailers", "(Lokhttp3/Headers;)V", "closed", "getClosed", "setClosed", "write", "", "source", "byteCount", "", "emitFrame", "outFinishedOnLastFrame", "flush", "timeout", "Lokio/Timeout;", "close", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHttp2Stream.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSink\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,743:1\n49#2,4:744\n63#2:748\n49#2,4:749\n63#2:753\n49#2,4:754\n63#2:758\n63#2:759\n38#2:760\n*S KotlinDebug\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSink\n*L\n582#1:744,4\n598#1:748\n628#1:749,4\n630#1:753\n644#1:754,4\n647#1:758\n676#1:759\n678#1:760\n*E\n"})
   public final class FramingSink implements Sink {
      private boolean finished;
      @NotNull
      private final Buffer sendBuffer;
      @Nullable
      private Headers trailers;
      private boolean closed;

      public FramingSink(boolean finished) {
         this.finished = finished;
         this.sendBuffer = new Buffer();
      }

      // $FF: synthetic method
      public FramingSink(boolean var2, int var3, DefaultConstructorMarker var4) {
         if ((var3 & 1) != 0) {
            var2 = false;
         }

         this(var2);
      }

      public final boolean getFinished() {
         return this.finished;
      }

      public final void setFinished(boolean var1) {
         this.finished = var1;
      }

      @Nullable
      public final Headers getTrailers() {
         return this.trailers;
      }

      public final void setTrailers(@Nullable Headers var1) {
         this.trailers = var1;
      }

      public final boolean getClosed() {
         return this.closed;
      }

      public final void setClosed(boolean var1) {
         this.closed = var1;
      }

      public void write(@NotNull Buffer source, long byteCount) throws IOException {
         Intrinsics.checkNotNullParameter(source, "source");
         Lockable $this$assertLockNotHeld$iv = (Lockable)Http2Stream.this;
         int $i$f$assertLockNotHeld = 0;
         if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
         } else {
            this.sendBuffer.write(source, byteCount);

            while(this.sendBuffer.size() >= 16384L) {
               this.emitFrame(false);
            }

         }
      }

      private final void emitFrame(boolean outFinishedOnLastFrame) throws IOException {
         long toWrite = 0L;
         boolean outFinished = false;
         Lockable $this$withLock$iv = (Lockable)Http2Stream.this;
         Http2Stream var4 = Http2Stream.this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv){}

         try {
            int var7 = 0;
            var4.getWriteTimeout$okhttp().enter();

            try {
               while(var4.getWriteBytesTotal() >= var4.getWriteBytesMaximum() && !this.finished && !this.closed && var4.getErrorCode$okhttp() == null) {
                  var4.waitForIo$okhttp();
               }
            } finally {
               var4.getWriteTimeout$okhttp().exitAndThrowIfTimedOut();
            }

            var4.checkOutNotClosed$okhttp();
            toWrite = Math.min(var4.getWriteBytesMaximum() - var4.getWriteBytesTotal(), this.sendBuffer.size());
            var4.setWriteBytesTotal$okhttp(var4.getWriteBytesTotal() + toWrite);
            outFinished = outFinishedOnLastFrame && toWrite == this.sendBuffer.size();
            Unit var9 = Unit.INSTANCE;
         } finally {
            ;
         }

         Http2Stream.this.getWriteTimeout$okhttp().enter();

         try {
            Http2Stream.this.getConnection().writeData(Http2Stream.this.getId(), outFinished, this.sendBuffer, toWrite);
         } finally {
            Http2Stream.this.getWriteTimeout$okhttp().exitAndThrowIfTimedOut();
         }

      }

      public void flush() throws IOException {
         Lockable $this$assertLockNotHeld$iv = (Lockable)Http2Stream.this;
         int $i$f$assertLockNotHeld = 0;
         if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
         } else {
            $this$assertLockNotHeld$iv = (Lockable)Http2Stream.this;
            Http2Stream var9 = Http2Stream.this;
            int $i$f$withLock = 0;
            synchronized($this$assertLockNotHeld$iv) {
               int var5 = 0;
               var9.checkOutNotClosed$okhttp();
               Unit var10 = Unit.INSTANCE;
            }

            while(this.sendBuffer.size() > 0L) {
               this.emitFrame(false);
               Http2Stream.this.getConnection().flush();
            }

         }
      }

      @NotNull
      public Timeout timeout() {
         return Http2Stream.this.getWriteTimeout$okhttp();
      }

      public void close() throws IOException {
         Lockable $this$assertLockNotHeld$iv = (Lockable)Http2Stream.this;
         int $i$f$assertLockNotHeld = 0;
         if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
         } else {
            boolean outFinished = false;
            Lockable $this$withLock$iv = (Lockable)Http2Stream.this;
            Http2Stream hasTrailers = Http2Stream.this;
            int $i$f$withLock = 0;
            synchronized($this$withLock$iv){}

            try {
               int var6 = 0;
               if (this.closed) {
                  return;
               }

               outFinished = hasTrailers.getErrorCode$okhttp() == null;
               Unit var7 = Unit.INSTANCE;
            } finally {
               ;
            }

            if (!Http2Stream.this.getSink().finished) {
               boolean hasData = this.sendBuffer.size() > 0L;
               boolean hasTrailers = this.trailers != null;
               if (!hasTrailers) {
                  if (hasData) {
                     while(this.sendBuffer.size() > 0L) {
                        this.emitFrame(true);
                     }
                  } else if (outFinished) {
                     Http2Stream.this.getConnection().writeData(Http2Stream.this.getId(), true, (Buffer)null, 0L);
                  }
               } else {
                  while(this.sendBuffer.size() > 0L) {
                     this.emitFrame(false);
                  }

                  Http2Connection var10000 = Http2Stream.this.getConnection();
                  int var10001 = Http2Stream.this.getId();
                  Headers var10003 = this.trailers;
                  Intrinsics.checkNotNull(var10003);
                  var10000.writeHeaders$okhttp(var10001, outFinished, _UtilJvmKt.toHeaderList(var10003));
               }
            }

            $this$withLock$iv = (Lockable)Http2Stream.this;
            hasTrailers = Http2Stream.this;
            $i$f$withLock = 0;
            synchronized($this$withLock$iv) {
               int var22 = 0;
               this.closed = true;
               Lockable $this$notifyAll$iv = hasTrailers;
               int $i$f$notifyAll = 0;
               Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
               ((Object)$this$notifyAll$iv).notifyAll();
               Unit var9 = Unit.INSTANCE;
            }

            Http2Stream.this.getConnection().flush();
            Http2Stream.this.cancelStreamIfNecessary$okhttp();
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lokhttp3/internal/http2/Http2Stream$Companion;", "", "<init>", "()V", "EMIT_BUFFER_SIZE", "", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0080\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0014J\u0012\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u0007H\u0014J\u0006\u0010\t\u001a\u00020\u0005¨\u0006\n"},
      d2 = {"Lokhttp3/internal/http2/Http2Stream$StreamTimeout;", "Lokio/AsyncTimeout;", "<init>", "(Lokhttp3/internal/http2/Http2Stream;)V", "timedOut", "", "newTimeoutException", "Ljava/io/IOException;", "cause", "exitAndThrowIfTimedOut", "okhttp"}
   )
   public final class StreamTimeout extends AsyncTimeout {
      protected void timedOut() {
         Http2Stream.this.closeLater(ErrorCode.CANCEL);
         Http2Stream.this.getConnection().sendDegradedPingLater$okhttp();
      }

      @NotNull
      protected IOException newTimeoutException(@Nullable IOException cause) {
         SocketTimeoutException $this$newTimeoutException_u24lambda_u240 = new SocketTimeoutException("timeout");
         int var4 = 0;
         if (cause != null) {
            $this$newTimeoutException_u24lambda_u240.initCause((Throwable)cause);
         }

         return (IOException)$this$newTimeoutException_u24lambda_u240;
      }

      public final void exitAndThrowIfTimedOut() throws IOException {
         if (this.exit()) {
            throw this.newTimeoutException((IOException)null);
         }
      }
   }
}
