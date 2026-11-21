package okhttp3.internal.http2.flowcontrol;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001a\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\r\u001a\u00020\tJ\b\u0010\u0013\u001a\u00020\u0014H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001e\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\t@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001e\u0010\r\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\t@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u000f\u001a\u00020\t8F¢\u0006\u0006\u001a\u0004\b\u0010\u0010\f¨\u0006\u0015"},
   d2 = {"Lokhttp3/internal/http2/flowcontrol/WindowCounter;", "", "streamId", "", "<init>", "(I)V", "getStreamId", "()I", "value", "", "total", "getTotal", "()J", "acknowledged", "getAcknowledged", "unacknowledged", "getUnacknowledged", "update", "", "toString", "", "okhttp"}
)
public final class WindowCounter {
   private final int streamId;
   private long total;
   private long acknowledged;

   public WindowCounter(int streamId) {
      this.streamId = streamId;
   }

   public final int getStreamId() {
      return this.streamId;
   }

   public final long getTotal() {
      return this.total;
   }

   public final long getAcknowledged() {
      return this.acknowledged;
   }

   public final synchronized long getUnacknowledged() {
      return this.total - this.acknowledged;
   }

   public final synchronized void update(long total, long acknowledged) {
      if (total < 0L) {
         throw new IllegalStateException("Check failed.");
      } else if (acknowledged < 0L) {
         throw new IllegalStateException("Check failed.");
      } else {
         this.total += total;
         this.acknowledged += acknowledged;
         if (this.acknowledged > this.total) {
            throw new IllegalStateException("Check failed.");
         }
      }
   }

   // $FF: synthetic method
   public static void update$default(WindowCounter var0, long var1, long var3, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = 0L;
      }

      if ((var5 & 2) != 0) {
         var3 = 0L;
      }

      var0.update(var1, var3);
   }

   @NotNull
   public String toString() {
      return "WindowCounter(streamId=" + this.streamId + ", total=" + this.total + ", acknowledged=" + this.acknowledged + ", unacknowledged=" + this.getUnacknowledged() + ')';
   }
}
