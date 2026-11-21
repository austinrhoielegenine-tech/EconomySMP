package okhttp3;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.internal._HostnamesCommonKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\rJ\r\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\b\u000eJ\r\u0010\u0006\u001a\u00020\u0007H\u0007¢\u0006\u0002\b\u000fJ\u0006\u0010\u0010\u001a\u00020\u0011J\u0013\u0010\u0012\u001a\u00020\u00112\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\b\u0010\u0016\u001a\u00020\u0017H\u0016R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\nR\u0013\u0010\u0004\u001a\u00020\u00058\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000bR\u0013\u0010\u0006\u001a\u00020\u00078\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\f¨\u0006\u0018"},
   d2 = {"Lokhttp3/Route;", "", "address", "Lokhttp3/Address;", "proxy", "Ljava/net/Proxy;", "socketAddress", "Ljava/net/InetSocketAddress;", "<init>", "(Lokhttp3/Address;Ljava/net/Proxy;Ljava/net/InetSocketAddress;)V", "()Lokhttp3/Address;", "()Ljava/net/Proxy;", "()Ljava/net/InetSocketAddress;", "-deprecated_address", "-deprecated_proxy", "-deprecated_socketAddress", "requiresTunnel", "", "equals", "other", "hashCode", "", "toString", "", "okhttp"}
)
public final class Route {
   @NotNull
   private final Address address;
   @NotNull
   private final Proxy proxy;
   @NotNull
   private final InetSocketAddress socketAddress;

   public Route(@NotNull Address address, @NotNull Proxy proxy, @NotNull InetSocketAddress socketAddress) {
      Intrinsics.checkNotNullParameter(address, "address");
      Intrinsics.checkNotNullParameter(proxy, "proxy");
      Intrinsics.checkNotNullParameter(socketAddress, "socketAddress");
      super();
      this.address = address;
      this.proxy = proxy;
      this.socketAddress = socketAddress;
   }

   @JvmName(
      name = "address"
   )
   @NotNull
   public final Address address() {
      return this.address;
   }

   @JvmName(
      name = "proxy"
   )
   @NotNull
   public final Proxy proxy() {
      return this.proxy;
   }

   @JvmName(
      name = "socketAddress"
   )
   @NotNull
   public final InetSocketAddress socketAddress() {
      return this.socketAddress;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "address",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_address"
   )
   @NotNull
   public final Address _deprecated_address/* $FF was: -deprecated_address*/() {
      return this.address;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "proxy",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_proxy"
   )
   @NotNull
   public final Proxy _deprecated_proxy/* $FF was: -deprecated_proxy*/() {
      return this.proxy;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "socketAddress",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_socketAddress"
   )
   @NotNull
   public final InetSocketAddress _deprecated_socketAddress/* $FF was: -deprecated_socketAddress*/() {
      return this.socketAddress;
   }

   public final boolean requiresTunnel() {
      if (this.proxy.type() != Type.HTTP) {
         return false;
      } else {
         return this.address.sslSocketFactory() != null || this.address.protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE);
      }
   }

   public boolean equals(@Nullable Object other) {
      return other instanceof Route && Intrinsics.areEqual((Object)((Route)other).address, (Object)this.address) && Intrinsics.areEqual((Object)((Route)other).proxy, (Object)this.proxy) && Intrinsics.areEqual((Object)((Route)other).socketAddress, (Object)this.socketAddress);
   }

   public int hashCode() {
      int result = 17;
      result = 31 * result + this.address.hashCode();
      result = 31 * result + this.proxy.hashCode();
      result = 31 * result + this.socketAddress.hashCode();
      return result;
   }

   @NotNull
   public String toString() {
      StringBuilder var1;
      String addressHostname;
      String var7;
      label42: {
         var1 = new StringBuilder();
         int var3 = 0;
         addressHostname = this.address.url().host();
         InetAddress var10000 = this.socketAddress.getAddress();
         if (var10000 != null) {
            String var6 = var10000.getHostAddress();
            if (var6 != null) {
               var7 = _HostnamesCommonKt.toCanonicalHost(var6);
               break label42;
            }
         }

         var7 = null;
      }

      String socketHostname = var7;
      if (StringsKt.contains$default((CharSequence)addressHostname, ':', false, 2, (Object)null)) {
         var1.append("[").append(addressHostname).append("]");
      } else {
         var1.append(addressHostname);
      }

      if (this.address.url().port() != this.socketAddress.getPort() || Intrinsics.areEqual((Object)addressHostname, (Object)socketHostname)) {
         var1.append(":");
         var1.append(this.address.url().port());
      }

      if (!Intrinsics.areEqual((Object)addressHostname, (Object)socketHostname)) {
         if (Intrinsics.areEqual((Object)this.proxy, (Object)Proxy.NO_PROXY)) {
            var1.append(" at ");
         } else {
            var1.append(" via proxy ");
         }

         if (socketHostname == null) {
            var1.append("<unresolved>");
         } else if (StringsKt.contains$default((CharSequence)socketHostname, ':', false, 2, (Object)null)) {
            var1.append("[").append(socketHostname).append("]");
         } else {
            var1.append(socketHostname);
         }

         var1.append(":");
         var1.append(this.socketAddress.getPort());
      }

      return var1.toString();
   }
}
