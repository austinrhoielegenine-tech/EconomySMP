package okhttp3.internal.connection;

import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\u0018\u00002\u00020\u0001B%\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\bR\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\t"},
   d2 = {"Lokhttp3/internal/connection/AddressPolicy;", "", "minimumConcurrentCalls", "", "backoffDelayMillis", "", "backoffJitterMillis", "<init>", "(IJI)V", "okhttp"}
)
public final class AddressPolicy {
   @JvmField
   public final int minimumConcurrentCalls;
   @JvmField
   public final long backoffDelayMillis;
   @JvmField
   public final int backoffJitterMillis;

   public AddressPolicy(int minimumConcurrentCalls, long backoffDelayMillis, int backoffJitterMillis) {
      this.minimumConcurrentCalls = minimumConcurrentCalls;
      this.backoffDelayMillis = backoffDelayMillis;
      this.backoffJitterMillis = backoffJitterMillis;
   }

   // $FF: synthetic method
   public AddressPolicy(int var1, long var2, int var4, int var5, DefaultConstructorMarker var6) {
      if ((var5 & 1) != 0) {
         var1 = 0;
      }

      if ((var5 & 2) != 0) {
         var2 = 60000L;
      }

      if ((var5 & 4) != 0) {
         var4 = 100;
      }

      this(var1, var2, var4);
   }

   public AddressPolicy() {
      this(0, 0L, 0, 7, (DefaultConstructorMarker)null);
   }
}
