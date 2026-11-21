package okhttp3.internal.http;

import java.io.IOException;
import java.net.ProtocolException;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TrailersSource;
import okhttp3.internal.UnreadableResponseBody;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.connection.Exchange;
import okhttp3.internal.http2.ConnectionShutdownException;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002¨\u0006\f"},
   d2 = {"Lokhttp3/internal/http/CallServerInterceptor;", "Lokhttp3/Interceptor;", "<init>", "()V", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "shouldIgnoreAndWaitForRealResponse", "", "code", "", "okhttp"}
)
public final class CallServerInterceptor implements Interceptor {
   @NotNull
   public static final CallServerInterceptor INSTANCE = new CallServerInterceptor();

   private CallServerInterceptor() {
   }

   @NotNull
   public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
      Intrinsics.checkNotNullParameter(chain, "chain");
      RealInterceptorChain realChain = (RealInterceptorChain)chain;
      Exchange var10000 = realChain.getExchange$okhttp();
      Intrinsics.checkNotNull(var10000);
      final Exchange exchange = var10000;
      Request request = realChain.getRequest$okhttp();
      RequestBody requestBody = request.body();
      long sentRequestMillis = System.currentTimeMillis();
      boolean invokeStartEvent = true;
      Response.Builder responseBuilder = null;
      IOException sendRequestException = null;
      boolean hasRequestBody = HttpMethod.permitsRequestBody(request.method()) && requestBody != null;
      boolean isUpgradeRequest = StringsKt.equals("upgrade", request.header("Connection"), true);

      try {
         exchange.writeRequestHeaders(request);
         if (hasRequestBody) {
            if (StringsKt.equals("100-continue", request.header("Expect"), true)) {
               exchange.flushRequest();
               responseBuilder = exchange.readResponseHeaders(true);
               exchange.responseHeadersStart();
               invokeStartEvent = false;
            }

            if (responseBuilder == null) {
               if (requestBody.isDuplex()) {
                  exchange.flushRequest();
                  BufferedSink bufferedRequestBody = Okio.buffer(exchange.createRequestBody(request, true));
                  requestBody.writeTo(bufferedRequestBody);
               } else {
                  BufferedSink bufferedRequestBody = Okio.buffer(exchange.createRequestBody(request, false));
                  requestBody.writeTo(bufferedRequestBody);
                  bufferedRequestBody.close();
               }
            } else {
               exchange.noRequestBody();
               if (!exchange.getConnection$okhttp().isMultiplexed$okhttp()) {
                  exchange.noNewExchangesOnConnection();
               }
            }
         } else {
            exchange.noRequestBody();
         }

         if (requestBody == null || !requestBody.isDuplex()) {
            exchange.finishRequest();
         }
      } catch (IOException e) {
         if (e instanceof ConnectionShutdownException) {
            throw e;
         }

         if (!exchange.getHasFailure$okhttp()) {
            throw e;
         }

         sendRequestException = e;
      }

      try {
         if (responseBuilder == null) {
            Response.Builder var24 = exchange.readResponseHeaders(false);
            Intrinsics.checkNotNull(var24);
            responseBuilder = var24;
            if (invokeStartEvent) {
               exchange.responseHeadersStart();
               invokeStartEvent = false;
            }
         }

         Response response = responseBuilder.request(request).handshake(exchange.getConnection$okhttp().handshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();

         int code;
         for(code = response.code(); this.shouldIgnoreAndWaitForRealResponse(code); code = response.code()) {
            Response.Builder var25 = exchange.readResponseHeaders(false);
            Intrinsics.checkNotNull(var25);
            responseBuilder = var25;
            if (invokeStartEvent) {
               exchange.responseHeadersStart();
            }

            response = responseBuilder.request(request).handshake(exchange.getConnection$okhttp().handshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
         }

         exchange.responseHeadersEnd(response);
         boolean isUpgradeCode = code == 101;
         if (isUpgradeCode && exchange.getConnection$okhttp().isMultiplexed$okhttp()) {
            throw new ProtocolException("Unexpected 101 code on HTTP/2 connection");
         } else {
            boolean isUpgradeResponse = isUpgradeCode && StringsKt.equals("upgrade", Response.header$default(response, "Connection", (String)null, 2, (Object)null), true);
            Response var26;
            if (isUpgradeRequest && isUpgradeResponse) {
               var26 = response.newBuilder().body(new UnreadableResponseBody(response.body().contentType(), response.body().contentLength())).socket(exchange.upgradeToSocket()).build();
            } else {
               final ResponseBody responseBody = exchange.openResponseBody(response);
               var26 = response.newBuilder().body(responseBody).trailers(new TrailersSource() {
                  public Headers peek() {
                     return exchange.peekTrailers();
                  }

                  public Headers get() {
                     BufferedSource source = responseBody.source();
                     if (source.isOpen()) {
                        _UtilJvmKt.skipAll(source);
                     }

                     Headers var10000 = this.peek();
                     if (var10000 == null) {
                        throw new IllegalStateException("null trailers after exhausting response body?!".toString());
                     } else {
                        return var10000;
                     }
                  }
               }).build();
            }

            response = var26;
            if (StringsKt.equals("close", response.request().header("Connection"), true) || StringsKt.equals("close", Response.header$default(response, "Connection", (String)null, 2, (Object)null), true)) {
               exchange.noNewExchangesOnConnection();
            }

            if ((code == 204 || code == 205) && response.body().contentLength() > 0L) {
               throw new ProtocolException("HTTP " + code + " had non-zero Content-Length: " + response.body().contentLength());
            } else {
               return response;
            }
         }
      } catch (IOException e) {
         if (sendRequestException != null) {
            ExceptionsKt.addSuppressed((Throwable)sendRequestException, (Throwable)e);
            throw sendRequestException;
         } else {
            throw e;
         }
      }
   }

   private final boolean shouldIgnoreAndWaitForRealResponse(int code) {
      return code == 100 ? true : (102 <= code ? code < 200 : false);
   }
}
