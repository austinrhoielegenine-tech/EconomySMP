package okhttp3.internal.connection;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealInterceptorChain;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"},
   d2 = {"Lokhttp3/internal/connection/ConnectInterceptor;", "Lokhttp3/Interceptor;", "<init>", "()V", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "okhttp"}
)
public final class ConnectInterceptor implements Interceptor {
   @NotNull
   public static final ConnectInterceptor INSTANCE = new ConnectInterceptor();

   private ConnectInterceptor() {
   }

   @NotNull
   public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
      Intrinsics.checkNotNullParameter(chain, "chain");
      RealInterceptorChain realChain = (RealInterceptorChain)chain;
      Exchange exchange = realChain.getCall$okhttp().initExchange$okhttp(realChain);
      RealInterceptorChain connectedChain = RealInterceptorChain.copy$okhttp$default(realChain, 0, exchange, (Request)null, 0, 0, 0, 61, (Object)null);
      return connectedChain.proceed(realChain.getRequest$okhttp());
   }
}
