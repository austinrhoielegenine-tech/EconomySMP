package okio;

import java.io.IOException;
import java.util.zip.Deflater;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.internal._ZlibJvmKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u0019\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\bJ\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\u0010\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\nH\u0002J\b\u0010\u0013\u001a\u00020\fH\u0016J\r\u0010\u0014\u001a\u00020\fH\u0000¢\u0006\u0002\b\u0015J\b\u0010\u0016\u001a\u00020\fH\u0016J\b\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001b"},
   d2 = {"Lokio/DeflaterSink;", "Lokio/Sink;", "sink", "Lokio/BufferedSink;", "deflater", "Ljava/util/zip/Deflater;", "<init>", "(Lokio/BufferedSink;Ljava/util/zip/Deflater;)V", "(Lokio/Sink;Ljava/util/zip/Deflater;)V", "closed", "", "write", "", "source", "Lokio/Buffer;", "byteCount", "", "deflate", "syncFlush", "flush", "finishDeflate", "finishDeflate$okio", "close", "timeout", "Lokio/Timeout;", "toString", "", "okio"}
)
@SourceDebugExtension({"SMAP\nDeflaterSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DeflaterSink.kt\nokio/DeflaterSink\n+ 2 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,140:1\n85#2:141\n*S KotlinDebug\n*F\n+ 1 DeflaterSink.kt\nokio/DeflaterSink\n*L\n39#1:141\n*E\n"})
public final class DeflaterSink implements Sink {
   @NotNull
   private final BufferedSink sink;
   @NotNull
   private final Deflater deflater;
   private boolean closed;

   public DeflaterSink(@NotNull BufferedSink sink, @NotNull Deflater deflater) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Intrinsics.checkNotNullParameter(deflater, "deflater");
      super();
      this.sink = sink;
      this.deflater = deflater;
   }

   public DeflaterSink(@NotNull Sink sink, @NotNull Deflater deflater) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Intrinsics.checkNotNullParameter(deflater, "deflater");
      this(Okio.buffer(sink), deflater);
   }

   public void write(@NotNull Buffer source, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);

      int toDeflate;
      for(long remaining = byteCount; remaining > 0L; remaining -= (long)toDeflate) {
         Segment var10000 = source.head;
         Intrinsics.checkNotNull(var10000);
         Segment head = var10000;
         int b$iv = head.limit - head.pos;
         int $i$f$minOf = 0;
         toDeflate = (int)Math.min(remaining, (long)b$iv);
         this.deflater.setInput(head.data, head.pos, toDeflate);
         this.deflate(false);
         source.setSize$okio(source.size() - (long)toDeflate);
         head.pos += toDeflate;
         if (head.pos == head.limit) {
            source.head = head.pop();
            SegmentPool.recycle(head);
         }
      }

      this.deflater.setInput(_ZlibJvmKt.getEMPTY_BYTE_ARRAY(), 0, 0);
   }

   private final void deflate(boolean syncFlush) {
      Buffer buffer = this.sink.getBuffer();

      while(true) {
         Segment s = buffer.writableSegment$okio(1);

         int deflated;
         try {
            deflated = syncFlush ? this.deflater.deflate(s.data, s.limit, 8192 - s.limit, 2) : this.deflater.deflate(s.data, s.limit, 8192 - s.limit);
         } catch (NullPointerException npe) {
            throw new IOException("Deflater already closed", (Throwable)npe);
         }

         if (deflated > 0) {
            s.limit += deflated;
            buffer.setSize$okio(buffer.size() + (long)deflated);
            this.sink.emitCompleteSegments();
         } else if (this.deflater.needsInput()) {
            if (s.pos == s.limit) {
               buffer.head = s.pop();
               SegmentPool.recycle(s);
            }

            return;
         }
      }
   }

   public void flush() throws IOException {
      this.deflate(true);
      this.sink.flush();
   }

   public final void finishDeflate$okio() {
      this.deflater.finish();
      this.deflate(false);
   }

   public void close() throws IOException {
      if (!this.closed) {
         Throwable thrown = null;

         try {
            this.finishDeflate$okio();
         } catch (Throwable e) {
            thrown = e;
         }

         try {
            this.deflater.end();
         } catch (Throwable e) {
            if (thrown == null) {
               thrown = e;
            }
         }

         try {
            this.sink.close();
         } catch (Throwable e) {
            if (thrown == null) {
               thrown = e;
            }
         }

         this.closed = true;
         if (thrown != null) {
            throw thrown;
         }
      }
   }

   @NotNull
   public Timeout timeout() {
      return this.sink.timeout();
   }

   @NotNull
   public String toString() {
      return "DeflaterSink(" + this.sink + ')';
   }
}
