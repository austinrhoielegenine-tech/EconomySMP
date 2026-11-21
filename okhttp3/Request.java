package okhttp3;

import java.net.URL;
import java.util.List;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import kotlin.text.StringsKt;
import okhttp3.internal.EmptyTags;
import okhttp3.internal.IsProbablyUtf8Kt;
import okhttp3.internal.Tags;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal.http.GzipRequestBody;
import okhttp3.internal.http.HttpMethod;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u00002\u00020\u0001:\u00015B\u0011\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B1\b\u0016\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\u0004\u0010\u000eJ\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u001e\u001a\u00020\u000bJ\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u000b0\u001f2\u0006\u0010\u001e\u001a\u00020\u000bJ\u001e\u0010 \u001a\u0004\u0018\u0001H!\"\n\b\u0000\u0010!\u0018\u0001*\u00020\u0001H\u0087\b¢\u0006\u0004\b\"\u0010#J%\u0010 \u001a\u0004\u0018\u0001H!\"\b\b\u0000\u0010!*\u00020\u00012\f\u0010$\u001a\b\u0012\u0004\u0012\u0002H!0%¢\u0006\u0002\u0010&J\b\u0010 \u001a\u0004\u0018\u00010\u0001J#\u0010 \u001a\u0004\u0018\u0001H!\"\u0004\b\u0000\u0010!2\u000e\u0010$\u001a\n\u0012\u0006\b\u0001\u0012\u0002H!0'¢\u0006\u0002\u0010(J\u0006\u0010)\u001a\u00020\u0003J\r\u0010\u0006\u001a\u00020\u0007H\u0007¢\u0006\u0002\b,J\r\u0010\n\u001a\u00020\u000bH\u0007¢\u0006\u0002\b-J\r\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b.J\u000f\u0010\f\u001a\u0004\u0018\u00010\rH\u0007¢\u0006\u0002\b/J\r\u0010*\u001a\u00020\u0019H\u0007¢\u0006\u0002\b0J\b\u00101\u001a\u00020\u000bH\u0016J\u0012\u00102\u001a\u00020\u000b2\b\b\u0002\u00103\u001a\u00020\u001bH\u0007J\f\u00104\u001a\u00020\u000b*\u00020\u000bH\u0002R\u0013\u0010\u0006\u001a\u00020\u00078\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u000fR\u0013\u0010\n\u001a\u00020\u000b8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0010R\u0013\u0010\b\u001a\u00020\t8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0011R\u0015\u0010\f\u001a\u0004\u0018\u00010\r8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0012R\u0015\u0010\u0013\u001a\u0004\u0018\u00010\u00078\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0014\u0010\u0014\u001a\u00020\u0015X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u001a\u001a\u00020\u001b8F¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001cR\u0011\u0010*\u001a\u00020\u00198G¢\u0006\u0006\u001a\u0004\b*\u0010+¨\u00066"},
   d2 = {"Lokhttp3/Request;", "", "builder", "Lokhttp3/Request$Builder;", "<init>", "(Lokhttp3/Request$Builder;)V", "url", "Lokhttp3/HttpUrl;", "headers", "Lokhttp3/Headers;", "method", "", "body", "Lokhttp3/RequestBody;", "(Lokhttp3/HttpUrl;Lokhttp3/Headers;Ljava/lang/String;Lokhttp3/RequestBody;)V", "()Lokhttp3/HttpUrl;", "()Ljava/lang/String;", "()Lokhttp3/Headers;", "()Lokhttp3/RequestBody;", "cacheUrlOverride", "tags", "Lokhttp3/internal/Tags;", "getTags$okhttp", "()Lokhttp3/internal/Tags;", "lazyCacheControl", "Lokhttp3/CacheControl;", "isHttps", "", "()Z", "header", "name", "", "tag", "T", "reifiedTag", "()Ljava/lang/Object;", "type", "Lkotlin/reflect/KClass;", "(Lkotlin/reflect/KClass;)Ljava/lang/Object;", "Ljava/lang/Class;", "(Ljava/lang/Class;)Ljava/lang/Object;", "newBuilder", "cacheControl", "()Lokhttp3/CacheControl;", "-deprecated_url", "-deprecated_method", "-deprecated_headers", "-deprecated_body", "-deprecated_cacheControl", "toString", "toCurl", "includeBody", "shellEscape", "Builder", "okhttp"}
)
@SourceDebugExtension({"SMAP\nRequest.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Request.kt\nokhttp3/Request\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,495:1\n92#1:497\n1#2:496\n1878#3,3:498\n*S KotlinDebug\n*F\n+ 1 Request.kt\nokhttp3/Request\n*L\n107#1:497\n180#1:498,3\n*E\n"})
public final class Request {
   @NotNull
   private final HttpUrl url;
   @NotNull
   private final String method;
   @NotNull
   private final Headers headers;
   @Nullable
   private final RequestBody body;
   @Nullable
   private final HttpUrl cacheUrlOverride;
   @NotNull
   private final Tags tags;
   @Nullable
   private CacheControl lazyCacheControl;

   public Request(@NotNull Builder builder) {
      Intrinsics.checkNotNullParameter(builder, "builder");
      super();
      HttpUrl var10001 = builder.getUrl$okhttp();
      if (var10001 == null) {
         int var2 = 0;
         String var4 = "url == null";
         throw new IllegalStateException(var4.toString());
      } else {
         this.url = var10001;
         this.method = builder.getMethod$okhttp();
         this.headers = builder.getHeaders$okhttp().build();
         this.body = builder.getBody$okhttp();
         this.cacheUrlOverride = builder.getCacheUrlOverride$okhttp();
         this.tags = builder.getTags$okhttp();
      }
   }

   @JvmName(
      name = "url"
   )
   @NotNull
   public final HttpUrl url() {
      return this.url;
   }

   @JvmName(
      name = "method"
   )
   @NotNull
   public final String method() {
      return this.method;
   }

   @JvmName(
      name = "headers"
   )
   @NotNull
   public final Headers headers() {
      return this.headers;
   }

   @JvmName(
      name = "body"
   )
   @Nullable
   public final RequestBody body() {
      return this.body;
   }

   @JvmName(
      name = "cacheUrlOverride"
   )
   @Nullable
   public final HttpUrl cacheUrlOverride() {
      return this.cacheUrlOverride;
   }

   @NotNull
   public final Tags getTags$okhttp() {
      return this.tags;
   }

   public final boolean isHttps() {
      return this.url.isHttps();
   }

   public Request(@NotNull HttpUrl url, @NotNull Headers headers, @NotNull String method, @Nullable RequestBody body) {
      Intrinsics.checkNotNullParameter(url, "url");
      Intrinsics.checkNotNullParameter(headers, "headers");
      Intrinsics.checkNotNullParameter(method, "method");
      this((new Builder()).url(url).headers(headers).method(!Intrinsics.areEqual((Object)method, (Object)"\u0000") ? method : (body != null ? "POST" : "GET"), body));
   }

   // $FF: synthetic method
   public Request(HttpUrl var1, Headers var2, String var3, RequestBody var4, int var5, DefaultConstructorMarker var6) {
      if ((var5 & 2) != 0) {
         var2 = Headers.Companion.of();
      }

      if ((var5 & 4) != 0) {
         var3 = "\u0000";
      }

      if ((var5 & 8) != 0) {
         var4 = null;
      }

      this(var1, var2, var3, var4);
   }

   @Nullable
   public final String header(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      return this.headers.get(name);
   }

   @NotNull
   public final List<String> headers(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      return this.headers.values(name);
   }

   // $FF: synthetic method
   @JvmName(
      name = "reifiedTag"
   )
   public final <T> T reifiedTag() {
      int $i$f$reifiedTag = 0;
      Intrinsics.reifiedOperationMarker(4, "T");
      return (T)this.tag(Reflection.getOrCreateKotlinClass(Object.class));
   }

   @Nullable
   public final <T> T tag(@NotNull KClass<T> type) {
      Intrinsics.checkNotNullParameter(type, "type");
      return (T)JvmClassMappingKt.getJavaClass(type).cast(this.tags.get(type));
   }

   @Nullable
   public final Object tag() {
      int $i$f$reifiedTag = 0;
      return this.tag(Reflection.getOrCreateKotlinClass(Object.class));
   }

   @Nullable
   public final <T> T tag(@NotNull Class<? extends T> type) {
      Intrinsics.checkNotNullParameter(type, "type");
      return (T)this.tag(JvmClassMappingKt.getKotlinClass(type));
   }

   @NotNull
   public final Builder newBuilder() {
      return new Builder(this);
   }

   @JvmName(
      name = "cacheControl"
   )
   @NotNull
   public final CacheControl cacheControl() {
      CacheControl result = this.lazyCacheControl;
      if (result == null) {
         result = CacheControl.Companion.parse(this.headers);
         this.lazyCacheControl = result;
      }

      return result;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "url",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_url"
   )
   @NotNull
   public final HttpUrl _deprecated_url/* $FF was: -deprecated_url*/() {
      return this.url;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "method",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_method"
   )
   @NotNull
   public final String _deprecated_method/* $FF was: -deprecated_method*/() {
      return this.method;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "headers",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_headers"
   )
   @NotNull
   public final Headers _deprecated_headers/* $FF was: -deprecated_headers*/() {
      return this.headers;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "body",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_body"
   )
   @Nullable
   public final RequestBody _deprecated_body/* $FF was: -deprecated_body*/() {
      return this.body;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "cacheControl",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_cacheControl"
   )
   @NotNull
   public final CacheControl _deprecated_cacheControl/* $FF was: -deprecated_cacheControl*/() {
      return this.cacheControl();
   }

   @NotNull
   public String toString() {
      byte var1 = 32;
      StringBuilder var2 = new StringBuilder(var1);
      StringBuilder $this$toString_u24lambda_u240 = var2;
      int var4 = 0;
      var2.append("Request{method=");
      var2.append(this.method);
      var2.append(", url=");
      var2.append(this.url);
      if (this.headers.size() != 0) {
         var2.append(", headers=[");
         Iterable $this$forEachIndexed$iv = this.headers;
         int $i$f$forEachIndexed = 0;
         int index$iv = 0;

         for(Object item$iv : $this$forEachIndexed$iv) {
            int index = index$iv++;
            if (index < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            Pair var11 = (Pair)item$iv;
            int var13 = 0;
            String name = (String)var11.component1();
            String value = (String)var11.component2();
            if (index > 0) {
               $this$toString_u24lambda_u240.append(", ");
            }

            $this$toString_u24lambda_u240.append(name);
            $this$toString_u24lambda_u240.append(':');
            $this$toString_u24lambda_u240.append(_UtilCommonKt.isSensitiveHeader(name) ? "██" : value);
         }

         $this$toString_u24lambda_u240.append(']');
      }

      if (!Intrinsics.areEqual((Object)this.tags, (Object)EmptyTags.INSTANCE)) {
         $this$toString_u24lambda_u240.append(", tags=");
         $this$toString_u24lambda_u240.append(this.tags);
      }

      $this$toString_u24lambda_u240.append('}');
      return var2.toString();
   }

   @JvmOverloads
   @NotNull
   public final String toCurl(boolean includeBody) {
      StringBuilder var2;
      StringBuilder $this$toCurl_u24lambda_u240;
      String var13;
      label53: {
         var2 = new StringBuilder();
         $this$toCurl_u24lambda_u240 = var2;
         int var4 = 0;
         var2.append("curl " + this.shellEscape(this.url.toString()));
         RequestBody var10000 = this.body;
         if (var10000 != null) {
            MediaType var12 = var10000.contentType();
            if (var12 != null) {
               var13 = var12.toString();
               break label53;
            }
         }

         var13 = null;
      }

      String contentType = var13;
      String defaultMethod = includeBody && this.body != null ? "POST" : "GET";
      if (!Intrinsics.areEqual((Object)this.method, (Object)defaultMethod)) {
         var2.append(" \\\n  -X " + this.shellEscape(this.method));
      }

      for(Pair var8 : this.headers) {
         String name = (String)var8.component1();
         String value = (String)var8.component2();
         if (contentType == null || !StringsKt.equals(name, "Content-Type", true)) {
            $this$toCurl_u24lambda_u240.append(" \\\n  -H " + this.shellEscape(name + ": " + value));
         }
      }

      if (contentType != null) {
         $this$toCurl_u24lambda_u240.append(" \\\n  -H " + this.shellEscape("Content-Type: " + contentType));
      }

      if (includeBody && this.body != null) {
         Buffer bodyBuffer = new Buffer();
         this.body.writeTo(bodyBuffer);
         if (IsProbablyUtf8Kt.isProbablyUtf8$default(bodyBuffer, 0L, 1, (Object)null)) {
            $this$toCurl_u24lambda_u240.append(" \\\n  --data " + this.shellEscape(bodyBuffer.readUtf8()));
         } else {
            $this$toCurl_u24lambda_u240.append(" \\\n  --data-binary " + this.shellEscape(bodyBuffer.readByteString().hex()));
         }
      }

      return var2.toString();
   }

   // $FF: synthetic method
   public static String toCurl$default(Request var0, boolean var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = true;
      }

      return var0.toCurl(var1);
   }

   private final String shellEscape(String $this$shellEscape) {
      return '\'' + StringsKt.replace$default($this$shellEscape, "'", "'\\''", false, 4, (Object)null) + '\'';
   }

   @JvmOverloads
   @NotNull
   public final String toCurl() {
      return toCurl$default(this, false, 1, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0016\u0018\u00002\u00020\u0001B\t\b\u0016¢\u0006\u0004\b\u0002\u0010\u0003B\u0011\b\u0010\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\u0007\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\u000eH\u0016J\u0010\u0010(\u001a\u00020\u000e2\u0006\u0010\u0007\u001a\u00020\u000eH\u0002J\u0010\u0010\u0007\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020)H\u0016J\u0018\u0010*\u001a\u00020\u00002\u0006\u0010+\u001a\u00020\u000e2\u0006\u0010,\u001a\u00020\u000eH\u0016J\u0018\u0010-\u001a\u00020\u00002\u0006\u0010+\u001a\u00020\u000e2\u0006\u0010,\u001a\u00020\u000eH\u0016J\u0010\u0010.\u001a\u00020\u00002\u0006\u0010+\u001a\u00020\u000eH\u0016J\u0010\u0010\u0013\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020/H\u0016J\u0010\u00100\u001a\u00020\u00002\u0006\u00100\u001a\u000201H\u0016J\b\u00102\u001a\u00020\u0000H\u0016J\b\u00103\u001a\u00020\u0000H\u0016J\u0010\u00104\u001a\u00020\u00002\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0014\u00105\u001a\u00020\u00002\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0017J\u0010\u00106\u001a\u00020\u00002\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0010\u00107\u001a\u00020\u00002\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0010\u00108\u001a\u00020\u00002\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u001a\u0010\r\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u000e2\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0016J&\u00109\u001a\u00020\u0000\"\n\b\u0000\u0010:\u0018\u0001*\u00020\u00012\b\u00109\u001a\u0004\u0018\u0001H:H\u0087\b¢\u0006\u0004\b;\u0010<J-\u00109\u001a\u00020\u0000\"\b\b\u0000\u0010:*\u00020\u00012\f\u0010=\u001a\b\u0012\u0004\u0012\u0002H:0>2\b\u00109\u001a\u0004\u0018\u0001H:¢\u0006\u0002\u0010?J\u0012\u00109\u001a\u00020\u00002\b\u00109\u001a\u0004\u0018\u00010\u0001H\u0016J-\u00109\u001a\u00020\u0000\"\u0004\b\u0000\u0010:2\u000e\u0010=\u001a\n\u0012\u0006\b\u0000\u0012\u0002H:0@2\b\u00109\u001a\u0004\u0018\u0001H:H\u0016¢\u0006\u0002\u0010AJ\u0010\u0010\u001f\u001a\u00020\u00002\b\u0010\u001f\u001a\u0004\u0018\u00010\bJ\u0006\u0010B\u001a\u00020\u0000J\b\u0010C\u001a\u00020\u0005H\u0016R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\r\u001a\u00020\u000eX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0013\u001a\u00020\u0014X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001c\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001c\u0010\u001f\u001a\u0004\u0018\u00010\bX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010\n\"\u0004\b!\u0010\fR\u001a\u0010\"\u001a\u00020#X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010'¨\u0006D"},
      d2 = {"Lokhttp3/Request$Builder;", "", "<init>", "()V", "request", "Lokhttp3/Request;", "(Lokhttp3/Request;)V", "url", "Lokhttp3/HttpUrl;", "getUrl$okhttp", "()Lokhttp3/HttpUrl;", "setUrl$okhttp", "(Lokhttp3/HttpUrl;)V", "method", "", "getMethod$okhttp", "()Ljava/lang/String;", "setMethod$okhttp", "(Ljava/lang/String;)V", "headers", "Lokhttp3/Headers$Builder;", "getHeaders$okhttp", "()Lokhttp3/Headers$Builder;", "setHeaders$okhttp", "(Lokhttp3/Headers$Builder;)V", "body", "Lokhttp3/RequestBody;", "getBody$okhttp", "()Lokhttp3/RequestBody;", "setBody$okhttp", "(Lokhttp3/RequestBody;)V", "cacheUrlOverride", "getCacheUrlOverride$okhttp", "setCacheUrlOverride$okhttp", "tags", "Lokhttp3/internal/Tags;", "getTags$okhttp", "()Lokhttp3/internal/Tags;", "setTags$okhttp", "(Lokhttp3/internal/Tags;)V", "canonicalUrl", "Ljava/net/URL;", "header", "name", "value", "addHeader", "removeHeader", "Lokhttp3/Headers;", "cacheControl", "Lokhttp3/CacheControl;", "get", "head", "post", "delete", "put", "patch", "query", "tag", "T", "reifiedTag", "(Ljava/lang/Object;)Lokhttp3/Request$Builder;", "type", "Lkotlin/reflect/KClass;", "(Lkotlin/reflect/KClass;Ljava/lang/Object;)Lokhttp3/Request$Builder;", "Ljava/lang/Class;", "(Ljava/lang/Class;Ljava/lang/Object;)Lokhttp3/Request$Builder;", "gzip", "build", "okhttp"}
   )
   public static class Builder {
      @Nullable
      private HttpUrl url;
      @NotNull
      private String method;
      @NotNull
      private Headers.Builder headers;
      @Nullable
      private RequestBody body;
      @Nullable
      private HttpUrl cacheUrlOverride;
      @NotNull
      private Tags tags;

      @Nullable
      public final HttpUrl getUrl$okhttp() {
         return this.url;
      }

      public final void setUrl$okhttp(@Nullable HttpUrl var1) {
         this.url = var1;
      }

      @NotNull
      public final String getMethod$okhttp() {
         return this.method;
      }

      public final void setMethod$okhttp(@NotNull String var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.method = var1;
      }

      @NotNull
      public final Headers.Builder getHeaders$okhttp() {
         return this.headers;
      }

      public final void setHeaders$okhttp(@NotNull Headers.Builder var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.headers = var1;
      }

      @Nullable
      public final RequestBody getBody$okhttp() {
         return this.body;
      }

      public final void setBody$okhttp(@Nullable RequestBody var1) {
         this.body = var1;
      }

      @Nullable
      public final HttpUrl getCacheUrlOverride$okhttp() {
         return this.cacheUrlOverride;
      }

      public final void setCacheUrlOverride$okhttp(@Nullable HttpUrl var1) {
         this.cacheUrlOverride = var1;
      }

      @NotNull
      public final Tags getTags$okhttp() {
         return this.tags;
      }

      public final void setTags$okhttp(@NotNull Tags var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.tags = var1;
      }

      public Builder() {
         this.tags = EmptyTags.INSTANCE;
         this.method = "GET";
         this.headers = new Headers.Builder();
      }

      public Builder(@NotNull Request request) {
         Intrinsics.checkNotNullParameter(request, "request");
         super();
         this.tags = EmptyTags.INSTANCE;
         this.url = request.url();
         this.method = request.method();
         this.body = request.body();
         this.tags = request.getTags$okhttp();
         this.headers = request.headers().newBuilder();
         this.cacheUrlOverride = request.cacheUrlOverride();
      }

      @NotNull
      public Builder url(@NotNull HttpUrl url) {
         Intrinsics.checkNotNullParameter(url, "url");
         Builder $this$url_u24lambda_u240 = this;
         int var4 = 0;
         $this$url_u24lambda_u240.url = url;
         return this;
      }

      @NotNull
      public Builder url(@NotNull String url) {
         Intrinsics.checkNotNullParameter(url, "url");
         return this.url(HttpUrl.Companion.get(this.canonicalUrl(url)));
      }

      private final String canonicalUrl(String url) {
         String var2;
         if (StringsKt.startsWith(url, "ws:", true)) {
            StringBuilder var10000 = (new StringBuilder()).append("http:");
            String var10001 = url.substring(3);
            Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
            var2 = var10000.append(var10001).toString();
         } else if (StringsKt.startsWith(url, "wss:", true)) {
            StringBuilder var3 = (new StringBuilder()).append("https:");
            String var4 = url.substring(4);
            Intrinsics.checkNotNullExpressionValue(var4, "substring(...)");
            var2 = var3.append(var4).toString();
         } else {
            var2 = url;
         }

         return var2;
      }

      @NotNull
      public Builder url(@NotNull URL url) {
         Intrinsics.checkNotNullParameter(url, "url");
         HttpUrl.Companion var10001 = HttpUrl.Companion;
         String var10002 = url.toString();
         Intrinsics.checkNotNullExpressionValue(var10002, "toString(...)");
         return this.url(var10001.get(var10002));
      }

      @NotNull
      public Builder header(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Builder $this$header_u24lambda_u240 = this;
         int var5 = 0;
         $this$header_u24lambda_u240.headers.set(name, value);
         return this;
      }

      @NotNull
      public Builder addHeader(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Builder $this$addHeader_u24lambda_u240 = this;
         int var5 = 0;
         $this$addHeader_u24lambda_u240.headers.add(name, value);
         return this;
      }

      @NotNull
      public Builder removeHeader(@NotNull String name) {
         Intrinsics.checkNotNullParameter(name, "name");
         Builder $this$removeHeader_u24lambda_u240 = this;
         int var4 = 0;
         $this$removeHeader_u24lambda_u240.headers.removeAll(name);
         return this;
      }

      @NotNull
      public Builder headers(@NotNull Headers headers) {
         Intrinsics.checkNotNullParameter(headers, "headers");
         Builder $this$headers_u24lambda_u240 = this;
         int var4 = 0;
         $this$headers_u24lambda_u240.headers = headers.newBuilder();
         return this;
      }

      @NotNull
      public Builder cacheControl(@NotNull CacheControl cacheControl) {
         Intrinsics.checkNotNullParameter(cacheControl, "cacheControl");
         String value = cacheControl.toString();
         return ((CharSequence)value).length() == 0 ? this.removeHeader("Cache-Control") : this.header("Cache-Control", value);
      }

      @NotNull
      public Builder get() {
         return this.method("GET", (RequestBody)null);
      }

      @NotNull
      public Builder head() {
         return this.method("HEAD", (RequestBody)null);
      }

      @NotNull
      public Builder post(@NotNull RequestBody body) {
         Intrinsics.checkNotNullParameter(body, "body");
         return this.method("POST", body);
      }

      @JvmOverloads
      @NotNull
      public Builder delete(@Nullable RequestBody body) {
         return this.method("DELETE", body);
      }

      // $FF: synthetic method
      public static Builder delete$default(Builder var0, RequestBody var1, int var2, Object var3) {
         if (var3 != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: delete");
         } else {
            if ((var2 & 1) != 0) {
               var1 = RequestBody.EMPTY;
            }

            return var0.delete(var1);
         }
      }

      @NotNull
      public Builder put(@NotNull RequestBody body) {
         Intrinsics.checkNotNullParameter(body, "body");
         return this.method("PUT", body);
      }

      @NotNull
      public Builder patch(@NotNull RequestBody body) {
         Intrinsics.checkNotNullParameter(body, "body");
         return this.method("PATCH", body);
      }

      @NotNull
      public Builder query(@NotNull RequestBody body) {
         Intrinsics.checkNotNullParameter(body, "body");
         return this.method("QUERY", body);
      }

      @NotNull
      public Builder method(@NotNull String method, @Nullable RequestBody body) {
         Intrinsics.checkNotNullParameter(method, "method");
         Builder $this$method_u24lambda_u240 = this;
         int var5 = 0;
         if (((CharSequence)method).length() <= 0) {
            int var10 = 0;
            String var11 = "method.isEmpty() == true";
            throw new IllegalArgumentException(var11.toString());
         } else {
            if (body == null) {
               if (HttpMethod.requiresRequestBody(method)) {
                  int var6 = 0;
                  String var7 = "method " + method + " must have a request body.";
                  throw new IllegalArgumentException(var7.toString());
               }
            } else if (!HttpMethod.permitsRequestBody(method)) {
               int var8 = 0;
               String var9 = "method " + method + " must not have a request body.";
               throw new IllegalArgumentException(var9.toString());
            }

            $this$method_u24lambda_u240.method = method;
            $this$method_u24lambda_u240.body = body;
            return this;
         }
      }

      // $FF: synthetic method
      @JvmName(
         name = "reifiedTag"
      )
      public final <T> Builder reifiedTag(T tag) {
         int $i$f$reifiedTag = 0;
         Intrinsics.reifiedOperationMarker(4, "T");
         return this.tag(Reflection.getOrCreateKotlinClass(Object.class), tag);
      }

      @NotNull
      public final <T> Builder tag(@NotNull KClass<T> type, @Nullable T tag) {
         Intrinsics.checkNotNullParameter(type, "type");
         Builder $this$tag_u24lambda_u240 = this;
         int var5 = 0;
         $this$tag_u24lambda_u240.tags = $this$tag_u24lambda_u240.tags.plus(type, tag);
         return this;
      }

      @NotNull
      public Builder tag(@Nullable Object tag) {
         return this.tag(Reflection.getOrCreateKotlinClass(Object.class), tag);
      }

      @NotNull
      public <T> Builder tag(@NotNull Class<? super T> type, @Nullable T tag) {
         Intrinsics.checkNotNullParameter(type, "type");
         return this.tag(JvmClassMappingKt.getKotlinClass(type), tag);
      }

      @NotNull
      public final Builder cacheUrlOverride(@Nullable HttpUrl cacheUrlOverride) {
         Builder $this$cacheUrlOverride_u24lambda_u240 = this;
         int var4 = 0;
         $this$cacheUrlOverride_u24lambda_u240.cacheUrlOverride = cacheUrlOverride;
         return this;
      }

      @NotNull
      public final Builder gzip() {
         Builder $this$gzip_u24lambda_u240 = this;
         int var3 = 0;
         RequestBody var10000 = $this$gzip_u24lambda_u240.body;
         if (var10000 == null) {
            throw new IllegalStateException("cannot gzip a request that has no body");
         } else {
            RequestBody identityBody = var10000;
            String contentEncoding = $this$gzip_u24lambda_u240.headers.get("Content-Encoding");
            if (contentEncoding != null) {
               int var6 = 0;
               String var7 = "Content-Encoding already set: " + contentEncoding;
               throw new IllegalStateException(var7.toString());
            } else {
               $this$gzip_u24lambda_u240.headers.add("Content-Encoding", "gzip");
               $this$gzip_u24lambda_u240.body = new GzipRequestBody(identityBody);
               return this;
            }
         }
      }

      @NotNull
      public Request build() {
         return new Request(this);
      }

      @JvmOverloads
      @NotNull
      public final Builder delete() {
         return delete$default(this, (RequestBody)null, 1, (Object)null);
      }
   }
}
