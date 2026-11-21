package okhttp3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.internal._HostnamesCommonKt;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.http.DateFormattingKt;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 *2\u00020\u0001:\u0002)*B[\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\u0006\u0010\f\u001a\u00020\n\u0012\u0006\u0010\r\u001a\u00020\n\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u000f\u0010\u0010J\u000e\u0010\u0014\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\u0016J\u0013\u0010\u0017\u001a\u00020\n2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u0019\u001a\u00020\u001aH\u0017J\b\u0010\u001b\u001a\u00020\u0003H\u0016J\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001cJ\r\u0010\u0004\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001dJ\r\u0010\f\u001a\u00020\nH\u0007¢\u0006\u0002\b\u001eJ\r\u0010\u0005\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u001fJ\r\u0010\r\u001a\u00020\nH\u0007¢\u0006\u0002\b J\r\u0010\u0007\u001a\u00020\u0003H\u0007¢\u0006\u0002\b!J\r\u0010\b\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\"J\r\u0010\u000b\u001a\u00020\nH\u0007¢\u0006\u0002\b#J\r\u0010\t\u001a\u00020\nH\u0007¢\u0006\u0002\b$J\u0015\u0010\u001b\u001a\u00020\u00032\u0006\u0010%\u001a\u00020\nH\u0000¢\u0006\u0002\b&J\u0006\u0010'\u001a\u00020(R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0011R\u0013\u0010\u0004\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0011R\u0013\u0010\u0005\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0012R\u0013\u0010\u0007\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0011R\u0013\u0010\b\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0011R\u0013\u0010\t\u001a\u00020\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0013R\u0013\u0010\u000b\u001a\u00020\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0013R\u0013\u0010\f\u001a\u00020\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0013R\u0013\u0010\r\u001a\u00020\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0013R\u0015\u0010\u000e\u001a\u0004\u0018\u00010\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0011¨\u0006+"},
   d2 = {"Lokhttp3/Cookie;", "", "name", "", "value", "expiresAt", "", "domain", "path", "secure", "", "httpOnly", "persistent", "hostOnly", "sameSite", "<init>", "(Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;ZZZZLjava/lang/String;)V", "()Ljava/lang/String;", "()J", "()Z", "matches", "url", "Lokhttp3/HttpUrl;", "equals", "other", "hashCode", "", "toString", "-deprecated_name", "-deprecated_value", "-deprecated_persistent", "-deprecated_expiresAt", "-deprecated_hostOnly", "-deprecated_domain", "-deprecated_path", "-deprecated_httpOnly", "-deprecated_secure", "forObsoleteRfc2965", "toString$okhttp", "newBuilder", "Lokhttp3/Cookie$Builder;", "Builder", "Companion", "okhttp"}
)
public final class Cookie {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final String name;
   @NotNull
   private final String value;
   private final long expiresAt;
   @NotNull
   private final String domain;
   @NotNull
   private final String path;
   private final boolean secure;
   private final boolean httpOnly;
   private final boolean persistent;
   private final boolean hostOnly;
   @Nullable
   private final String sameSite;
   private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
   private static final Pattern MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
   private static final Pattern DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
   private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");

   private Cookie(String name, String value, long expiresAt, String domain, String path, boolean secure, boolean httpOnly, boolean persistent, boolean hostOnly, String sameSite) {
      this.name = name;
      this.value = value;
      this.expiresAt = expiresAt;
      this.domain = domain;
      this.path = path;
      this.secure = secure;
      this.httpOnly = httpOnly;
      this.persistent = persistent;
      this.hostOnly = hostOnly;
      this.sameSite = sameSite;
   }

   @JvmName(
      name = "name"
   )
   @NotNull
   public final String name() {
      return this.name;
   }

   @JvmName(
      name = "value"
   )
   @NotNull
   public final String value() {
      return this.value;
   }

   @JvmName(
      name = "expiresAt"
   )
   public final long expiresAt() {
      return this.expiresAt;
   }

   @JvmName(
      name = "domain"
   )
   @NotNull
   public final String domain() {
      return this.domain;
   }

   @JvmName(
      name = "path"
   )
   @NotNull
   public final String path() {
      return this.path;
   }

   @JvmName(
      name = "secure"
   )
   public final boolean secure() {
      return this.secure;
   }

   @JvmName(
      name = "httpOnly"
   )
   public final boolean httpOnly() {
      return this.httpOnly;
   }

   @JvmName(
      name = "persistent"
   )
   public final boolean persistent() {
      return this.persistent;
   }

   @JvmName(
      name = "hostOnly"
   )
   public final boolean hostOnly() {
      return this.hostOnly;
   }

   @JvmName(
      name = "sameSite"
   )
   @Nullable
   public final String sameSite() {
      return this.sameSite;
   }

   public final boolean matches(@NotNull HttpUrl url) {
      Intrinsics.checkNotNullParameter(url, "url");
      boolean domainMatch = this.hostOnly ? Intrinsics.areEqual((Object)url.host(), (Object)this.domain) : Companion.domainMatch(url.host(), this.domain);
      if (!domainMatch) {
         return false;
      } else if (!Companion.pathMatch(url, this.path)) {
         return false;
      } else {
         return !this.secure || url.isHttps();
      }
   }

   public boolean equals(@Nullable Object other) {
      return other instanceof Cookie && Intrinsics.areEqual((Object)((Cookie)other).name, (Object)this.name) && Intrinsics.areEqual((Object)((Cookie)other).value, (Object)this.value) && ((Cookie)other).expiresAt == this.expiresAt && Intrinsics.areEqual((Object)((Cookie)other).domain, (Object)this.domain) && Intrinsics.areEqual((Object)((Cookie)other).path, (Object)this.path) && ((Cookie)other).secure == this.secure && ((Cookie)other).httpOnly == this.httpOnly && ((Cookie)other).persistent == this.persistent && ((Cookie)other).hostOnly == this.hostOnly && Intrinsics.areEqual((Object)((Cookie)other).sameSite, (Object)this.sameSite);
   }

   @IgnoreJRERequirement
   public int hashCode() {
      int result = 17;
      result = 31 * result + this.name.hashCode();
      result = 31 * result + this.value.hashCode();
      result = 31 * result + Long.hashCode(this.expiresAt);
      result = 31 * result + this.domain.hashCode();
      result = 31 * result + this.path.hashCode();
      result = 31 * result + Boolean.hashCode(this.secure);
      result = 31 * result + Boolean.hashCode(this.httpOnly);
      result = 31 * result + Boolean.hashCode(this.persistent);
      result = 31 * result + Boolean.hashCode(this.hostOnly);
      int var10000 = 31 * result;
      String var10001 = this.sameSite;
      result = var10000 + (var10001 != null ? var10001.hashCode() : 0);
      return result;
   }

   @NotNull
   public String toString() {
      return this.toString$okhttp(false);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "name",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_name"
   )
   @NotNull
   public final String _deprecated_name/* $FF was: -deprecated_name*/() {
      return this.name;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "value",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_value"
   )
   @NotNull
   public final String _deprecated_value/* $FF was: -deprecated_value*/() {
      return this.value;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "persistent",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_persistent"
   )
   public final boolean _deprecated_persistent/* $FF was: -deprecated_persistent*/() {
      return this.persistent;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "expiresAt",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_expiresAt"
   )
   public final long _deprecated_expiresAt/* $FF was: -deprecated_expiresAt*/() {
      return this.expiresAt;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "hostOnly",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_hostOnly"
   )
   public final boolean _deprecated_hostOnly/* $FF was: -deprecated_hostOnly*/() {
      return this.hostOnly;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "domain",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_domain"
   )
   @NotNull
   public final String _deprecated_domain/* $FF was: -deprecated_domain*/() {
      return this.domain;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "path",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_path"
   )
   @NotNull
   public final String _deprecated_path/* $FF was: -deprecated_path*/() {
      return this.path;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "httpOnly",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_httpOnly"
   )
   public final boolean _deprecated_httpOnly/* $FF was: -deprecated_httpOnly*/() {
      return this.httpOnly;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "secure",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_secure"
   )
   public final boolean _deprecated_secure/* $FF was: -deprecated_secure*/() {
      return this.secure;
   }

   @NotNull
   public final String toString$okhttp(boolean forObsoleteRfc2965) {
      StringBuilder $this$toString_u24lambda_u240 = new StringBuilder();
      int var3 = 0;
      $this$toString_u24lambda_u240.append(this.name);
      $this$toString_u24lambda_u240.append('=');
      $this$toString_u24lambda_u240.append(this.value);
      if (this.persistent) {
         if (this.expiresAt == Long.MIN_VALUE) {
            $this$toString_u24lambda_u240.append("; max-age=0");
         } else {
            $this$toString_u24lambda_u240.append("; expires=").append(DateFormattingKt.toHttpDateString(new Date(this.expiresAt)));
         }
      }

      if (!this.hostOnly) {
         $this$toString_u24lambda_u240.append("; domain=");
         if (forObsoleteRfc2965) {
            $this$toString_u24lambda_u240.append(".");
         }

         $this$toString_u24lambda_u240.append(this.domain);
      }

      $this$toString_u24lambda_u240.append("; path=").append(this.path);
      if (this.secure) {
         $this$toString_u24lambda_u240.append("; secure");
      }

      if (this.httpOnly) {
         $this$toString_u24lambda_u240.append("; httponly");
      }

      if (this.sameSite != null) {
         $this$toString_u24lambda_u240.append("; samesite=").append(this.sameSite);
      }

      String var10000 = $this$toString_u24lambda_u240.toString();
      Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
      return var10000;
   }

   @NotNull
   public final Builder newBuilder() {
      return new Builder(this);
   }

   @JvmStatic
   @Nullable
   public static final Cookie parse(@NotNull HttpUrl url, @NotNull String setCookie) {
      return Companion.parse(url, setCookie);
   }

   @JvmStatic
   @NotNull
   public static final List<Cookie> parseAll(@NotNull HttpUrl url, @NotNull Headers headers) {
      return Companion.parseAll(url, headers);
   }

   // $FF: synthetic method
   public Cookie(String name, String value, long expiresAt, String domain, String path, boolean secure, boolean httpOnly, boolean persistent, boolean hostOnly, String sameSite, DefaultConstructorMarker $constructor_marker) {
      this(name, value, expiresAt, domain, path, secure, httpOnly, persistent, hostOnly, sameSite);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003B\u0011\b\u0010\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\t\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010\n\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\f\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\bJ\u000e\u0010\u0014\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\bJ\u0018\u0010\f\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\u000fH\u0002J\u000e\u0010\r\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\bJ\u0006\u0010\u000e\u001a\u00020\u0000J\u0006\u0010\u0010\u001a\u00020\u0000J\u000e\u0010\u0013\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\bJ\u0006\u0010\u0015\u001a\u00020\u0005R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0016"},
      d2 = {"Lokhttp3/Cookie$Builder;", "", "<init>", "()V", "cookie", "Lokhttp3/Cookie;", "(Lokhttp3/Cookie;)V", "name", "", "value", "expiresAt", "", "domain", "path", "secure", "", "httpOnly", "persistent", "hostOnly", "sameSite", "hostOnlyDomain", "build", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nCookie.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Cookie.kt\nokhttp3/Cookie$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,727:1\n1#2:728\n*E\n"})
   public static final class Builder {
      @Nullable
      private String name;
      @Nullable
      private String value;
      private long expiresAt;
      @Nullable
      private String domain;
      @NotNull
      private String path;
      private boolean secure;
      private boolean httpOnly;
      private boolean persistent;
      private boolean hostOnly;
      @Nullable
      private String sameSite;

      public Builder() {
         this.expiresAt = 253402300799999L;
         this.path = "/";
      }

      public Builder(@NotNull Cookie cookie) {
         Intrinsics.checkNotNullParameter(cookie, "cookie");
         this();
         this.name = cookie.name();
         this.value = cookie.value();
         this.expiresAt = cookie.expiresAt();
         this.domain = cookie.domain();
         this.path = cookie.path();
         this.secure = cookie.secure();
         this.httpOnly = cookie.httpOnly();
         this.persistent = cookie.persistent();
         this.hostOnly = cookie.hostOnly();
         this.sameSite = cookie.sameSite();
      }

      @NotNull
      public final Builder name(@NotNull String name) {
         Intrinsics.checkNotNullParameter(name, "name");
         Builder $this$name_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)StringsKt.trim((CharSequence)name).toString(), (Object)name)) {
            int var5 = 0;
            String var6 = "name is not trimmed";
            throw new IllegalArgumentException(var6.toString());
         } else {
            $this$name_u24lambda_u240.name = name;
            return this;
         }
      }

      @NotNull
      public final Builder value(@NotNull String value) {
         Intrinsics.checkNotNullParameter(value, "value");
         Builder $this$value_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)StringsKt.trim((CharSequence)value).toString(), (Object)value)) {
            int var5 = 0;
            String var6 = "value is not trimmed";
            throw new IllegalArgumentException(var6.toString());
         } else {
            $this$value_u24lambda_u240.value = value;
            return this;
         }
      }

      @NotNull
      public final Builder expiresAt(long expiresAt) {
         Builder $this$expiresAt_u24lambda_u240 = this;
         int var5 = 0;
         long expiresAt = expiresAt;
         if (expiresAt <= 0L) {
            expiresAt = Long.MIN_VALUE;
         }

         if (expiresAt > 253402300799999L) {
            expiresAt = 253402300799999L;
         }

         $this$expiresAt_u24lambda_u240.expiresAt = expiresAt;
         $this$expiresAt_u24lambda_u240.persistent = true;
         return this;
      }

      @NotNull
      public final Builder domain(@NotNull String domain) {
         Intrinsics.checkNotNullParameter(domain, "domain");
         return this.domain(domain, false);
      }

      @NotNull
      public final Builder hostOnlyDomain(@NotNull String domain) {
         Intrinsics.checkNotNullParameter(domain, "domain");
         return this.domain(domain, true);
      }

      private final Builder domain(String domain, boolean hostOnly) {
         Builder $this$domain_u24lambda_u240 = this;
         int var5 = 0;
         String var10000 = _HostnamesCommonKt.toCanonicalHost(domain);
         if (var10000 == null) {
            throw new IllegalArgumentException("unexpected domain: " + domain);
         } else {
            String canonicalDomain = var10000;
            $this$domain_u24lambda_u240.domain = canonicalDomain;
            $this$domain_u24lambda_u240.hostOnly = hostOnly;
            return this;
         }
      }

      @NotNull
      public final Builder path(@NotNull String path) {
         Intrinsics.checkNotNullParameter(path, "path");
         Builder $this$path_u24lambda_u240 = this;
         int var4 = 0;
         if (!StringsKt.startsWith$default(path, "/", false, 2, (Object)null)) {
            int var5 = 0;
            String var6 = "path must start with '/'";
            throw new IllegalArgumentException(var6.toString());
         } else {
            $this$path_u24lambda_u240.path = path;
            return this;
         }
      }

      @NotNull
      public final Builder secure() {
         Builder $this$secure_u24lambda_u240 = this;
         int var3 = 0;
         $this$secure_u24lambda_u240.secure = true;
         return this;
      }

      @NotNull
      public final Builder httpOnly() {
         Builder $this$httpOnly_u24lambda_u240 = this;
         int var3 = 0;
         $this$httpOnly_u24lambda_u240.httpOnly = true;
         return this;
      }

      @NotNull
      public final Builder sameSite(@NotNull String sameSite) {
         Intrinsics.checkNotNullParameter(sameSite, "sameSite");
         Builder $this$sameSite_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)StringsKt.trim((CharSequence)sameSite).toString(), (Object)sameSite)) {
            int var5 = 0;
            String var6 = "sameSite is not trimmed";
            throw new IllegalArgumentException(var6.toString());
         } else {
            $this$sameSite_u24lambda_u240.sameSite = sameSite;
            return this;
         }
      }

      @NotNull
      public final Cookie build() {
         Cookie var10000 = new Cookie;
         String var10002 = this.name;
         if (var10002 == null) {
            throw new NullPointerException("builder.name == null");
         } else {
            String var10003 = this.value;
            if (var10003 == null) {
               throw new NullPointerException("builder.value == null");
            } else {
               long var10004 = this.expiresAt;
               String var10005 = this.domain;
               if (var10005 == null) {
                  throw new NullPointerException("builder.domain == null");
               } else {
                  var10000.<init>(var10002, var10003, var10004, var10005, this.path, this.secure, this.httpOnly, this.persistent, this.hostOnly, this.sameSite, (DefaultConstructorMarker)null);
                  return var10000;
               }
            }
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0002J\u0018\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\rH\u0002J\u001a\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\rH\u0007J'\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\rH\u0000¢\u0006\u0002\b\u0018J \u0010\u0019\u001a\u00020\u00172\u0006\u0010\u001a\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001cH\u0002J(\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020\u000bH\u0002J\u0010\u0010!\u001a\u00020\u00172\u0006\u0010\u001a\u001a\u00020\rH\u0002J\u0010\u0010\"\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\rH\u0002J\u001e\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00140$2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010%\u001a\u00020&H\u0007R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006'"},
      d2 = {"Lokhttp3/Cookie$Companion;", "", "<init>", "()V", "YEAR_PATTERN", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "MONTH_PATTERN", "DAY_OF_MONTH_PATTERN", "TIME_PATTERN", "domainMatch", "", "urlHost", "", "domain", "pathMatch", "url", "Lokhttp3/HttpUrl;", "path", "parse", "Lokhttp3/Cookie;", "setCookie", "currentTimeMillis", "", "parse$okhttp", "parseExpires", "s", "pos", "", "limit", "dateCharacterOffset", "input", "invert", "parseMaxAge", "parseDomain", "parseAll", "", "headers", "Lokhttp3/Headers;", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nCookie.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Cookie.kt\nokhttp3/Cookie$Companion\n+ 2 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n*L\n1#1,727:1\n242#2:728\n*S KotlinDebug\n*F\n+ 1 Cookie.kt\nokhttp3/Cookie$Companion\n*L\n723#1:728\n*E\n"})
   public static final class Companion {
      private Companion() {
      }

      private final boolean domainMatch(String urlHost, String domain) {
         if (Intrinsics.areEqual((Object)urlHost, (Object)domain)) {
            return true;
         } else {
            return StringsKt.endsWith$default(urlHost, domain, false, 2, (Object)null) && urlHost.charAt(urlHost.length() - domain.length() - 1) == '.' && !_HostnamesCommonKt.canParseAsIpAddress(urlHost);
         }
      }

      private final boolean pathMatch(HttpUrl url, String path) {
         String urlPath = url.encodedPath();
         if (Intrinsics.areEqual((Object)urlPath, (Object)path)) {
            return true;
         } else {
            if (StringsKt.startsWith$default(urlPath, path, false, 2, (Object)null)) {
               if (StringsKt.endsWith$default(path, "/", false, 2, (Object)null)) {
                  return true;
               }

               if (urlPath.charAt(path.length()) == '/') {
                  return true;
               }
            }

            return false;
         }
      }

      @JvmStatic
      @Nullable
      public final Cookie parse(@NotNull HttpUrl url, @NotNull String setCookie) {
         Intrinsics.checkNotNullParameter(url, "url");
         Intrinsics.checkNotNullParameter(setCookie, "setCookie");
         return this.parse$okhttp(System.currentTimeMillis(), url, setCookie);
      }

      @Nullable
      public final Cookie parse$okhttp(long currentTimeMillis, @NotNull HttpUrl url, @NotNull String setCookie) {
         Intrinsics.checkNotNullParameter(url, "url");
         Intrinsics.checkNotNullParameter(setCookie, "setCookie");
         int cookiePairEnd = _UtilCommonKt.delimiterOffset$default(setCookie, ';', 0, 0, 6, (Object)null);
         int pairEqualsSign = _UtilCommonKt.delimiterOffset$default(setCookie, '=', 0, cookiePairEnd, 2, (Object)null);
         if (pairEqualsSign == cookiePairEnd) {
            return null;
         } else {
            String cookieName = _UtilCommonKt.trimSubstring$default(setCookie, 0, pairEqualsSign, 1, (Object)null);
            if (((CharSequence)cookieName).length() != 0 && _UtilCommonKt.indexOfControlOrNonAscii(cookieName) == -1) {
               String cookieValue = _UtilCommonKt.trimSubstring(setCookie, pairEqualsSign + 1, cookiePairEnd);
               if (_UtilCommonKt.indexOfControlOrNonAscii(cookieValue) != -1) {
                  return null;
               } else {
                  long expiresAt = 253402300799999L;
                  long deltaSeconds = -1L;
                  String domain = null;
                  String path = null;
                  boolean secureOnly = false;
                  boolean httpOnly = false;
                  boolean hostOnly = true;
                  boolean persistent = false;
                  String sameSite = null;
                  int pos = cookiePairEnd + 1;

                  int attributePairEnd;
                  for(int limit = setCookie.length(); pos < limit; pos = attributePairEnd + 1) {
                     attributePairEnd = _UtilCommonKt.delimiterOffset(setCookie, ';', pos, limit);
                     int attributeEqualsSign = _UtilCommonKt.delimiterOffset(setCookie, '=', pos, attributePairEnd);
                     String attributeName = _UtilCommonKt.trimSubstring(setCookie, pos, attributeEqualsSign);
                     String attributeValue = attributeEqualsSign < attributePairEnd ? _UtilCommonKt.trimSubstring(setCookie, attributeEqualsSign + 1, attributePairEnd) : "";
                     if (StringsKt.equals(attributeName, "expires", true)) {
                        try {
                           expiresAt = this.parseExpires(attributeValue, 0, attributeValue.length());
                           persistent = true;
                        } catch (IllegalArgumentException var29) {
                        }
                     } else if (StringsKt.equals(attributeName, "max-age", true)) {
                        try {
                           deltaSeconds = this.parseMaxAge(attributeValue);
                           persistent = true;
                        } catch (NumberFormatException var28) {
                        }
                     } else if (StringsKt.equals(attributeName, "domain", true)) {
                        try {
                           domain = this.parseDomain(attributeValue);
                           hostOnly = false;
                        } catch (IllegalArgumentException var27) {
                        }
                     } else if (StringsKt.equals(attributeName, "path", true)) {
                        path = attributeValue;
                     } else if (StringsKt.equals(attributeName, "secure", true)) {
                        secureOnly = true;
                     } else if (StringsKt.equals(attributeName, "httponly", true)) {
                        httpOnly = true;
                     } else if (StringsKt.equals(attributeName, "samesite", true)) {
                        sameSite = attributeValue;
                     }
                  }

                  if (deltaSeconds == Long.MIN_VALUE) {
                     expiresAt = Long.MIN_VALUE;
                  } else if (deltaSeconds != -1L) {
                     long deltaMilliseconds = deltaSeconds <= 9223372036854775L ? deltaSeconds * (long)1000 : Long.MAX_VALUE;
                     expiresAt = currentTimeMillis + deltaMilliseconds;
                     if (expiresAt < currentTimeMillis || expiresAt > 253402300799999L) {
                        expiresAt = 253402300799999L;
                     }
                  }

                  String urlHost = url.host();
                  if (domain == null) {
                     domain = urlHost;
                  } else if (!this.domainMatch(urlHost, domain)) {
                     return null;
                  }

                  if (urlHost.length() != domain.length() && PublicSuffixDatabase.Companion.get().getEffectiveTldPlusOne(domain) == null) {
                     return null;
                  } else {
                     if (path == null || !StringsKt.startsWith$default(path, "/", false, 2, (Object)null)) {
                        String encodedPath = url.encodedPath();
                        int lastSlash = StringsKt.lastIndexOf$default((CharSequence)encodedPath, '/', 0, false, 6, (Object)null);
                        String var10000;
                        if (lastSlash != 0) {
                           var10000 = encodedPath.substring(0, lastSlash);
                           Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
                        } else {
                           var10000 = "/";
                        }

                        path = var10000;
                     }

                     return new Cookie(cookieName, cookieValue, expiresAt, domain, path, secureOnly, httpOnly, persistent, hostOnly, sameSite, (DefaultConstructorMarker)null);
                  }
               }
            } else {
               return null;
            }
         }
      }

      private final long parseExpires(String s, int pos, int limit) {
         int pos = this.dateCharacterOffset(s, pos, limit, false);
         int hour = 0;
         hour = -1;
         int minute = 0;
         minute = -1;
         int second = 0;
         second = -1;
         int dayOfMonth = 0;
         dayOfMonth = -1;
         int month = 0;
         month = -1;
         int year = 0;
         year = -1;

         int end;
         for(Matcher matcher = Cookie.TIME_PATTERN.matcher((CharSequence)s); pos < limit; pos = this.dateCharacterOffset(s, end + 1, limit, false)) {
            end = this.dateCharacterOffset(s, pos + 1, limit, true);
            matcher.region(pos, end);
            if (hour == -1 && matcher.usePattern(Cookie.TIME_PATTERN).matches()) {
               String var34 = matcher.group(1);
               Intrinsics.checkNotNullExpressionValue(var34, "group(...)");
               hour = Integer.parseInt(var34);
               var34 = matcher.group(2);
               Intrinsics.checkNotNullExpressionValue(var34, "group(...)");
               minute = Integer.parseInt(var34);
               var34 = matcher.group(3);
               Intrinsics.checkNotNullExpressionValue(var34, "group(...)");
               second = Integer.parseInt(var34);
            } else if (dayOfMonth == -1 && matcher.usePattern(Cookie.DAY_OF_MONTH_PATTERN).matches()) {
               String var33 = matcher.group(1);
               Intrinsics.checkNotNullExpressionValue(var33, "group(...)");
               dayOfMonth = Integer.parseInt(var33);
            } else if (month == -1 && matcher.usePattern(Cookie.MONTH_PATTERN).matches()) {
               String var29 = matcher.group(1);
               Intrinsics.checkNotNullExpressionValue(var29, "group(...)");
               String var14 = var29;
               Locale var30 = Locale.US;
               Intrinsics.checkNotNullExpressionValue(var30, "US");
               String var31 = var14.toLowerCase(var30);
               Intrinsics.checkNotNullExpressionValue(var31, "toLowerCase(...)");
               String monthString = var31;
               var31 = Cookie.MONTH_PATTERN.pattern();
               Intrinsics.checkNotNullExpressionValue(var31, "pattern(...)");
               month = StringsKt.indexOf$default((CharSequence)var31, monthString, 0, false, 6, (Object)null) / 4;
            } else if (year == -1 && matcher.usePattern(Cookie.YEAR_PATTERN).matches()) {
               String var10000 = matcher.group(1);
               Intrinsics.checkNotNullExpressionValue(var10000, "group(...)");
               year = Integer.parseInt(var10000);
            }
         }

         if (70 <= year ? year < 100 : false) {
            year += 1900;
         }

         if (0 <= year ? year < 70 : false) {
            year += 2000;
         }

         if (year < 1601) {
            String var27 = "Failed requirement.";
            throw new IllegalArgumentException(var27.toString());
         } else if (month == -1) {
            String var26 = "Failed requirement.";
            throw new IllegalArgumentException(var26.toString());
         } else if (!(1 <= dayOfMonth ? dayOfMonth < 32 : false)) {
            String var25 = "Failed requirement.";
            throw new IllegalArgumentException(var25.toString());
         } else if (!(0 <= hour ? hour < 24 : false)) {
            String var24 = "Failed requirement.";
            throw new IllegalArgumentException(var24.toString());
         } else if (!(0 <= minute ? minute < 60 : false)) {
            String var23 = "Failed requirement.";
            throw new IllegalArgumentException(var23.toString());
         } else if (!(0 <= second ? second < 60 : false)) {
            String var22 = "Failed requirement.";
            throw new IllegalArgumentException(var22.toString());
         } else {
            GregorianCalendar $this$parseExpires_u24lambda_u240 = new GregorianCalendar(_UtilJvmKt.UTC);
            int var28 = 0;
            $this$parseExpires_u24lambda_u240.setLenient(false);
            $this$parseExpires_u24lambda_u240.set(1, year);
            $this$parseExpires_u24lambda_u240.set(2, month - 1);
            $this$parseExpires_u24lambda_u240.set(5, dayOfMonth);
            $this$parseExpires_u24lambda_u240.set(11, hour);
            $this$parseExpires_u24lambda_u240.set(12, minute);
            $this$parseExpires_u24lambda_u240.set(13, second);
            $this$parseExpires_u24lambda_u240.set(14, 0);
            return $this$parseExpires_u24lambda_u240.getTimeInMillis();
         }
      }

      private final int dateCharacterOffset(String input, int pos, int limit, boolean invert) {
         for(int i = pos; i < limit; ++i) {
            int c = input.charAt(i);
            boolean dateCharacter = c < 32 && c != 9 || c >= 127 || (48 <= c ? c < 58 : false) || (97 <= c ? c < 123 : false) || (65 <= c ? c < 91 : false) || c == 58;
            if (dateCharacter == !invert) {
               return i;
            }
         }

         return limit;
      }

      private final long parseMaxAge(String s) {
         try {
            long parsed = Long.parseLong(s);
            return parsed <= 0L ? Long.MIN_VALUE : parsed;
         } catch (NumberFormatException e) {
            CharSequence var4 = (CharSequence)s;
            if ((new Regex("-?\\d+")).matches(var4)) {
               return StringsKt.startsWith$default(s, "-", false, 2, (Object)null) ? Long.MIN_VALUE : Long.MAX_VALUE;
            } else {
               throw e;
            }
         }
      }

      private final String parseDomain(String s) {
         if (StringsKt.endsWith$default(s, ".", false, 2, (Object)null)) {
            String var2 = "Failed requirement.";
            throw new IllegalArgumentException(var2.toString());
         } else {
            String var10000 = _HostnamesCommonKt.toCanonicalHost(StringsKt.removePrefix(s, (CharSequence)"."));
            if (var10000 == null) {
               throw new IllegalArgumentException();
            } else {
               return var10000;
            }
         }
      }

      @JvmStatic
      @NotNull
      public final List<Cookie> parseAll(@NotNull HttpUrl url, @NotNull Headers headers) {
         Intrinsics.checkNotNullParameter(url, "url");
         Intrinsics.checkNotNullParameter(headers, "headers");
         List cookieStrings = headers.values("Set-Cookie");
         List cookies = null;
         int i = 0;

         for(int var6 = cookieStrings.size(); i < var6; ++i) {
            Cookie var10000 = this.parse(url, (String)cookieStrings.get(i));
            if (var10000 != null) {
               Cookie cookie = var10000;
               if (cookies == null) {
                  cookies = (List)(new ArrayList());
               }

               cookies.add(cookie);
            }
         }

         List var9;
         if (cookies != null) {
            int $i$f$unmodifiable = 0;
            var9 = Collections.unmodifiableList(cookies);
            Intrinsics.checkNotNullExpressionValue(var9, "unmodifiableList(...)");
         } else {
            var9 = null;
         }

         if (var9 == null) {
            var9 = CollectionsKt.emptyList();
         }

         return var9;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
