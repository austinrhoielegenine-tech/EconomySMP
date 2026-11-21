package okhttp3.internal.connection;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0001\n\u0002\b\u0003\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\r\u001a\u00020\u0007H\u0016J\b\u0010\u000e\u001a\u00020\u0007H\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0010H\u0016J\b\u0010\u0012\u001a\u00020\u0010H\u0016R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\n\u001a\u00020\u000bX\u0096D¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\f¨\u0006\u0013"},
   d2 = {"Lokhttp3/internal/connection/FailedPlan;", "Lokhttp3/internal/connection/RoutePlanner$Plan;", "e", "", "<init>", "(Ljava/lang/Throwable;)V", "result", "Lokhttp3/internal/connection/RoutePlanner$ConnectResult;", "getResult", "()Lokhttp3/internal/connection/RoutePlanner$ConnectResult;", "isReady", "", "()Z", "connectTcp", "connectTlsEtc", "handleSuccess", "", "cancel", "retry", "okhttp"}
)
public final class FailedPlan implements RoutePlanner.Plan {
   @NotNull
   private final RoutePlanner.ConnectResult result;
   private final boolean isReady;

   public FailedPlan(@NotNull Throwable e) {
      Intrinsics.checkNotNullParameter(e, "e");
      super();
      this.result = new RoutePlanner.ConnectResult(this, (RoutePlanner.Plan)null, e, 2, (DefaultConstructorMarker)null);
   }

   @NotNull
   public final RoutePlanner.ConnectResult getResult() {
      return this.result;
   }

   public boolean isReady() {
      return this.isReady;
   }

   @NotNull
   public RoutePlanner.ConnectResult connectTcp() {
      return this.result;
   }

   @NotNull
   public RoutePlanner.ConnectResult connectTlsEtc() {
      return this.result;
   }

   @NotNull
   public Void handleSuccess() {
      throw new IllegalStateException("unexpected call".toString());
   }

   @NotNull
   public Void cancel() {
      throw new IllegalStateException("unexpected cancel".toString());
   }

   @NotNull
   public Void retry() {
      throw new IllegalStateException("unexpected retry".toString());
   }
}
