package okhttp3.internal.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Address;
import okhttp3.HttpUrl;
import okhttp3.Route;
import okhttp3.internal._HostnamesCommonKt;
import okhttp3.internal._UtilJvmKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \"2\u00020\u0001:\u0002!\"B)\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0016\u001a\u00020\tH\u0086\u0002J\t\u0010\u0017\u001a\u00020\u0018H\u0086\u0002J\u001a\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u000eH\u0002J\b\u0010\u001e\u001a\u00020\tH\u0002J\b\u0010\u001f\u001a\u00020\u000eH\u0002J\u0010\u0010 \u001a\u00020\u001a2\u0006\u0010\u001d\u001a\u00020\u000eH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006#"},
   d2 = {"Lokhttp3/internal/connection/RouteSelector;", "", "address", "Lokhttp3/Address;", "routeDatabase", "Lokhttp3/internal/connection/RouteDatabase;", "call", "Lokhttp3/internal/connection/RealCall;", "fastFallback", "", "<init>", "(Lokhttp3/Address;Lokhttp3/internal/connection/RouteDatabase;Lokhttp3/internal/connection/RealCall;Z)V", "proxies", "", "Ljava/net/Proxy;", "nextProxyIndex", "", "inetSocketAddresses", "Ljava/net/InetSocketAddress;", "postponedRoutes", "", "Lokhttp3/Route;", "hasNext", "next", "Lokhttp3/internal/connection/RouteSelector$Selection;", "resetNextProxy", "", "url", "Lokhttp3/HttpUrl;", "proxy", "hasNextProxy", "nextProxy", "resetNextInetSocketAddress", "Selection", "Companion", "okhttp"}
)
public final class RouteSelector {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Address address;
   @NotNull
   private final RouteDatabase routeDatabase;
   @NotNull
   private final RealCall call;
   private final boolean fastFallback;
   @NotNull
   private List<? extends Proxy> proxies;
   private int nextProxyIndex;
   @NotNull
   private List<? extends InetSocketAddress> inetSocketAddresses;
   @NotNull
   private final List<Route> postponedRoutes;

   public RouteSelector(@NotNull Address address, @NotNull RouteDatabase routeDatabase, @NotNull RealCall call, boolean fastFallback) {
      Intrinsics.checkNotNullParameter(address, "address");
      Intrinsics.checkNotNullParameter(routeDatabase, "routeDatabase");
      Intrinsics.checkNotNullParameter(call, "call");
      super();
      this.address = address;
      this.routeDatabase = routeDatabase;
      this.call = call;
      this.fastFallback = fastFallback;
      this.proxies = CollectionsKt.emptyList();
      this.inetSocketAddresses = CollectionsKt.emptyList();
      this.postponedRoutes = (List)(new ArrayList());
      this.resetNextProxy(this.address.url(), this.address.proxy());
   }

   public final boolean hasNext() {
      return this.hasNextProxy() || !((Collection)this.postponedRoutes).isEmpty();
   }

   @NotNull
   public final Selection next() throws IOException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         List routes = (List)(new ArrayList());

         while(this.hasNextProxy()) {
            Proxy proxy = this.nextProxy();

            for(InetSocketAddress inetSocketAddress : this.inetSocketAddresses) {
               Route route = new Route(this.address, proxy, inetSocketAddress);
               if (this.routeDatabase.shouldPostpone(route)) {
                  ((Collection)this.postponedRoutes).add(route);
               } else {
                  ((Collection)routes).add(route);
               }
            }

            if (!((Collection)routes).isEmpty()) {
               break;
            }
         }

         if (routes.isEmpty()) {
            CollectionsKt.addAll((Collection)routes, (Iterable)this.postponedRoutes);
            this.postponedRoutes.clear();
         }

         return new Selection(routes);
      }
   }

   private final void resetNextProxy(HttpUrl url, Proxy proxy) {
      this.call.getEventListener$okhttp().proxySelectStart(this.call, url);
      this.proxies = resetNextProxy$selectProxies(proxy, url, this);
      this.nextProxyIndex = 0;
      this.call.getEventListener$okhttp().proxySelectEnd(this.call, url, this.proxies);
   }

   private final boolean hasNextProxy() {
      return this.nextProxyIndex < this.proxies.size();
   }

   private final Proxy nextProxy() throws IOException {
      if (!this.hasNextProxy()) {
         throw new SocketException("No route to " + this.address.url().host() + "; exhausted proxy configurations: " + this.proxies);
      } else {
         List var10000 = this.proxies;
         int var2 = this.nextProxyIndex++;
         Proxy result = (Proxy)var10000.get(var2);
         this.resetNextInetSocketAddress(result);
         return result;
      }
   }

   private final void resetNextInetSocketAddress(Proxy proxy) throws IOException {
      List mutableInetSocketAddresses = (List)(new ArrayList());
      this.inetSocketAddresses = mutableInetSocketAddresses;
      String socketHost = null;
      int socketPort = 0;
      if (proxy.type() != Type.DIRECT && proxy.type() != Type.SOCKS) {
         SocketAddress proxyAddress = proxy.address();
         if (!(proxyAddress instanceof InetSocketAddress)) {
            int var13 = 0;
            String var14 = "Proxy.address() is not an InetSocketAddress: " + proxyAddress.getClass();
            throw new IllegalArgumentException(var14.toString());
         }

         socketHost = Companion.getSocketHost((InetSocketAddress)proxyAddress);
         socketPort = ((InetSocketAddress)proxyAddress).getPort();
      } else {
         socketHost = this.address.url().host();
         socketPort = this.address.url().port();
      }

      if (!(1 <= socketPort ? socketPort < 65536 : false)) {
         throw new SocketException("No route to " + socketHost + ':' + socketPort + "; port is out of range");
      } else {
         if (proxy.type() == Type.SOCKS) {
            ((Collection)mutableInetSocketAddresses).add(InetSocketAddress.createUnresolved(socketHost, socketPort));
         } else {
            List var10000;
            if (_HostnamesCommonKt.canParseAsIpAddress(socketHost)) {
               var10000 = CollectionsKt.listOf(InetAddress.getByName(socketHost));
            } else {
               this.call.getEventListener$okhttp().dnsStart(this.call, socketHost);
               List result = this.address.dns().lookup(socketHost);
               if (result.isEmpty()) {
                  throw new UnknownHostException(this.address.dns() + " returned no addresses for " + socketHost);
               }

               this.call.getEventListener$okhttp().dnsEnd(this.call, socketHost, result);
               var10000 = result;
            }

            List addresses = var10000;

            for(InetAddress inetAddress : this.fastFallback ? InetAddressOrderKt.reorderForHappyEyeballs(addresses) : addresses) {
               ((Collection)mutableInetSocketAddresses).add(new InetSocketAddress(inetAddress, socketPort));
            }
         }

      }
   }

   private static final List<Proxy> resetNextProxy$selectProxies(Proxy $proxy, HttpUrl $url, RouteSelector this$0) {
      if ($proxy != null) {
         return CollectionsKt.listOf($proxy);
      } else {
         URI uri = $url.uri();
         if (uri.getHost() == null) {
            Proxy[] var6 = new Proxy[]{Proxy.NO_PROXY};
            return _UtilJvmKt.<Proxy>immutableListOf(var6);
         } else {
            List proxiesOrNull = this$0.address.proxySelector().select(uri);
            Collection var5 = (Collection)proxiesOrNull;
            if (var5 == null || var5.isEmpty()) {
               Proxy[] var7 = new Proxy[]{Proxy.NO_PROXY};
               return _UtilJvmKt.<Proxy>immutableListOf(var7);
            } else {
               return _UtilJvmKt.toImmutableList(proxiesOrNull);
            }
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\u000b\u001a\u00020\fH\u0086\u0002J\t\u0010\r\u001a\u00020\u0004H\u0086\u0002R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000e"},
      d2 = {"Lokhttp3/internal/connection/RouteSelector$Selection;", "", "routes", "", "Lokhttp3/Route;", "<init>", "(Ljava/util/List;)V", "getRoutes", "()Ljava/util/List;", "nextRouteIndex", "", "hasNext", "", "next", "okhttp"}
   )
   public static final class Selection {
      @NotNull
      private final List<Route> routes;
      private int nextRouteIndex;

      public Selection(@NotNull List<Route> routes) {
         Intrinsics.checkNotNullParameter(routes, "routes");
         super();
         this.routes = routes;
      }

      @NotNull
      public final List<Route> getRoutes() {
         return this.routes;
      }

      public final boolean hasNext() {
         return this.nextRouteIndex < this.routes.size();
      }

      @NotNull
      public final Route next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            List var10000 = this.routes;
            int var1 = this.nextRouteIndex++;
            return (Route)var10000.get(var1);
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0015\u0010\u0004\u001a\u00020\u0005*\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b¨\u0006\t"},
      d2 = {"Lokhttp3/internal/connection/RouteSelector$Companion;", "", "<init>", "()V", "socketHost", "", "Ljava/net/InetSocketAddress;", "getSocketHost", "(Ljava/net/InetSocketAddress;)Ljava/lang/String;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final String getSocketHost(@NotNull InetSocketAddress $this$socketHost) {
         Intrinsics.checkNotNullParameter($this$socketHost, "<this>");
         InetAddress var10000 = $this$socketHost.getAddress();
         if (var10000 == null) {
            String var4 = $this$socketHost.getHostName();
            Intrinsics.checkNotNullExpressionValue(var4, "getHostName(...)");
            return var4;
         } else {
            InetAddress address = var10000;
            String var3 = address.getHostAddress();
            Intrinsics.checkNotNullExpressionValue(var3, "getHostAddress(...)");
            return var3;
         }
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
