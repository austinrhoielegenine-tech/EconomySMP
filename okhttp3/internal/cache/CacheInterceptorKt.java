package okhttp3.internal.cache;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.internal.http.HttpMethod;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0001H\u0002¨\u0006\u0002"},
   d2 = {"requestForCache", "Lokhttp3/Request;", "okhttp"}
)
public final class CacheInterceptorKt {
   private static final Request requestForCache(Request $this$requestForCache) {
      HttpUrl cacheUrlOverride = $this$requestForCache.cacheUrlOverride();
      return cacheUrlOverride == null || !HttpMethod.INSTANCE.isCacheable($this$requestForCache.method()) && !Intrinsics.areEqual((Object)$this$requestForCache.method(), (Object)"POST") ? $this$requestForCache : $this$requestForCache.newBuilder().get().url(cacheUrlOverride).cacheUrlOverride((HttpUrl)null).build();
   }

   // $FF: synthetic method
   public static final Request access$requestForCache(Request $receiver) {
      return requestForCache($receiver);
   }
}
