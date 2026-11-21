package okhttp3;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0014\u0010\u0004\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\u0004\b\u0006\u0010\u0007B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\tJ\u000e\u0010\u000f\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\rJ\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0010J\u001b\u0010\u0004\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0012\u0004\u0012\u00020\u00030\u0005H\u0007¢\u0006\u0002\b\u0011J\u000f\u0010\b\u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\b\u0012J\r\u0010\f\u001a\u00020\rH\u0007¢\u0006\u0002\b\u0013J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u0003H\u0016R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\nR!\u0010\u0004\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0012\u0004\u0012\u00020\u00030\u00058\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000bR\u0013\u0010\b\u001a\u0004\u0018\u00010\u00038G¢\u0006\u0006\u001a\u0004\b\b\u0010\nR\u0011\u0010\f\u001a\u00020\r8G¢\u0006\u0006\u001a\u0004\b\f\u0010\u000e¨\u0006\u001a"},
   d2 = {"Lokhttp3/Challenge;", "", "scheme", "", "authParams", "", "<init>", "(Ljava/lang/String;Ljava/util/Map;)V", "realm", "(Ljava/lang/String;Ljava/lang/String;)V", "()Ljava/lang/String;", "()Ljava/util/Map;", "charset", "Ljava/nio/charset/Charset;", "()Ljava/nio/charset/Charset;", "withCharset", "-deprecated_scheme", "-deprecated_authParams", "-deprecated_realm", "-deprecated_charset", "equals", "", "other", "hashCode", "", "toString", "okhttp"}
)
@SourceDebugExtension({"SMAP\nChallenge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Challenge.kt\nokhttp3/Challenge\n+ 2 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n*L\n1#1,124:1\n250#2:125\n*S KotlinDebug\n*F\n+ 1 Challenge.kt\nokhttp3/Challenge\n*L\n68#1:125\n*E\n"})
public final class Challenge {
   @NotNull
   private final String scheme;
   @NotNull
   private final Map<String, String> authParams;

   public Challenge(@NotNull String scheme, @NotNull Map<String, String> authParams) {
      Intrinsics.checkNotNullParameter(scheme, "scheme");
      Intrinsics.checkNotNullParameter(authParams, "authParams");
      super();
      this.scheme = scheme;
      Map newAuthParams = (Map)(new LinkedHashMap());

      for(Map.Entry var5 : authParams.entrySet()) {
         String key = (String)var5.getKey();
         String value = (String)var5.getValue();
         String var11;
         if (key != null) {
            Locale var10000 = Locale.US;
            Intrinsics.checkNotNullExpressionValue(var10000, "US");
            var11 = key.toLowerCase(var10000);
            Intrinsics.checkNotNullExpressionValue(var11, "toLowerCase(...)");
         } else {
            var11 = null;
         }

         String newKey = var11;
         newAuthParams.put(newKey, value);
      }

      int $i$f$unmodifiable = 0;
      Map var10001 = Collections.unmodifiableMap(newAuthParams);
      Intrinsics.checkNotNullExpressionValue(var10001, "unmodifiableMap(...)");
      this.authParams = var10001;
   }

   @JvmName(
      name = "scheme"
   )
   @NotNull
   public final String scheme() {
      return this.scheme;
   }

   @JvmName(
      name = "authParams"
   )
   @NotNull
   public final Map<String, String> authParams() {
      return this.authParams;
   }

   @JvmName(
      name = "realm"
   )
   @Nullable
   public final String realm() {
      return (String)this.authParams.get("realm");
   }

   @JvmName(
      name = "charset"
   )
   @NotNull
   public final Charset charset() {
      String charset = (String)this.authParams.get("charset");
      if (charset != null) {
         try {
            Charset var10000 = Charset.forName(charset);
            Intrinsics.checkNotNullExpressionValue(var10000, "forName(...)");
            return var10000;
         } catch (Exception var3) {
         }
      }

      return Charsets.ISO_8859_1;
   }

   public Challenge(@NotNull String scheme, @NotNull String realm) {
      Intrinsics.checkNotNullParameter(scheme, "scheme");
      Intrinsics.checkNotNullParameter(realm, "realm");
      Map var10002 = Collections.singletonMap("realm", realm);
      Intrinsics.checkNotNullExpressionValue(var10002, "singletonMap(...)");
      this(scheme, var10002);
   }

   @NotNull
   public final Challenge withCharset(@NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(charset, "charset");
      Map authParams = MapsKt.toMutableMap(this.authParams);
      authParams.put("charset", charset.name());
      return new Challenge(this.scheme, authParams);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "scheme",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_scheme"
   )
   @NotNull
   public final String _deprecated_scheme/* $FF was: -deprecated_scheme*/() {
      return this.scheme;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "authParams",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_authParams"
   )
   @NotNull
   public final Map<String, String> _deprecated_authParams/* $FF was: -deprecated_authParams*/() {
      return this.authParams;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "realm",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_realm"
   )
   @Nullable
   public final String _deprecated_realm/* $FF was: -deprecated_realm*/() {
      return this.realm();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "charset",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_charset"
   )
   @NotNull
   public final Charset _deprecated_charset/* $FF was: -deprecated_charset*/() {
      return this.charset();
   }

   public boolean equals(@Nullable Object other) {
      return other instanceof Challenge && Intrinsics.areEqual((Object)((Challenge)other).scheme, (Object)this.scheme) && Intrinsics.areEqual((Object)((Challenge)other).authParams, (Object)this.authParams);
   }

   public int hashCode() {
      int result = 29;
      result = 31 * result + this.scheme.hashCode();
      result = 31 * result + this.authParams.hashCode();
      return result;
   }

   @NotNull
   public String toString() {
      return this.scheme + " authParams=" + this.authParams;
   }
}
