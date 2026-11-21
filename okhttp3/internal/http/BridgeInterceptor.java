package okhttp3.internal.http;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal._UtilJvmKt;
import okio.GzipSource;
import okio.Okio;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u0016\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"},
   d2 = {"Lokhttp3/internal/http/BridgeInterceptor;", "Lokhttp3/Interceptor;", "cookieJar", "Lokhttp3/CookieJar;", "<init>", "(Lokhttp3/CookieJar;)V", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "cookieHeader", "", "cookies", "", "Lokhttp3/Cookie;", "okhttp"}
)
@SourceDebugExtension({"SMAP\nBridgeInterceptor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BridgeInterceptor.kt\nokhttp3/internal/http/BridgeInterceptor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,125:1\n1878#2,3:126\n*S KotlinDebug\n*F\n+ 1 BridgeInterceptor.kt\nokhttp3/internal/http/BridgeInterceptor\n*L\n119#1:126,3\n*E\n"})
public final class BridgeInterceptor implements Interceptor {
   @NotNull
   private final CookieJar cookieJar;

   public BridgeInterceptor(@NotNull CookieJar cookieJar) {
      Intrinsics.checkNotNullParameter(cookieJar, "cookieJar");
      super();
      this.cookieJar = cookieJar;
   }

   @NotNull
   public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
      Intrinsics.checkNotNullParameter(chain, "chain");
      Request userRequest = chain.request();
      Request.Builder requestBuilder = userRequest.newBuilder();
      RequestBody body = userRequest.body();
      if (body != null) {
         MediaType contentType = body.contentType();
         if (contentType != null) {
            requestBuilder.header("Content-Type", contentType.toString());
         }

         long contentLength = body.contentLength();
         if (contentLength != -1L) {
            requestBuilder.header("Content-Length", String.valueOf(contentLength));
            requestBuilder.removeHeader("Transfer-Encoding");
         } else {
            requestBuilder.header("Transfer-Encoding", "chunked");
            requestBuilder.removeHeader("Content-Length");
         }
      }

      if (userRequest.header("Host") == null) {
         requestBuilder.header("Host", _UtilJvmKt.toHostHeader$default(userRequest.url(), false, 1, (Object)null));
      }

      if (userRequest.header("Connection") == null) {
         requestBuilder.header("Connection", "Keep-Alive");
      }

      boolean transparentGzip = false;
      if (userRequest.header("Accept-Encoding") == null && userRequest.header("Range") == null) {
         transparentGzip = true;
         requestBuilder.header("Accept-Encoding", "gzip");
      }

      List cookies = this.cookieJar.loadForRequest(userRequest.url());
      if (!((Collection)cookies).isEmpty()) {
         requestBuilder.header("Cookie", this.cookieHeader(cookies));
      }

      if (userRequest.header("User-Agent") == null) {
         requestBuilder.header("User-Agent", "okhttp/5.3.2");
      }

      Request networkRequest = requestBuilder.build();
      Response networkResponse = chain.proceed(networkRequest);
      HttpHeaders.receiveHeaders(this.cookieJar, networkRequest.url(), networkResponse.headers());
      Response.Builder responseBuilder = networkResponse.newBuilder().request(networkRequest);
      if (transparentGzip && StringsKt.equals("gzip", Response.header$default(networkResponse, "Content-Encoding", (String)null, 2, (Object)null), true) && HttpHeaders.promisesBody(networkResponse)) {
         ResponseBody responseBody = networkResponse.body();
         if (responseBody != null) {
            GzipSource gzipSource = new GzipSource(responseBody.source());
            Headers strippedHeaders = networkResponse.headers().newBuilder().removeAll("Content-Encoding").removeAll("Content-Length").build();
            responseBuilder.headers(strippedHeaders);
            String contentType = Response.header$default(networkResponse, "Content-Type", (String)null, 2, (Object)null);
            responseBuilder.body(new RealResponseBody(contentType, -1L, Okio.buffer(gzipSource)));
         }
      }

      return responseBuilder.build();
   }

   private final String cookieHeader(List<Cookie> cookies) {
      StringBuilder var2 = new StringBuilder();
      StringBuilder $this$cookieHeader_u24lambda_u240 = var2;
      int var4 = 0;
      Iterable $this$forEachIndexed$iv = (Iterable)cookies;
      int $i$f$forEachIndexed = 0;
      int index$iv = 0;

      for(Object item$iv : $this$forEachIndexed$iv) {
         int index = index$iv++;
         if (index < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Cookie cookie = (Cookie)item$iv;
         int var13 = 0;
         if (index > 0) {
            $this$cookieHeader_u24lambda_u240.append("; ");
         }

         $this$cookieHeader_u24lambda_u240.append(cookie.name()).append('=').append(cookie.value());
      }

      return var2.toString();
   }
}
