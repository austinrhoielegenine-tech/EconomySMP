package okhttp3.internal.http;

import java.net.Proxy;
import java.net.Proxy.Type;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u000e\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000e¨\u0006\u000f"},
   d2 = {"Lokhttp3/internal/http/RequestLine;", "", "<init>", "()V", "get", "", "request", "Lokhttp3/Request;", "proxyType", "Ljava/net/Proxy$Type;", "includeAuthorityInRequestLine", "", "requestPath", "url", "Lokhttp3/HttpUrl;", "okhttp"}
)
public final class RequestLine {
   @NotNull
   public static final RequestLine INSTANCE = new RequestLine();

   private RequestLine() {
   }

   @NotNull
   public final String get(@NotNull Request request, @NotNull Proxy.Type proxyType) {
      Intrinsics.checkNotNullParameter(request, "request");
      Intrinsics.checkNotNullParameter(proxyType, "proxyType");
      StringBuilder var3 = new StringBuilder();
      int var5 = 0;
      var3.append(request.method());
      var3.append(' ');
      if (INSTANCE.includeAuthorityInRequestLine(request, proxyType)) {
         var3.append(request.url());
      } else {
         var3.append(INSTANCE.requestPath(request.url()));
      }

      var3.append(" HTTP/1.1");
      return var3.toString();
   }

   private final boolean includeAuthorityInRequestLine(Request request, Proxy.Type proxyType) {
      return !request.isHttps() && proxyType == Type.HTTP;
   }

   @NotNull
   public final String requestPath(@NotNull HttpUrl url) {
      Intrinsics.checkNotNullParameter(url, "url");
      String path = url.encodedPath();
      String query = url.encodedQuery();
      return query != null ? path + '?' + query : path;
   }
}
