package okhttp3.internal.http;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\t\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u000e\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u0007¨\u0006\u000e"},
   d2 = {"Lokhttp3/internal/http/HttpMethod;", "", "<init>", "()V", "invalidatesCache", "", "method", "", "requiresRequestBody", "permitsRequestBody", "redirectsWithBody", "redirectsToGet", "isCacheable", "requestMethod", "okhttp"}
)
public final class HttpMethod {
   @NotNull
   public static final HttpMethod INSTANCE = new HttpMethod();

   private HttpMethod() {
   }

   @JvmStatic
   public static final boolean invalidatesCache(@NotNull String method) {
      Intrinsics.checkNotNullParameter(method, "method");
      return Intrinsics.areEqual((Object)method, (Object)"POST") || Intrinsics.areEqual((Object)method, (Object)"PATCH") || Intrinsics.areEqual((Object)method, (Object)"PUT") || Intrinsics.areEqual((Object)method, (Object)"DELETE") || Intrinsics.areEqual((Object)method, (Object)"MOVE");
   }

   @JvmStatic
   public static final boolean requiresRequestBody(@NotNull String method) {
      Intrinsics.checkNotNullParameter(method, "method");
      return Intrinsics.areEqual((Object)method, (Object)"POST") || Intrinsics.areEqual((Object)method, (Object)"PUT") || Intrinsics.areEqual((Object)method, (Object)"PATCH") || Intrinsics.areEqual((Object)method, (Object)"PROPPATCH") || Intrinsics.areEqual((Object)method, (Object)"QUERY") || Intrinsics.areEqual((Object)method, (Object)"REPORT");
   }

   @JvmStatic
   public static final boolean permitsRequestBody(@NotNull String method) {
      Intrinsics.checkNotNullParameter(method, "method");
      return !Intrinsics.areEqual((Object)method, (Object)"GET") && !Intrinsics.areEqual((Object)method, (Object)"HEAD");
   }

   public final boolean redirectsWithBody(@NotNull String method) {
      Intrinsics.checkNotNullParameter(method, "method");
      return Intrinsics.areEqual((Object)method, (Object)"PROPFIND");
   }

   public final boolean redirectsToGet(@NotNull String method) {
      Intrinsics.checkNotNullParameter(method, "method");
      return !Intrinsics.areEqual((Object)method, (Object)"PROPFIND");
   }

   public final boolean isCacheable(@NotNull String requestMethod) {
      Intrinsics.checkNotNullParameter(requestMethod, "requestMethod");
      return Intrinsics.areEqual((Object)requestMethod, (Object)"GET") || Intrinsics.areEqual((Object)requestMethod, (Object)"QUERY");
   }
}
