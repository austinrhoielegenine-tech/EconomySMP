package okhttp3.internal;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationKt;
import kotlin.time.DurationUnit;
import okhttp3.CacheControl;
import okhttp3.Headers;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000.\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\f\u0010\u0003\u001a\u00020\u0004*\u00020\u0005H\u0000\u001a\f\u0010\u0006\u001a\u00020\u0002*\u00020\u0007H\u0000\u001a\f\u0010\b\u001a\u00020\u0002*\u00020\u0007H\u0000\u001a\f\u0010\t\u001a\u00020\u0002*\u00020\nH\u0000\u001a\f\u0010\u000b\u001a\u00020\n*\u00020\nH\u0000\u001a\f\u0010\f\u001a\u00020\n*\u00020\nH\u0000\u001a\f\u0010\r\u001a\u00020\n*\u00020\nH\u0000\u001a\f\u0010\u000e\u001a\u00020\n*\u00020\nH\u0000\u001a\f\u0010\u000f\u001a\u00020\n*\u00020\nH\u0000\u001a\u0014\u0010\u0010\u001a\u00020\u0002*\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0012H\u0000\u001a\u001e\u0010\u0013\u001a\u00020\u0004*\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00012\b\b\u0002\u0010\u0015\u001a\u00020\u0004H\u0002¨\u0006\u0016"},
   d2 = {"commonToString", "", "Lokhttp3/CacheControl;", "commonClampToInt", "", "", "commonForceNetwork", "Lokhttp3/CacheControl$Companion;", "commonForceCache", "commonBuild", "Lokhttp3/CacheControl$Builder;", "commonNoCache", "commonNoStore", "commonOnlyIfCached", "commonNoTransform", "commonImmutable", "commonParse", "headers", "Lokhttp3/Headers;", "indexOfElement", "characters", "startIndex", "okhttp"}
)
public final class _CacheControlCommonKt {
   @NotNull
   public static final String commonToString(@NotNull CacheControl $this$commonToString) {
      Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
      String result = $this$commonToString.getHeaderValue$okhttp();
      if (result == null) {
         StringBuilder var2 = new StringBuilder();
         int var4 = 0;
         if ($this$commonToString.noCache()) {
            var2.append("no-cache, ");
         }

         if ($this$commonToString.noStore()) {
            var2.append("no-store, ");
         }

         if ($this$commonToString.maxAgeSeconds() != -1) {
            var2.append("max-age=").append($this$commonToString.maxAgeSeconds()).append(", ");
         }

         if ($this$commonToString.sMaxAgeSeconds() != -1) {
            var2.append("s-maxage=").append($this$commonToString.sMaxAgeSeconds()).append(", ");
         }

         if ($this$commonToString.isPrivate()) {
            var2.append("private, ");
         }

         if ($this$commonToString.isPublic()) {
            var2.append("public, ");
         }

         if ($this$commonToString.mustRevalidate()) {
            var2.append("must-revalidate, ");
         }

         if ($this$commonToString.maxStaleSeconds() != -1) {
            var2.append("max-stale=").append($this$commonToString.maxStaleSeconds()).append(", ");
         }

         if ($this$commonToString.minFreshSeconds() != -1) {
            var2.append("min-fresh=").append($this$commonToString.minFreshSeconds()).append(", ");
         }

         if ($this$commonToString.onlyIfCached()) {
            var2.append("only-if-cached, ");
         }

         if ($this$commonToString.noTransform()) {
            var2.append("no-transform, ");
         }

         if ($this$commonToString.immutable()) {
            var2.append("immutable, ");
         }

         if (((CharSequence)var2).length() == 0) {
            return "";
         }

         Intrinsics.checkNotNullExpressionValue(var2.delete(var2.length() - 2, var2.length()), "delete(...)");
         result = var2.toString();
         $this$commonToString.setHeaderValue$okhttp(result);
      }

      return result;
   }

   public static final int commonClampToInt(long $this$commonClampToInt) {
      return $this$commonClampToInt > 2147483647L ? Integer.MAX_VALUE : (int)$this$commonClampToInt;
   }

   @NotNull
   public static final CacheControl commonForceNetwork(@NotNull CacheControl.Companion $this$commonForceNetwork) {
      Intrinsics.checkNotNullParameter($this$commonForceNetwork, "<this>");
      return (new CacheControl.Builder()).noCache().build();
   }

   @NotNull
   public static final CacheControl commonForceCache(@NotNull CacheControl.Companion $this$commonForceCache) {
      Intrinsics.checkNotNullParameter($this$commonForceCache, "<this>");
      CacheControl.Builder var10000 = (new CacheControl.Builder()).onlyIfCached();
      Duration.Companion var10001 = Duration.Companion;
      return var10000.maxStale-LRDsOJo(DurationKt.toDuration(Integer.MAX_VALUE, DurationUnit.SECONDS)).build();
   }

   @NotNull
   public static final CacheControl commonBuild(@NotNull CacheControl.Builder $this$commonBuild) {
      Intrinsics.checkNotNullParameter($this$commonBuild, "<this>");
      return new CacheControl($this$commonBuild.getNoCache$okhttp(), $this$commonBuild.getNoStore$okhttp(), $this$commonBuild.getMaxAgeSeconds$okhttp(), -1, false, false, false, $this$commonBuild.getMaxStaleSeconds$okhttp(), $this$commonBuild.getMinFreshSeconds$okhttp(), $this$commonBuild.getOnlyIfCached$okhttp(), $this$commonBuild.getNoTransform$okhttp(), $this$commonBuild.getImmutable$okhttp(), (String)null);
   }

   @NotNull
   public static final CacheControl.Builder commonNoCache(@NotNull CacheControl.Builder $this$commonNoCache) {
      Intrinsics.checkNotNullParameter($this$commonNoCache, "<this>");
      int var3 = 0;
      $this$commonNoCache.setNoCache$okhttp(true);
      return $this$commonNoCache;
   }

   @NotNull
   public static final CacheControl.Builder commonNoStore(@NotNull CacheControl.Builder $this$commonNoStore) {
      Intrinsics.checkNotNullParameter($this$commonNoStore, "<this>");
      int var3 = 0;
      $this$commonNoStore.setNoStore$okhttp(true);
      return $this$commonNoStore;
   }

   @NotNull
   public static final CacheControl.Builder commonOnlyIfCached(@NotNull CacheControl.Builder $this$commonOnlyIfCached) {
      Intrinsics.checkNotNullParameter($this$commonOnlyIfCached, "<this>");
      int var3 = 0;
      $this$commonOnlyIfCached.setOnlyIfCached$okhttp(true);
      return $this$commonOnlyIfCached;
   }

   @NotNull
   public static final CacheControl.Builder commonNoTransform(@NotNull CacheControl.Builder $this$commonNoTransform) {
      Intrinsics.checkNotNullParameter($this$commonNoTransform, "<this>");
      int var3 = 0;
      $this$commonNoTransform.setNoTransform$okhttp(true);
      return $this$commonNoTransform;
   }

   @NotNull
   public static final CacheControl.Builder commonImmutable(@NotNull CacheControl.Builder $this$commonImmutable) {
      Intrinsics.checkNotNullParameter($this$commonImmutable, "<this>");
      int var3 = 0;
      $this$commonImmutable.setImmutable$okhttp(true);
      return $this$commonImmutable;
   }

   @NotNull
   public static final CacheControl commonParse(@NotNull CacheControl.Companion $this$commonParse, @NotNull Headers headers) {
      Intrinsics.checkNotNullParameter($this$commonParse, "<this>");
      Intrinsics.checkNotNullParameter(headers, "headers");
      boolean noCache = false;
      boolean noStore = false;
      int maxAgeSeconds = -1;
      int sMaxAgeSeconds = -1;
      boolean isPrivate = false;
      boolean isPublic = false;
      boolean mustRevalidate = false;
      int maxStaleSeconds = -1;
      int minFreshSeconds = -1;
      boolean onlyIfCached = false;
      boolean noTransform = false;
      boolean immutable = false;
      boolean canUseHeaderValue = true;
      String headerValue = null;
      int i = 0;

      for(int var17 = headers.size(); i < var17; ++i) {
         String name = headers.name(i);
         String value = headers.value(i);
         if (StringsKt.equals(name, "Cache-Control", true)) {
            if (headerValue != null) {
               canUseHeaderValue = false;
            } else {
               headerValue = value;
            }
         } else {
            if (!StringsKt.equals(name, "Pragma", true)) {
               continue;
            }

            canUseHeaderValue = false;
         }

         int pos = 0;

         while(pos < value.length()) {
            int tokenStart = pos;
            pos = indexOfElement(value, "=,;", pos);
            String var10000 = value.substring(tokenStart, pos);
            Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
            String directive = StringsKt.trim((CharSequence)var10000).toString();
            String parameter = null;
            if (pos != value.length() && value.charAt(pos) != ',' && value.charAt(pos) != ';') {
               ++pos;
               pos = _UtilCommonKt.indexOfNonWhitespace(value, pos);
               if (pos < value.length() && value.charAt(pos) == '"') {
                  ++pos;
                  pos = StringsKt.indexOf$default((CharSequence)value, '"', pos, false, 4, (Object)null);
                  var10000 = value.substring(pos, pos);
                  Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
                  parameter = var10000;
                  ++pos;
               } else {
                  pos = indexOfElement(value, ",;", pos);
                  var10000 = value.substring(pos, pos);
                  Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
                  parameter = StringsKt.trim((CharSequence)var10000).toString();
               }
            } else {
               ++pos;
               parameter = null;
            }

            if (StringsKt.equals("no-cache", directive, true)) {
               noCache = true;
            } else if (StringsKt.equals("no-store", directive, true)) {
               noStore = true;
            } else if (StringsKt.equals("max-age", directive, true)) {
               maxAgeSeconds = _UtilCommonKt.toNonNegativeInt(parameter, -1);
            } else if (StringsKt.equals("s-maxage", directive, true)) {
               sMaxAgeSeconds = _UtilCommonKt.toNonNegativeInt(parameter, -1);
            } else if (StringsKt.equals("private", directive, true)) {
               isPrivate = true;
            } else if (StringsKt.equals("public", directive, true)) {
               isPublic = true;
            } else if (StringsKt.equals("must-revalidate", directive, true)) {
               mustRevalidate = true;
            } else if (StringsKt.equals("max-stale", directive, true)) {
               maxStaleSeconds = _UtilCommonKt.toNonNegativeInt(parameter, Integer.MAX_VALUE);
            } else if (StringsKt.equals("min-fresh", directive, true)) {
               minFreshSeconds = _UtilCommonKt.toNonNegativeInt(parameter, -1);
            } else if (StringsKt.equals("only-if-cached", directive, true)) {
               onlyIfCached = true;
            } else if (StringsKt.equals("no-transform", directive, true)) {
               noTransform = true;
            } else if (StringsKt.equals("immutable", directive, true)) {
               immutable = true;
            }
         }
      }

      if (!canUseHeaderValue) {
         headerValue = null;
      }

      return new CacheControl(noCache, noStore, maxAgeSeconds, sMaxAgeSeconds, isPrivate, isPublic, mustRevalidate, maxStaleSeconds, minFreshSeconds, onlyIfCached, noTransform, immutable, headerValue);
   }

   private static final int indexOfElement(String $this$indexOfElement, String characters, int startIndex) {
      int i = startIndex;

      for(int var4 = $this$indexOfElement.length(); i < var4; ++i) {
         if (StringsKt.contains$default((CharSequence)characters, $this$indexOfElement.charAt(i), false, 2, (Object)null)) {
            return i;
         }
      }

      return $this$indexOfElement.length();
   }

   // $FF: synthetic method
   static int indexOfElement$default(String var0, String var1, int var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = 0;
      }

      return indexOfElement(var0, var1, var2);
   }
}
