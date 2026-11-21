package okhttp3;

import java.util.concurrent.TimeUnit;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.time.Duration;
import okhttp3.internal._CacheControlCommonKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0016\u0018\u0000 &2\u00020\u0001:\u0002%&Bs\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\u0006\u0010\f\u001a\u00020\u0006\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011¢\u0006\u0004\b\u0012\u0010\u0013J\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001aJ\r\u0010\u0004\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001bJ\r\u0010\u0005\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u001cJ\r\u0010\u0007\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u001dJ\r\u0010\n\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001eJ\r\u0010\u000b\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u001fJ\r\u0010\f\u001a\u00020\u0006H\u0007¢\u0006\u0002\b J\r\u0010\r\u001a\u00020\u0003H\u0007¢\u0006\u0002\b!J\r\u0010\u000e\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\"J\r\u0010\u000f\u001a\u00020\u0003H\u0007¢\u0006\u0002\b#J\b\u0010$\u001a\u00020\u0011H\u0016R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0014R\u0013\u0010\u0004\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0014R\u0013\u0010\u0005\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0015R\u0013\u0010\u0007\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0015R\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0014R\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0014R\u0013\u0010\n\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0014R\u0013\u0010\u000b\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0015R\u0013\u0010\f\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0015R\u0013\u0010\r\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0014R\u0013\u0010\u000e\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0014R\u0013\u0010\u000f\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0014R\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019¨\u0006'"},
   d2 = {"Lokhttp3/CacheControl;", "", "noCache", "", "noStore", "maxAgeSeconds", "", "sMaxAgeSeconds", "isPrivate", "isPublic", "mustRevalidate", "maxStaleSeconds", "minFreshSeconds", "onlyIfCached", "noTransform", "immutable", "headerValue", "", "<init>", "(ZZIIZZZIIZZZLjava/lang/String;)V", "()Z", "()I", "getHeaderValue$okhttp", "()Ljava/lang/String;", "setHeaderValue$okhttp", "(Ljava/lang/String;)V", "-deprecated_noCache", "-deprecated_noStore", "-deprecated_maxAgeSeconds", "-deprecated_sMaxAgeSeconds", "-deprecated_mustRevalidate", "-deprecated_maxStaleSeconds", "-deprecated_minFreshSeconds", "-deprecated_onlyIfCached", "-deprecated_noTransform", "-deprecated_immutable", "toString", "Builder", "Companion", "okhttp"}
)
public final class CacheControl {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   private final boolean noCache;
   private final boolean noStore;
   private final int maxAgeSeconds;
   private final int sMaxAgeSeconds;
   private final boolean isPrivate;
   private final boolean isPublic;
   private final boolean mustRevalidate;
   private final int maxStaleSeconds;
   private final int minFreshSeconds;
   private final boolean onlyIfCached;
   private final boolean noTransform;
   private final boolean immutable;
   @Nullable
   private String headerValue;
   @JvmField
   @NotNull
   public static final CacheControl FORCE_NETWORK;
   @JvmField
   @NotNull
   public static final CacheControl FORCE_CACHE;

   public CacheControl(boolean noCache, boolean noStore, int maxAgeSeconds, int sMaxAgeSeconds, boolean isPrivate, boolean isPublic, boolean mustRevalidate, int maxStaleSeconds, int minFreshSeconds, boolean onlyIfCached, boolean noTransform, boolean immutable, @Nullable String headerValue) {
      this.noCache = noCache;
      this.noStore = noStore;
      this.maxAgeSeconds = maxAgeSeconds;
      this.sMaxAgeSeconds = sMaxAgeSeconds;
      this.isPrivate = isPrivate;
      this.isPublic = isPublic;
      this.mustRevalidate = mustRevalidate;
      this.maxStaleSeconds = maxStaleSeconds;
      this.minFreshSeconds = minFreshSeconds;
      this.onlyIfCached = onlyIfCached;
      this.noTransform = noTransform;
      this.immutable = immutable;
      this.headerValue = headerValue;
   }

   @JvmName(
      name = "noCache"
   )
   public final boolean noCache() {
      return this.noCache;
   }

   @JvmName(
      name = "noStore"
   )
   public final boolean noStore() {
      return this.noStore;
   }

   @JvmName(
      name = "maxAgeSeconds"
   )
   public final int maxAgeSeconds() {
      return this.maxAgeSeconds;
   }

   @JvmName(
      name = "sMaxAgeSeconds"
   )
   public final int sMaxAgeSeconds() {
      return this.sMaxAgeSeconds;
   }

   public final boolean isPrivate() {
      return this.isPrivate;
   }

   public final boolean isPublic() {
      return this.isPublic;
   }

   @JvmName(
      name = "mustRevalidate"
   )
   public final boolean mustRevalidate() {
      return this.mustRevalidate;
   }

   @JvmName(
      name = "maxStaleSeconds"
   )
   public final int maxStaleSeconds() {
      return this.maxStaleSeconds;
   }

   @JvmName(
      name = "minFreshSeconds"
   )
   public final int minFreshSeconds() {
      return this.minFreshSeconds;
   }

   @JvmName(
      name = "onlyIfCached"
   )
   public final boolean onlyIfCached() {
      return this.onlyIfCached;
   }

   @JvmName(
      name = "noTransform"
   )
   public final boolean noTransform() {
      return this.noTransform;
   }

   @JvmName(
      name = "immutable"
   )
   public final boolean immutable() {
      return this.immutable;
   }

   @Nullable
   public final String getHeaderValue$okhttp() {
      return this.headerValue;
   }

   public final void setHeaderValue$okhttp(@Nullable String var1) {
      this.headerValue = var1;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "noCache",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_noCache"
   )
   public final boolean _deprecated_noCache/* $FF was: -deprecated_noCache*/() {
      return this.noCache;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "noStore",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_noStore"
   )
   public final boolean _deprecated_noStore/* $FF was: -deprecated_noStore*/() {
      return this.noStore;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "maxAgeSeconds",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_maxAgeSeconds"
   )
   public final int _deprecated_maxAgeSeconds/* $FF was: -deprecated_maxAgeSeconds*/() {
      return this.maxAgeSeconds;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "sMaxAgeSeconds",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_sMaxAgeSeconds"
   )
   public final int _deprecated_sMaxAgeSeconds/* $FF was: -deprecated_sMaxAgeSeconds*/() {
      return this.sMaxAgeSeconds;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "mustRevalidate",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_mustRevalidate"
   )
   public final boolean _deprecated_mustRevalidate/* $FF was: -deprecated_mustRevalidate*/() {
      return this.mustRevalidate;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "maxStaleSeconds",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_maxStaleSeconds"
   )
   public final int _deprecated_maxStaleSeconds/* $FF was: -deprecated_maxStaleSeconds*/() {
      return this.maxStaleSeconds;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "minFreshSeconds",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_minFreshSeconds"
   )
   public final int _deprecated_minFreshSeconds/* $FF was: -deprecated_minFreshSeconds*/() {
      return this.minFreshSeconds;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "onlyIfCached",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_onlyIfCached"
   )
   public final boolean _deprecated_onlyIfCached/* $FF was: -deprecated_onlyIfCached*/() {
      return this.onlyIfCached;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "noTransform",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_noTransform"
   )
   public final boolean _deprecated_noTransform/* $FF was: -deprecated_noTransform*/() {
      return this.noTransform;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "immutable",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_immutable"
   )
   public final boolean _deprecated_immutable/* $FF was: -deprecated_immutable*/() {
      return this.immutable;
   }

   @NotNull
   public String toString() {
      return _CacheControlCommonKt.commonToString(this);
   }

   @JvmStatic
   @NotNull
   public static final CacheControl parse(@NotNull Headers headers) {
      return Companion.parse(headers);
   }

   static {
      FORCE_NETWORK = _CacheControlCommonKt.commonForceNetwork(Companion);
      FORCE_CACHE = _CacheControlCommonKt.commonForceCache(Companion);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0000J\u0006\u0010\n\u001a\u00020\u0000J\u0006\u0010\u0019\u001a\u00020\u0000J\u0006\u0010\u001c\u001a\u00020\u0000J\u0006\u0010\u001f\u001a\u00020\u0000J\u0015\u0010\"\u001a\u00020\u00002\u0006\u0010\"\u001a\u00020#¢\u0006\u0004\b$\u0010%J\u0015\u0010&\u001a\u00020\u00002\u0006\u0010&\u001a\u00020#¢\u0006\u0004\b'\u0010%J\u0015\u0010(\u001a\u00020\u00002\u0006\u0010(\u001a\u00020#¢\u0006\u0004\b)\u0010%J\u0016\u0010\"\u001a\u00020\u00002\u0006\u0010\"\u001a\u00020\u000e2\u0006\u0010*\u001a\u00020+J\u0016\u0010&\u001a\u00020\u00002\u0006\u0010&\u001a\u00020\u000e2\u0006\u0010*\u001a\u00020+J\u0016\u0010(\u001a\u00020\u00002\u0006\u0010(\u001a\u00020\u000e2\u0006\u0010*\u001a\u00020+J\u0006\u0010,\u001a\u00020-R\u001a\u0010\u0004\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\u0007\"\u0004\b\f\u0010\tR\u001a\u0010\r\u001a\u00020\u000eX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0013\u001a\u00020\u000eX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0010\"\u0004\b\u0015\u0010\u0012R\u001a\u0010\u0016\u001a\u00020\u000eX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0010\"\u0004\b\u0018\u0010\u0012R\u001a\u0010\u0019\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0007\"\u0004\b\u001b\u0010\tR\u001a\u0010\u001c\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0007\"\u0004\b\u001e\u0010\tR\u001a\u0010\u001f\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u0007\"\u0004\b!\u0010\t¨\u0006."},
      d2 = {"Lokhttp3/CacheControl$Builder;", "", "<init>", "()V", "noCache", "", "getNoCache$okhttp", "()Z", "setNoCache$okhttp", "(Z)V", "noStore", "getNoStore$okhttp", "setNoStore$okhttp", "maxAgeSeconds", "", "getMaxAgeSeconds$okhttp", "()I", "setMaxAgeSeconds$okhttp", "(I)V", "maxStaleSeconds", "getMaxStaleSeconds$okhttp", "setMaxStaleSeconds$okhttp", "minFreshSeconds", "getMinFreshSeconds$okhttp", "setMinFreshSeconds$okhttp", "onlyIfCached", "getOnlyIfCached$okhttp", "setOnlyIfCached$okhttp", "noTransform", "getNoTransform$okhttp", "setNoTransform$okhttp", "immutable", "getImmutable$okhttp", "setImmutable$okhttp", "maxAge", "Lkotlin/time/Duration;", "maxAge-LRDsOJo", "(J)Lokhttp3/CacheControl$Builder;", "maxStale", "maxStale-LRDsOJo", "minFresh", "minFresh-LRDsOJo", "timeUnit", "Ljava/util/concurrent/TimeUnit;", "build", "Lokhttp3/CacheControl;", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nCacheControl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CacheControl.kt\nokhttp3/CacheControl$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,286:1\n1#2:287\n*E\n"})
   public static final class Builder {
      private boolean noCache;
      private boolean noStore;
      private int maxAgeSeconds = -1;
      private int maxStaleSeconds = -1;
      private int minFreshSeconds = -1;
      private boolean onlyIfCached;
      private boolean noTransform;
      private boolean immutable;

      public final boolean getNoCache$okhttp() {
         return this.noCache;
      }

      public final void setNoCache$okhttp(boolean var1) {
         this.noCache = var1;
      }

      public final boolean getNoStore$okhttp() {
         return this.noStore;
      }

      public final void setNoStore$okhttp(boolean var1) {
         this.noStore = var1;
      }

      public final int getMaxAgeSeconds$okhttp() {
         return this.maxAgeSeconds;
      }

      public final void setMaxAgeSeconds$okhttp(int var1) {
         this.maxAgeSeconds = var1;
      }

      public final int getMaxStaleSeconds$okhttp() {
         return this.maxStaleSeconds;
      }

      public final void setMaxStaleSeconds$okhttp(int var1) {
         this.maxStaleSeconds = var1;
      }

      public final int getMinFreshSeconds$okhttp() {
         return this.minFreshSeconds;
      }

      public final void setMinFreshSeconds$okhttp(int var1) {
         this.minFreshSeconds = var1;
      }

      public final boolean getOnlyIfCached$okhttp() {
         return this.onlyIfCached;
      }

      public final void setOnlyIfCached$okhttp(boolean var1) {
         this.onlyIfCached = var1;
      }

      public final boolean getNoTransform$okhttp() {
         return this.noTransform;
      }

      public final void setNoTransform$okhttp(boolean var1) {
         this.noTransform = var1;
      }

      public final boolean getImmutable$okhttp() {
         return this.immutable;
      }

      public final void setImmutable$okhttp(boolean var1) {
         this.immutable = var1;
      }

      @NotNull
      public final Builder noCache() {
         return _CacheControlCommonKt.commonNoCache(this);
      }

      @NotNull
      public final Builder noStore() {
         return _CacheControlCommonKt.commonNoStore(this);
      }

      @NotNull
      public final Builder onlyIfCached() {
         return _CacheControlCommonKt.commonOnlyIfCached(this);
      }

      @NotNull
      public final Builder noTransform() {
         return _CacheControlCommonKt.commonNoTransform(this);
      }

      @NotNull
      public final Builder immutable() {
         return _CacheControlCommonKt.commonImmutable(this);
      }

      @NotNull
      public final Builder maxAge_LRDsOJo/* $FF was: maxAge-LRDsOJo*/(long var1) {
         Builder $this$maxAge_LRDsOJo_u24lambda_u240 = this;
         int var5 = 0;
         long maxAgeSeconds = Duration.getInWholeSeconds-impl(var1);
         if (maxAgeSeconds < 0L) {
            int var8 = 0;
            String var9 = "maxAge < 0: " + maxAgeSeconds;
            throw new IllegalArgumentException(var9.toString());
         } else {
            $this$maxAge_LRDsOJo_u24lambda_u240.maxAgeSeconds = _CacheControlCommonKt.commonClampToInt(maxAgeSeconds);
            return this;
         }
      }

      @NotNull
      public final Builder maxStale_LRDsOJo/* $FF was: maxStale-LRDsOJo*/(long var1) {
         Builder $this$maxStale_LRDsOJo_u24lambda_u240 = this;
         int var5 = 0;
         long maxStaleSeconds = Duration.getInWholeSeconds-impl(var1);
         if (maxStaleSeconds < 0L) {
            int var8 = 0;
            String var9 = "maxStale < 0: " + maxStaleSeconds;
            throw new IllegalArgumentException(var9.toString());
         } else {
            $this$maxStale_LRDsOJo_u24lambda_u240.maxStaleSeconds = _CacheControlCommonKt.commonClampToInt(maxStaleSeconds);
            return this;
         }
      }

      @NotNull
      public final Builder minFresh_LRDsOJo/* $FF was: minFresh-LRDsOJo*/(long var1) {
         Builder $this$minFresh_LRDsOJo_u24lambda_u240 = this;
         int var5 = 0;
         long minFreshSeconds = Duration.getInWholeSeconds-impl(var1);
         if (minFreshSeconds < 0L) {
            int var8 = 0;
            String var9 = "minFresh < 0: " + minFreshSeconds;
            throw new IllegalArgumentException(var9.toString());
         } else {
            $this$minFresh_LRDsOJo_u24lambda_u240.minFreshSeconds = _CacheControlCommonKt.commonClampToInt(minFreshSeconds);
            return this;
         }
      }

      @NotNull
      public final Builder maxAge(int maxAge, @NotNull TimeUnit timeUnit) {
         Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
         Builder $this$maxAge_u24lambda_u240 = this;
         int var5 = 0;
         if (maxAge < 0) {
            int var6 = 0;
            String var9 = "maxAge < 0: " + maxAge;
            throw new IllegalArgumentException(var9.toString());
         } else {
            long maxAgeSecondsLong = timeUnit.toSeconds((long)maxAge);
            $this$maxAge_u24lambda_u240.maxAgeSeconds = _CacheControlCommonKt.commonClampToInt(maxAgeSecondsLong);
            return this;
         }
      }

      @NotNull
      public final Builder maxStale(int maxStale, @NotNull TimeUnit timeUnit) {
         Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
         Builder $this$maxStale_u24lambda_u240 = this;
         int var5 = 0;
         if (maxStale < 0) {
            int var6 = 0;
            String var9 = "maxStale < 0: " + maxStale;
            throw new IllegalArgumentException(var9.toString());
         } else {
            long maxStaleSecondsLong = timeUnit.toSeconds((long)maxStale);
            $this$maxStale_u24lambda_u240.maxStaleSeconds = _CacheControlCommonKt.commonClampToInt(maxStaleSecondsLong);
            return this;
         }
      }

      @NotNull
      public final Builder minFresh(int minFresh, @NotNull TimeUnit timeUnit) {
         Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
         Builder $this$minFresh_u24lambda_u240 = this;
         int var5 = 0;
         if (minFresh < 0) {
            int var6 = 0;
            String var9 = "minFresh < 0: " + minFresh;
            throw new IllegalArgumentException(var9.toString());
         } else {
            long minFreshSecondsLong = timeUnit.toSeconds((long)minFresh);
            $this$minFresh_u24lambda_u240.minFreshSeconds = _CacheControlCommonKt.commonClampToInt(minFreshSecondsLong);
            return this;
         }
      }

      @NotNull
      public final CacheControl build() {
         return _CacheControlCommonKt.commonBuild(this);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\u0007R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\n"},
      d2 = {"Lokhttp3/CacheControl$Companion;", "", "<init>", "()V", "FORCE_NETWORK", "Lokhttp3/CacheControl;", "FORCE_CACHE", "parse", "headers", "Lokhttp3/Headers;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final CacheControl parse(@NotNull Headers headers) {
         Intrinsics.checkNotNullParameter(headers, "headers");
         return _CacheControlCommonKt.commonParse(this, headers);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
