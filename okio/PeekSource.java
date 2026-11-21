package okio;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0018\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u000fH\u0016J\b\u0010\u0013\u001a\u00020\u0014H\u0016J\b\u0010\u0015\u001a\u00020\u0016H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0017"},
   d2 = {"Lokio/PeekSource;", "Lokio/Source;", "upstream", "Lokio/BufferedSource;", "<init>", "(Lokio/BufferedSource;)V", "buffer", "Lokio/Buffer;", "expectedSegment", "Lokio/Segment;", "expectedPos", "", "closed", "", "pos", "", "read", "sink", "byteCount", "timeout", "Lokio/Timeout;", "close", "", "okio"}
)
@SourceDebugExtension({"SMAP\nPeekSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PeekSource.kt\nokio/PeekSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,74:1\n1#2:75\n*E\n"})
public final class PeekSource implements Source {
   @NotNull
   private final BufferedSource upstream;
   @NotNull
   private final Buffer buffer;
   @Nullable
   private Segment expectedSegment;
   private int expectedPos;
   private boolean closed;
   private long pos;

   public PeekSource(@NotNull BufferedSource upstream) {
      Intrinsics.checkNotNullParameter(upstream, "upstream");
      super();
      this.upstream = upstream;
      this.buffer = this.upstream.getBuffer();
      this.expectedSegment = this.buffer.head;
      Segment var10001 = this.buffer.head;
      this.expectedPos = var10001 != null ? var10001.pos : -1;
   }

   public long read(@NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      if (byteCount < 0L) {
         int var9 = 0;
         String var10 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var10.toString());
      } else if (this.closed) {
         int var7 = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int var11;
         label67: {
            if (this.expectedSegment != null) {
               label63: {
                  if (this.expectedSegment == this.buffer.head) {
                     var11 = this.expectedPos;
                     Segment var10001 = this.buffer.head;
                     Intrinsics.checkNotNull(var10001);
                     if (var11 == var10001.pos) {
                        break label63;
                     }
                  }

                  var11 = 0;
                  break label67;
               }
            }

            var11 = 1;
         }

         if (!var11) {
            int var5 = 0;
            String var6 = "Peek source is invalid because upstream source was used";
            throw new IllegalStateException(var6.toString());
         } else if (byteCount == 0L) {
            return 0L;
         } else if (!this.upstream.request(this.pos + 1L)) {
            return -1L;
         } else {
            if (this.expectedSegment == null && this.buffer.head != null) {
               this.expectedSegment = this.buffer.head;
               Segment var12 = this.buffer.head;
               Intrinsics.checkNotNull(var12);
               this.expectedPos = var12.pos;
            }

            long toCopy = Math.min(byteCount, this.buffer.size() - this.pos);
            this.buffer.copyTo(sink, this.pos, toCopy);
            this.pos += toCopy;
            return toCopy;
         }
      }
   }

   @NotNull
   public Timeout timeout() {
      return this.upstream.timeout();
   }

   public void close() {
      this.closed = true;
   }
}
