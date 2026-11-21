package okhttp3;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.url._UrlKt;
import okio.Buffer;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u0000 \u001d2\u00020\u0001:\u0002\u001c\u001dB%\b\u0000\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\r\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b\u000bJ\u000e\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\tJ\u000e\u0010\u000e\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\tJ\u000e\u0010\u000f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\tJ\u000e\u0010\u0010\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\tJ\b\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0014H\u0016J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\u001a\u0010\u0019\u001a\u00020\u00142\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u001a\u001a\u00020\u001bH\u0002R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\t8G¢\u0006\u0006\u001a\u0004\b\b\u0010\n¨\u0006\u001e"},
   d2 = {"Lokhttp3/FormBody;", "Lokhttp3/RequestBody;", "encodedNames", "", "", "encodedValues", "<init>", "(Ljava/util/List;Ljava/util/List;)V", "size", "", "()I", "-deprecated_size", "encodedName", "index", "name", "encodedValue", "value", "contentType", "Lokhttp3/MediaType;", "contentLength", "", "writeTo", "", "sink", "Lokio/BufferedSink;", "writeOrCountBytes", "countBytes", "", "Builder", "Companion", "okhttp"}
)
public final class FormBody extends RequestBody {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final List<String> encodedNames;
   @NotNull
   private final List<String> encodedValues;
   @NotNull
   private static final MediaType CONTENT_TYPE;

   public FormBody(@NotNull List<String> encodedNames, @NotNull List<String> encodedValues) {
      Intrinsics.checkNotNullParameter(encodedNames, "encodedNames");
      Intrinsics.checkNotNullParameter(encodedValues, "encodedValues");
      super();
      this.encodedNames = _UtilJvmKt.toImmutableList(encodedNames);
      this.encodedValues = _UtilJvmKt.toImmutableList(encodedValues);
   }

   @JvmName(
      name = "size"
   )
   public final int size() {
      return this.encodedNames.size();
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

   @NotNull
   public final String encodedName(int index) {
      return (String)this.encodedNames.get(index);
   }

   @NotNull
   public final String name(int index) {
      return _UrlKt.percentDecode$default(this.encodedName(index), 0, 0, true, 3, (Object)null);
   }

   @NotNull
   public final String encodedValue(int index) {
      return (String)this.encodedValues.get(index);
   }

   @NotNull
   public final String value(int index) {
      return _UrlKt.percentDecode$default(this.encodedValue(index), 0, 0, true, 3, (Object)null);
   }

   @NotNull
   public MediaType contentType() {
      return CONTENT_TYPE;
   }

   public long contentLength() {
      return this.writeOrCountBytes((BufferedSink)null, true);
   }

   public void writeTo(@NotNull BufferedSink sink) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      this.writeOrCountBytes(sink, false);
   }

   private final long writeOrCountBytes(BufferedSink sink, boolean countBytes) {
      long byteCount = 0L;
      Buffer var10000;
      if (countBytes) {
         var10000 = new Buffer();
      } else {
         Intrinsics.checkNotNull(sink);
         var10000 = sink.getBuffer();
      }

      Buffer buffer = var10000;
      int i = 0;

      for(int var7 = this.encodedNames.size(); i < var7; ++i) {
         if (i > 0) {
            buffer.writeByte(38);
         }

         buffer.writeUtf8((String)this.encodedNames.get(i));
         buffer.writeByte(61);
         buffer.writeUtf8((String)this.encodedValues.get(i));
      }

      if (countBytes) {
         byteCount = buffer.size();
         buffer.clear();
      }

      return byteCount;
   }

   static {
      CONTENT_TYPE = MediaType.Companion.get("application/x-www-form-urlencoded");
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0015\b\u0007\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\n\u001a\u00020\u00002\u0006\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\bJ\u0016\u0010\r\u001a\u00020\u00002\u0006\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\bJ\u0006\u0010\u000e\u001a\u00020\u000fR\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
      d2 = {"Lokhttp3/FormBody$Builder;", "", "charset", "Ljava/nio/charset/Charset;", "<init>", "(Ljava/nio/charset/Charset;)V", "names", "", "", "values", "add", "name", "value", "addEncoded", "build", "Lokhttp3/FormBody;", "okhttp"}
   )
   public static final class Builder {
      @Nullable
      private final Charset charset;
      @NotNull
      private final List<String> names;
      @NotNull
      private final List<String> values;

      @JvmOverloads
      public Builder(@Nullable Charset charset) {
         this.charset = charset;
         this.names = (List)(new ArrayList());
         this.values = (List)(new ArrayList());
      }

      // $FF: synthetic method
      public Builder(Charset var1, int var2, DefaultConstructorMarker var3) {
         if ((var2 & 1) != 0) {
            var1 = null;
         }

         this(var1);
      }

      @NotNull
      public final Builder add(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Builder $this$add_u24lambda_u240 = this;
         int var5 = 0;
         ((Collection)$this$add_u24lambda_u240.names).add(_UrlKt.canonicalizeWithCharset$default(name, 0, 0, " !\"#$&'()+,/:;<=>?@[\\]^`{|}~", false, false, false, false, $this$add_u24lambda_u240.charset, 91, (Object)null));
         ((Collection)$this$add_u24lambda_u240.values).add(_UrlKt.canonicalizeWithCharset$default(value, 0, 0, " !\"#$&'()+,/:;<=>?@[\\]^`{|}~", false, false, false, false, $this$add_u24lambda_u240.charset, 91, (Object)null));
         return this;
      }

      @NotNull
      public final Builder addEncoded(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Builder $this$addEncoded_u24lambda_u240 = this;
         int var5 = 0;
         ((Collection)$this$addEncoded_u24lambda_u240.names).add(_UrlKt.canonicalizeWithCharset$default(name, 0, 0, " !\"#$&'()+,/:;<=>?@[\\]^`{|}~", true, false, true, false, $this$addEncoded_u24lambda_u240.charset, 83, (Object)null));
         ((Collection)$this$addEncoded_u24lambda_u240.values).add(_UrlKt.canonicalizeWithCharset$default(value, 0, 0, " !\"#$&'()+,/:;<=>?@[\\]^`{|}~", true, false, true, false, $this$addEncoded_u24lambda_u240.charset, 83, (Object)null));
         return this;
      }

      @NotNull
      public final FormBody build() {
         return new FormBody(this.names, this.values);
      }

      @JvmOverloads
      public Builder() {
         this((Charset)null, 1, (DefaultConstructorMarker)null);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lokhttp3/FormBody$Companion;", "", "<init>", "()V", "CONTENT_TYPE", "Lokhttp3/MediaType;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
