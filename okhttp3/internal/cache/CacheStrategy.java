package okhttp3.internal.cache;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal.http.DateFormattingKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u0000 \r2\u00020\u0001:\u0002\f\rB\u001d\b\u0000\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u000e"},
   d2 = {"Lokhttp3/internal/cache/CacheStrategy;", "", "networkRequest", "Lokhttp3/Request;", "cacheResponse", "Lokhttp3/Response;", "<init>", "(Lokhttp3/Request;Lokhttp3/Response;)V", "getNetworkRequest", "()Lokhttp3/Request;", "getCacheResponse", "()Lokhttp3/Response;", "Factory", "Companion", "okhttp"}
)
public final class CacheStrategy {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @Nullable
   private final Request networkRequest;
   @Nullable
   private final Response cacheResponse;

   public CacheStrategy(@Nullable Request networkRequest, @Nullable Response cacheResponse) {
      this.networkRequest = networkRequest;
      this.cacheResponse = cacheResponse;
   }

   @Nullable
   public final Request getNetworkRequest() {
      return this.networkRequest;
   }

   @Nullable
   public final Response getCacheResponse() {
      return this.cacheResponse;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tJ\b\u0010\u0018\u001a\u00020\u0019H\u0002J\u0006\u0010\u001a\u001a\u00020\u001bJ\b\u0010\u001c\u001a\u00020\u001bH\u0002J\b\u0010\u001d\u001a\u00020\u0003H\u0002J\b\u0010\u001e\u001a\u00020\u0003H\u0002J\u0010\u0010\u001f\u001a\u00020\u00192\u0006\u0010\u0004\u001a\u00020\u0005H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006 "},
      d2 = {"Lokhttp3/internal/cache/CacheStrategy$Factory;", "", "nowMillis", "", "request", "Lokhttp3/Request;", "cacheResponse", "Lokhttp3/Response;", "<init>", "(JLokhttp3/Request;Lokhttp3/Response;)V", "getRequest$okhttp", "()Lokhttp3/Request;", "servedDate", "Ljava/util/Date;", "servedDateString", "", "lastModified", "lastModifiedString", "expires", "sentRequestMillis", "receivedResponseMillis", "etag", "ageSeconds", "", "isFreshnessLifetimeHeuristic", "", "compute", "Lokhttp3/internal/cache/CacheStrategy;", "computeCandidate", "computeFreshnessLifetime", "cacheResponseAge", "hasConditions", "okhttp"}
   )
   public static final class Factory {
      private final long nowMillis;
      @NotNull
      private final Request request;
      @Nullable
      private final Response cacheResponse;
      @Nullable
      private Date servedDate;
      @Nullable
      private String servedDateString;
      @Nullable
      private Date lastModified;
      @Nullable
      private String lastModifiedString;
      @Nullable
      private Date expires;
      private long sentRequestMillis;
      private long receivedResponseMillis;
      @Nullable
      private String etag;
      private int ageSeconds;

      public Factory(long nowMillis, @NotNull Request request, @Nullable Response cacheResponse) {
         Intrinsics.checkNotNullParameter(request, "request");
         super();
         this.nowMillis = nowMillis;
         this.request = request;
         this.cacheResponse = cacheResponse;
         this.ageSeconds = -1;
         if (this.cacheResponse != null) {
            this.sentRequestMillis = this.cacheResponse.sentRequestAtMillis();
            this.receivedResponseMillis = this.cacheResponse.receivedResponseAtMillis();
            Headers headers = this.cacheResponse.headers();
            int i = 0;

            for(int var7 = headers.size(); i < var7; ++i) {
               String fieldName = headers.name(i);
               String value = headers.value(i);
               if (StringsKt.equals(fieldName, "Date", true)) {
                  this.servedDate = DateFormattingKt.toHttpDateOrNull(value);
                  this.servedDateString = value;
               } else if (StringsKt.equals(fieldName, "Expires", true)) {
                  this.expires = DateFormattingKt.toHttpDateOrNull(value);
               } else if (StringsKt.equals(fieldName, "Last-Modified", true)) {
                  this.lastModified = DateFormattingKt.toHttpDateOrNull(value);
                  this.lastModifiedString = value;
               } else if (StringsKt.equals(fieldName, "ETag", true)) {
                  this.etag = value;
               } else if (StringsKt.equals(fieldName, "Age", true)) {
                  this.ageSeconds = _UtilCommonKt.toNonNegativeInt(value, -1);
               }
            }
         }

      }

      @NotNull
      public final Request getRequest$okhttp() {
         return this.request;
      }

      private final boolean isFreshnessLifetimeHeuristic() {
         Response var10000 = this.cacheResponse;
         Intrinsics.checkNotNull(var10000);
         return var10000.cacheControl().maxAgeSeconds() == -1 && this.expires == null;
      }

      @NotNull
      public final CacheStrategy compute() {
         CacheStrategy candidate = this.computeCandidate();
         return candidate.getNetworkRequest() != null && this.request.cacheControl().onlyIfCached() ? new CacheStrategy((Request)null, (Response)null) : candidate;
      }

      private final CacheStrategy computeCandidate() {
         if (this.cacheResponse == null) {
            return new CacheStrategy(this.request, (Response)null);
         } else if (this.request.isHttps() && this.cacheResponse.handshake() == null) {
            return new CacheStrategy(this.request, (Response)null);
         } else if (!CacheStrategy.Companion.isCacheable(this.cacheResponse, this.request)) {
            return new CacheStrategy(this.request, (Response)null);
         } else {
            CacheControl requestCaching = this.request.cacheControl();
            if (!requestCaching.noCache() && !this.hasConditions(this.request)) {
               CacheControl responseCaching = this.cacheResponse.cacheControl();
               long ageMillis = this.cacheResponseAge();
               long freshMillis = this.computeFreshnessLifetime();
               if (requestCaching.maxAgeSeconds() != -1) {
                  freshMillis = Math.min(freshMillis, TimeUnit.SECONDS.toMillis((long)requestCaching.maxAgeSeconds()));
               }

               long minFreshMillis = 0L;
               if (requestCaching.minFreshSeconds() != -1) {
                  minFreshMillis = TimeUnit.SECONDS.toMillis((long)requestCaching.minFreshSeconds());
               }

               long maxStaleMillis = 0L;
               if (!responseCaching.mustRevalidate() && requestCaching.maxStaleSeconds() != -1) {
                  maxStaleMillis = TimeUnit.SECONDS.toMillis((long)requestCaching.maxStaleSeconds());
               }

               if (!responseCaching.noCache() && ageMillis + minFreshMillis < freshMillis + maxStaleMillis) {
                  Response.Builder builder = this.cacheResponse.newBuilder();
                  if (ageMillis + minFreshMillis >= freshMillis) {
                     builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
                  }

                  long oneDayMillis = 86400000L;
                  if (ageMillis > oneDayMillis && this.isFreshnessLifetimeHeuristic()) {
                     builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
                  }

                  return new CacheStrategy((Request)null, builder.build());
               } else {
                  String conditionName = null;
                  String conditionValue = null;
                  if (this.etag != null) {
                     conditionName = "If-None-Match";
                     conditionValue = this.etag;
                  } else if (this.lastModified != null) {
                     conditionName = "If-Modified-Since";
                     conditionValue = this.lastModifiedString;
                  } else {
                     if (this.servedDate == null) {
                        return new CacheStrategy(this.request, (Response)null);
                     }

                     conditionName = "If-Modified-Since";
                     conditionValue = this.servedDateString;
                  }

                  Headers.Builder conditionalRequestHeaders = this.request.headers().newBuilder();
                  Intrinsics.checkNotNull(conditionValue);
                  conditionalRequestHeaders.addLenient$okhttp(conditionName, conditionValue);
                  Request conditionalRequest = this.request.newBuilder().headers(conditionalRequestHeaders.build()).build();
                  return new CacheStrategy(conditionalRequest, this.cacheResponse);
               }
            } else {
               return new CacheStrategy(this.request, (Response)null);
            }
         }
      }

      private final long computeFreshnessLifetime() {
         Response var10000 = this.cacheResponse;
         Intrinsics.checkNotNull(var10000);
         CacheControl responseCaching = var10000.cacheControl();
         if (responseCaching.maxAgeSeconds() != -1) {
            return TimeUnit.SECONDS.toMillis((long)responseCaching.maxAgeSeconds());
         } else {
            Date expires = this.expires;
            if (expires != null) {
               Date var10 = this.servedDate;
               long servedMillis = var10 != null ? var10.getTime() : this.receivedResponseMillis;
               long delta = expires.getTime() - servedMillis;
               return delta > 0L ? delta : 0L;
            } else if (this.lastModified != null && this.cacheResponse.request().url().query() == null) {
               Date var9 = this.servedDate;
               long servedMillis = var9 != null ? var9.getTime() : this.sentRequestMillis;
               Date var10001 = this.lastModified;
               Intrinsics.checkNotNull(var10001);
               long delta = servedMillis - var10001.getTime();
               return delta > 0L ? delta / (long)10 : 0L;
            } else {
               return 0L;
            }
         }
      }

      private final long cacheResponseAge() {
         Date servedDate = this.servedDate;
         long apparentReceivedAge = servedDate != null ? Math.max(0L, this.receivedResponseMillis - servedDate.getTime()) : 0L;
         long receivedAge = this.ageSeconds != -1 ? Math.max(apparentReceivedAge, TimeUnit.SECONDS.toMillis((long)this.ageSeconds)) : apparentReceivedAge;
         long responseDuration = Math.max(0L, this.receivedResponseMillis - this.sentRequestMillis);
         long residentDuration = Math.max(0L, this.nowMillis - this.receivedResponseMillis);
         return receivedAge + responseDuration + residentDuration;
      }

      private final boolean hasConditions(Request request) {
         return request.header("If-Modified-Since") != null || request.header("If-None-Match") != null;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t¨\u0006\n"},
      d2 = {"Lokhttp3/internal/cache/CacheStrategy$Companion;", "", "<init>", "()V", "isCacheable", "", "response", "Lokhttp3/Response;", "request", "Lokhttp3/Request;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final boolean isCacheable(@NotNull Response response, @NotNull Request request) {
         Intrinsics.checkNotNullParameter(response, "response");
         Intrinsics.checkNotNullParameter(request, "request");
         switch (response.code()) {
            case 200:
            case 203:
            case 204:
            case 300:
            case 301:
            case 308:
            case 404:
            case 405:
            case 410:
            case 414:
            case 501:
               break;
            case 302:
            case 307:
               if (Response.header$default(response, "Expires", (String)null, 2, (Object)null) == null && response.cacheControl().maxAgeSeconds() == -1 && !response.cacheControl().isPublic() && !response.cacheControl().isPrivate()) {
                  return false;
               }
               break;
            default:
               return false;
         }

         return !response.cacheControl().noStore() && !request.cacheControl().noStore();
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
