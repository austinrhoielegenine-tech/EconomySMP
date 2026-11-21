package okhttp3;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import java.util.Objects;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal._UtilJvmKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0007\u0018\u00002\u00020\u0001B{\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u0011\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013\u0012\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015\u0012\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0015\u0012\u0006\u0010\u0019\u001a\u00020\u001a¢\u0006\u0004\b\u001b\u0010\u001cJ\r\u0010%\u001a\u00020&H\u0007¢\u0006\u0002\b)J\r\u0010\u0006\u001a\u00020\u0007H\u0007¢\u0006\u0002\b*J\r\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b+J\r\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\b,J\u0013\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015H\u0007¢\u0006\u0002\b-J\u0013\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0015H\u0007¢\u0006\u0002\b.J\r\u0010\u0019\u001a\u00020\u001aH\u0007¢\u0006\u0002\b/J\u000f\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0007¢\u0006\u0002\b0J\u000f\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0007¢\u0006\u0002\b1J\u000f\u0010\f\u001a\u0004\u0018\u00010\rH\u0007¢\u0006\u0002\b2J\u000f\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0007¢\u0006\u0002\b3J\u0013\u00104\u001a\u0002052\b\u00106\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u00107\u001a\u00020\u0005H\u0016J\u0015\u00108\u001a\u0002052\u0006\u00109\u001a\u00020\u0000H\u0000¢\u0006\u0002\b:J\b\u0010;\u001a\u00020\u0003H\u0016R\u0013\u0010\u0006\u001a\u00020\u00078\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u001dR\u0013\u0010\b\u001a\u00020\t8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u001eR\u0015\u0010\n\u001a\u0004\u0018\u00010\u000b8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u001fR\u0015\u0010\f\u001a\u0004\u0018\u00010\r8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010 R\u0015\u0010\u000e\u001a\u0004\u0018\u00010\u000f8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010!R\u0013\u0010\u0010\u001a\u00020\u00118\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\"R\u0015\u0010\u0012\u001a\u0004\u0018\u00010\u00138\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010#R\u0013\u0010\u0019\u001a\u00020\u001a8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010$R\u0013\u0010%\u001a\u00020&8\u0007¢\u0006\b\n\u0000\u001a\u0004\b%\u0010'R\u0019\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u00158\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010(R\u0019\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u00158\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010(¨\u0006<"},
   d2 = {"Lokhttp3/Address;", "", "uriHost", "", "uriPort", "", "dns", "Lokhttp3/Dns;", "socketFactory", "Ljavax/net/SocketFactory;", "sslSocketFactory", "Ljavax/net/ssl/SSLSocketFactory;", "hostnameVerifier", "Ljavax/net/ssl/HostnameVerifier;", "certificatePinner", "Lokhttp3/CertificatePinner;", "proxyAuthenticator", "Lokhttp3/Authenticator;", "proxy", "Ljava/net/Proxy;", "protocols", "", "Lokhttp3/Protocol;", "connectionSpecs", "Lokhttp3/ConnectionSpec;", "proxySelector", "Ljava/net/ProxySelector;", "<init>", "(Ljava/lang/String;ILokhttp3/Dns;Ljavax/net/SocketFactory;Ljavax/net/ssl/SSLSocketFactory;Ljavax/net/ssl/HostnameVerifier;Lokhttp3/CertificatePinner;Lokhttp3/Authenticator;Ljava/net/Proxy;Ljava/util/List;Ljava/util/List;Ljava/net/ProxySelector;)V", "()Lokhttp3/Dns;", "()Ljavax/net/SocketFactory;", "()Ljavax/net/ssl/SSLSocketFactory;", "()Ljavax/net/ssl/HostnameVerifier;", "()Lokhttp3/CertificatePinner;", "()Lokhttp3/Authenticator;", "()Ljava/net/Proxy;", "()Ljava/net/ProxySelector;", "url", "Lokhttp3/HttpUrl;", "()Lokhttp3/HttpUrl;", "()Ljava/util/List;", "-deprecated_url", "-deprecated_dns", "-deprecated_socketFactory", "-deprecated_proxyAuthenticator", "-deprecated_protocols", "-deprecated_connectionSpecs", "-deprecated_proxySelector", "-deprecated_proxy", "-deprecated_sslSocketFactory", "-deprecated_hostnameVerifier", "-deprecated_certificatePinner", "equals", "", "other", "hashCode", "equalsNonHost", "that", "equalsNonHost$okhttp", "toString", "okhttp"}
)
public final class Address {
   @NotNull
   private final Dns dns;
   @NotNull
   private final SocketFactory socketFactory;
   @Nullable
   private final SSLSocketFactory sslSocketFactory;
   @Nullable
   private final HostnameVerifier hostnameVerifier;
   @Nullable
   private final CertificatePinner certificatePinner;
   @NotNull
   private final Authenticator proxyAuthenticator;
   @Nullable
   private final Proxy proxy;
   @NotNull
   private final ProxySelector proxySelector;
   @NotNull
   private final HttpUrl url;
   @NotNull
   private final List<Protocol> protocols;
   @NotNull
   private final List<ConnectionSpec> connectionSpecs;

   public Address(@NotNull String uriHost, int uriPort, @NotNull Dns dns, @NotNull SocketFactory socketFactory, @Nullable SSLSocketFactory sslSocketFactory, @Nullable HostnameVerifier hostnameVerifier, @Nullable CertificatePinner certificatePinner, @NotNull Authenticator proxyAuthenticator, @Nullable Proxy proxy, @NotNull List<? extends Protocol> protocols, @NotNull List<ConnectionSpec> connectionSpecs, @NotNull ProxySelector proxySelector) {
      Intrinsics.checkNotNullParameter(uriHost, "uriHost");
      Intrinsics.checkNotNullParameter(dns, "dns");
      Intrinsics.checkNotNullParameter(socketFactory, "socketFactory");
      Intrinsics.checkNotNullParameter(proxyAuthenticator, "proxyAuthenticator");
      Intrinsics.checkNotNullParameter(protocols, "protocols");
      Intrinsics.checkNotNullParameter(connectionSpecs, "connectionSpecs");
      Intrinsics.checkNotNullParameter(proxySelector, "proxySelector");
      super();
      this.dns = dns;
      this.socketFactory = socketFactory;
      this.sslSocketFactory = sslSocketFactory;
      this.hostnameVerifier = hostnameVerifier;
      this.certificatePinner = certificatePinner;
      this.proxyAuthenticator = proxyAuthenticator;
      this.proxy = proxy;
      this.proxySelector = proxySelector;
      this.url = (new HttpUrl.Builder()).scheme(this.sslSocketFactory != null ? "https" : "http").host(uriHost).port(uriPort).build();
      this.protocols = _UtilJvmKt.toImmutableList(protocols);
      this.connectionSpecs = _UtilJvmKt.toImmutableList(connectionSpecs);
   }

   @JvmName(
      name = "dns"
   )
   @NotNull
   public final Dns dns() {
      return this.dns;
   }

   @JvmName(
      name = "socketFactory"
   )
   @NotNull
   public final SocketFactory socketFactory() {
      return this.socketFactory;
   }

   @JvmName(
      name = "sslSocketFactory"
   )
   @Nullable
   public final SSLSocketFactory sslSocketFactory() {
      return this.sslSocketFactory;
   }

   @JvmName(
      name = "hostnameVerifier"
   )
   @Nullable
   public final HostnameVerifier hostnameVerifier() {
      return this.hostnameVerifier;
   }

   @JvmName(
      name = "certificatePinner"
   )
   @Nullable
   public final CertificatePinner certificatePinner() {
      return this.certificatePinner;
   }

   @JvmName(
      name = "proxyAuthenticator"
   )
   @NotNull
   public final Authenticator proxyAuthenticator() {
      return this.proxyAuthenticator;
   }

   @JvmName(
      name = "proxy"
   )
   @Nullable
   public final Proxy proxy() {
      return this.proxy;
   }

   @JvmName(
      name = "proxySelector"
   )
   @NotNull
   public final ProxySelector proxySelector() {
      return this.proxySelector;
   }

   @JvmName(
      name = "url"
   )
   @NotNull
   public final HttpUrl url() {
      return this.url;
   }

   @JvmName(
      name = "protocols"
   )
   @NotNull
   public final List<Protocol> protocols() {
      return this.protocols;
   }

   @JvmName(
      name = "connectionSpecs"
   )
   @NotNull
   public final List<ConnectionSpec> connectionSpecs() {
      return this.connectionSpecs;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "url",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_url"
   )
   @NotNull
   public final HttpUrl _deprecated_url/* $FF was: -deprecated_url*/() {
      return this.url;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "dns",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_dns"
   )
   @NotNull
   public final Dns _deprecated_dns/* $FF was: -deprecated_dns*/() {
      return this.dns;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "socketFactory",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_socketFactory"
   )
   @NotNull
   public final SocketFactory _deprecated_socketFactory/* $FF was: -deprecated_socketFactory*/() {
      return this.socketFactory;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "proxyAuthenticator",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_proxyAuthenticator"
   )
   @NotNull
   public final Authenticator _deprecated_proxyAuthenticator/* $FF was: -deprecated_proxyAuthenticator*/() {
      return this.proxyAuthenticator;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "protocols",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_protocols"
   )
   @NotNull
   public final List<Protocol> _deprecated_protocols/* $FF was: -deprecated_protocols*/() {
      return this.protocols;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "connectionSpecs",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_connectionSpecs"
   )
   @NotNull
   public final List<ConnectionSpec> _deprecated_connectionSpecs/* $FF was: -deprecated_connectionSpecs*/() {
      return this.connectionSpecs;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "proxySelector",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_proxySelector"
   )
   @NotNull
   public final ProxySelector _deprecated_proxySelector/* $FF was: -deprecated_proxySelector*/() {
      return this.proxySelector;
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
   @Nullable
   public final Proxy _deprecated_proxy/* $FF was: -deprecated_proxy*/() {
      return this.proxy;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "sslSocketFactory",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_sslSocketFactory"
   )
   @Nullable
   public final SSLSocketFactory _deprecated_sslSocketFactory/* $FF was: -deprecated_sslSocketFactory*/() {
      return this.sslSocketFactory;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "hostnameVerifier",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_hostnameVerifier"
   )
   @Nullable
   public final HostnameVerifier _deprecated_hostnameVerifier/* $FF was: -deprecated_hostnameVerifier*/() {
      return this.hostnameVerifier;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "certificatePinner",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_certificatePinner"
   )
   @Nullable
   public final CertificatePinner _deprecated_certificatePinner/* $FF was: -deprecated_certificatePinner*/() {
      return this.certificatePinner;
   }

   public boolean equals(@Nullable Object other) {
      return other instanceof Address && Intrinsics.areEqual((Object)this.url, (Object)((Address)other).url) && this.equalsNonHost$okhttp((Address)other);
   }

   public int hashCode() {
      int result = 17;
      result = 31 * result + this.url.hashCode();
      result = 31 * result + this.dns.hashCode();
      result = 31 * result + this.proxyAuthenticator.hashCode();
      result = 31 * result + this.protocols.hashCode();
      result = 31 * result + this.connectionSpecs.hashCode();
      result = 31 * result + this.proxySelector.hashCode();
      result = 31 * result + Objects.hashCode(this.proxy);
      result = 31 * result + Objects.hashCode(this.sslSocketFactory);
      result = 31 * result + Objects.hashCode(this.hostnameVerifier);
      result = 31 * result + Objects.hashCode(this.certificatePinner);
      return result;
   }

   public final boolean equalsNonHost$okhttp(@NotNull Address that) {
      Intrinsics.checkNotNullParameter(that, "that");
      return Intrinsics.areEqual((Object)this.dns, (Object)that.dns) && Intrinsics.areEqual((Object)this.proxyAuthenticator, (Object)that.proxyAuthenticator) && Intrinsics.areEqual((Object)this.protocols, (Object)that.protocols) && Intrinsics.areEqual((Object)this.connectionSpecs, (Object)that.connectionSpecs) && Intrinsics.areEqual((Object)this.proxySelector, (Object)that.proxySelector) && Intrinsics.areEqual((Object)this.proxy, (Object)that.proxy) && Intrinsics.areEqual((Object)this.sslSocketFactory, (Object)that.sslSocketFactory) && Intrinsics.areEqual((Object)this.hostnameVerifier, (Object)that.hostnameVerifier) && Intrinsics.areEqual((Object)this.certificatePinner, (Object)that.certificatePinner) && this.url.port() == that.url.port();
   }

   @NotNull
   public String toString() {
      return "Address{" + this.url.host() + ':' + this.url.port() + ", " + (this.proxy != null ? "proxy=" + this.proxy : "proxySelector=" + this.proxySelector) + '}';
   }
}
