package okhttp3.internal;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocket;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import okhttp3.Cache;
import okhttp3.CipherSuite;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.connection.ConnectionListener;
import okhttp3.internal.connection.Exchange;
import okhttp3.internal.connection.RealConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0088\u0001\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\"\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0000\u001a\u0018\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000bH\u0000\u001a\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u0007H\u0000\u001a \u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0000\u001a\u001a\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0000\u001a \u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u000bH\u0000\u001a%\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00070 *\u00020\u001b2\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00070 H\u0000¢\u0006\u0002\u0010\"\u001a\u001c\u0010#\u001a\u0010\u0012\u0004\u0012\u00020%\u0012\u0006\u0012\u0004\u0018\u00010&0$*\u0004\u0018\u00010&H\u0000\u001a\u000e\u0010'\u001a\u00020%*\u0004\u0018\u00010&H\u0000\u001a\u0014\u0010,\u001a\u00020-*\u00020-2\u0006\u0010.\u001a\u00020/H\u0000\u001a\u0018\u00100\u001a\u0002012\u0006\u00102\u001a\u0002032\u0006\u0010.\u001a\u00020/H\u0000\"\u0018\u0010(\u001a\u00020)*\u00020\u00138@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b*\u0010+¨\u00064"},
   d2 = {"parseCookie", "Lokhttp3/Cookie;", "currentTimeMillis", "", "url", "Lokhttp3/HttpUrl;", "setCookie", "", "cookieToString", "cookie", "forObsoleteRfc2965", "", "addHeaderLenient", "Lokhttp3/Headers$Builder;", "builder", "line", "name", "value", "cacheGet", "Lokhttp3/Response;", "cache", "Lokhttp3/Cache;", "request", "Lokhttp3/Request;", "applyConnectionSpec", "", "connectionSpec", "Lokhttp3/ConnectionSpec;", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "isFallback", "effectiveCipherSuites", "", "socketEnabledCipherSuites", "(Lokhttp3/ConnectionSpec;[Ljava/lang/String;)[Ljava/lang/String;", "chooseCharset", "Lkotlin/Pair;", "Ljava/nio/charset/Charset;", "Lokhttp3/MediaType;", "charsetOrUtf8", "connection", "Lokhttp3/internal/connection/RealConnection;", "getConnection", "(Lokhttp3/Response;)Lokhttp3/internal/connection/RealConnection;", "taskRunnerInternal", "Lokhttp3/OkHttpClient$Builder;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "buildConnectionPool", "Lokhttp3/ConnectionPool;", "connectionListener", "Lokhttp3/internal/connection/ConnectionListener;", "okhttp"}
)
@JvmName(
   name = "Internal"
)
public final class Internal {
   @Nullable
   public static final Cookie parseCookie(long currentTimeMillis, @NotNull HttpUrl url, @NotNull String setCookie) {
      Intrinsics.checkNotNullParameter(url, "url");
      Intrinsics.checkNotNullParameter(setCookie, "setCookie");
      return Cookie.Companion.parse$okhttp(currentTimeMillis, url, setCookie);
   }

   @NotNull
   public static final String cookieToString(@NotNull Cookie cookie, boolean forObsoleteRfc2965) {
      Intrinsics.checkNotNullParameter(cookie, "cookie");
      return cookie.toString$okhttp(forObsoleteRfc2965);
   }

   @NotNull
   public static final Headers.Builder addHeaderLenient(@NotNull Headers.Builder builder, @NotNull String line) {
      Intrinsics.checkNotNullParameter(builder, "builder");
      Intrinsics.checkNotNullParameter(line, "line");
      return builder.addLenient$okhttp(line);
   }

   @NotNull
   public static final Headers.Builder addHeaderLenient(@NotNull Headers.Builder builder, @NotNull String name, @NotNull String value) {
      Intrinsics.checkNotNullParameter(builder, "builder");
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(value, "value");
      return builder.addLenient$okhttp(name, value);
   }

   @Nullable
   public static final Response cacheGet(@NotNull Cache cache, @NotNull Request request) {
      Intrinsics.checkNotNullParameter(cache, "cache");
      Intrinsics.checkNotNullParameter(request, "request");
      return cache.get$okhttp(request);
   }

   public static final void applyConnectionSpec(@NotNull ConnectionSpec connectionSpec, @NotNull SSLSocket sslSocket, boolean isFallback) {
      Intrinsics.checkNotNullParameter(connectionSpec, "connectionSpec");
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      connectionSpec.apply$okhttp(sslSocket, isFallback);
   }

   @NotNull
   public static final String[] effectiveCipherSuites(@NotNull ConnectionSpec $this$effectiveCipherSuites, @NotNull String[] socketEnabledCipherSuites) {
      Intrinsics.checkNotNullParameter($this$effectiveCipherSuites, "<this>");
      Intrinsics.checkNotNullParameter(socketEnabledCipherSuites, "socketEnabledCipherSuites");
      return $this$effectiveCipherSuites.getCipherSuitesAsString$okhttp() != null ? _UtilCommonKt.intersect($this$effectiveCipherSuites.getCipherSuitesAsString$okhttp(), socketEnabledCipherSuites, CipherSuite.Companion.getORDER_BY_NAME$okhttp()) : socketEnabledCipherSuites;
   }

   @NotNull
   public static final Pair<Charset, MediaType> chooseCharset(@Nullable MediaType $this$chooseCharset) {
      Charset charset = Charsets.UTF_8;
      MediaType finalContentType = $this$chooseCharset;
      if ($this$chooseCharset != null) {
         Charset resolvedCharset = MediaType.charset$default($this$chooseCharset, (Charset)null, 1, (Object)null);
         if (resolvedCharset == null) {
            charset = Charsets.UTF_8;
            finalContentType = MediaType.Companion.parse($this$chooseCharset + "; charset=utf-8");
         } else {
            charset = resolvedCharset;
         }
      }

      return TuplesKt.<Charset, MediaType>to(charset, finalContentType);
   }

   @NotNull
   public static final Charset charsetOrUtf8(@Nullable MediaType $this$charsetOrUtf8) {
      Charset var10000;
      if ($this$charsetOrUtf8 != null) {
         var10000 = MediaType.charset$default($this$charsetOrUtf8, (Charset)null, 1, (Object)null);
         if (var10000 != null) {
            return var10000;
         }
      }

      var10000 = Charsets.UTF_8;
      return var10000;
   }

   @NotNull
   public static final RealConnection getConnection(@NotNull Response $this$connection) {
      Intrinsics.checkNotNullParameter($this$connection, "<this>");
      Exchange var10000 = $this$connection.exchange();
      Intrinsics.checkNotNull(var10000);
      return var10000.getConnection$okhttp();
   }

   @NotNull
   public static final OkHttpClient.Builder taskRunnerInternal(@NotNull OkHttpClient.Builder $this$taskRunnerInternal, @NotNull TaskRunner taskRunner) {
      Intrinsics.checkNotNullParameter($this$taskRunnerInternal, "<this>");
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      return $this$taskRunnerInternal.taskRunner$okhttp(taskRunner);
   }

   @NotNull
   public static final ConnectionPool buildConnectionPool(@NotNull ConnectionListener connectionListener, @NotNull TaskRunner taskRunner) {
      Intrinsics.checkNotNullParameter(connectionListener, "connectionListener");
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      return new ConnectionPool(0, 0L, (TimeUnit)null, taskRunner, connectionListener, 7, (DefaultConstructorMarker)null);
   }
}
