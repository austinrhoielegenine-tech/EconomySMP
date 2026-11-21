package okhttp3.internal.connection;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.collections.ArrayDeque;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Address;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001:\u0002\u0014\u0015J\b\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\bH&J\u0014\u0010\u000e\u001a\u00020\f2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0010H&J\u0010\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u0013H&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0018\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\n¨\u0006\u0016À\u0006\u0003"},
   d2 = {"Lokhttp3/internal/connection/RoutePlanner;", "", "address", "Lokhttp3/Address;", "getAddress", "()Lokhttp3/Address;", "deferredPlans", "Lkotlin/collections/ArrayDeque;", "Lokhttp3/internal/connection/RoutePlanner$Plan;", "getDeferredPlans", "()Lkotlin/collections/ArrayDeque;", "isCanceled", "", "plan", "hasNext", "failedConnection", "Lokhttp3/internal/connection/RealConnection;", "sameHostAndPort", "url", "Lokhttp3/HttpUrl;", "Plan", "ConnectResult", "okhttp"}
)
public interface RoutePlanner {
   @NotNull
   Address getAddress();

   @NotNull
   ArrayDeque<Plan> getDeferredPlans();

   boolean isCanceled();

   @NotNull
   Plan plan() throws IOException;

   boolean hasNext(@Nullable RealConnection var1);

   // $FF: synthetic method
   static boolean hasNext$default(RoutePlanner var0, RealConnection var1, int var2, Object var3) {
      if (var3 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: hasNext");
      } else {
         if ((var2 & 1) != 0) {
            var1 = null;
         }

         return var0.hasNext(var1);
      }
   }

   boolean sameHostAndPort(@NotNull HttpUrl var1);

   @Metadata(
      mv = {2, 2, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      // $FF: synthetic method
      public static boolean hasNext$default(RoutePlanner var0, RealConnection var1, int var2, Object var3) {
         return RoutePlanner.hasNext$default(var0, var1, var2, var3);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0006HÆ\u0003J+\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006HÆ\u0001J\u0013\u0010\u0015\u001a\u00020\u000f2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0018HÖ\u0001J\t\u0010\u0019\u001a\u00020\u001aHÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000f8F¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u0010¨\u0006\u001b"},
      d2 = {"Lokhttp3/internal/connection/RoutePlanner$ConnectResult;", "", "plan", "Lokhttp3/internal/connection/RoutePlanner$Plan;", "nextPlan", "throwable", "", "<init>", "(Lokhttp3/internal/connection/RoutePlanner$Plan;Lokhttp3/internal/connection/RoutePlanner$Plan;Ljava/lang/Throwable;)V", "getPlan", "()Lokhttp3/internal/connection/RoutePlanner$Plan;", "getNextPlan", "getThrowable", "()Ljava/lang/Throwable;", "isSuccess", "", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "", "okhttp"}
   )
   public static final class ConnectResult {
      @NotNull
      private final Plan plan;
      @Nullable
      private final Plan nextPlan;
      @Nullable
      private final Throwable throwable;

      public ConnectResult(@NotNull Plan plan, @Nullable Plan nextPlan, @Nullable Throwable throwable) {
         Intrinsics.checkNotNullParameter(plan, "plan");
         super();
         this.plan = plan;
         this.nextPlan = nextPlan;
         this.throwable = throwable;
      }

      // $FF: synthetic method
      public ConnectResult(Plan var1, Plan var2, Throwable var3, int var4, DefaultConstructorMarker var5) {
         if ((var4 & 2) != 0) {
            var2 = null;
         }

         if ((var4 & 4) != 0) {
            var3 = null;
         }

         this(var1, var2, var3);
      }

      @NotNull
      public final Plan getPlan() {
         return this.plan;
      }

      @Nullable
      public final Plan getNextPlan() {
         return this.nextPlan;
      }

      @Nullable
      public final Throwable getThrowable() {
         return this.throwable;
      }

      public final boolean isSuccess() {
         return this.nextPlan == null && this.throwable == null;
      }

      @NotNull
      public final Plan component1() {
         return this.plan;
      }

      @Nullable
      public final Plan component2() {
         return this.nextPlan;
      }

      @Nullable
      public final Throwable component3() {
         return this.throwable;
      }

      @NotNull
      public final ConnectResult copy(@NotNull Plan plan, @Nullable Plan nextPlan, @Nullable Throwable throwable) {
         Intrinsics.checkNotNullParameter(plan, "plan");
         return new ConnectResult(plan, nextPlan, throwable);
      }

      // $FF: synthetic method
      public static ConnectResult copy$default(ConnectResult var0, Plan var1, Plan var2, Throwable var3, int var4, Object var5) {
         if ((var4 & 1) != 0) {
            var1 = var0.plan;
         }

         if ((var4 & 2) != 0) {
            var2 = var0.nextPlan;
         }

         if ((var4 & 4) != 0) {
            var3 = var0.throwable;
         }

         return var0.copy(var1, var2, var3);
      }

      @NotNull
      public String toString() {
         return "ConnectResult(plan=" + this.plan + ", nextPlan=" + this.nextPlan + ", throwable=" + this.throwable + ')';
      }

      public int hashCode() {
         int result = this.plan.hashCode();
         result = result * 31 + (this.nextPlan == null ? 0 : this.nextPlan.hashCode());
         result = result * 31 + (this.throwable == null ? 0 : this.throwable.hashCode());
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof ConnectResult)) {
            return false;
         } else {
            ConnectResult var2 = (ConnectResult)other;
            if (!Intrinsics.areEqual((Object)this.plan, (Object)var2.plan)) {
               return false;
            } else if (!Intrinsics.areEqual((Object)this.nextPlan, (Object)var2.nextPlan)) {
               return false;
            } else {
               return Intrinsics.areEqual((Object)this.throwable, (Object)var2.throwable);
            }
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\u0006H&J\b\u0010\b\u001a\u00020\tH&J\b\u0010\n\u001a\u00020\u000bH&J\n\u0010\f\u001a\u0004\u0018\u00010\u0000H&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0002\u0010\u0004¨\u0006\rÀ\u0006\u0003"},
      d2 = {"Lokhttp3/internal/connection/RoutePlanner$Plan;", "", "isReady", "", "()Z", "connectTcp", "Lokhttp3/internal/connection/RoutePlanner$ConnectResult;", "connectTlsEtc", "handleSuccess", "Lokhttp3/internal/connection/RealConnection;", "cancel", "", "retry", "okhttp"}
   )
   public interface Plan {
      boolean isReady();

      @NotNull
      ConnectResult connectTcp();

      @NotNull
      ConnectResult connectTlsEtc();

      @NotNull
      RealConnection handleSuccess();

      void cancel();

      @Nullable
      Plan retry();
   }
}
