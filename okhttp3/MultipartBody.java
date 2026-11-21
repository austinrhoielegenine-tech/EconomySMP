package okhttp3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilJvmKt;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 &2\u00020\u0001:\u0003$%&B'\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0004\b\t\u0010\nJ\u000e\u0010\u0016\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u0014J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\b\u0010\r\u001a\u00020\u0005H\u0016J\r\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\b\u001aJ\r\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\b\u001bJ\r\u0010\u0013\u001a\u00020\u0014H\u0007¢\u0006\u0002\b\u001cJ\u0013\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0007¢\u0006\u0002\b\u001dJ\b\u0010\u000e\u001a\u00020\u000fH\u0016J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!H\u0016J\u001a\u0010\"\u001a\u00020\u000f2\b\u0010 \u001a\u0004\u0018\u00010!2\u0006\u0010#\u001a\u00020\u0019H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0004\u001a\u00020\u00058\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000bR\u0019\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\fR\u000e\u0010\r\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0010\u001a\u00020\u00118G¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0012R\u0011\u0010\u0013\u001a\u00020\u00148G¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0015¨\u0006'"},
   d2 = {"Lokhttp3/MultipartBody;", "Lokhttp3/RequestBody;", "boundaryByteString", "Lokio/ByteString;", "type", "Lokhttp3/MediaType;", "parts", "", "Lokhttp3/MultipartBody$Part;", "<init>", "(Lokio/ByteString;Lokhttp3/MediaType;Ljava/util/List;)V", "()Lokhttp3/MediaType;", "()Ljava/util/List;", "contentType", "contentLength", "", "boundary", "", "()Ljava/lang/String;", "size", "", "()I", "part", "index", "isOneShot", "", "-deprecated_type", "-deprecated_boundary", "-deprecated_size", "-deprecated_parts", "writeTo", "", "sink", "Lokio/BufferedSink;", "writeOrCountBytes", "countBytes", "Part", "Builder", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nMultipartBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartBody.kt\nokhttp3/MultipartBody\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,381:1\n1761#2,3:382\n*S KotlinDebug\n*F\n+ 1 MultipartBody.kt\nokhttp3/MultipartBody\n*L\n52#1:382,3\n*E\n"})
public final class MultipartBody extends RequestBody {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final ByteString boundaryByteString;
   @NotNull
   private final MediaType type;
   @NotNull
   private final List<Part> parts;
   @NotNull
   private final MediaType contentType;
   private long contentLength;
   @JvmField
   @NotNull
   public static final MediaType MIXED;
   @JvmField
   @NotNull
   public static final MediaType ALTERNATIVE;
   @JvmField
   @NotNull
   public static final MediaType DIGEST;
   @JvmField
   @NotNull
   public static final MediaType PARALLEL;
   @JvmField
   @NotNull
   public static final MediaType FORM;
   @NotNull
   private static final byte[] COLONSPACE;
   @NotNull
   private static final byte[] CRLF;
   @NotNull
   private static final byte[] DASHDASH;

   public MultipartBody(@NotNull ByteString boundaryByteString, @NotNull MediaType type, @NotNull List<Part> parts) {
      Intrinsics.checkNotNullParameter(boundaryByteString, "boundaryByteString");
      Intrinsics.checkNotNullParameter(type, "type");
      Intrinsics.checkNotNullParameter(parts, "parts");
      super();
      this.boundaryByteString = boundaryByteString;
      this.type = type;
      this.parts = parts;
      this.contentType = MediaType.Companion.get(this.type + "; boundary=" + this.boundary());
      this.contentLength = -1L;
   }

   @JvmName(
      name = "type"
   )
   @NotNull
   public final MediaType type() {
      return this.type;
   }

   @JvmName(
      name = "parts"
   )
   @NotNull
   public final List<Part> parts() {
      return this.parts;
   }

   @JvmName(
      name = "boundary"
   )
   @NotNull
   public final String boundary() {
      return this.boundaryByteString.utf8();
   }

   @JvmName(
      name = "size"
   )
   public final int size() {
      return this.parts.size();
   }

   @NotNull
   public final Part part(int index) {
      return (Part)this.parts.get(index);
   }

   public boolean isOneShot() {
      Iterable $this$any$iv = (Iterable)this.parts;
      int $i$f$any = 0;
      boolean var10000;
      if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
         var10000 = false;
      } else {
         Iterator var3 = $this$any$iv.iterator();

         while(true) {
            if (!var3.hasNext()) {
               var10000 = false;
               break;
            }

            Object element$iv = var3.next();
            Part it = (Part)element$iv;
            int var6 = 0;
            if (it.body().isOneShot()) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   @NotNull
   public MediaType contentType() {
      return this.contentType;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "type",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_type"
   )
   @NotNull
   public final MediaType _deprecated_type/* $FF was: -deprecated_type*/() {
      return this.type;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "boundary",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_boundary"
   )
   @NotNull
   public final String _deprecated_boundary/* $FF was: -deprecated_boundary*/() {
      return this.boundary();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "size",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_size"
   )
   public final int _deprecated_size/* $FF was: -deprecated_size*/() {
      return this.size();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "parts",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_parts"
   )
   @NotNull
   public final List<Part> _deprecated_parts/* $FF was: -deprecated_parts*/() {
      return this.parts;
   }

   public long contentLength() throws IOException {
      long result = this.contentLength;
      if (result == -1L) {
         result = this.writeOrCountBytes((BufferedSink)null, true);
         this.contentLength = result;
      }

      return result;
   }

   public void writeTo(@NotNull BufferedSink sink) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      this.writeOrCountBytes(sink, false);
   }

   private final long writeOrCountBytes(BufferedSink sink, boolean countBytes) throws IOException {
      BufferedSink sink = sink;
      long byteCount = 0L;
      Buffer byteCountBuffer = null;
      if (countBytes) {
         byteCountBuffer = new Buffer();
         sink = byteCountBuffer;
      }

      int p = 0;

      for(int var8 = this.parts.size(); p < var8; ++p) {
         Part part = (Part)this.parts.get(p);
         Headers headers = part.headers();
         RequestBody body = part.body();
         Intrinsics.checkNotNull(sink);
         sink.write(DASHDASH);
         sink.write(this.boundaryByteString);
         sink.write(CRLF);
         if (headers != null) {
            int h = 0;

            for(int var13 = headers.size(); h < var13; ++h) {
               sink.writeUtf8(headers.name(h)).write(COLONSPACE).writeUtf8(headers.value(h)).write(CRLF);
            }
         }

         MediaType contentType = body.contentType();
         if (contentType != null) {
            sink.writeUtf8("Content-Type: ").writeUtf8(contentType.toString()).write(CRLF);
         }

         long contentLength = body.contentLength();
         if (contentLength == -1L && countBytes) {
            Intrinsics.checkNotNull(byteCountBuffer);
            byteCountBuffer.clear();
            return -1L;
         }

         sink.write(CRLF);
         if (countBytes) {
            byteCount += contentLength;
         } else {
            body.writeTo(sink);
         }

         sink.write(CRLF);
      }

      Intrinsics.checkNotNull(sink);
      sink.write(DASHDASH);
      sink.write(this.boundaryByteString);
      sink.write(DASHDASH);
      sink.write(CRLF);
      if (countBytes) {
         Intrinsics.checkNotNull(byteCountBuffer);
         byteCount += byteCountBuffer.size();
         byteCountBuffer.clear();
      }

      return byteCount;
   }

   static {
      MIXED = MediaType.Companion.get("multipart/mixed");
      ALTERNATIVE = MediaType.Companion.get("multipart/alternative");
      DIGEST = MediaType.Companion.get("multipart/digest");
      PARALLEL = MediaType.Companion.get("multipart/parallel");
      FORM = MediaType.Companion.get("multipart/form-data");
      byte[] var0 = new byte[]{58, 32};
      COLONSPACE = var0;
      var0 = new byte[]{13, 10};
      CRLF = var0;
      var0 = new byte[]{45, 45};
      DASHDASH = var0;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 \f2\u00020\u0001:\u0001\fB\u001b\b\u0002\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u0007¢\u0006\u0002\b\nJ\r\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\b\u000bR\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\bR\u0013\u0010\u0004\u001a\u00020\u00058\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\t¨\u0006\r"},
      d2 = {"Lokhttp3/MultipartBody$Part;", "", "headers", "Lokhttp3/Headers;", "body", "Lokhttp3/RequestBody;", "<init>", "(Lokhttp3/Headers;Lokhttp3/RequestBody;)V", "()Lokhttp3/Headers;", "()Lokhttp3/RequestBody;", "-deprecated_headers", "-deprecated_body", "Companion", "okhttp"}
   )
   public static final class Part {
      @NotNull
      public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
      @Nullable
      private final Headers headers;
      @NotNull
      private final RequestBody body;

      private Part(Headers headers, RequestBody body) {
         this.headers = headers;
         this.body = body;
      }

      @JvmName(
         name = "headers"
      )
      @Nullable
      public final Headers headers() {
         return this.headers;
      }

      @JvmName(
         name = "body"
      )
      @NotNull
      public final RequestBody body() {
         return this.body;
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
      @Nullable
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
      @NotNull
      public final RequestBody _deprecated_body/* $FF was: -deprecated_body*/() {
         return this.body;
      }

      @JvmStatic
      @NotNull
      public static final Part create(@NotNull RequestBody body) {
         return Companion.create(body);
      }

      @JvmStatic
      @NotNull
      public static final Part create(@Nullable Headers headers, @NotNull RequestBody body) {
         return Companion.create(headers, body);
      }

      @JvmStatic
      @NotNull
      public static final Part createFormData(@NotNull String name, @NotNull String value) {
         return Companion.createFormData(name, value);
      }

      @JvmStatic
      @NotNull
      public static final Part createFormData(@NotNull String name, @Nullable String filename, @NotNull RequestBody body) {
         return Companion.createFormData(name, filename, body);
      }

      // $FF: synthetic method
      public Part(Headers headers, RequestBody body, DefaultConstructorMarker $constructor_marker) {
         this(headers, body);
      }

      @Metadata(
         mv = {2, 2, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u001a\u0010\u0004\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\u0007J\"\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u000e\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0006\u001a\u00020\u0007H\u0007¨\u0006\u000f"},
         d2 = {"Lokhttp3/MultipartBody$Part$Companion;", "", "<init>", "()V", "create", "Lokhttp3/MultipartBody$Part;", "body", "Lokhttp3/RequestBody;", "headers", "Lokhttp3/Headers;", "createFormData", "name", "", "value", "filename", "okhttp"}
      )
      @SourceDebugExtension({"SMAP\nMultipartBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartBody.kt\nokhttp3/MultipartBody$Part$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,381:1\n1#2:382\n*E\n"})
      public static final class Companion {
         private Companion() {
         }

         @JvmStatic
         @NotNull
         public final Part create(@NotNull RequestBody body) {
            Intrinsics.checkNotNullParameter(body, "body");
            return this.create((Headers)null, body);
         }

         @JvmStatic
         @NotNull
         public final Part create(@Nullable Headers headers, @NotNull RequestBody body) {
            Intrinsics.checkNotNullParameter(body, "body");
            if ((headers != null ? headers.get("Content-Type") : null) != null) {
               int var5 = 0;
               String var6 = "Unexpected header: Content-Type";
               throw new IllegalArgumentException(var6.toString());
            } else if ((headers != null ? headers.get("Content-Length") : null) != null) {
               int var3 = 0;
               String var4 = "Unexpected header: Content-Length";
               throw new IllegalArgumentException(var4.toString());
            } else {
               return new Part(headers, body, (DefaultConstructorMarker)null);
            }
         }

         @JvmStatic
         @NotNull
         public final Part createFormData(@NotNull String name, @NotNull String value) {
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(value, "value");
            return this.createFormData(name, (String)null, RequestBody.Companion.create$default(RequestBody.Companion, (String)value, (MediaType)null, 1, (Object)null));
         }

         @JvmStatic
         @NotNull
         public final Part createFormData(@NotNull String name, @Nullable String filename, @NotNull RequestBody body) {
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(body, "body");
            StringBuilder $this$createFormData_u24lambda_u240 = new StringBuilder();
            int var7 = 0;
            $this$createFormData_u24lambda_u240.append("form-data; name=");
            MultipartBody.Companion.appendQuotedString$okhttp($this$createFormData_u24lambda_u240, name);
            if (filename != null) {
               $this$createFormData_u24lambda_u240.append("; filename=");
               MultipartBody.Companion.appendQuotedString$okhttp($this$createFormData_u24lambda_u240, filename);
            }

            String disposition = $this$createFormData_u24lambda_u240.toString();
            Headers headers = (new Headers.Builder()).addUnsafeNonAscii("Content-Disposition", disposition).build();
            return this.create(headers, body);
         }

         // $FF: synthetic method
         public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0013\b\u0007\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\f\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\r\u001a\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u000fJ\u0018\u0010\r\u001a\u00020\u00002\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u000e\u001a\u00020\u000fJ\u0016\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u0003J \u0010\u0012\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u00032\b\u0010\u0015\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010\r\u001a\u00020\u00002\u0006\u0010\u0016\u001a\u00020\u000bJ\u0006\u0010\u0017\u001a\u00020\u0018R\u000e\u0010\u0002\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"},
      d2 = {"Lokhttp3/MultipartBody$Builder;", "", "boundary", "", "<init>", "(Ljava/lang/String;)V", "Lokio/ByteString;", "type", "Lokhttp3/MediaType;", "parts", "", "Lokhttp3/MultipartBody$Part;", "setType", "addPart", "body", "Lokhttp3/RequestBody;", "headers", "Lokhttp3/Headers;", "addFormDataPart", "name", "value", "filename", "part", "build", "Lokhttp3/MultipartBody;", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nMultipartBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartBody.kt\nokhttp3/MultipartBody$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,381:1\n1#2:382\n*E\n"})
   public static final class Builder {
      @NotNull
      private final ByteString boundary;
      @NotNull
      private MediaType type;
      @NotNull
      private final List<Part> parts;

      @JvmOverloads
      public Builder(@NotNull String boundary) {
         Intrinsics.checkNotNullParameter(boundary, "boundary");
         super();
         this.boundary = ByteString.Companion.encodeUtf8(boundary);
         this.type = MultipartBody.MIXED;
         this.parts = (List)(new ArrayList());
      }

      // $FF: synthetic method
      public Builder(String var1, int var2, DefaultConstructorMarker var3) {
         if ((var2 & 1) != 0) {
            String var10000 = UUID.randomUUID().toString();
            Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
            var1 = var10000;
         }

         this(var1);
      }

      @NotNull
      public final Builder setType(@NotNull MediaType type) {
         Intrinsics.checkNotNullParameter(type, "type");
         Builder $this$setType_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)type.type(), (Object)"multipart")) {
            int var5 = 0;
            String var6 = "multipart != " + type;
            throw new IllegalArgumentException(var6.toString());
         } else {
            $this$setType_u24lambda_u240.type = type;
            return this;
         }
      }

      @NotNull
      public final Builder addPart(@NotNull RequestBody body) {
         Intrinsics.checkNotNullParameter(body, "body");
         Builder $this$addPart_u24lambda_u240 = this;
         int var4 = 0;
         $this$addPart_u24lambda_u240.addPart(MultipartBody.Part.Companion.create(body));
         return this;
      }

      @NotNull
      public final Builder addPart(@Nullable Headers headers, @NotNull RequestBody body) {
         Intrinsics.checkNotNullParameter(body, "body");
         Builder $this$addPart_u24lambda_u241 = this;
         int var5 = 0;
         $this$addPart_u24lambda_u241.addPart(MultipartBody.Part.Companion.create(headers, body));
         return this;
      }

      @NotNull
      public final Builder addFormDataPart(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Builder $this$addFormDataPart_u24lambda_u240 = this;
         int var5 = 0;
         $this$addFormDataPart_u24lambda_u240.addPart(MultipartBody.Part.Companion.createFormData(name, value));
         return this;
      }

      @NotNull
      public final Builder addFormDataPart(@NotNull String name, @Nullable String filename, @NotNull RequestBody body) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(body, "body");
         Builder $this$addFormDataPart_u24lambda_u241 = this;
         int var6 = 0;
         $this$addFormDataPart_u24lambda_u241.addPart(MultipartBody.Part.Companion.createFormData(name, filename, body));
         return this;
      }

      @NotNull
      public final Builder addPart(@NotNull Part part) {
         Intrinsics.checkNotNullParameter(part, "part");
         Builder $this$addPart_u24lambda_u242 = this;
         int var4 = 0;
         ((Collection)$this$addPart_u24lambda_u242.parts).add(part);
         return this;
      }

      @NotNull
      public final MultipartBody build() {
         if (((Collection)this.parts).isEmpty()) {
            int var1 = 0;
            String var2 = "Multipart body must have at least one part.";
            throw new IllegalStateException(var2.toString());
         } else {
            return new MultipartBody(this.boundary, this.type, _UtilJvmKt.toImmutableList(this.parts));
         }
      }

      @JvmOverloads
      public Builder() {
         this((String)null, 1, (DefaultConstructorMarker)null);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\u000e\u001a\u00020\u000f*\u00060\u0010j\u0002`\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0000¢\u0006\u0002\b\u0014R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"},
      d2 = {"Lokhttp3/MultipartBody$Companion;", "", "<init>", "()V", "MIXED", "Lokhttp3/MediaType;", "ALTERNATIVE", "DIGEST", "PARALLEL", "FORM", "COLONSPACE", "", "CRLF", "DASHDASH", "appendQuotedString", "", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "key", "", "appendQuotedString$okhttp", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final void appendQuotedString$okhttp(@NotNull StringBuilder $this$appendQuotedString, @NotNull String key) {
         Intrinsics.checkNotNullParameter($this$appendQuotedString, "<this>");
         Intrinsics.checkNotNullParameter(key, "key");
         $this$appendQuotedString.append('"');
         int i = 0;

         for(int var4 = key.length(); i < var4; ++i) {
            char ch = key.charAt(i);
            switch (ch) {
               case '\n':
                  $this$appendQuotedString.append("%0A");
                  break;
               case '\r':
                  $this$appendQuotedString.append("%0D");
                  break;
               case '"':
                  $this$appendQuotedString.append("%22");
                  break;
               default:
                  $this$appendQuotedString.append(ch);
            }
         }

         $this$appendQuotedString.append('"');
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
