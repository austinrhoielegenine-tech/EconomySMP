package okhttp3.internal.connection;

import kotlin.Metadata;
import kotlin.collections.ArrayDeque;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Address;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\u0013\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0096\u0001J\t\u0010\f\u001a\u00020\tH\u0096\u0001J\u0011\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH\u0096\u0001R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0010\u001a\u00020\u0011X\u0096\u0005¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0018\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00070\u0015X\u0096\u0005¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017¨\u0006\u0018"},
   d2 = {"Lokhttp3/internal/connection/ForceConnectRoutePlanner;", "Lokhttp3/internal/connection/RoutePlanner;", "delegate", "Lokhttp3/internal/connection/RealRoutePlanner;", "<init>", "(Lokhttp3/internal/connection/RealRoutePlanner;)V", "plan", "Lokhttp3/internal/connection/RoutePlanner$Plan;", "hasNext", "", "failedConnection", "Lokhttp3/internal/connection/RealConnection;", "isCanceled", "sameHostAndPort", "url", "Lokhttp3/HttpUrl;", "address", "Lokhttp3/Address;", "getAddress", "()Lokhttp3/Address;", "deferredPlans", "Lkotlin/collections/ArrayDeque;", "getDeferredPlans", "()Lkotlin/collections/ArrayDeque;", "okhttp"}
)
public final class ForceConnectRoutePlanner implements RoutePlanner {
   @NotNull
   private final RealRoutePlanner delegate;

   public ForceConnectRoutePlanner(@NotNull RealRoutePlanner delegate) {
      Intrinsics.checkNotNullParameter(delegate, "delegate");
      super();
      this.delegate = delegate;
   }

   @NotNull
   public RoutePlanner.Plan plan() {
      return this.delegate.planConnect$okhttp();
   }

   @NotNull
   public Address getAddress() {
      return this.delegate.getAddress();
   }

   @NotNull
   public ArrayDeque<RoutePlanner.Plan> getDeferredPlans() {
      return this.delegate.getDeferredPlans();
   }

   public boolean isCanceled() {
      return this.delegate.isCanceled();
   }

   public boolean hasNext(@Nullable RealConnection failedConnection) {
      return this.delegate.hasNext(failedConnection);
   }

   public boolean sameHostAndPort(@NotNull HttpUrl url) {
      Intrinsics.checkNotNullParameter(url, "url");
      return this.delegate.sameHostAndPort(url);
   }
}
