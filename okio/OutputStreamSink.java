package okio;

import java.io.OutputStream;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\tH\u0016J\b\u0010\u000f\u001a\u00020\tH\u0016J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"},
   d2 = {"Lokio/OutputStreamSink;", "Lokio/Sink;", "out", "Ljava/io/OutputStream;", "timeout", "Lokio/Timeout;", "<init>", "(Ljava/io/OutputStream;Lokio/Timeout;)V", "write", "", "source", "Lokio/Buffer;", "byteCount", "", "flush", "close", "toString", "", "okio"}
)
@SourceDebugExtension({"SMAP\nJvmOkio.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JvmOkio.kt\nokio/OutputStreamSink\n+ 2 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,234:1\n85#2:235\n*S KotlinDebug\n*F\n+ 1 JvmOkio.kt\nokio/OutputStreamSink\n*L\n56#1:235\n*E\n"})
final class OutputStreamSink implements Sink {
   @NotNull
   private final OutputStream out;
   @NotNull
   private final Timeout timeout;

   public OutputStreamSink(@NotNull OutputStream out, @NotNull Timeout timeout) {
      Intrinsics.checkNotNullParameter(out, "out");
      Intrinsics.checkNotNullParameter(timeout, "timeout");
      super();
      this.out = out;
      this.timeout = timeout;
   }

   public void write(@NotNull Buffer source, long byteCount) {
      Intrinsics.checkNotNullParameter(source, "source");
      -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);
      long remaining = byteCount;

      while(remaining > 0L) {
         this.timeout.throwIfReached();
         Segment var10000 = source.head;
         Intrinsics.checkNotNull(var10000);
         Segment head = var10000;
         int b$iv = head.limit - head.pos;
         int $i$f$minOf = 0;
         int toCopy = (int)Math.min(remaining, (long)b$iv);
         this.out.write(head.data, head.pos, toCopy);
         head.pos += toCopy;
         remaining -= (long)toCopy;
         source.setSize$okio(source.size() - (long)toCopy);
         if (head.pos == head.limit) {
            source.head = head.pop();
            SegmentPool.recycle(head);
         }
      }

   }

   public void flush() {
      this.out.flush();
   }

   public void close() {
      this.out.close();
   }

   @NotNull
   public Timeout timeout() {
      return this.timeout;
   }

   @NotNull
   public String toString() {
      return "sink(" + this.out + ')';
   }
}
