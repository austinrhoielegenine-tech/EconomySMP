package okhttp3.internal.connection;

import java.io.IOException;
import java.net.UnknownServiceException;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.ArrayDeque;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Address;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001Bq\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\r\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0012\u0012\u0006\u0010\u0013\u001a\u00020\u0014\u0012\u0006\u0010\u0015\u001a\u00020\u0016¢\u0006\u0004\b\u0017\u0010\u0018J\b\u0010'\u001a\u00020\rH\u0016J\b\u0010(\u001a\u00020$H\u0016J\n\u0010)\u001a\u0004\u0018\u00010*H\u0002J\r\u0010+\u001a\u00020,H\u0000¢\u0006\u0002\b-J-\u0010.\u001a\u0004\u0018\u00010*2\n\b\u0002\u0010/\u001a\u0004\u0018\u00010,2\u0010\b\u0002\u00100\u001a\n\u0012\u0004\u0012\u00020!\u0018\u000101H\u0000¢\u0006\u0002\b2J'\u00103\u001a\u00020,2\u0006\u00104\u001a\u00020!2\u0010\b\u0002\u00100\u001a\n\u0012\u0004\u0012\u00020!\u0018\u000101H\u0000¢\u0006\u0002\b5J\u0010\u00106\u001a\u00020\u00162\u0006\u00104\u001a\u00020!H\u0002J\u0012\u00107\u001a\u00020\r2\b\u00108\u001a\u0004\u0018\u000109H\u0016J\u0012\u0010:\u001a\u0004\u0018\u00010!2\u0006\u0010;\u001a\u000209H\u0002J\u0010\u0010<\u001a\u00020\r2\u0006\u0010=\u001a\u00020>H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\u00020\u0010X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u001fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\"\u001a\b\u0012\u0004\u0012\u00020$0#X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b%\u0010&¨\u0006?"},
   d2 = {"Lokhttp3/internal/connection/RealRoutePlanner;", "Lokhttp3/internal/connection/RoutePlanner;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "connectionPool", "Lokhttp3/internal/connection/RealConnectionPool;", "readTimeoutMillis", "", "writeTimeoutMillis", "socketConnectTimeoutMillis", "socketReadTimeoutMillis", "pingIntervalMillis", "retryOnConnectionFailure", "", "fastFallback", "address", "Lokhttp3/Address;", "routeDatabase", "Lokhttp3/internal/connection/RouteDatabase;", "call", "Lokhttp3/internal/connection/RealCall;", "request", "Lokhttp3/Request;", "<init>", "(Lokhttp3/internal/concurrent/TaskRunner;Lokhttp3/internal/connection/RealConnectionPool;IIIIIZZLokhttp3/Address;Lokhttp3/internal/connection/RouteDatabase;Lokhttp3/internal/connection/RealCall;Lokhttp3/Request;)V", "getAddress", "()Lokhttp3/Address;", "doExtensiveHealthChecks", "routeSelection", "Lokhttp3/internal/connection/RouteSelector$Selection;", "routeSelector", "Lokhttp3/internal/connection/RouteSelector;", "nextRouteToTry", "Lokhttp3/Route;", "deferredPlans", "Lkotlin/collections/ArrayDeque;", "Lokhttp3/internal/connection/RoutePlanner$Plan;", "getDeferredPlans", "()Lkotlin/collections/ArrayDeque;", "isCanceled", "plan", "planReuseCallConnection", "Lokhttp3/internal/connection/ReusePlan;", "planConnect", "Lokhttp3/internal/connection/ConnectPlan;", "planConnect$okhttp", "planReusePooledConnection", "planToReplace", "routes", "", "planReusePooledConnection$okhttp", "planConnectToRoute", "route", "planConnectToRoute$okhttp", "createTunnelRequest", "hasNext", "failedConnection", "Lokhttp3/internal/connection/RealConnection;", "retryRoute", "connection", "sameHostAndPort", "url", "Lokhttp3/HttpUrl;", "okhttp"}
)
@SourceDebugExtension({"SMAP\nRealRoutePlanner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealRoutePlanner.kt\nokhttp3/internal/connection/RealRoutePlanner\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,348:1\n63#2:349\n63#2:350\n*S KotlinDebug\n*F\n+ 1 RealRoutePlanner.kt\nokhttp3/internal/connection/RealRoutePlanner\n*L\n100#1:349\n327#1:350\n*E\n"})
public final class RealRoutePlanner implements RoutePlanner {
   @NotNull
   private final TaskRunner taskRunner;
   @NotNull
   private final RealConnectionPool connectionPool;
   private final int readTimeoutMillis;
   private final int writeTimeoutMillis;
   private final int socketConnectTimeoutMillis;
   private final int socketReadTimeoutMillis;
   private final int pingIntervalMillis;
   private final boolean retryOnConnectionFailure;
   private final boolean fastFallback;
   @NotNull
   private final Address address;
   @NotNull
   private final RouteDatabase routeDatabase;
   @NotNull
   private final RealCall call;
   private final boolean doExtensiveHealthChecks;
   @Nullable
   private RouteSelector.Selection routeSelection;
   @Nullable
   private RouteSelector routeSelector;
   @Nullable
   private Route nextRouteToTry;
   @NotNull
   private final ArrayDeque<RoutePlanner.Plan> deferredPlans;

   public RealRoutePlanner(@NotNull TaskRunner taskRunner, @NotNull RealConnectionPool connectionPool, int readTimeoutMillis, int writeTimeoutMillis, int socketConnectTimeoutMillis, int socketReadTimeoutMillis, int pingIntervalMillis, boolean retryOnConnectionFailure, boolean fastFallback, @NotNull Address address, @NotNull RouteDatabase routeDatabase, @NotNull RealCall call, @NotNull Request request) {
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      Intrinsics.checkNotNullParameter(connectionPool, "connectionPool");
      Intrinsics.checkNotNullParameter(address, "address");
      Intrinsics.checkNotNullParameter(routeDatabase, "routeDatabase");
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(request, "request");
      super();
      this.taskRunner = taskRunner;
      this.connectionPool = connectionPool;
      this.readTimeoutMillis = readTimeoutMillis;
      this.writeTimeoutMillis = writeTimeoutMillis;
      this.socketConnectTimeoutMillis = socketConnectTimeoutMillis;
      this.socketReadTimeoutMillis = socketReadTimeoutMillis;
      this.pingIntervalMillis = pingIntervalMillis;
      this.retryOnConnectionFailure = retryOnConnectionFailure;
      this.fastFallback = fastFallback;
      this.address = address;
      this.routeDatabase = routeDatabase;
      this.call = call;
      this.doExtensiveHealthChecks = !Intrinsics.areEqual((Object)request.method(), (Object)"GET");
      this.deferredPlans = new ArrayDeque<RoutePlanner.Plan>();
   }

   @NotNull
   public Address getAddress() {
      return this.address;
   }

   @NotNull
   public ArrayDeque<RoutePlanner.Plan> getDeferredPlans() {
      return this.deferredPlans;
   }

   public boolean isCanceled() {
      return this.call.isCanceled();
   }

   @NotNull
   public RoutePlanner.Plan plan() throws IOException {
      ReusePlan reuseCallConnection = this.planReuseCallConnection();
      if (reuseCallConnection != null) {
         return reuseCallConnection;
      } else {
         ReusePlan pooled1 = planReusePooledConnection$okhttp$default(this, (ConnectPlan)null, (List)null, 3, (Object)null);
         if (pooled1 != null) {
            return pooled1;
         } else if (!((Collection)this.getDeferredPlans()).isEmpty()) {
            return (RoutePlanner.Plan)this.getDeferredPlans().removeFirst();
         } else {
            ConnectPlan connect = this.planConnect$okhttp();
            ReusePlan pooled2 = this.planReusePooledConnection$okhttp(connect, connect.getRoutes$okhttp());
            return pooled2 != null ? (RoutePlanner.Plan)pooled2 : (RoutePlanner.Plan)connect;
         }
      }
   }

   private final ReusePlan planReuseCallConnection() {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public final ConnectPlan planConnect$okhttp() throws IOException {
      Route localNextRouteToTry = this.nextRouteToTry;
      if (localNextRouteToTry != null) {
         this.nextRouteToTry = null;
         return planConnectToRoute$okhttp$default(this, localNextRouteToTry, (List)null, 2, (Object)null);
      } else {
         RouteSelector.Selection existingRouteSelection = this.routeSelection;
         if (existingRouteSelection != null && existingRouteSelection.hasNext()) {
            return planConnectToRoute$okhttp$default(this, existingRouteSelection.next(), (List)null, 2, (Object)null);
         } else {
            RouteSelector newRouteSelector = this.routeSelector;
            if (newRouteSelector == null) {
               newRouteSelector = new RouteSelector(this.getAddress(), this.routeDatabase, this.call, this.fastFallback);
               this.routeSelector = newRouteSelector;
            }

            if (!newRouteSelector.hasNext()) {
               throw new IOException("exhausted all routes");
            } else {
               RouteSelector.Selection newRouteSelection = newRouteSelector.next();
               this.routeSelection = newRouteSelection;
               if (this.isCanceled()) {
                  throw new IOException("Canceled");
               } else {
                  return this.planConnectToRoute$okhttp(newRouteSelection.next(), newRouteSelection.getRoutes());
               }
            }
         }
      }
   }

   @Nullable
   public final ReusePlan planReusePooledConnection$okhttp(@Nullable ConnectPlan planToReplace, @Nullable List<Route> routes) {
      RealConnection var10000 = this.connectionPool.callAcquirePooledConnection$okhttp(this.doExtensiveHealthChecks, this.getAddress(), this.call, routes, planToReplace != null && planToReplace.isReady());
      if (var10000 == null) {
         return null;
      } else {
         RealConnection result = var10000;
         if (planToReplace != null) {
            this.nextRouteToTry = planToReplace.getRoute();
            planToReplace.closeQuietly();
         }

         this.call.getEventListener$okhttp().connectionAcquired(this.call, result);
         result.getConnectionListener$okhttp().connectionAcquired(result, this.call);
         return new ReusePlan(result);
      }
   }

   // $FF: synthetic method
   public static ReusePlan planReusePooledConnection$okhttp$default(RealRoutePlanner var0, ConnectPlan var1, List var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = null;
      }

      if ((var3 & 2) != 0) {
         var2 = null;
      }

      return var0.planReusePooledConnection$okhttp(var1, var2);
   }

   @NotNull
   public final ConnectPlan planConnectToRoute$okhttp(@NotNull Route route, @Nullable List<Route> routes) throws IOException {
      Intrinsics.checkNotNullParameter(route, "route");
      if (route.address().sslSocketFactory() == null) {
         if (!route.address().connectionSpecs().contains(ConnectionSpec.CLEARTEXT)) {
            throw new UnknownServiceException("CLEARTEXT communication not enabled for client");
         }

         String host = route.address().url().host();
         if (!Platform.Companion.get().isCleartextTrafficPermitted(host)) {
            throw new UnknownServiceException("CLEARTEXT communication to " + host + " not permitted by network security policy");
         }
      } else if (route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
         throw new UnknownServiceException("H2_PRIOR_KNOWLEDGE cannot be used with HTTPS");
      }

      Request tunnelRequest = route.requiresTunnel() ? this.createTunnelRequest(route) : null;
      return new ConnectPlan(this.taskRunner, this.connectionPool, this.readTimeoutMillis, this.writeTimeoutMillis, this.socketConnectTimeoutMillis, this.socketReadTimeoutMillis, this.pingIntervalMillis, this.retryOnConnectionFailure, this.call, this, route, routes, 0, tunnelRequest, -1, false);
   }

   // $FF: synthetic method
   public static ConnectPlan planConnectToRoute$okhttp$default(RealRoutePlanner var0, Route var1, List var2, int var3, Object var4) throws IOException {
      if ((var3 & 2) != 0) {
         var2 = null;
      }

      return var0.planConnectToRoute$okhttp(var1, var2);
   }

   private final Request createTunnelRequest(Route route) throws IOException {
      Request proxyConnectRequest = (new Request.Builder()).url(route.address().url()).method("CONNECT", (RequestBody)null).header("Host", _UtilJvmKt.toHostHeader(route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", "okhttp/5.3.2").build();
      Response fakeAuthChallengeResponse = (new Response.Builder()).request(proxyConnectRequest).protocol(Protocol.HTTP_1_1).code(407).message("Preemptive Authenticate").sentRequestAtMillis(-1L).receivedResponseAtMillis(-1L).header("Proxy-Authenticate", "OkHttp-Preemptive").build();
      Request authenticatedRequest = route.address().proxyAuthenticator().authenticate(route, fakeAuthChallengeResponse);
      Request var10000 = authenticatedRequest;
      if (authenticatedRequest == null) {
         var10000 = proxyConnectRequest;
      }

      return var10000;
   }

   public boolean hasNext(@Nullable RealConnection failedConnection) {
      if (!((Collection)this.getDeferredPlans()).isEmpty()) {
         return true;
      } else if (this.nextRouteToTry != null) {
         return true;
      } else {
         if (failedConnection != null) {
            Route retryRoute = this.retryRoute(failedConnection);
            if (retryRoute != null) {
               this.nextRouteToTry = retryRoute;
               return true;
            }
         }

         RouteSelector.Selection var10000 = this.routeSelection;
         if (var10000 != null ? var10000.hasNext() : false) {
            return true;
         } else {
            RouteSelector var4 = this.routeSelector;
            if (var4 == null) {
               return true;
            } else {
               RouteSelector localRouteSelector = var4;
               return localRouteSelector.hasNext();
            }
         }
      }
   }

   private final Route retryRoute(RealConnection param1) {
      // $FF: Couldn't be decompiled
   }

   public boolean sameHostAndPort(@NotNull HttpUrl url) {
      Intrinsics.checkNotNullParameter(url, "url");
      HttpUrl routeUrl = this.getAddress().url();
      return url.port() == routeUrl.port() && Intrinsics.areEqual((Object)url.host(), (Object)routeUrl.host());
   }
}
