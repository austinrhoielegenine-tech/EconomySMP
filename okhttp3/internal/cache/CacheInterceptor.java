package okhttp3.internal.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.UnreadableResponseBodyKt;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.connection.RealCall;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u0011\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u001a\u0010\f\u001a\u00020\t2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\tH\u0002R\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011"},
   d2 = {"Lokhttp3/internal/cache/CacheInterceptor;", "Lokhttp3/Interceptor;", "cache", "Lokhttp3/Cache;", "<init>", "(Lokhttp3/Cache;)V", "getCache$okhttp", "()Lokhttp3/Cache;", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "cacheWritingResponse", "cacheRequest", "Lokhttp3/internal/cache/CacheRequest;", "response", "Companion", "okhttp"}
)
public final class CacheInterceptor implements Interceptor {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @Nullable
   private final Cache cache;

   public CacheInterceptor(@Nullable Cache cache) {
      this.cache = cache;
   }

   @Nullable
   public final Cache getCache$okhttp() {
      return this.cache;
   }

   @NotNull
   public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
      Intrinsics.checkNotNullParameter(chain, "chain");
      Call call = chain.call();
      Cache var10000 = this.cache;
      Response cacheCandidate = var10000 != null ? var10000.get$okhttp(CacheInterceptorKt.access$requestForCache(chain.request())) : null;
      long now = System.currentTimeMillis();
      CacheStrategy strategy = (new CacheStrategy.Factory(now, chain.request(), cacheCandidate)).compute();
      Request networkRequest = strategy.getNetworkRequest();
      Response cacheResponse = strategy.getCacheResponse();
      var10000 = this.cache;
      if (var10000 != null) {
         var10000.trackResponse$okhttp(strategy);
      }

      label220: {
         RealCall var32 = call instanceof RealCall ? (RealCall)call : null;
         if ((call instanceof RealCall ? (RealCall)call : null) != null) {
            var33 = var32.getEventListener$okhttp();
            if (var33 != null) {
               break label220;
            }
         }

         var33 = EventListener.NONE;
      }

      EventListener listener = var33;
      if (cacheCandidate != null && cacheResponse == null) {
         _UtilCommonKt.closeQuietly(cacheCandidate.body());
      }

      if (networkRequest == null && cacheResponse == null) {
         Response it = (new Response.Builder()).request(chain.request()).protocol(Protocol.HTTP_1_1).code(504).message("Unsatisfiable Request (only-if-cached)").sentRequestAtMillis(-1L).receivedResponseAtMillis(System.currentTimeMillis()).build();
         int cacheNetworkRequest = 0;
         listener.satisfactionFailure(call, it);
         return it;
      } else if (networkRequest == null) {
         Intrinsics.checkNotNull(cacheResponse);
         Response it = cacheResponse.newBuilder().cacheResponse(UnreadableResponseBodyKt.stripBody(cacheResponse)).build();
         int var26 = 0;
         listener.cacheHit(call, it);
         return it;
      } else {
         if (cacheResponse != null) {
            listener.cacheConditionalHit(call, cacheResponse);
         } else if (this.cache != null) {
            listener.cacheMiss(call);
         }

         Response networkResponse = null;
         boolean var19 = false;

         try {
            var19 = true;
            networkResponse = chain.proceed(networkRequest);
            var19 = false;
         } finally {
            if (var19) {
               if (cacheCandidate != null) {
                  _UtilCommonKt.closeQuietly(cacheCandidate.body());
               }

            }
         }

         if (networkResponse == null && cacheCandidate != null) {
            _UtilCommonKt.closeQuietly(cacheCandidate.body());
         }

         if (cacheResponse != null) {
            if (networkResponse != null ? networkResponse.code() == 304 : false) {
               Response response = cacheResponse.newBuilder().headers(Companion.combine(cacheResponse.headers(), networkResponse.headers())).sentRequestAtMillis(networkResponse.sentRequestAtMillis()).receivedResponseAtMillis(networkResponse.receivedResponseAtMillis()).cacheResponse(UnreadableResponseBodyKt.stripBody(cacheResponse)).networkResponse(UnreadableResponseBodyKt.stripBody(networkResponse)).build();
               networkResponse.body().close();
               var10000 = this.cache;
               Intrinsics.checkNotNull(var10000);
               var10000.trackConditionalCacheHit$okhttp();
               this.cache.update$okhttp(cacheResponse, response);
               int var28 = 0;
               listener.cacheHit(call, response);
               return response;
            }

            _UtilCommonKt.closeQuietly(cacheResponse.body());
         }

         Intrinsics.checkNotNull(networkResponse);
         Response response = networkResponse.newBuilder().cacheResponse(cacheResponse != null ? UnreadableResponseBodyKt.stripBody(cacheResponse) : null).networkResponse(UnreadableResponseBodyKt.stripBody(networkResponse)).build();
         if (this.cache != null) {
            Request cacheNetworkRequest = CacheInterceptorKt.access$requestForCache(networkRequest);
            if (HttpHeaders.promisesBody(response) && CacheStrategy.Companion.isCacheable(response, cacheNetworkRequest)) {
               CacheRequest cacheRequest = this.cache.put$okhttp(response.newBuilder().request(cacheNetworkRequest).build());
               Response var14 = this.cacheWritingResponse(cacheRequest, response);
               int var16 = 0;
               if (cacheResponse != null) {
                  listener.cacheMiss(call);
               }

               return var14;
            }

            if (HttpMethod.invalidatesCache(networkRequest.method())) {
               try {
                  this.cache.remove$okhttp(networkRequest);
               } catch (IOException var20) {
               }
            }
         }

         return response;
      }
   }

   private final Response cacheWritingResponse(final CacheRequest cacheRequest, Response response) throws IOException {
      if (cacheRequest == null) {
         return response;
      } else {
         Sink cacheBodyUnbuffered = cacheRequest.body();
         final BufferedSource source = response.body().source();
         final BufferedSink cacheBody = Okio.buffer(cacheBodyUnbuffered);
         <undefinedtype> cacheWritingSource = new Source() {
            private boolean cacheRequestClosed;

            public long read(Buffer sink, long byteCount) throws IOException {
               Intrinsics.checkNotNullParameter(sink, "sink");
               long bytesRead = 0L;

               try {
                  bytesRead = source.read(sink, byteCount);
               } catch (IOException e) {
                  if (!this.cacheRequestClosed) {
                     this.cacheRequestClosed = true;
                     cacheRequest.abort();
                  }

                  throw e;
               }

               if (bytesRead == -1L) {
                  if (!this.cacheRequestClosed) {
                     this.cacheRequestClosed = true;
                     cacheBody.close();
                  }

                  return -1L;
               } else {
                  sink.copyTo(cacheBody.getBuffer(), sink.size() - bytesRead, bytesRead);
                  cacheBody.emitCompleteSegments();
                  return bytesRead;
               }
            }

            public Timeout timeout() {
               return source.timeout();
            }

            public void close() throws IOException {
               if (!this.cacheRequestClosed && !_UtilJvmKt.discard(this, 100, TimeUnit.MILLISECONDS)) {
                  this.cacheRequestClosed = true;
                  cacheRequest.abort();
               }

               source.close();
            }
         };
         String contentType = Response.header$default(response, "Content-Type", (String)null, 2, (Object)null);
         long contentLength = response.body().contentLength();
         return response.newBuilder().body(new RealResponseBody(contentType, contentLength, Okio.buffer(cacheWritingSource))).build();
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u0005H\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0010\u0010\f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002¨\u0006\r"},
      d2 = {"Lokhttp3/internal/cache/CacheInterceptor$Companion;", "", "<init>", "()V", "combine", "Lokhttp3/Headers;", "cachedHeaders", "networkHeaders", "isEndToEnd", "", "fieldName", "", "isContentSpecificHeader", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final Headers combine(Headers cachedHeaders, Headers networkHeaders) {
         Headers.Builder result = new Headers.Builder();
         int index = 0;

         for(int var5 = cachedHeaders.size(); index < var5; ++index) {
            String fieldName = cachedHeaders.name(index);
            String value = cachedHeaders.value(index);
            if ((!StringsKt.equals("Warning", fieldName, true) || !StringsKt.startsWith$default(value, "1", false, 2, (Object)null)) && (this.isContentSpecificHeader(fieldName) || !this.isEndToEnd(fieldName) || networkHeaders.get(fieldName) == null)) {
               result.addLenient$okhttp(fieldName, value);
            }
         }

         index = 0;

         for(int var9 = networkHeaders.size(); index < var9; ++index) {
            String fieldName = networkHeaders.name(index);
            if (!this.isContentSpecificHeader(fieldName) && this.isEndToEnd(fieldName)) {
               result.addLenient$okhttp(fieldName, networkHeaders.value(index));
            }
         }

         return result.build();
      }

      private final boolean isEndToEnd(String fieldName) {
         return !StringsKt.equals("Connection", fieldName, true) && !StringsKt.equals("Keep-Alive", fieldName, true) && !StringsKt.equals("Proxy-Authenticate", fieldName, true) && !StringsKt.equals("Proxy-Authorization", fieldName, true) && !StringsKt.equals("TE", fieldName, true) && !StringsKt.equals("Trailers", fieldName, true) && !StringsKt.equals("Transfer-Encoding", fieldName, true) && !StringsKt.equals("Upgrade", fieldName, true);
      }

      private final boolean isContentSpecificHeader(String fieldName) {
         return StringsKt.equals("Content-Length", fieldName, true) || StringsKt.equals("Content-Encoding", fieldName, true) || StringsKt.equals("Content-Type", fieldName, true);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
