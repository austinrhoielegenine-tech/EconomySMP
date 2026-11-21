package okhttp3.internal.connection;

import java.io.IOException;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\b\u001a\u00020\tH\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\n"},
   d2 = {"Lokhttp3/internal/connection/SequentialExchangeFinder;", "Lokhttp3/internal/connection/ExchangeFinder;", "routePlanner", "Lokhttp3/internal/connection/RoutePlanner;", "<init>", "(Lokhttp3/internal/connection/RoutePlanner;)V", "getRoutePlanner", "()Lokhttp3/internal/connection/RoutePlanner;", "find", "Lokhttp3/internal/connection/RealConnection;", "okhttp"}
)
public final class SequentialExchangeFinder implements ExchangeFinder {
   @NotNull
   private final RoutePlanner routePlanner;

   public SequentialExchangeFinder(@NotNull RoutePlanner routePlanner) {
      Intrinsics.checkNotNullParameter(routePlanner, "routePlanner");
      super();
      this.routePlanner = routePlanner;
   }

   @NotNull
   public RoutePlanner getRoutePlanner() {
      return this.routePlanner;
   }

   @NotNull
   public RealConnection find() {
      IOException firstException = null;

      while(!this.getRoutePlanner().isCanceled()) {
         try {
            RoutePlanner.Plan plan = this.getRoutePlanner().plan();
            if (!plan.isReady()) {
               RoutePlanner.ConnectResult tcpConnectResult = plan.connectTcp();
               RoutePlanner.ConnectResult connectResult = tcpConnectResult.isSuccess() ? plan.connectTlsEtc() : tcpConnectResult;
               RoutePlanner.Plan nextPlan = connectResult.component2();
               Throwable failure = connectResult.component3();
               if (failure != null) {
                  throw failure;
               }

               if (nextPlan != null) {
                  this.getRoutePlanner().getDeferredPlans().addFirst(nextPlan);
                  continue;
               }
            }

            return plan.handleSuccess();
         } catch (IOException e) {
            if (firstException == null) {
               firstException = e;
            } else {
               ExceptionsKt.addSuppressed((Throwable)firstException, (Throwable)e);
            }

            if (!RoutePlanner.hasNext$default(this.getRoutePlanner(), (RealConnection)null, 1, (Object)null)) {
               throw firstException;
            }
         }
      }

      throw new IOException("Canceled");
   }
}
