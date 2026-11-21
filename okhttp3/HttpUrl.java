package okhttp3;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntProgression;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.internal._HostnamesCommonKt;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import okhttp3.internal.url._UrlKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\"\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u001b\u0018\u0000 K2\u00020\u0001:\u0002JKBc\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n\u0012\u0010\u0010\u000b\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0018\u00010\n\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\r\u001a\u00020\u0003¢\u0006\u0004\b\u000e\u0010\u000fJ\r\u0010\u0016\u001a\u00020\u0017H\u0007¢\u0006\u0002\b\rJ\r\u0010\u0018\u001a\u00020\u0019H\u0007¢\u0006\u0002\b\u001aJ\u0010\u0010#\u001a\u0004\u0018\u00010\u00032\u0006\u0010$\u001a\u00020\u0003J\u0016\u0010(\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\n2\u0006\u0010$\u001a\u00020\u0003J\u000e\u0010)\u001a\u00020\u00032\u0006\u0010*\u001a\u00020\bJ\u0010\u0010+\u001a\u0004\u0018\u00010\u00032\u0006\u0010*\u001a\u00020\bJ\u0006\u0010-\u001a\u00020\u0003J\u0010\u0010.\u001a\u0004\u0018\u00010\u00002\u0006\u0010/\u001a\u00020\u0003J\u0006\u00100\u001a\u000201J\u0010\u00100\u001a\u0004\u0018\u0001012\u0006\u0010/\u001a\u00020\u0003J\u0013\u00102\u001a\u00020\u00142\b\u00103\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u00104\u001a\u00020\bH\u0016J\b\u00105\u001a\u00020\u0003H\u0016J\b\u00106\u001a\u0004\u0018\u00010\u0003J\r\u0010\r\u001a\u00020\u0017H\u0007¢\u0006\u0002\b7J\r\u0010\u001a\u001a\u00020\u0019H\u0007¢\u0006\u0002\b8J\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b9J\r\u0010\u001b\u001a\u00020\u0003H\u0007¢\u0006\u0002\b:J\r\u0010\u0004\u001a\u00020\u0003H\u0007¢\u0006\u0002\b;J\r\u0010\u001c\u001a\u00020\u0003H\u0007¢\u0006\u0002\b<J\r\u0010\u0005\u001a\u00020\u0003H\u0007¢\u0006\u0002\b=J\r\u0010\u0006\u001a\u00020\u0003H\u0007¢\u0006\u0002\b>J\r\u0010\u0007\u001a\u00020\bH\u0007¢\u0006\u0002\b?J\r\u0010\u001d\u001a\u00020\bH\u0007¢\u0006\u0002\b@J\r\u0010\u001e\u001a\u00020\u0003H\u0007¢\u0006\u0002\bAJ\u0013\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00030\nH\u0007¢\u0006\u0002\bBJ\u0013\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\nH\u0007¢\u0006\u0002\bCJ\u000f\u0010 \u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\bDJ\u000f\u0010!\u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\bEJ\r\u0010\"\u001a\u00020\bH\u0007¢\u0006\u0002\bFJ\u0013\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00030&H\u0007¢\u0006\u0002\bGJ\u000f\u0010,\u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\bHJ\u000f\u0010\f\u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\bIR\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0010R\u0013\u0010\u0004\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0010R\u0013\u0010\u0005\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0010R\u0013\u0010\u0006\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0010R\u0013\u0010\u0007\u001a\u00020\b8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0011R\u0019\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0012R\u0018\u0010\u000b\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0018\u00010\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0015\u0010\f\u001a\u0004\u0018\u00010\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0010R\u000e\u0010\r\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0013\u001a\u00020\u00148F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0015R\u0011\u0010\u001b\u001a\u00020\u00038G¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u0010R\u0011\u0010\u001c\u001a\u00020\u00038G¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u0010R\u0011\u0010\u001d\u001a\u00020\b8G¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u0011R\u0011\u0010\u001e\u001a\u00020\u00038G¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u0010R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00030\n8G¢\u0006\u0006\u001a\u0004\b\u001f\u0010\u0012R\u0013\u0010 \u001a\u0004\u0018\u00010\u00038G¢\u0006\u0006\u001a\u0004\b \u0010\u0010R\u0013\u0010!\u001a\u0004\u0018\u00010\u00038G¢\u0006\u0006\u001a\u0004\b!\u0010\u0010R\u0011\u0010\"\u001a\u00020\b8G¢\u0006\u0006\u001a\u0004\b\"\u0010\u0011R\u0017\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00030&8G¢\u0006\u0006\u001a\u0004\b%\u0010'R\u0013\u0010,\u001a\u0004\u0018\u00010\u00038G¢\u0006\u0006\u001a\u0004\b,\u0010\u0010¨\u0006L"},
   d2 = {"Lokhttp3/HttpUrl;", "", "scheme", "", "username", "password", "host", "port", "", "pathSegments", "", "queryNamesAndValues", "fragment", "url", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V", "()Ljava/lang/String;", "()I", "()Ljava/util/List;", "isHttps", "", "()Z", "toUrl", "Ljava/net/URL;", "toUri", "Ljava/net/URI;", "uri", "encodedUsername", "encodedPassword", "pathSize", "encodedPath", "encodedPathSegments", "encodedQuery", "query", "querySize", "queryParameter", "name", "queryParameterNames", "", "()Ljava/util/Set;", "queryParameterValues", "queryParameterName", "index", "queryParameterValue", "encodedFragment", "redact", "resolve", "link", "newBuilder", "Lokhttp3/HttpUrl$Builder;", "equals", "other", "hashCode", "toString", "topPrivateDomain", "-deprecated_url", "-deprecated_uri", "-deprecated_scheme", "-deprecated_encodedUsername", "-deprecated_username", "-deprecated_encodedPassword", "-deprecated_password", "-deprecated_host", "-deprecated_port", "-deprecated_pathSize", "-deprecated_encodedPath", "-deprecated_encodedPathSegments", "-deprecated_pathSegments", "-deprecated_encodedQuery", "-deprecated_query", "-deprecated_querySize", "-deprecated_queryParameterNames", "-deprecated_encodedFragment", "-deprecated_fragment", "Builder", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nHttpUrl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HttpUrl.kt\nokhttp3/HttpUrl\n+ 2 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n*L\n1#1,1810:1\n246#2:1811\n242#2:1812\n*S KotlinDebug\n*F\n+ 1 HttpUrl.kt\nokhttp3/HttpUrl\n*L\n624#1:1811\n648#1:1812\n*E\n"})
public final class HttpUrl {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final String scheme;
   @NotNull
   private final String username;
   @NotNull
   private final String password;
   @NotNull
   private final String host;
   private final int port;
   @NotNull
   private final List<String> pathSegments;
   @Nullable
   private final List<String> queryNamesAndValues;
   @Nullable
   private final String fragment;
   @NotNull
   private final String url;

   private HttpUrl(String scheme, String username, String password, String host, int port, List<String> pathSegments, List<String> queryNamesAndValues, String fragment, String url) {
      this.scheme = scheme;
      this.username = username;
      this.password = password;
      this.host = host;
      this.port = port;
      this.pathSegments = pathSegments;
      this.queryNamesAndValues = queryNamesAndValues;
      this.fragment = fragment;
      this.url = url;
   }

   @JvmName(
      name = "scheme"
   )
   @NotNull
   public final String scheme() {
      return this.scheme;
   }

   @JvmName(
      name = "username"
   )
   @NotNull
   public final String username() {
      return this.username;
   }

   @JvmName(
      name = "password"
   )
   @NotNull
   public final String password() {
      return this.password;
   }

   @JvmName(
      name = "host"
   )
   @NotNull
   public final String host() {
      return this.host;
   }

   @JvmName(
      name = "port"
   )
   public final int port() {
      return this.port;
   }

   @JvmName(
      name = "pathSegments"
   )
   @NotNull
   public final List<String> pathSegments() {
      return this.pathSegments;
   }

   @JvmName(
      name = "fragment"
   )
   @Nullable
   public final String fragment() {
      return this.fragment;
   }

   public final boolean isHttps() {
      return Intrinsics.areEqual((Object)this.scheme, (Object)"https");
   }

   @JvmName(
      name = "url"
   )
   @NotNull
   public final URL url() {
      try {
         return new URL(this.url);
      } catch (MalformedURLException e) {
         throw new RuntimeException((Throwable)e);
      }
   }

   @JvmName(
      name = "uri"
   )
   @NotNull
   public final URI uri() {
      String uri = this.newBuilder().reencodeForUri$okhttp().toString();

      URI var2;
      try {
         var2 = new URI(uri);
      } catch (URISyntaxException e) {
         URI var11;
         try {
            CharSequence var6 = (CharSequence)uri;
            Regex var7 = new Regex("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]");
            String var8 = "";
            String stripped = var7.replace(var6, var8);
            var11 = URI.create(stripped);
         } catch (Exception var9) {
            throw new RuntimeException((Throwable)e);
         }

         Intrinsics.checkNotNull(var11);
         var2 = var11;
      }

      return var2;
   }

   @JvmName(
      name = "encodedUsername"
   )
   @NotNull
   public final String encodedUsername() {
      if (((CharSequence)this.username).length() == 0) {
         return "";
      } else {
         int usernameStart = this.scheme.length() + 3;
         int usernameEnd = _UtilCommonKt.delimiterOffset(this.url, ":@", usernameStart, this.url.length());
         String var10000 = this.url.substring(usernameStart, usernameEnd);
         Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
         return var10000;
      }
   }

   @JvmName(
      name = "encodedPassword"
   )
   @NotNull
   public final String encodedPassword() {
      if (((CharSequence)this.password).length() == 0) {
         return "";
      } else {
         int passwordStart = StringsKt.indexOf$default((CharSequence)this.url, ':', this.scheme.length() + 3, false, 4, (Object)null) + 1;
         int passwordEnd = StringsKt.indexOf$default((CharSequence)this.url, '@', 0, false, 6, (Object)null);
         String var10000 = this.url.substring(passwordStart, passwordEnd);
         Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
         return var10000;
      }
   }

   @JvmName(
      name = "pathSize"
   )
   public final int pathSize() {
      return this.pathSegments.size();
   }

   @JvmName(
      name = "encodedPath"
   )
   @NotNull
   public final String encodedPath() {
      int pathStart = StringsKt.indexOf$default((CharSequence)this.url, '/', this.scheme.length() + 3, false, 4, (Object)null);
      int pathEnd = _UtilCommonKt.delimiterOffset(this.url, "?#", pathStart, this.url.length());
      String var10000 = this.url.substring(pathStart, pathEnd);
      Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
      return var10000;
   }

   @JvmName(
      name = "encodedPathSegments"
   )
   @NotNull
   public final List<String> encodedPathSegments() {
      int pathStart = StringsKt.indexOf$default((CharSequence)this.url, '/', this.scheme.length() + 3, false, 4, (Object)null);
      int pathEnd = _UtilCommonKt.delimiterOffset(this.url, "?#", pathStart, this.url.length());
      List result = (List)(new ArrayList());

      int segmentEnd;
      for(int i = pathStart; i < pathEnd; i = segmentEnd) {
         ++i;
         segmentEnd = _UtilCommonKt.delimiterOffset(this.url, '/', i, pathEnd);
         String var10001 = this.url.substring(i, segmentEnd);
         Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
         result.add(var10001);
      }

      return result;
   }

   @JvmName(
      name = "encodedQuery"
   )
   @Nullable
   public final String encodedQuery() {
      if (this.queryNamesAndValues == null) {
         return null;
      } else {
         int queryStart = StringsKt.indexOf$default((CharSequence)this.url, '?', 0, false, 6, (Object)null) + 1;
         int queryEnd = _UtilCommonKt.delimiterOffset(this.url, '#', queryStart, this.url.length());
         String var10000 = this.url.substring(queryStart, queryEnd);
         Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
         return var10000;
      }
   }

   @JvmName(
      name = "query"
   )
   @Nullable
   public final String query() {
      if (this.queryNamesAndValues == null) {
         return null;
      } else {
         StringBuilder result = new StringBuilder();
         Companion.toQueryString(this.queryNamesAndValues, result);
         return result.toString();
      }
   }

   @JvmName(
      name = "querySize"
   )
   public final int querySize() {
      return this.queryNamesAndValues != null ? this.queryNamesAndValues.size() / 2 : 0;
   }

   @Nullable
   public final String queryParameter(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      if (this.queryNamesAndValues == null) {
         return null;
      } else {
         IntProgression var2 = RangesKt.step(RangesKt.until(0, this.queryNamesAndValues.size()), 2);
         int i = var2.getFirst();
         int var4 = var2.getLast();
         int var5 = var2.getStep();
         if (var5 > 0 && i <= var4 || var5 < 0 && var4 <= i) {
            while(true) {
               if (Intrinsics.areEqual((Object)name, (Object)this.queryNamesAndValues.get(i))) {
                  return (String)this.queryNamesAndValues.get(i + 1);
               }

               if (i == var4) {
                  break;
               }

               i += var5;
            }
         }

         return null;
      }
   }

   @JvmName(
      name = "queryParameterNames"
   )
   @NotNull
   public final Set<String> queryParameterNames() {
      if (this.queryNamesAndValues == null) {
         return SetsKt.emptySet();
      } else {
         LinkedHashSet result = new LinkedHashSet(this.queryNamesAndValues.size() / 2, 1.0F);
         IntProgression var2 = RangesKt.step(RangesKt.until(0, this.queryNamesAndValues.size()), 2);
         int i = var2.getFirst();
         int var4 = var2.getLast();
         int var5 = var2.getStep();
         if (var5 > 0 && i <= var4 || var5 < 0 && var4 <= i) {
            while(true) {
               Object var10001 = this.queryNamesAndValues.get(i);
               Intrinsics.checkNotNull(var10001);
               result.add(var10001);
               if (i == var4) {
                  break;
               }

               i += var5;
            }
         }

         Set $this$unmodifiable$iv = (Set)result;
         i = 0;
         Set var10000 = Collections.unmodifiableSet($this$unmodifiable$iv);
         Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableSet(...)");
         return var10000;
      }
   }

   @NotNull
   public final List<String> queryParameterValues(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      if (this.queryNamesAndValues == null) {
         return CollectionsKt.emptyList();
      } else {
         ArrayList result = new ArrayList(4);
         IntProgression var3 = RangesKt.step(RangesKt.until(0, this.queryNamesAndValues.size()), 2);
         int i = var3.getFirst();
         int var5 = var3.getLast();
         int var6 = var3.getStep();
         if (var6 > 0 && i <= var5 || var6 < 0 && var5 <= i) {
            while(true) {
               if (Intrinsics.areEqual((Object)name, (Object)this.queryNamesAndValues.get(i))) {
                  result.add(this.queryNamesAndValues.get(i + 1));
               }

               if (i == var5) {
                  break;
               }

               i += var6;
            }
         }

         List $this$unmodifiable$iv = (List)result;
         i = 0;
         List var10000 = Collections.unmodifiableList($this$unmodifiable$iv);
         Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableList(...)");
         return var10000;
      }
   }

   @NotNull
   public final String queryParameterName(int index) {
      if (this.queryNamesAndValues == null) {
         throw new IndexOutOfBoundsException();
      } else {
         Object var10000 = this.queryNamesAndValues.get(index * 2);
         Intrinsics.checkNotNull(var10000);
         return (String)var10000;
      }
   }

   @Nullable
   public final String queryParameterValue(int index) {
      if (this.queryNamesAndValues == null) {
         throw new IndexOutOfBoundsException();
      } else {
         return (String)this.queryNamesAndValues.get(index * 2 + 1);
      }
   }

   @JvmName(
      name = "encodedFragment"
   )
   @Nullable
   public final String encodedFragment() {
      if (this.fragment == null) {
         return null;
      } else {
         int fragmentStart = StringsKt.indexOf$default((CharSequence)this.url, '#', 0, false, 6, (Object)null) + 1;
         String var10000 = this.url.substring(fragmentStart);
         Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
         return var10000;
      }
   }

   @NotNull
   public final String redact() {
      Builder var10000 = this.newBuilder("/...");
      Intrinsics.checkNotNull(var10000);
      return var10000.username("").password("").build().toString();
   }

   @Nullable
   public final HttpUrl resolve(@NotNull String link) {
      Intrinsics.checkNotNullParameter(link, "link");
      Builder var10000 = this.newBuilder(link);
      return var10000 != null ? var10000.build() : null;
   }

   @NotNull
   public final Builder newBuilder() {
      Builder result = new Builder();
      result.setScheme$okhttp(this.scheme);
      result.setEncodedUsername$okhttp(this.encodedUsername());
      result.setEncodedPassword$okhttp(this.encodedPassword());
      result.setHost$okhttp(this.host);
      result.setPort$okhttp(this.port != Companion.defaultPort(this.scheme) ? this.port : -1);
      result.getEncodedPathSegments$okhttp().clear();
      result.getEncodedPathSegments$okhttp().addAll((Collection)this.encodedPathSegments());
      result.encodedQuery(this.encodedQuery());
      result.setEncodedFragment$okhttp(this.encodedFragment());
      return result;
   }

   @Nullable
   public final Builder newBuilder(@NotNull String link) {
      Intrinsics.checkNotNullParameter(link, "link");

      Builder var2;
      try {
         var2 = (new Builder()).parse$okhttp(this, link);
      } catch (IllegalArgumentException var4) {
         var2 = null;
      }

      return var2;
   }

   public boolean equals(@Nullable Object other) {
      return other instanceof HttpUrl && Intrinsics.areEqual((Object)((HttpUrl)other).url, (Object)this.url);
   }

   public int hashCode() {
      return this.url.hashCode();
   }

   @NotNull
   public String toString() {
      return this.url;
   }

   @Nullable
   public final String topPrivateDomain() {
      return _HostnamesCommonKt.canParseAsIpAddress(this.host) ? null : PublicSuffixDatabase.Companion.get().getEffectiveTldPlusOne(this.host);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to toUrl()",
      replaceWith = @ReplaceWith(
   expression = "toUrl()",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_url"
   )
   @NotNull
   public final URL _deprecated_url/* $FF was: -deprecated_url*/() {
      return this.url();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to toUri()",
      replaceWith = @ReplaceWith(
   expression = "toUri()",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_uri"
   )
   @NotNull
   public final URI _deprecated_uri/* $FF was: -deprecated_uri*/() {
      return this.uri();
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
   expression = "encodedUsername",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_encodedUsername"
   )
   @NotNull
   public final String _deprecated_encodedUsername/* $FF was: -deprecated_encodedUsername*/() {
      return this.encodedUsername();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "username",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_username"
   )
   @NotNull
   public final String _deprecated_username/* $FF was: -deprecated_username*/() {
      return this.username;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "encodedPassword",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_encodedPassword"
   )
   @NotNull
   public final String _deprecated_encodedPassword/* $FF was: -deprecated_encodedPassword*/() {
      return this.encodedPassword();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "password",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_password"
   )
   @NotNull
   public final String _deprecated_password/* $FF was: -deprecated_password*/() {
      return this.password;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "host",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_host"
   )
   @NotNull
   public final String _deprecated_host/* $FF was: -deprecated_host*/() {
      return this.host;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "port",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_port"
   )
   public final int _deprecated_port/* $FF was: -deprecated_port*/() {
      return this.port;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "pathSize",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_pathSize"
   )
   public final int _deprecated_pathSize/* $FF was: -deprecated_pathSize*/() {
      return this.pathSize();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "encodedPath",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_encodedPath"
   )
   @NotNull
   public final String _deprecated_encodedPath/* $FF was: -deprecated_encodedPath*/() {
      return this.encodedPath();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "encodedPathSegments",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_encodedPathSegments"
   )
   @NotNull
   public final List<String> _deprecated_encodedPathSegments/* $FF was: -deprecated_encodedPathSegments*/() {
      return this.encodedPathSegments();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "pathSegments",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_pathSegments"
   )
   @NotNull
   public final List<String> _deprecated_pathSegments/* $FF was: -deprecated_pathSegments*/() {
      return this.pathSegments;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "encodedQuery",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_encodedQuery"
   )
   @Nullable
   public final String _deprecated_encodedQuery/* $FF was: -deprecated_encodedQuery*/() {
      return this.encodedQuery();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "query",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_query"
   )
   @Nullable
   public final String _deprecated_query/* $FF was: -deprecated_query*/() {
      return this.query();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "querySize",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_querySize"
   )
   public final int _deprecated_querySize/* $FF was: -deprecated_querySize*/() {
      return this.querySize();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "queryParameterNames",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_queryParameterNames"
   )
   @NotNull
   public final Set<String> _deprecated_queryParameterNames/* $FF was: -deprecated_queryParameterNames*/() {
      return this.queryParameterNames();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "encodedFragment",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_encodedFragment"
   )
   @Nullable
   public final String _deprecated_encodedFragment/* $FF was: -deprecated_encodedFragment*/() {
      return this.encodedFragment();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "fragment",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_fragment"
   )
   @Nullable
   public final String _deprecated_fragment/* $FF was: -deprecated_fragment*/() {
      return this.fragment;
   }

   @JvmStatic
   public static final int defaultPort(@NotNull String scheme) {
      return Companion.defaultPort(scheme);
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @NotNull
   public static final HttpUrl get(@NotNull String $this$get) {
      return Companion.get($this$get);
   }

   @JvmStatic
   @JvmName(
      name = "parse"
   )
   @Nullable
   public static final HttpUrl parse(@NotNull String $this$parse) {
      return Companion.parse($this$parse);
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @Nullable
   public static final HttpUrl get(@NotNull URL $this$get) {
      return Companion.get($this$get);
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @Nullable
   public static final HttpUrl get(@NotNull URI $this$get) {
      return Companion.get($this$get);
   }

   // $FF: synthetic method
   public HttpUrl(String scheme, String username, String password, String host, int port, List pathSegments, List queryNamesAndValues, String fragment, String url, DefaultConstructorMarker $constructor_marker) {
      this(scheme, username, password, host, port, pathSegments, queryNamesAndValues, fragment, url);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0013\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00002\u0006\u0010\u0004\u001a\u00020\u0005J\u000e\u0010$\u001a\u00020\u00002\u0006\u0010$\u001a\u00020\u0005J\u000e\u0010\n\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0005J\u000e\u0010%\u001a\u00020\u00002\u0006\u0010%\u001a\u00020\u0005J\u000e\u0010\r\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u0005J\u000e\u0010\u0010\u001a\u00020\u00002\u0006\u0010\u0010\u001a\u00020\u0005J\u000e\u0010\u0013\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u0014J\u000e\u0010&\u001a\u00020\u00002\u0006\u0010'\u001a\u00020\u0005J\u000e\u0010(\u001a\u00020\u00002\u0006\u0010)\u001a\u00020\u0005J\u000e\u0010*\u001a\u00020\u00002\u0006\u0010+\u001a\u00020\u0005J\u000e\u0010,\u001a\u00020\u00002\u0006\u0010\u0019\u001a\u00020\u0005J\u0018\u0010(\u001a\u00020\u00002\u0006\u0010)\u001a\u00020\u00052\u0006\u0010-\u001a\u00020.H\u0002J\u0016\u0010/\u001a\u00020\u00002\u0006\u00100\u001a\u00020\u00142\u0006\u0010'\u001a\u00020\u0005J\u0016\u00101\u001a\u00020\u00002\u0006\u00100\u001a\u00020\u00142\u0006\u0010+\u001a\u00020\u0005J\u000e\u00102\u001a\u00020\u00002\u0006\u00100\u001a\u00020\u0014J\u000e\u00103\u001a\u00020\u00002\u0006\u00103\u001a\u00020\u0005J\u0010\u00104\u001a\u00020\u00002\b\u00104\u001a\u0004\u0018\u00010\u0005J\u0010\u00105\u001a\u00020\u00002\b\u00105\u001a\u0004\u0018\u00010\u0005J\u0018\u00106\u001a\u00020\u00002\u0006\u00107\u001a\u00020\u00052\b\u00108\u001a\u0004\u0018\u00010\u0005J\u0018\u00109\u001a\u00020\u00002\u0006\u0010:\u001a\u00020\u00052\b\u0010;\u001a\u0004\u0018\u00010\u0005J\u0018\u0010<\u001a\u00020\u00002\u0006\u00107\u001a\u00020\u00052\b\u00108\u001a\u0004\u0018\u00010\u0005J\u0018\u0010=\u001a\u00020\u00002\u0006\u0010:\u001a\u00020\u00052\b\u0010;\u001a\u0004\u0018\u00010\u0005J\u000e\u0010>\u001a\u00020\u00002\u0006\u00107\u001a\u00020\u0005J\u000e\u0010?\u001a\u00020\u00002\u0006\u0010:\u001a\u00020\u0005J\u0010\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020\u0005H\u0002J\u0010\u0010C\u001a\u00020\u00002\b\u0010C\u001a\u0004\u0018\u00010\u0005J\u0010\u0010!\u001a\u00020\u00002\b\u0010!\u001a\u0004\u0018\u00010\u0005J\r\u0010D\u001a\u00020\u0000H\u0000¢\u0006\u0002\bEJ\u0006\u0010F\u001a\u00020GJ\b\u0010H\u001a\u00020\u0014H\u0002J\b\u0010I\u001a\u00020\u0005H\u0016J\u001e\u0010J\u001a\u00020A*\b\u0012\u0004\u0012\u00020\u00050K2\n\u0010L\u001a\u00060Mj\u0002`NH\u0002J\u001f\u0010O\u001a\u00020\u00002\b\u0010P\u001a\u0004\u0018\u00010G2\u0006\u0010Q\u001a\u00020\u0005H\u0000¢\u0006\u0002\bRJ \u0010S\u001a\u00020A2\u0006\u0010Q\u001a\u00020\u00052\u0006\u0010T\u001a\u00020\u00142\u0006\u0010U\u001a\u00020\u0014H\u0002J0\u0010V\u001a\u00020A2\u0006\u0010Q\u001a\u00020\u00052\u0006\u0010W\u001a\u00020\u00142\u0006\u0010U\u001a\u00020\u00142\u0006\u0010X\u001a\u00020.2\u0006\u0010-\u001a\u00020.H\u0002J\b\u0010Y\u001a\u00020AH\u0002J\u0010\u0010Z\u001a\u00020.2\u0006\u0010Q\u001a\u00020\u0005H\u0002J\u0010\u0010[\u001a\u00020.2\u0006\u0010Q\u001a\u00020\u0005H\u0002J\u0014\u0010\\\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u001a*\u00020\u0005H\u0002J \u0010]\u001a\u00020\u00142\u0006\u0010Q\u001a\u00020\u00052\u0006\u0010W\u001a\u00020\u00142\u0006\u0010U\u001a\u00020\u0014H\u0002J\u001c\u0010^\u001a\u00020\u0014*\u00020\u00052\u0006\u0010W\u001a\u00020\u00142\u0006\u0010U\u001a\u00020\u0014H\u0002J \u0010_\u001a\u00020\u00142\u0006\u0010Q\u001a\u00020\u00052\u0006\u0010W\u001a\u00020\u00142\u0006\u0010U\u001a\u00020\u0014H\u0002J \u0010`\u001a\u00020\u00142\u0006\u0010Q\u001a\u00020\u00052\u0006\u0010W\u001a\u00020\u00142\u0006\u0010U\u001a\u00020\u0014H\u0002R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\u0007\"\u0004\b\f\u0010\tR\u001a\u0010\r\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u0007\"\u0004\b\u000f\u0010\tR\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0007\"\u0004\b\u0012\u0010\tR\u001a\u0010\u0013\u001a\u00020\u0014X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00050\u001aX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR$\u0010\u001d\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0005\u0018\u00010\u001aX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001c\"\u0004\b\u001f\u0010 R\u001c\u0010!\u001a\u0004\u0018\u00010\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u0007\"\u0004\b#\u0010\t¨\u0006a"},
      d2 = {"Lokhttp3/HttpUrl$Builder;", "", "<init>", "()V", "scheme", "", "getScheme$okhttp", "()Ljava/lang/String;", "setScheme$okhttp", "(Ljava/lang/String;)V", "encodedUsername", "getEncodedUsername$okhttp", "setEncodedUsername$okhttp", "encodedPassword", "getEncodedPassword$okhttp", "setEncodedPassword$okhttp", "host", "getHost$okhttp", "setHost$okhttp", "port", "", "getPort$okhttp", "()I", "setPort$okhttp", "(I)V", "encodedPathSegments", "", "getEncodedPathSegments$okhttp", "()Ljava/util/List;", "encodedQueryNamesAndValues", "getEncodedQueryNamesAndValues$okhttp", "setEncodedQueryNamesAndValues$okhttp", "(Ljava/util/List;)V", "encodedFragment", "getEncodedFragment$okhttp", "setEncodedFragment$okhttp", "username", "password", "addPathSegment", "pathSegment", "addPathSegments", "pathSegments", "addEncodedPathSegment", "encodedPathSegment", "addEncodedPathSegments", "alreadyEncoded", "", "setPathSegment", "index", "setEncodedPathSegment", "removePathSegment", "encodedPath", "query", "encodedQuery", "addQueryParameter", "name", "value", "addEncodedQueryParameter", "encodedName", "encodedValue", "setQueryParameter", "setEncodedQueryParameter", "removeAllQueryParameters", "removeAllEncodedQueryParameters", "removeAllCanonicalQueryParameters", "", "canonicalName", "fragment", "reencodeForUri", "reencodeForUri$okhttp", "build", "Lokhttp3/HttpUrl;", "effectivePort", "toString", "toPathString", "", "out", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "parse", "base", "input", "parse$okhttp", "resolvePath", "startPos", "limit", "push", "pos", "addTrailingSlash", "pop", "isDot", "isDotDot", "toQueryNamesAndValues", "schemeDelimiterOffset", "slashCount", "portColonOffset", "parsePort", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHttpUrl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HttpUrl.kt\nokhttp3/HttpUrl$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1810:1\n1#2:1811\n1563#3:1812\n1634#3,3:1813\n1563#3:1816\n1634#3,3:1817\n*S KotlinDebug\n*F\n+ 1 HttpUrl.kt\nokhttp3/HttpUrl$Builder\n*L\n1265#1:1812\n1265#1:1813,3\n1266#1:1816\n1266#1:1817,3\n*E\n"})
   public static final class Builder {
      @Nullable
      private String scheme;
      @NotNull
      private String encodedUsername = "";
      @NotNull
      private String encodedPassword = "";
      @Nullable
      private String host;
      private int port = -1;
      @NotNull
      private final List<String> encodedPathSegments;
      @Nullable
      private List<String> encodedQueryNamesAndValues;
      @Nullable
      private String encodedFragment;

      public Builder() {
         String[] var1 = new String[]{""};
         this.encodedPathSegments = CollectionsKt.mutableListOf(var1);
      }

      @Nullable
      public final String getScheme$okhttp() {
         return this.scheme;
      }

      public final void setScheme$okhttp(@Nullable String var1) {
         this.scheme = var1;
      }

      @NotNull
      public final String getEncodedUsername$okhttp() {
         return this.encodedUsername;
      }

      public final void setEncodedUsername$okhttp(@NotNull String var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.encodedUsername = var1;
      }

      @NotNull
      public final String getEncodedPassword$okhttp() {
         return this.encodedPassword;
      }

      public final void setEncodedPassword$okhttp(@NotNull String var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.encodedPassword = var1;
      }

      @Nullable
      public final String getHost$okhttp() {
         return this.host;
      }

      public final void setHost$okhttp(@Nullable String var1) {
         this.host = var1;
      }

      public final int getPort$okhttp() {
         return this.port;
      }

      public final void setPort$okhttp(int var1) {
         this.port = var1;
      }

      @NotNull
      public final List<String> getEncodedPathSegments$okhttp() {
         return this.encodedPathSegments;
      }

      @Nullable
      public final List<String> getEncodedQueryNamesAndValues$okhttp() {
         return this.encodedQueryNamesAndValues;
      }

      public final void setEncodedQueryNamesAndValues$okhttp(@Nullable List<String> var1) {
         this.encodedQueryNamesAndValues = var1;
      }

      @Nullable
      public final String getEncodedFragment$okhttp() {
         return this.encodedFragment;
      }

      public final void setEncodedFragment$okhttp(@Nullable String var1) {
         this.encodedFragment = var1;
      }

      @NotNull
      public final Builder scheme(@NotNull String scheme) {
         Intrinsics.checkNotNullParameter(scheme, "scheme");
         Builder $this$scheme_u24lambda_u240 = this;
         int var4 = 0;
         if (StringsKt.equals(scheme, "http", true)) {
            $this$scheme_u24lambda_u240.scheme = "http";
         } else {
            if (!StringsKt.equals(scheme, "https", true)) {
               throw new IllegalArgumentException("unexpected scheme: " + scheme);
            }

            $this$scheme_u24lambda_u240.scheme = "https";
         }

         return this;
      }

      @NotNull
      public final Builder username(@NotNull String username) {
         Intrinsics.checkNotNullParameter(username, "username");
         Builder $this$username_u24lambda_u240 = this;
         int var4 = 0;
         $this$username_u24lambda_u240.encodedUsername = _UrlKt.canonicalize$default(username, 0, 0, " \"':;<=>@[]^`{}|/\\?#", false, false, false, false, 123, (Object)null);
         return this;
      }

      @NotNull
      public final Builder encodedUsername(@NotNull String encodedUsername) {
         Intrinsics.checkNotNullParameter(encodedUsername, "encodedUsername");
         Builder $this$encodedUsername_u24lambda_u240 = this;
         int var4 = 0;
         $this$encodedUsername_u24lambda_u240.encodedUsername = _UrlKt.canonicalize$default(encodedUsername, 0, 0, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, 115, (Object)null);
         return this;
      }

      @NotNull
      public final Builder password(@NotNull String password) {
         Intrinsics.checkNotNullParameter(password, "password");
         Builder $this$password_u24lambda_u240 = this;
         int var4 = 0;
         $this$password_u24lambda_u240.encodedPassword = _UrlKt.canonicalize$default(password, 0, 0, " \"':;<=>@[]^`{}|/\\?#", false, false, false, false, 123, (Object)null);
         return this;
      }

      @NotNull
      public final Builder encodedPassword(@NotNull String encodedPassword) {
         Intrinsics.checkNotNullParameter(encodedPassword, "encodedPassword");
         Builder $this$encodedPassword_u24lambda_u240 = this;
         int var4 = 0;
         $this$encodedPassword_u24lambda_u240.encodedPassword = _UrlKt.canonicalize$default(encodedPassword, 0, 0, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, 115, (Object)null);
         return this;
      }

      @NotNull
      public final Builder host(@NotNull String host) {
         Intrinsics.checkNotNullParameter(host, "host");
         Builder $this$host_u24lambda_u240 = this;
         int var4 = 0;
         String var10000 = _HostnamesCommonKt.toCanonicalHost(_UrlKt.percentDecode$default(host, 0, 0, false, 7, (Object)null));
         if (var10000 == null) {
            throw new IllegalArgumentException("unexpected host: " + host);
         } else {
            String encoded = var10000;
            $this$host_u24lambda_u240.host = encoded;
            return this;
         }
      }

      @NotNull
      public final Builder port(int port) {
         Builder $this$port_u24lambda_u240 = this;
         int var4 = 0;
         if (!(1 <= port ? port < 65536 : false)) {
            int var5 = 0;
            String var6 = "unexpected port: " + port;
            throw new IllegalArgumentException(var6.toString());
         } else {
            $this$port_u24lambda_u240.port = port;
            return this;
         }
      }

      @NotNull
      public final Builder addPathSegment(@NotNull String pathSegment) {
         Intrinsics.checkNotNullParameter(pathSegment, "pathSegment");
         Builder $this$addPathSegment_u24lambda_u240 = this;
         int var4 = 0;
         $this$addPathSegment_u24lambda_u240.push(pathSegment, 0, pathSegment.length(), false, false);
         return this;
      }

      @NotNull
      public final Builder addPathSegments(@NotNull String pathSegments) {
         Intrinsics.checkNotNullParameter(pathSegments, "pathSegments");
         return this.addPathSegments(pathSegments, false);
      }

      @NotNull
      public final Builder addEncodedPathSegment(@NotNull String encodedPathSegment) {
         Intrinsics.checkNotNullParameter(encodedPathSegment, "encodedPathSegment");
         Builder $this$addEncodedPathSegment_u24lambda_u240 = this;
         int var4 = 0;
         $this$addEncodedPathSegment_u24lambda_u240.push(encodedPathSegment, 0, encodedPathSegment.length(), false, true);
         return this;
      }

      @NotNull
      public final Builder addEncodedPathSegments(@NotNull String encodedPathSegments) {
         Intrinsics.checkNotNullParameter(encodedPathSegments, "encodedPathSegments");
         return this.addPathSegments(encodedPathSegments, true);
      }

      private final Builder addPathSegments(String pathSegments, boolean alreadyEncoded) {
         Builder $this$addPathSegments_u24lambda_u240 = this;
         int var5 = 0;
         int offset = 0;

         do {
            int segmentEnd = _UtilCommonKt.delimiterOffset(pathSegments, "/\\", offset, pathSegments.length());
            boolean addTrailingSlash = segmentEnd < pathSegments.length();
            $this$addPathSegments_u24lambda_u240.push(pathSegments, offset, segmentEnd, addTrailingSlash, alreadyEncoded);
            offset = segmentEnd + 1;
         } while(offset <= pathSegments.length());

         return this;
      }

      @NotNull
      public final Builder setPathSegment(int index, @NotNull String pathSegment) {
         Intrinsics.checkNotNullParameter(pathSegment, "pathSegment");
         Builder $this$setPathSegment_u24lambda_u240 = this;
         int var5 = 0;
         String canonicalPathSegment = _UrlKt.canonicalize$default(pathSegment, 0, 0, " \"<>^`{}|/\\?#", false, false, false, false, 123, (Object)null);
         if ($this$setPathSegment_u24lambda_u240.isDot(canonicalPathSegment) || $this$setPathSegment_u24lambda_u240.isDotDot(canonicalPathSegment)) {
            int var7 = 0;
            String var8 = "unexpected path segment: " + pathSegment;
            throw new IllegalArgumentException(var8.toString());
         } else {
            $this$setPathSegment_u24lambda_u240.encodedPathSegments.set(index, canonicalPathSegment);
            return this;
         }
      }

      @NotNull
      public final Builder setEncodedPathSegment(int index, @NotNull String encodedPathSegment) {
         Intrinsics.checkNotNullParameter(encodedPathSegment, "encodedPathSegment");
         Builder $this$setEncodedPathSegment_u24lambda_u240 = this;
         int var5 = 0;
         String canonicalPathSegment = _UrlKt.canonicalize$default(encodedPathSegment, 0, 0, " \"<>^`{}|/\\?#", true, false, false, false, 115, (Object)null);
         $this$setEncodedPathSegment_u24lambda_u240.encodedPathSegments.set(index, canonicalPathSegment);
         if ($this$setEncodedPathSegment_u24lambda_u240.isDot(canonicalPathSegment) || $this$setEncodedPathSegment_u24lambda_u240.isDotDot(canonicalPathSegment)) {
            int var7 = 0;
            String var8 = "unexpected path segment: " + encodedPathSegment;
            throw new IllegalArgumentException(var8.toString());
         } else {
            return this;
         }
      }

      @NotNull
      public final Builder removePathSegment(int index) {
         Builder $this$removePathSegment_u24lambda_u240 = this;
         int var4 = 0;
         $this$removePathSegment_u24lambda_u240.encodedPathSegments.remove(index);
         if ($this$removePathSegment_u24lambda_u240.encodedPathSegments.isEmpty()) {
            $this$removePathSegment_u24lambda_u240.encodedPathSegments.add("");
         }

         return this;
      }

      @NotNull
      public final Builder encodedPath(@NotNull String encodedPath) {
         Intrinsics.checkNotNullParameter(encodedPath, "encodedPath");
         Builder $this$encodedPath_u24lambda_u240 = this;
         int var4 = 0;
         if (!StringsKt.startsWith$default(encodedPath, "/", false, 2, (Object)null)) {
            int var5 = 0;
            String var6 = "unexpected encodedPath: " + encodedPath;
            throw new IllegalArgumentException(var6.toString());
         } else {
            $this$encodedPath_u24lambda_u240.resolvePath(encodedPath, 0, encodedPath.length());
            return this;
         }
      }

      @NotNull
      public final Builder query(@Nullable String query) {
         Builder $this$query_u24lambda_u240;
         List var10001;
         label12: {
            $this$query_u24lambda_u240 = this;
            int var4 = 0;
            if (query != null) {
               String var5 = _UrlKt.canonicalize$default(query, 0, 0, " \"'<>#", false, false, true, false, 91, (Object)null);
               if (var5 != null) {
                  var10001 = $this$query_u24lambda_u240.toQueryNamesAndValues(var5);
                  break label12;
               }
            }

            var10001 = null;
         }

         $this$query_u24lambda_u240.encodedQueryNamesAndValues = var10001;
         return this;
      }

      @NotNull
      public final Builder encodedQuery(@Nullable String encodedQuery) {
         Builder $this$encodedQuery_u24lambda_u240;
         List var10001;
         label12: {
            $this$encodedQuery_u24lambda_u240 = this;
            int var4 = 0;
            if (encodedQuery != null) {
               String var5 = _UrlKt.canonicalize$default(encodedQuery, 0, 0, " \"'<>#", true, false, true, false, 83, (Object)null);
               if (var5 != null) {
                  var10001 = $this$encodedQuery_u24lambda_u240.toQueryNamesAndValues(var5);
                  break label12;
               }
            }

            var10001 = null;
         }

         $this$encodedQuery_u24lambda_u240.encodedQueryNamesAndValues = var10001;
         return this;
      }

      @NotNull
      public final Builder addQueryParameter(@NotNull String name, @Nullable String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Builder $this$addQueryParameter_u24lambda_u240 = this;
         int var5 = 0;
         if ($this$addQueryParameter_u24lambda_u240.encodedQueryNamesAndValues == null) {
            $this$addQueryParameter_u24lambda_u240.encodedQueryNamesAndValues = (List)(new ArrayList());
         }

         List var10000 = $this$addQueryParameter_u24lambda_u240.encodedQueryNamesAndValues;
         Intrinsics.checkNotNull(var10000);
         var10000.add(_UrlKt.canonicalize$default(name, 0, 0, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, false, 91, (Object)null));
         var10000 = $this$addQueryParameter_u24lambda_u240.encodedQueryNamesAndValues;
         Intrinsics.checkNotNull(var10000);
         var10000.add(value != null ? _UrlKt.canonicalize$default(value, 0, 0, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, false, 91, (Object)null) : null);
         return this;
      }

      @NotNull
      public final Builder addEncodedQueryParameter(@NotNull String encodedName, @Nullable String encodedValue) {
         Intrinsics.checkNotNullParameter(encodedName, "encodedName");
         Builder $this$addEncodedQueryParameter_u24lambda_u240 = this;
         int var5 = 0;
         if ($this$addEncodedQueryParameter_u24lambda_u240.encodedQueryNamesAndValues == null) {
            $this$addEncodedQueryParameter_u24lambda_u240.encodedQueryNamesAndValues = (List)(new ArrayList());
         }

         List var10000 = $this$addEncodedQueryParameter_u24lambda_u240.encodedQueryNamesAndValues;
         Intrinsics.checkNotNull(var10000);
         var10000.add(_UrlKt.canonicalize$default(encodedName, 0, 0, " \"'<>#&=", true, false, true, false, 83, (Object)null));
         var10000 = $this$addEncodedQueryParameter_u24lambda_u240.encodedQueryNamesAndValues;
         Intrinsics.checkNotNull(var10000);
         var10000.add(encodedValue != null ? _UrlKt.canonicalize$default(encodedValue, 0, 0, " \"'<>#&=", true, false, true, false, 83, (Object)null) : null);
         return this;
      }

      @NotNull
      public final Builder setQueryParameter(@NotNull String name, @Nullable String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Builder $this$setQueryParameter_u24lambda_u240 = this;
         int var5 = 0;
         $this$setQueryParameter_u24lambda_u240.removeAllQueryParameters(name);
         $this$setQueryParameter_u24lambda_u240.addQueryParameter(name, value);
         return this;
      }

      @NotNull
      public final Builder setEncodedQueryParameter(@NotNull String encodedName, @Nullable String encodedValue) {
         Intrinsics.checkNotNullParameter(encodedName, "encodedName");
         Builder $this$setEncodedQueryParameter_u24lambda_u240 = this;
         int var5 = 0;
         $this$setEncodedQueryParameter_u24lambda_u240.removeAllEncodedQueryParameters(encodedName);
         $this$setEncodedQueryParameter_u24lambda_u240.addEncodedQueryParameter(encodedName, encodedValue);
         return this;
      }

      @NotNull
      public final Builder removeAllQueryParameters(@NotNull String name) {
         Intrinsics.checkNotNullParameter(name, "name");
         Builder $this$removeAllQueryParameters_u24lambda_u240 = this;
         int var4 = 0;
         if ($this$removeAllQueryParameters_u24lambda_u240.encodedQueryNamesAndValues == null) {
            return $this$removeAllQueryParameters_u24lambda_u240;
         } else {
            String nameToRemove = _UrlKt.canonicalize$default(name, 0, 0, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, false, 91, (Object)null);
            $this$removeAllQueryParameters_u24lambda_u240.removeAllCanonicalQueryParameters(nameToRemove);
            return this;
         }
      }

      @NotNull
      public final Builder removeAllEncodedQueryParameters(@NotNull String encodedName) {
         Intrinsics.checkNotNullParameter(encodedName, "encodedName");
         Builder $this$removeAllEncodedQueryParameters_u24lambda_u240 = this;
         int var4 = 0;
         if ($this$removeAllEncodedQueryParameters_u24lambda_u240.encodedQueryNamesAndValues == null) {
            return $this$removeAllEncodedQueryParameters_u24lambda_u240;
         } else {
            $this$removeAllEncodedQueryParameters_u24lambda_u240.removeAllCanonicalQueryParameters(_UrlKt.canonicalize$default(encodedName, 0, 0, " \"'<>#&=", true, false, true, false, 83, (Object)null));
            return this;
         }
      }

      private final void removeAllCanonicalQueryParameters(String canonicalName) {
         List var10000 = this.encodedQueryNamesAndValues;
         Intrinsics.checkNotNull(var10000);
         int var2 = var10000.size() - 2;
         int i = var2;
         int var4 = ProgressionUtilKt.getProgressionLastElement(var2, 0, -2);
         if (var4 <= var2) {
            while(true) {
               List var10001 = this.encodedQueryNamesAndValues;
               Intrinsics.checkNotNull(var10001);
               if (Intrinsics.areEqual((Object)canonicalName, (Object)var10001.get(i))) {
                  var10000 = this.encodedQueryNamesAndValues;
                  Intrinsics.checkNotNull(var10000);
                  var10000.remove(i + 1);
                  var10000 = this.encodedQueryNamesAndValues;
                  Intrinsics.checkNotNull(var10000);
                  var10000.remove(i);
                  var10000 = this.encodedQueryNamesAndValues;
                  Intrinsics.checkNotNull(var10000);
                  if (var10000.isEmpty()) {
                     this.encodedQueryNamesAndValues = null;
                     return;
                  }
               }

               if (i == var4) {
                  break;
               }

               i -= 2;
            }
         }

      }

      @NotNull
      public final Builder fragment(@Nullable String fragment) {
         Builder $this$fragment_u24lambda_u240 = this;
         int var4 = 0;
         $this$fragment_u24lambda_u240.encodedFragment = fragment != null ? _UrlKt.canonicalize$default(fragment, 0, 0, "", false, false, false, true, 59, (Object)null) : null;
         return this;
      }

      @NotNull
      public final Builder encodedFragment(@Nullable String encodedFragment) {
         Builder $this$encodedFragment_u24lambda_u240 = this;
         int var4 = 0;
         $this$encodedFragment_u24lambda_u240.encodedFragment = encodedFragment != null ? _UrlKt.canonicalize$default(encodedFragment, 0, 0, "", true, false, false, true, 51, (Object)null) : null;
         return this;
      }

      @NotNull
      public final Builder reencodeForUri$okhttp() {
         Builder $this$reencodeForUri_u24lambda_u240 = this;
         int var3 = 0;
         String var10001 = $this$reencodeForUri_u24lambda_u240.host;
         if (var10001 != null) {
            CharSequence var4 = (CharSequence)var10001;
            Regex var5 = new Regex("[\"<>^`{|}]");
            String var6 = "";
            var10001 = var5.replace(var4, var6);
         } else {
            var10001 = null;
         }

         $this$reencodeForUri_u24lambda_u240.host = var10001;
         int i = 0;

         for(int var8 = $this$reencodeForUri_u24lambda_u240.encodedPathSegments.size(); i < var8; ++i) {
            $this$reencodeForUri_u24lambda_u240.encodedPathSegments.set(i, _UrlKt.canonicalize$default((String)$this$reencodeForUri_u24lambda_u240.encodedPathSegments.get(i), 0, 0, "[]", true, true, false, false, 99, (Object)null));
         }

         List encodedQueryNamesAndValues = $this$reencodeForUri_u24lambda_u240.encodedQueryNamesAndValues;
         if (encodedQueryNamesAndValues != null) {
            int i = 0;

            for(int var10 = encodedQueryNamesAndValues.size(); i < var10; ++i) {
               String var10002 = (String)encodedQueryNamesAndValues.get(i);
               encodedQueryNamesAndValues.set(i, var10002 != null ? _UrlKt.canonicalize$default(var10002, 0, 0, "\\^`{|}", true, true, true, false, 67, (Object)null) : null);
            }
         }

         var10001 = $this$reencodeForUri_u24lambda_u240.encodedFragment;
         $this$reencodeForUri_u24lambda_u240.encodedFragment = var10001 != null ? _UrlKt.canonicalize$default(var10001, 0, 0, " \"#<>\\^`{|}", true, true, false, true, 35, (Object)null) : null;
         return this;
      }

      @NotNull
      public final HttpUrl build() {
         String var10000 = this.scheme;
         if (var10000 == null) {
            throw new IllegalStateException("scheme == null");
         } else {
            String var10001 = _UrlKt.percentDecode$default(this.encodedUsername, 0, 0, false, 7, (Object)null);
            String var10002 = _UrlKt.percentDecode$default(this.encodedPassword, 0, 0, false, 7, (Object)null);
            String var10003 = this.host;
            if (var10003 == null) {
               throw new IllegalStateException("host == null");
            } else {
               int var10004 = this.effectivePort();
               Iterable $this$map$iv = (Iterable)this.encodedPathSegments;
               int var15 = var10004;
               String var14 = var10003;
               String var13 = var10002;
               String var12 = var10001;
               String var11 = var10000;
               int $i$f$map = 0;
               Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
               int $i$f$mapTo = 0;

               for(Object item$iv$iv : $this$map$iv) {
                  String it = (String)item$iv$iv;
                  int var9 = 0;
                  destination$iv$iv.add(_UrlKt.percentDecode$default(it, 0, 0, false, 7, (Object)null));
               }

               List var16 = (List)destination$iv$iv;
               var10000 = var11;
               var10001 = var12;
               var10002 = var13;
               var10003 = var14;
               var10004 = var15;
               List var10005 = var16;
               List var10006 = this.encodedQueryNamesAndValues;
               if (var10006 != null) {
                  Iterable $this$map$iv = (Iterable)var10006;
                  int $i$f$map = 0;
                  Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
                  int $i$f$mapTo = 0;

                  for(Object item$iv$iv : $this$map$iv) {
                     String it = (String)item$iv$iv;
                     int var10 = 0;
                     destination$iv$iv.add(it != null ? _UrlKt.percentDecode$default(it, 0, 0, true, 3, (Object)null) : null);
                  }

                  List var17 = (List)destination$iv$iv;
                  var10000 = var11;
                  var10001 = var12;
                  var10002 = var13;
                  var10003 = var14;
                  var10004 = var15;
                  var10005 = var16;
                  var10006 = var17;
               } else {
                  var10006 = null;
               }

               String var10007 = this.encodedFragment;
               var10007 = var10007 != null ? _UrlKt.percentDecode$default(var10007, 0, 0, false, 7, (Object)null) : null;
               String var10008 = this.toString();
               Object var18 = null;
               String var19 = var10008;
               String var20 = var10007;
               List var21 = var10006;
               List var22 = var10005;
               int var23 = var10004;
               String var24 = var10003;
               String var25 = var10002;
               String var26 = var10001;
               String var27 = var10000;
               return new HttpUrl(var27, var26, var25, var24, var23, var22, var21, var20, var19, (DefaultConstructorMarker)var18);
            }
         }
      }

      private final int effectivePort() {
         int var10000;
         if (this.port != -1) {
            var10000 = this.port;
         } else {
            Companion var1 = HttpUrl.Companion;
            String var10001 = this.scheme;
            Intrinsics.checkNotNull(var10001);
            var10000 = var1.defaultPort(var10001);
         }

         return var10000;
      }

      @NotNull
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         int var3 = 0;
         if (this.scheme != null) {
            var1.append(this.scheme);
            var1.append("://");
         } else {
            var1.append("//");
         }

         if (((CharSequence)this.encodedUsername).length() > 0 || ((CharSequence)this.encodedPassword).length() > 0) {
            var1.append(this.encodedUsername);
            if (((CharSequence)this.encodedPassword).length() > 0) {
               var1.append(':');
               var1.append(this.encodedPassword);
            }

            var1.append('@');
         }

         if (this.host != null) {
            String var10000 = this.host;
            Intrinsics.checkNotNull(var10000);
            if (StringsKt.contains$default((CharSequence)var10000, ':', false, 2, (Object)null)) {
               var1.append('[');
               var1.append(this.host);
               var1.append(']');
            } else {
               var1.append(this.host);
            }
         }

         if (this.port != -1 || this.scheme != null) {
            label78: {
               int effectivePort = this.effectivePort();
               if (this.scheme != null) {
                  Companion var10001 = HttpUrl.Companion;
                  String var10002 = this.scheme;
                  Intrinsics.checkNotNull(var10002);
                  if (effectivePort == var10001.defaultPort(var10002)) {
                     break label78;
                  }
               }

               var1.append(':');
               var1.append(effectivePort);
            }
         }

         this.toPathString(this.encodedPathSegments, var1);
         if (this.encodedQueryNamesAndValues != null) {
            var1.append('?');
            Companion var5 = HttpUrl.Companion;
            List var6 = this.encodedQueryNamesAndValues;
            Intrinsics.checkNotNull(var6);
            var5.toQueryString(var6, var1);
         }

         if (this.encodedFragment != null) {
            var1.append('#');
            var1.append(this.encodedFragment);
         }

         return var1.toString();
      }

      private final void toPathString(List<String> $this$toPathString, StringBuilder out) {
         int i = 0;

         for(int var4 = $this$toPathString.size(); i < var4; ++i) {
            out.append('/');
            out.append((String)$this$toPathString.get(i));
         }

      }

      @NotNull
      public final Builder parse$okhttp(@Nullable HttpUrl base, @NotNull String input) {
         Intrinsics.checkNotNullParameter(input, "input");
         int pos = 0;
         pos = _UtilCommonKt.indexOfFirstNonAsciiWhitespace$default(input, 0, 0, 3, (Object)null);
         int limit = _UtilCommonKt.indexOfLastNonAsciiWhitespace$default(input, pos, 0, 2, (Object)null);
         int schemeDelimiterOffset = this.schemeDelimiterOffset(input, pos, limit);
         if (schemeDelimiterOffset != -1) {
            if (StringsKt.startsWith(input, "https:", pos, true)) {
               this.scheme = "https";
               pos += 6;
            } else {
               if (!StringsKt.startsWith(input, "http:", pos, true)) {
                  StringBuilder var10002 = (new StringBuilder()).append("Expected URL scheme 'http' or 'https' but was '");
                  String var10003 = input.substring(0, schemeDelimiterOffset);
                  Intrinsics.checkNotNullExpressionValue(var10003, "substring(...)");
                  throw new IllegalArgumentException(var10002.append(var10003).append('\'').toString());
               }

               this.scheme = "http";
               pos += 5;
            }
         } else {
            if (base == null) {
               String truncated = input.length() > 6 ? StringsKt.take(input, 6) + "..." : input;
               throw new IllegalArgumentException("Expected URL scheme 'http' or 'https' but no scheme was found for " + truncated);
            }

            this.scheme = base.scheme();
         }

         boolean hasUsername = false;
         boolean hasPassword = false;
         int slashCount = this.slashCount(input, pos, limit);
         if (slashCount < 2 && base != null && Intrinsics.areEqual((Object)base.scheme(), (Object)this.scheme)) {
            this.encodedUsername = base.encodedUsername();
            this.encodedPassword = base.encodedPassword();
            this.host = base.host();
            this.port = base.port();
            this.encodedPathSegments.clear();
            this.encodedPathSegments.addAll((Collection)base.encodedPathSegments());
            if (pos == limit || input.charAt(pos) == '#') {
               this.encodedQuery(base.encodedQuery());
            }
         } else {
            pos += slashCount;

            label94:
            while(true) {
               int componentDelimiterOffset = _UtilCommonKt.delimiterOffset(input, "@/\\?#", pos, limit);
               int c = componentDelimiterOffset != limit ? input.charAt(componentDelimiterOffset) : -1;
               switch (c) {
                  case -1:
                  case 35:
                  case 47:
                  case 63:
                  case 92:
                     int portColonOffset = this.portColonOffset(input, pos, componentDelimiterOffset);
                     if (portColonOffset + 1 < componentDelimiterOffset) {
                        this.host = _HostnamesCommonKt.toCanonicalHost(_UrlKt.percentDecode$default(input, pos, portColonOffset, false, 4, (Object)null));
                        this.port = this.parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                        if (this.port == -1) {
                           int var13 = 0;
                           StringBuilder var10000 = (new StringBuilder()).append("Invalid URL port: \"");
                           String var10001 = input.substring(portColonOffset + 1, componentDelimiterOffset);
                           Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
                           String var21 = var10000.append(var10001).append('"').toString();
                           throw new IllegalArgumentException(var21.toString());
                        }
                     } else {
                        this.host = _HostnamesCommonKt.toCanonicalHost(_UrlKt.percentDecode$default(input, pos, portColonOffset, false, 4, (Object)null));
                        Companion var25 = HttpUrl.Companion;
                        String var27 = this.scheme;
                        Intrinsics.checkNotNull(var27);
                        this.port = var25.defaultPort(var27);
                     }

                     if (this.host == null) {
                        int var22 = 0;
                        StringBuilder var24 = (new StringBuilder()).append("Invalid URL host: \"");
                        String var26 = input.substring(pos, portColonOffset);
                        Intrinsics.checkNotNullExpressionValue(var26, "substring(...)");
                        String var23 = var24.append(var26).append('"').toString();
                        throw new IllegalArgumentException(var23.toString());
                     }

                     pos = componentDelimiterOffset;
                     break label94;
                  case 64:
                     if (!hasPassword) {
                        int passwordColonOffset = _UtilCommonKt.delimiterOffset(input, ':', pos, componentDelimiterOffset);
                        String canonicalUsername = _UrlKt.canonicalize$default(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, 112, (Object)null);
                        this.encodedUsername = hasUsername ? this.encodedUsername + "%40" + canonicalUsername : canonicalUsername;
                        if (passwordColonOffset != componentDelimiterOffset) {
                           hasPassword = true;
                           this.encodedPassword = _UrlKt.canonicalize$default(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, 112, (Object)null);
                        }

                        hasUsername = true;
                     } else {
                        this.encodedPassword = this.encodedPassword + "%40" + _UrlKt.canonicalize$default(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, 112, (Object)null);
                     }

                     pos = componentDelimiterOffset + 1;
               }
            }
         }

         int pathDelimiterOffset = _UtilCommonKt.delimiterOffset(input, "?#", pos, limit);
         this.resolvePath(input, pos, pathDelimiterOffset);
         pos = pathDelimiterOffset;
         if (pathDelimiterOffset < limit && input.charAt(pathDelimiterOffset) == '?') {
            int queryDelimiterOffset = _UtilCommonKt.delimiterOffset(input, '#', pathDelimiterOffset, limit);
            this.encodedQueryNamesAndValues = this.toQueryNamesAndValues(_UrlKt.canonicalize$default(input, pathDelimiterOffset + 1, queryDelimiterOffset, " \"'<>#", true, false, true, false, 80, (Object)null));
            pos = queryDelimiterOffset;
         }

         if (pos < limit && input.charAt(pos) == '#') {
            this.encodedFragment = _UrlKt.canonicalize$default(input, pos + 1, limit, "", true, false, false, true, 48, (Object)null);
         }

         return this;
      }

      private final void resolvePath(String input, int startPos, int limit) {
         int pos = startPos;
         if (startPos != limit) {
            char c = input.charAt(startPos);
            switch (c) {
               case '/':
               case '\\':
                  this.encodedPathSegments.clear();
                  this.encodedPathSegments.add("");
                  pos = startPos + 1;
                  break;
               default:
                  this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
            }

            int i = pos;

            while(i < limit) {
               int pathSegmentDelimiterOffset = _UtilCommonKt.delimiterOffset(input, "/\\", i, limit);
               boolean segmentHasTrailingSlash = pathSegmentDelimiterOffset < limit;
               this.push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
               i = pathSegmentDelimiterOffset;
               if (segmentHasTrailingSlash) {
                  i = pathSegmentDelimiterOffset + 1;
               }
            }

         }
      }

      private final void push(String input, int pos, int limit, boolean addTrailingSlash, boolean alreadyEncoded) {
         String segment = _UrlKt.canonicalize$default(input, pos, limit, " \"<>^`{}|/\\?#", alreadyEncoded, false, false, false, 112, (Object)null);
         if (!this.isDot(segment)) {
            if (this.isDotDot(segment)) {
               this.pop();
            } else {
               if (((CharSequence)this.encodedPathSegments.get(this.encodedPathSegments.size() - 1)).length() == 0) {
                  this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, segment);
               } else {
                  this.encodedPathSegments.add(segment);
               }

               if (addTrailingSlash) {
                  this.encodedPathSegments.add("");
               }

            }
         }
      }

      private final void pop() {
         String removed = (String)this.encodedPathSegments.remove(this.encodedPathSegments.size() - 1);
         if (((CharSequence)removed).length() == 0 && !((Collection)this.encodedPathSegments).isEmpty()) {
            this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
         } else {
            this.encodedPathSegments.add("");
         }

      }

      private final boolean isDot(String input) {
         return Intrinsics.areEqual((Object)input, (Object)".") || StringsKt.equals(input, "%2e", true);
      }

      private final boolean isDotDot(String input) {
         return Intrinsics.areEqual((Object)input, (Object)"..") || StringsKt.equals(input, "%2e.", true) || StringsKt.equals(input, ".%2e", true) || StringsKt.equals(input, "%2e%2e", true);
      }

      private final List<String> toQueryNamesAndValues(String $this$toQueryNamesAndValues) {
         List result = (List)(new ArrayList());

         int ampersandOffset;
         for(int pos = 0; pos <= $this$toQueryNamesAndValues.length(); pos = ampersandOffset + 1) {
            ampersandOffset = StringsKt.indexOf$default((CharSequence)$this$toQueryNamesAndValues, '&', pos, false, 4, (Object)null);
            if (ampersandOffset == -1) {
               ampersandOffset = $this$toQueryNamesAndValues.length();
            }

            int equalsOffset = StringsKt.indexOf$default((CharSequence)$this$toQueryNamesAndValues, '=', pos, false, 4, (Object)null);
            if (equalsOffset != -1 && equalsOffset <= ampersandOffset) {
               String var6 = $this$toQueryNamesAndValues.substring(pos, equalsOffset);
               Intrinsics.checkNotNullExpressionValue(var6, "substring(...)");
               result.add(var6);
               var6 = $this$toQueryNamesAndValues.substring(equalsOffset + 1, ampersandOffset);
               Intrinsics.checkNotNullExpressionValue(var6, "substring(...)");
               result.add(var6);
            } else {
               String var10001 = $this$toQueryNamesAndValues.substring(pos, ampersandOffset);
               Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
               result.add(var10001);
               result.add((Object)null);
            }
         }

         return result;
      }

      private final int schemeDelimiterOffset(String input, int pos, int limit) {
         if (limit - pos < 2) {
            return -1;
         } else {
            char c0 = input.charAt(pos);
            if (Intrinsics.compare(c0, 97) >= 0 && Intrinsics.compare(c0, 122) <= 0 || Intrinsics.compare(c0, 65) >= 0 && Intrinsics.compare(c0, 90) <= 0) {
               for(int i = pos + 1; i < limit; ++i) {
                  char var6 = input.charAt(i);
                  if (!('a' <= var6 ? var6 < '{' : false) && !('A' <= var6 ? var6 < '[' : false) && !('0' <= var6 ? var6 < ':' : false) && var6 != '+' && var6 != '-' && var6 != '.') {
                     return var6 == ':' ? i : -1;
                  }
               }

               return -1;
            } else {
               return -1;
            }
         }
      }

      private final int slashCount(String $this$slashCount, int pos, int limit) {
         int slashCount = 0;

         for(int i = pos; i < limit; ++i) {
            char c = $this$slashCount.charAt(i);
            switch (c) {
               case '/':
               case '\\':
                  ++slashCount;
                  break;
               default:
                  return slashCount;
            }
         }

         return slashCount;
      }

      private final int portColonOffset(String input, int pos, int limit) {
         for(int i = pos; i < limit; ++i) {
            switch (input.charAt(i)) {
               case ':':
                  return i;
               case '[':
                  while(true) {
                     ++i;
                     if (i >= limit || input.charAt(i) == ']') {
                        break;
                     }
                  }
            }
         }

         return limit;
      }

      private final int parsePort(String input, int pos, int limit) {
         int portString;
         try {
            String portString = _UrlKt.canonicalize$default(input, pos, limit, "", false, false, false, false, 120, (Object)null);
            int i = Integer.parseInt(portString);
            portString = (1 <= i ? i < 65536 : false) ? i : -1;
         } catch (NumberFormatException var6) {
            portString = -1;
         }

         return portString;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J \u0010\b\u001a\u00020\t*\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\n2\n\u0010\u000b\u001a\u00060\fj\u0002`\rH\u0002J\u0011\u0010\u000e\u001a\u00020\u000f*\u00020\u0007H\u0007¢\u0006\u0002\b\u0010J\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u000f*\u00020\u0007H\u0007¢\u0006\u0002\b\u0012J\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u000f*\u00020\u0013H\u0007¢\u0006\u0002\b\u0010J\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u000f*\u00020\u0014H\u0007¢\u0006\u0002\b\u0010J\u0015\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0007H\u0007¢\u0006\u0002\b\u0016J\u0017\u0010\u0012\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0015\u001a\u00020\u0007H\u0007¢\u0006\u0002\b\u0017J\u0017\u0010\u0010\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0015\u001a\u00020\u0013H\u0007¢\u0006\u0002\b\u0016J\u0017\u0010\u0010\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0018\u001a\u00020\u0014H\u0007¢\u0006\u0002\b\u0016¨\u0006\u0019"},
      d2 = {"Lokhttp3/HttpUrl$Companion;", "", "<init>", "()V", "defaultPort", "", "scheme", "", "toQueryString", "", "", "out", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "toHttpUrl", "Lokhttp3/HttpUrl;", "get", "toHttpUrlOrNull", "parse", "Ljava/net/URL;", "Ljava/net/URI;", "url", "-deprecated_get", "-deprecated_parse", "uri", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      public final int defaultPort(@NotNull String scheme) {
         Intrinsics.checkNotNullParameter(scheme, "scheme");
         return Intrinsics.areEqual((Object)scheme, (Object)"http") ? 80 : (Intrinsics.areEqual((Object)scheme, (Object)"https") ? 443 : -1);
      }

      private final void toQueryString(List<String> $this$toQueryString, StringBuilder out) {
         IntProgression var3 = RangesKt.step(RangesKt.until(0, $this$toQueryString.size()), 2);
         int i = var3.getFirst();
         int var5 = var3.getLast();
         int var6 = var3.getStep();
         if (var6 > 0 && i <= var5 || var6 < 0 && var5 <= i) {
            while(true) {
               String name = (String)$this$toQueryString.get(i);
               String value = (String)$this$toQueryString.get(i + 1);
               if (i > 0) {
                  out.append('&');
               }

               out.append(name);
               if (value != null) {
                  out.append('=');
                  out.append(value);
               }

               if (i == var5) {
                  break;
               }

               i += var6;
            }
         }

      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @NotNull
      public final HttpUrl get(@NotNull String $this$toHttpUrl) {
         Intrinsics.checkNotNullParameter($this$toHttpUrl, "<this>");
         return (new Builder()).parse$okhttp((HttpUrl)null, $this$toHttpUrl).build();
      }

      @JvmStatic
      @JvmName(
         name = "parse"
      )
      @Nullable
      public final HttpUrl parse(@NotNull String $this$toHttpUrlOrNull) {
         Intrinsics.checkNotNullParameter($this$toHttpUrlOrNull, "<this>");

         HttpUrl var2;
         try {
            var2 = this.get($this$toHttpUrlOrNull);
         } catch (IllegalArgumentException var4) {
            var2 = null;
         }

         return var2;
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @Nullable
      public final HttpUrl get(@NotNull URL $this$toHttpUrlOrNull) {
         Intrinsics.checkNotNullParameter($this$toHttpUrlOrNull, "<this>");
         String var10001 = $this$toHttpUrlOrNull.toString();
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         return this.parse(var10001);
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @Nullable
      public final HttpUrl get(@NotNull URI $this$toHttpUrlOrNull) {
         Intrinsics.checkNotNullParameter($this$toHttpUrlOrNull, "<this>");
         String var10001 = $this$toHttpUrlOrNull.toString();
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         return this.parse(var10001);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "url.toHttpUrl()",
   imports = {"okhttp3.HttpUrl.Companion.toHttpUrl"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_get"
      )
      @NotNull
      public final HttpUrl _deprecated_get/* $FF was: -deprecated_get*/(@NotNull String url) {
         Intrinsics.checkNotNullParameter(url, "url");
         return this.get(url);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "url.toHttpUrlOrNull()",
   imports = {"okhttp3.HttpUrl.Companion.toHttpUrlOrNull"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_parse"
      )
      @Nullable
      public final HttpUrl _deprecated_parse/* $FF was: -deprecated_parse*/(@NotNull String url) {
         Intrinsics.checkNotNullParameter(url, "url");
         return this.parse(url);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "url.toHttpUrlOrNull()",
   imports = {"okhttp3.HttpUrl.Companion.toHttpUrlOrNull"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_get"
      )
      @Nullable
      public final HttpUrl _deprecated_get/* $FF was: -deprecated_get*/(@NotNull URL url) {
         Intrinsics.checkNotNullParameter(url, "url");
         return this.get(url);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "uri.toHttpUrlOrNull()",
   imports = {"okhttp3.HttpUrl.Companion.toHttpUrlOrNull"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_get"
      )
      @Nullable
      public final HttpUrl _deprecated_get/* $FF was: -deprecated_get*/(@NotNull URI uri) {
         Intrinsics.checkNotNullParameter(uri, "uri");
         return this.get(uri);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
