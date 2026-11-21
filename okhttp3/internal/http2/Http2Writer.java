package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.Lockable;
import okio.Buffer;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\t\u0018\u0000 <2\u00020\u00012\u00020\u0002:\u0001<B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0016J$\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\f2\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001bJ\u0006\u0010\u001d\u001a\u00020\u0013J\u0016\u0010\u001e\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\f2\u0006\u0010\u001f\u001a\u00020 J\u0006\u0010!\u001a\u00020\fJ(\u0010\"\u001a\u00020\u00132\u0006\u0010#\u001a\u00020\u00062\u0006\u0010\u0018\u001a\u00020\f2\b\u0010$\u001a\u0004\u0018\u00010\n2\u0006\u0010%\u001a\u00020\fJ(\u0010&\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\f2\u0006\u0010'\u001a\u00020\f2\b\u0010(\u001a\u0004\u0018\u00010\n2\u0006\u0010%\u001a\u00020\fJ\u000e\u0010)\u001a\u00020\u00132\u0006\u0010)\u001a\u00020\u0016J\u001e\u0010*\u001a\u00020\u00132\u0006\u0010+\u001a\u00020\u00062\u0006\u0010,\u001a\u00020\f2\u0006\u0010-\u001a\u00020\fJ\u001e\u0010.\u001a\u00020\u00132\u0006\u0010/\u001a\u00020\f2\u0006\u0010\u001f\u001a\u00020 2\u0006\u00100\u001a\u000201J\u0016\u00102\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\f2\u0006\u00103\u001a\u000204J&\u00105\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\f2\u0006\u00106\u001a\u00020\f2\u0006\u00107\u001a\u00020\f2\u0006\u0010'\u001a\u00020\fJ\b\u00108\u001a\u00020\u0013H\u0016J\u0018\u00109\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\f2\u0006\u0010%\u001a\u000204H\u0002J$\u0010:\u001a\u00020\u00132\u0006\u0010#\u001a\u00020\u00062\u0006\u0010\u0018\u001a\u00020\f2\f\u0010;\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006="},
   d2 = {"Lokhttp3/internal/http2/Http2Writer;", "Ljava/io/Closeable;", "Lokhttp3/internal/concurrent/Lockable;", "sink", "Lokio/BufferedSink;", "client", "", "<init>", "(Lokio/BufferedSink;Z)V", "hpackBuffer", "Lokio/Buffer;", "maxFrameSize", "", "closed", "hpackWriter", "Lokhttp3/internal/http2/Hpack$Writer;", "getHpackWriter", "()Lokhttp3/internal/http2/Hpack$Writer;", "connectionPreface", "", "applyAndAckSettings", "peerSettings", "Lokhttp3/internal/http2/Settings;", "pushPromise", "streamId", "promisedStreamId", "requestHeaders", "", "Lokhttp3/internal/http2/Header;", "flush", "rstStream", "errorCode", "Lokhttp3/internal/http2/ErrorCode;", "maxDataLength", "data", "outFinished", "source", "byteCount", "dataFrame", "flags", "buffer", "settings", "ping", "ack", "payload1", "payload2", "goAway", "lastGoodStreamId", "debugData", "", "windowUpdate", "windowSizeIncrement", "", "frameHeader", "length", "type", "close", "writeContinuationFrames", "headers", "headerBlock", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nHttp2Writer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Writer.kt\nokhttp3/internal/http2/Http2Writer\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,384:1\n63#2:385\n63#2:386\n63#2:387\n63#2:388\n63#2:389\n63#2:390\n63#2:391\n63#2:392\n63#2:393\n63#2:395\n63#2:396\n63#2:397\n1#3:394\n*S KotlinDebug\n*F\n+ 1 Http2Writer.kt\nokhttp3/internal/http2/Http2Writer\n*L\n60#1:385\n74#1:386\n108#1:387\n129#1:388\n140#1:389\n172#1:390\n201#1:391\n228#1:392\n256#1:393\n283#1:395\n329#1:396\n360#1:397\n*E\n"})
public final class Http2Writer implements Closeable, Lockable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final BufferedSink sink;
   private final boolean client;
   @NotNull
   private final Buffer hpackBuffer;
   private int maxFrameSize;
   private boolean closed;
   @NotNull
   private final Hpack.Writer hpackWriter;
   private static final Logger logger = Logger.getLogger(Http2.class.getName());

   public Http2Writer(@NotNull BufferedSink sink, boolean client) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      super();
      this.sink = sink;
      this.client = client;
      this.hpackBuffer = new Buffer();
      this.maxFrameSize = 16384;
      this.hpackWriter = new Hpack.Writer(0, false, this.hpackBuffer, 3, (DefaultConstructorMarker)null);
   }

   @NotNull
   public final Hpack.Writer getHpackWriter() {
      return this.hpackWriter;
   }

   public final void connectionPreface() throws IOException {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var4 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         if (this.client) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine(_UtilJvmKt.format(">> CONNECTION " + Http2.CONNECTION_PREFACE.hex()));
            }

            this.sink.write(Http2.CONNECTION_PREFACE);
            this.sink.flush();
            Unit var8 = Unit.INSTANCE;
            return;
         }
      } finally {
         ;
      }

   }

   public final void applyAndAckSettings(@NotNull Settings peerSettings) throws IOException {
      Intrinsics.checkNotNullParameter(peerSettings, "peerSettings");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var5 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         this.maxFrameSize = peerSettings.getMaxFrameSize(this.maxFrameSize);
         if (peerSettings.getHeaderTableSize() != -1) {
            this.hpackWriter.resizeHeaderTable(peerSettings.getHeaderTableSize());
         }

         this.frameHeader(0, 0, 4, 1);
         this.sink.flush();
         Unit var6 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final void pushPromise(int streamId, int promisedStreamId, @NotNull List<Header> requestHeaders) throws IOException {
      Intrinsics.checkNotNullParameter(requestHeaders, "requestHeaders");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var7 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         this.hpackWriter.writeHeaders(requestHeaders);
         long byteCount = this.hpackBuffer.size();
         int length = (int)Math.min((long)this.maxFrameSize - 4L, byteCount);
         this.frameHeader(streamId, length + 4, 5, byteCount == (long)length ? 4 : 0);
         this.sink.writeInt(promisedStreamId & Integer.MAX_VALUE);
         this.sink.write(this.hpackBuffer, (long)length);
         if (byteCount > (long)length) {
            this.writeContinuationFrames(streamId, byteCount - (long)length);
         }

         Unit var11 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final void flush() throws IOException {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var4 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         this.sink.flush();
         Unit var8 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final void rstStream(int streamId, @NotNull ErrorCode errorCode) throws IOException {
      Intrinsics.checkNotNullParameter(errorCode, "errorCode");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var6 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         if (errorCode.getHttpCode() == -1) {
            String var7 = "Failed requirement.";
            throw new IllegalArgumentException(var7.toString());
         }

         this.frameHeader(streamId, 4, 3, 0);
         this.sink.writeInt(errorCode.getHttpCode());
         this.sink.flush();
         Unit var8 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final int maxDataLength() {
      return this.maxFrameSize;
   }

   public final void data(boolean outFinished, int streamId, @Nullable Buffer source, int byteCount) throws IOException {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var8 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         int flags = 0;
         if (outFinished) {
            flags |= 1;
         }

         this.dataFrame(streamId, flags, source, byteCount);
         Unit var10 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final void dataFrame(int streamId, int flags, @Nullable Buffer buffer, int byteCount) throws IOException {
      this.frameHeader(streamId, byteCount, 0, flags);
      if (byteCount > 0) {
         BufferedSink var10000 = this.sink;
         Intrinsics.checkNotNull(buffer);
         var10000.write(buffer, (long)byteCount);
      }

   }

   public final void settings(@NotNull Settings settings) throws IOException {
      Intrinsics.checkNotNullParameter(settings, "settings");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var5 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         this.frameHeader(0, settings.size() * 6, 4, 0);

         for(int i = 0; i < 10; ++i) {
            if (settings.isSet(i)) {
               this.sink.writeShort(i);
               this.sink.writeInt(settings.get(i));
            }
         }

         this.sink.flush();
         Unit var7 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final void ping(boolean ack, int payload1, int payload2) throws IOException {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var7 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         this.frameHeader(0, 8, 6, ack ? 1 : 0);
         this.sink.writeInt(payload1);
         this.sink.writeInt(payload2);
         this.sink.flush();
         Unit var8 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final void goAway(int lastGoodStreamId, @NotNull ErrorCode errorCode, @NotNull byte[] debugData) throws IOException {
      Intrinsics.checkNotNullParameter(errorCode, "errorCode");
      Intrinsics.checkNotNullParameter(debugData, "debugData");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var7 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         if (errorCode.getHttpCode() == -1) {
            int var8 = 0;
            String var13 = "errorCode.httpCode == -1";
            throw new IllegalArgumentException(var13.toString());
         }

         this.frameHeader(0, 8 + debugData.length, 7, 0);
         this.sink.writeInt(lastGoodStreamId);
         this.sink.writeInt(errorCode.getHttpCode());
         if (debugData.length != 0) {
            this.sink.write(debugData);
         }

         this.sink.flush();
         Unit var9 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final void windowUpdate(int streamId, long windowSizeIncrement) throws IOException {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var7 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         if (windowSizeIncrement == 0L || windowSizeIncrement > 2147483647L) {
            int var8 = 0;
            String var13 = "windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: " + windowSizeIncrement;
            throw new IllegalArgumentException(var13.toString());
         }

         if (logger.isLoggable(Level.FINE)) {
            logger.fine(Http2.INSTANCE.frameLogWindowUpdate(false, streamId, 4, windowSizeIncrement));
         }

         this.frameHeader(streamId, 4, 8, 0);
         this.sink.writeInt((int)windowSizeIncrement);
         this.sink.flush();
         Unit var9 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final void frameHeader(int streamId, int length, int type, int flags) throws IOException {
      if (type != 8 && logger.isLoggable(Level.FINE)) {
         logger.fine(Http2.INSTANCE.frameLog(false, streamId, length, type, flags));
      }

      if (length > this.maxFrameSize) {
         int var7 = 0;
         String var8 = "FRAME_SIZE_ERROR length > " + this.maxFrameSize + ": " + length;
         throw new IllegalArgumentException(var8.toString());
      } else if ((streamId & Integer.MIN_VALUE) != 0) {
         int var5 = 0;
         String var6 = "reserved bit set: " + streamId;
         throw new IllegalArgumentException(var6.toString());
      } else {
         _UtilCommonKt.writeMedium(this.sink, length);
         this.sink.writeByte(type & 255);
         this.sink.writeByte(flags & 255);
         this.sink.writeInt(streamId & Integer.MAX_VALUE);
      }
   }

   public void close() throws IOException {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var4 = 0;
         this.closed = true;
         this.sink.close();
         Unit var7 = Unit.INSTANCE;
      }

   }

   private final void writeContinuationFrames(int streamId, long byteCount) throws IOException {
      long byteCount = byteCount;

      while(byteCount > 0L) {
         long length = Math.min((long)this.maxFrameSize, byteCount);
         byteCount -= length;
         this.frameHeader(streamId, (int)length, 9, byteCount == 0L ? 4 : 0);
         this.sink.write(this.hpackBuffer, length);
      }

   }

   public final void headers(boolean outFinished, int streamId, @NotNull List<Header> headerBlock) throws IOException {
      Intrinsics.checkNotNullParameter(headerBlock, "headerBlock");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var7 = 0;
         if (this.closed) {
            throw new IOException("closed");
         }

         this.hpackWriter.writeHeaders(headerBlock);
         long byteCount = this.hpackBuffer.size();
         long length = Math.min((long)this.maxFrameSize, byteCount);
         int flags = byteCount == length ? 4 : 0;
         if (outFinished) {
            flags |= 1;
         }

         this.frameHeader(streamId, (int)length, 1, flags);
         this.sink.write(this.hpackBuffer, length);
         if (byteCount > length) {
            this.writeContinuationFrames(streamId, byteCount - length);
         }

         Unit var13 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"},
      d2 = {"Lokhttp3/internal/http2/Http2Writer$Companion;", "", "<init>", "()V", "logger", "Ljava/util/logging/Logger;", "kotlin.jvm.PlatformType", "okhttp"}
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
