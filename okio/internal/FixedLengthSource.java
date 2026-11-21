package okio.internal;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import okio.ForwardingSource;
import okio.Source;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0018\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0005H\u0016J\u0014\u0010\u000f\u001a\u00020\u0010*\u00020\r2\u0006\u0010\u0011\u001a\u00020\u0005H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"},
   d2 = {"Lokio/internal/FixedLengthSource;", "Lokio/ForwardingSource;", "delegate", "Lokio/Source;", "size", "", "truncate", "", "<init>", "(Lokio/Source;JZ)V", "bytesReceived", "read", "sink", "Lokio/Buffer;", "byteCount", "truncateToSize", "", "newSize", "okio"}
)
public final class FixedLengthSource extends ForwardingSource {
   private final long size;
   private final boolean truncate;
   private long bytesReceived;

   public FixedLengthSource(@NotNull Source delegate, long size, boolean truncate) {
      Intrinsics.checkNotNullParameter(delegate, "delegate");
      super(delegate);
      this.size = size;
      this.truncate = truncate;
   }

   public long read(@NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      long var10000;
      if (this.bytesReceived > this.size) {
         var10000 = 0L;
      } else if (this.truncate) {
         long remaining = this.size - this.bytesReceived;
         if (remaining == 0L) {
            return -1L;
         }

         var10000 = Math.min(byteCount, remaining);
      } else {
         var10000 = byteCount;
      }

      long toRead = var10000;
      long result = super.read(sink, toRead);
      if (result != -1L) {
         this.bytesReceived += result;
      }

      if ((this.bytesReceived >= this.size || result != -1L) && this.bytesReceived <= this.size) {
         return result;
      } else {
         if (result > 0L && this.bytesReceived > this.size) {
            this.truncateToSize(sink, sink.size() - (this.bytesReceived - this.size));
         }

         throw new IOException("expected " + this.size + " bytes but got " + this.bytesReceived);
      }
   }

   private final void truncateToSize(Buffer $this$truncateToSize, long newSize) {
      Buffer scratch = new Buffer();
      scratch.writeAll($this$truncateToSize);
      $this$truncateToSize.write(scratch, newSize);
      scratch.clear();
   }
}
