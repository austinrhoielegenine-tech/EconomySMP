package okhttp3.internal.authenticator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.Authenticator.RequestorType;
import java.net.Proxy.Type;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Address;
import okhttp3.Authenticator;
import okhttp3.Challenge;
import okhttp3.Credentials;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u001c\u0010\f\u001a\u00020\r*\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0003H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"},
   d2 = {"Lokhttp3/internal/authenticator/JavaNetAuthenticator;", "Lokhttp3/Authenticator;", "defaultDns", "Lokhttp3/Dns;", "<init>", "(Lokhttp3/Dns;)V", "authenticate", "Lokhttp3/Request;", "route", "Lokhttp3/Route;", "response", "Lokhttp3/Response;", "connectToInetAddress", "Ljava/net/InetAddress;", "Ljava/net/Proxy;", "url", "Lokhttp3/HttpUrl;", "dns", "okhttp"}
)
public final class JavaNetAuthenticator implements Authenticator {
   @NotNull
   private final Dns defaultDns;

   public JavaNetAuthenticator(@NotNull Dns defaultDns) {
      Intrinsics.checkNotNullParameter(defaultDns, "defaultDns");
      super();
      this.defaultDns = defaultDns;
   }

   // $FF: synthetic method
   public JavaNetAuthenticator(Dns var1, int var2, DefaultConstructorMarker var3) {
      if ((var2 & 1) != 0) {
         var1 = Dns.SYSTEM;
      }

      this(var1);
   }

   @Nullable
   public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
      List challenges;
      Request request;
      HttpUrl url;
      boolean proxyAuthorization;
      Proxy var10000;
      label48: {
         Intrinsics.checkNotNullParameter(response, "response");
         challenges = response.challenges();
         request = response.request();
         url = request.url();
         proxyAuthorization = response.code() == 407;
         if (route != null) {
            var10000 = route.proxy();
            if (var10000 != null) {
               break label48;
            }
         }

         var10000 = Proxy.NO_PROXY;
      }

      Proxy proxy = var10000;

      for(Challenge challenge : challenges) {
         if (StringsKt.equals("Basic", challenge.scheme(), true)) {
            label39: {
               if (route != null) {
                  Address var16 = route.address();
                  if (var16 != null) {
                     var17 = var16.dns();
                     if (var17 != null) {
                        break label39;
                     }
                  }
               }

               var17 = this.defaultDns;
            }

            Dns dns = var17;
            PasswordAuthentication var20;
            if (proxyAuthorization) {
               SocketAddress var18 = proxy.address();
               Intrinsics.checkNotNull(var18, "null cannot be cast to non-null type java.net.InetSocketAddress");
               InetSocketAddress proxyAddress = (InetSocketAddress)var18;
               String var19 = proxyAddress.getHostName();
               Intrinsics.checkNotNull(proxy);
               var20 = java.net.Authenticator.requestPasswordAuthentication(var19, this.connectToInetAddress(proxy, url, dns), proxyAddress.getPort(), url.scheme(), challenge.realm(), challenge.scheme(), url.url(), RequestorType.PROXY);
            } else {
               String var21 = url.host();
               Intrinsics.checkNotNull(proxy);
               var20 = java.net.Authenticator.requestPasswordAuthentication(var21, this.connectToInetAddress(proxy, url, dns), url.port(), url.scheme(), challenge.realm(), challenge.scheme(), url.url(), RequestorType.SERVER);
            }

            PasswordAuthentication auth = var20;
            if (auth != null) {
               String credentialHeader = proxyAuthorization ? "Proxy-Authorization" : "Authorization";
               String var22 = auth.getUserName();
               Intrinsics.checkNotNullExpressionValue(var22, "getUserName(...)");
               char[] var10001 = auth.getPassword();
               Intrinsics.checkNotNullExpressionValue(var10001, "getPassword(...)");
               char[] var14 = var10001;
               String credential = Credentials.basic(var22, new String(var14), challenge.charset());
               return request.newBuilder().header(credentialHeader, credential).build();
            }
         }
      }

      return null;
   }

   private final InetAddress connectToInetAddress(Proxy $this$connectToInetAddress, HttpUrl url, Dns dns) throws IOException {
      Proxy.Type var10000 = $this$connectToInetAddress.type();
      InetAddress var4;
      if ((var10000 == null ? -1 : JavaNetAuthenticator.WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) == 1) {
         var4 = (InetAddress)CollectionsKt.first(dns.lookup(url.host()));
      } else {
         SocketAddress var5 = $this$connectToInetAddress.address();
         Intrinsics.checkNotNull(var5, "null cannot be cast to non-null type java.net.InetSocketAddress");
         var4 = ((InetSocketAddress)var5).getAddress();
         Intrinsics.checkNotNullExpressionValue(var4, "getAddress(...)");
      }

      return var4;
   }

   public JavaNetAuthenticator() {
      this((Dns)null, 1, (DefaultConstructorMarker)null);
   }

   // $FF: synthetic class
   @Metadata(
      mv = {2, 2, 0},
      k = 3,
      xi = 48
   )
   public static final class WhenMappings {
      // $FF: synthetic field
      public static final int[] $EnumSwitchMapping$0;

      static {
         int[] var0 = new int[Type.values().length];

         try {
            var0[Type.DIRECT.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         $EnumSwitchMapping$0 = var0;
      }
   }
}
