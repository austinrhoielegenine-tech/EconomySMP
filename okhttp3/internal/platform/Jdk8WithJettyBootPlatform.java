package okhttp3.internal.platform;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLSocket;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Protocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u00172\u00020\u0001:\u0002\u0016\u0017B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\n\u0010\u0006\u001a\u0006\u0012\u0002\b\u00030\u0007\u0012\n\u0010\b\u001a\u0006\u0012\u0002\b\u00030\u0007¢\u0006\u0004\b\t\u0010\nJ(\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012H\u0016J\u0010\u0010\u0014\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00102\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u0006\u0012\u0002\b\u00030\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u0006\u0012\u0002\b\u00030\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0018"},
   d2 = {"Lokhttp3/internal/platform/Jdk8WithJettyBootPlatform;", "Lokhttp3/internal/platform/Platform;", "putMethod", "Ljava/lang/reflect/Method;", "getMethod", "removeMethod", "clientProviderClass", "Ljava/lang/Class;", "serverProviderClass", "<init>", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/Class;Ljava/lang/Class;)V", "configureTlsExtensions", "", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "hostname", "", "protocols", "", "Lokhttp3/Protocol;", "afterHandshake", "getSelectedProtocol", "AlpnProvider", "Companion", "okhttp"}
)
public final class Jdk8WithJettyBootPlatform extends Platform {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Method putMethod;
   @NotNull
   private final Method getMethod;
   @NotNull
   private final Method removeMethod;
   @NotNull
   private final Class<?> clientProviderClass;
   @NotNull
   private final Class<?> serverProviderClass;

   public Jdk8WithJettyBootPlatform(@NotNull Method putMethod, @NotNull Method getMethod, @NotNull Method removeMethod, @NotNull Class<?> clientProviderClass, @NotNull Class<?> serverProviderClass) {
      Intrinsics.checkNotNullParameter(putMethod, "putMethod");
      Intrinsics.checkNotNullParameter(getMethod, "getMethod");
      Intrinsics.checkNotNullParameter(removeMethod, "removeMethod");
      Intrinsics.checkNotNullParameter(clientProviderClass, "clientProviderClass");
      Intrinsics.checkNotNullParameter(serverProviderClass, "serverProviderClass");
      super();
      this.putMethod = putMethod;
      this.getMethod = getMethod;
      this.removeMethod = removeMethod;
      this.clientProviderClass = clientProviderClass;
      this.serverProviderClass = serverProviderClass;
   }

   public void configureTlsExtensions(@NotNull SSLSocket sslSocket, @Nullable String hostname, @NotNull List<? extends Protocol> protocols) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      Intrinsics.checkNotNullParameter(protocols, "protocols");
      List names = Platform.Companion.alpnProtocolNames(protocols);

      try {
         ClassLoader var10000 = Platform.class.getClassLoader();
         Class[] var6 = new Class[]{this.clientProviderClass, this.serverProviderClass};
         Object alpnProvider = Proxy.newProxyInstance(var10000, var6, new AlpnProvider(names));
         Method var10 = this.putMethod;
         Object[] var9 = new Object[]{sslSocket, alpnProvider};
         var10.invoke((Object)null, var9);
      } catch (InvocationTargetException e) {
         throw new AssertionError("failed to set ALPN", (Throwable)e);
      } catch (IllegalAccessException e) {
         throw new AssertionError("failed to set ALPN", (Throwable)e);
      }
   }

   public void afterHandshake(@NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");

      try {
         Method var10000 = this.removeMethod;
         Object[] var2 = new Object[]{sslSocket};
         var10000.invoke((Object)null, var2);
      } catch (IllegalAccessException e) {
         throw new AssertionError("failed to remove ALPN", (Throwable)e);
      } catch (InvocationTargetException e) {
         throw new AssertionError("failed to remove ALPN", (Throwable)e);
      }
   }

   @Nullable
   public String getSelectedProtocol(@NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");

      try {
         Method var10000 = this.getMethod;
         Object[] var4 = new Object[]{sslSocket};
         InvocationHandler var7 = Proxy.getInvocationHandler(var10000.invoke((Object)null, var4));
         Intrinsics.checkNotNull(var7, "null cannot be cast to non-null type okhttp3.internal.platform.Jdk8WithJettyBootPlatform.AlpnProvider");
         AlpnProvider provider = (AlpnProvider)var7;
         if (!provider.getUnsupported() && provider.getSelected() == null) {
            Platform.log$default(this, "ALPN callback dropped: HTTP/2 is disabled. Is alpn-boot on the boot class path?", 0, (Throwable)null, 6, (Object)null);
            return null;
         } else {
            return provider.getUnsupported() ? null : provider.getSelected();
         }
      } catch (InvocationTargetException e) {
         throw new AssertionError("failed to get ALPN selected protocol", (Throwable)e);
      } catch (IllegalAccessException e) {
         throw new AssertionError("failed to get ALPN selected protocol", (Throwable)e);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J0\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u00162\u000e\u0010\u0017\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\u0018H\u0096\u0002¢\u0006\u0002\u0010\u0019R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001c\u0010\r\u001a\u0004\u0018\u00010\u0004X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011¨\u0006\u001a"},
      d2 = {"Lokhttp3/internal/platform/Jdk8WithJettyBootPlatform$AlpnProvider;", "Ljava/lang/reflect/InvocationHandler;", "protocols", "", "", "<init>", "(Ljava/util/List;)V", "unsupported", "", "getUnsupported", "()Z", "setUnsupported", "(Z)V", "selected", "getSelected", "()Ljava/lang/String;", "setSelected", "(Ljava/lang/String;)V", "invoke", "", "proxy", "method", "Ljava/lang/reflect/Method;", "args", "", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", "okhttp"}
   )
   private static final class AlpnProvider implements InvocationHandler {
      @NotNull
      private final List<String> protocols;
      private boolean unsupported;
      @Nullable
      private String selected;

      public AlpnProvider(@NotNull List<String> protocols) {
         Intrinsics.checkNotNullParameter(protocols, "protocols");
         super();
         this.protocols = protocols;
      }

      public final boolean getUnsupported() {
         return this.unsupported;
      }

      public final void setUnsupported(boolean var1) {
         this.unsupported = var1;
      }

      @Nullable
      public final String getSelected() {
         return this.selected;
      }

      public final void setSelected(@Nullable String var1) {
         this.selected = var1;
      }

      @Nullable
      public Object invoke(@NotNull Object proxy, @NotNull Method method, @Nullable Object[] args) throws Throwable {
         Intrinsics.checkNotNullParameter(proxy, "proxy");
         Intrinsics.checkNotNullParameter(method, "method");
         Object var10000 = args;
         if (args == null) {
            var10000 = new Object[0];
         }

         Object[] callArgs = (Object[])var10000;
         String methodName = method.getName();
         Class returnType = method.getReturnType();
         if (Intrinsics.areEqual((Object)methodName, (Object)"supports") && Intrinsics.areEqual((Object)Boolean.TYPE, (Object)returnType)) {
            return true;
         } else if (Intrinsics.areEqual((Object)methodName, (Object)"unsupported") && Intrinsics.areEqual((Object)Void.TYPE, (Object)returnType)) {
            this.unsupported = true;
            return null;
         } else if (Intrinsics.areEqual((Object)methodName, (Object)"protocols") && callArgs.length == 0) {
            return this.protocols;
         } else if ((Intrinsics.areEqual((Object)methodName, (Object)"selectProtocol") || Intrinsics.areEqual((Object)methodName, (Object)"select")) && Intrinsics.areEqual((Object)String.class, (Object)returnType) && callArgs.length == 1 && callArgs[0] instanceof List) {
            var10000 = callArgs[0];
            Intrinsics.checkNotNull(callArgs[0], "null cannot be cast to non-null type kotlin.collections.List<*>");
            List peerProtocols = (List)var10000;
            int i = 0;
            int var9 = peerProtocols.size();
            if (i <= var9) {
               while(true) {
                  var10000 = peerProtocols.get(i);
                  Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type kotlin.String");
                  String protocol = (String)var10000;
                  if (this.protocols.contains(protocol)) {
                     this.selected = protocol;
                     return this.selected;
                  }

                  if (i == var9) {
                     break;
                  }

                  ++i;
               }
            }

            this.selected = (String)this.protocols.get(0);
            return this.selected;
         } else if ((Intrinsics.areEqual((Object)methodName, (Object)"protocolSelected") || Intrinsics.areEqual((Object)methodName, (Object)"selected")) && callArgs.length == 1) {
            Object var10001 = callArgs[0];
            Intrinsics.checkNotNull(callArgs[0], "null cannot be cast to non-null type kotlin.String");
            this.selected = (String)var10001;
            return null;
         } else {
            return method.invoke(this, Arrays.copyOf(callArgs, callArgs.length));
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¨\u0006\u0006"},
      d2 = {"Lokhttp3/internal/platform/Jdk8WithJettyBootPlatform$Companion;", "", "<init>", "()V", "buildIfSupported", "Lokhttp3/internal/platform/Platform;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @Nullable
      public final Platform buildIfSupported() {
         String jvmVersion = System.getProperty("java.specification.version", "unknown");

         try {
            Intrinsics.checkNotNull(jvmVersion);
            int version = Integer.parseInt(jvmVersion);
            if (version >= 9) {
               return null;
            }
         } catch (NumberFormatException var13) {
         }

         try {
            String alpnClassName = "org.eclipse.jetty.alpn.ALPN";
            Class alpnClass = Class.forName(alpnClassName, true, (ClassLoader)null);
            Class providerClass = Class.forName(alpnClassName + "$Provider", true, (ClassLoader)null);
            Class clientProviderClass = Class.forName(alpnClassName + "$ClientProvider", true, (ClassLoader)null);
            Class serverProviderClass = Class.forName(alpnClassName + "$ServerProvider", true, (ClassLoader)null);
            Class[] var8 = new Class[]{SSLSocket.class, providerClass};
            Method putMethod = alpnClass.getMethod("put", var8);
            Class[] var9 = new Class[]{SSLSocket.class};
            Method getMethod = alpnClass.getMethod("get", var9);
            Class[] var10 = new Class[]{SSLSocket.class};
            Method removeMethod = alpnClass.getMethod("remove", var10);
            Intrinsics.checkNotNull(putMethod);
            Intrinsics.checkNotNull(getMethod);
            Intrinsics.checkNotNull(removeMethod);
            Intrinsics.checkNotNull(clientProviderClass);
            Intrinsics.checkNotNull(serverProviderClass);
            return new Jdk8WithJettyBootPlatform(putMethod, getMethod, removeMethod, clientProviderClass, serverProviderClass);
         } catch (ClassNotFoundException var11) {
         } catch (NoSuchMethodException var12) {
         }

         return null;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
