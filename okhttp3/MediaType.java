package okhttp3;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.MatchGroup;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB/\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007¢\u0006\u0004\b\b\u0010\tJ\u0016\u0010\r\u001a\u0004\u0018\u00010\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000eH\u0007J\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0011\u001a\u00020\u0003J\r\u0010\u0004\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0012J\r\u0010\u0005\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0013J\b\u0010\u0014\u001a\u00020\u0003H\u0016J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0004\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000bR\u0013\u0010\u0005\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u000bR\u0016\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\f¨\u0006\u001b"},
   d2 = {"Lokhttp3/MediaType;", "", "mediaType", "", "type", "subtype", "parameterNamesAndValues", "", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V", "getMediaType$okhttp", "()Ljava/lang/String;", "[Ljava/lang/String;", "charset", "Ljava/nio/charset/Charset;", "defaultValue", "parameter", "name", "-deprecated_type", "-deprecated_subtype", "toString", "equals", "", "other", "hashCode", "", "Companion", "okhttp"}
)
public final class MediaType {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final String mediaType;
   @NotNull
   private final String type;
   @NotNull
   private final String subtype;
   @NotNull
   private final String[] parameterNamesAndValues;
   @NotNull
   private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
   @NotNull
   private static final String QUOTED = "\"([^\"]*)\"";
   @NotNull
   private static final Regex TYPE_SUBTYPE = new Regex("([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)/([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)");
   @NotNull
   private static final Regex PARAMETER = new Regex(";\\s*(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)=(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)|\"([^\"]*)\"))?");

   public MediaType(@NotNull String mediaType, @NotNull String type, @NotNull String subtype, @NotNull String[] parameterNamesAndValues) {
      Intrinsics.checkNotNullParameter(mediaType, "mediaType");
      Intrinsics.checkNotNullParameter(type, "type");
      Intrinsics.checkNotNullParameter(subtype, "subtype");
      Intrinsics.checkNotNullParameter(parameterNamesAndValues, "parameterNamesAndValues");
      super();
      this.mediaType = mediaType;
      this.type = type;
      this.subtype = subtype;
      this.parameterNamesAndValues = parameterNamesAndValues;
   }

   @NotNull
   public final String getMediaType$okhttp() {
      return this.mediaType;
   }

   @JvmName(
      name = "type"
   )
   @NotNull
   public final String type() {
      return this.type;
   }

   @JvmName(
      name = "subtype"
   )
   @NotNull
   public final String subtype() {
      return this.subtype;
   }

   @JvmOverloads
   @Nullable
   public final Charset charset(@Nullable Charset defaultValue) {
      String var10000 = this.parameter("charset");
      if (var10000 == null) {
         return defaultValue;
      } else {
         String charset = var10000;

         Charset var3;
         try {
            var3 = Charset.forName(charset);
         } catch (IllegalArgumentException var5) {
            var3 = defaultValue;
         }

         return var3;
      }
   }

   // $FF: synthetic method
   public static Charset charset$default(MediaType var0, Charset var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = null;
      }

      return var0.charset(var1);
   }

   @Nullable
   public final String parameter(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      int var2 = this.parameterNamesAndValues.length + -1;
      int i = 0;
      int var4 = ProgressionUtilKt.getProgressionLastElement(0, var2, 2);
      if (i <= var4) {
         while(true) {
            if (StringsKt.equals(this.parameterNamesAndValues[i], name, true)) {
               return this.parameterNamesAndValues[i + 1];
            }

            if (i == var4) {
               break;
            }

            i += 2;
         }
      }

      return null;
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
   public final String _deprecated_type/* $FF was: -deprecated_type*/() {
      return this.type;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "subtype",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_subtype"
   )
   @NotNull
   public final String _deprecated_subtype/* $FF was: -deprecated_subtype*/() {
      return this.subtype;
   }

   @NotNull
   public String toString() {
      return this.mediaType;
   }

   public boolean equals(@Nullable Object other) {
      return other instanceof MediaType && Intrinsics.areEqual((Object)((MediaType)other).mediaType, (Object)this.mediaType);
   }

   public int hashCode() {
      return this.mediaType.hashCode();
   }

   @JvmOverloads
   @Nullable
   public final Charset charset() {
      return charset$default(this, (Charset)null, 1, (Object)null);
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @NotNull
   public static final MediaType get(@NotNull String $this$get) {
      return Companion.get($this$get);
   }

   @JvmStatic
   @JvmName(
      name = "parse"
   )
   @Nullable
   public static final MediaType parse(@NotNull String $this$parse) {
      return Companion.parse($this$parse);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0011\u0010\n\u001a\u00020\u000b*\u00020\u0005H\u0007¢\u0006\u0002\b\fJ\u0013\u0010\r\u001a\u0004\u0018\u00010\u000b*\u00020\u0005H\u0007¢\u0006\u0002\b\u000eJ\u0015\u0010\f\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u0005H\u0007¢\u0006\u0002\b\u0010J\u0017\u0010\u000e\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u000f\u001a\u00020\u0005H\u0007¢\u0006\u0002\b\u0011R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"},
      d2 = {"Lokhttp3/MediaType$Companion;", "", "<init>", "()V", "TOKEN", "", "QUOTED", "TYPE_SUBTYPE", "Lkotlin/text/Regex;", "PARAMETER", "toMediaType", "Lokhttp3/MediaType;", "get", "toMediaTypeOrNull", "parse", "mediaType", "-deprecated_get", "-deprecated_parse", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nMediaType.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaType.kt\nokhttp3/MediaType$Companion\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,183:1\n37#2,2:184\n*S KotlinDebug\n*F\n+ 1 MediaType.kt\nokhttp3/MediaType$Companion\n*L\n145#1:184,2\n*E\n"})
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @NotNull
      public final MediaType get(@NotNull String $this$toMediaType) {
         Intrinsics.checkNotNullParameter($this$toMediaType, "<this>");
         MatchResult var10000 = MediaType.TYPE_SUBTYPE.matchAt((CharSequence)$this$toMediaType, 0);
         if (var10000 == null) {
            throw new IllegalArgumentException("No subtype found for: \"" + $this$toMediaType + '"');
         } else {
            MatchResult typeSubtype = var10000;
            String var16 = ((String)typeSubtype.getGroupValues().get(1)).toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(var16, "toLowerCase(...)");
            String type = var16;
            var16 = ((String)typeSubtype.getGroupValues().get(2)).toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(var16, "toLowerCase(...)");
            String subtype = var16;
            List parameterNamesAndValues = (List)(new ArrayList());
            int s = 0;
            s = typeSubtype.getRange().getLast() + 1;

            while(s < $this$toMediaType.length()) {
               MatchResult parameter = MediaType.PARAMETER.matchAt((CharSequence)$this$toMediaType, s);
               if (parameter == null) {
                  int token = 0;
                  StringBuilder var22 = (new StringBuilder()).append("Parameter is not formatted correctly: \"");
                  String var10001 = $this$toMediaType.substring(s);
                  Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
                  String thisCollection$iv = var22.append(var10001).append("\" for: \"").append($this$toMediaType).append('"').toString();
                  throw new IllegalArgumentException(thisCollection$iv.toString());
               }

               MatchGroup var18 = parameter.getGroups().get(1);
               String name = var18 != null ? var18.getValue() : null;
               if (name == null) {
                  s = parameter.getRange().getLast() + 1;
               } else {
                  var18 = parameter.getGroups().get(2);
                  String token = var18 != null ? var18.getValue() : null;
                  String var21;
                  if (token == null) {
                     MatchGroup var20 = parameter.getGroups().get(3);
                     Intrinsics.checkNotNull(var20);
                     var21 = var20.getValue();
                  } else if (StringsKt.startsWith$default((CharSequence)token, '\'', false, 2, (Object)null) && StringsKt.endsWith$default((CharSequence)token, '\'', false, 2, (Object)null) && token.length() > 2) {
                     var21 = token.substring(1, token.length() - 1);
                     Intrinsics.checkNotNullExpressionValue(var21, "substring(...)");
                  } else {
                     var21 = token;
                  }

                  String value = var21;
                  ((Collection)parameterNamesAndValues).add(name);
                  ((Collection)parameterNamesAndValues).add(value);
                  s = parameter.getRange().getLast() + 1;
               }
            }

            Collection $this$toTypedArray$iv = (Collection)parameterNamesAndValues;
            int $i$f$toTypedArray = 0;
            return new MediaType($this$toMediaType, type, subtype, (String[])$this$toTypedArray$iv.toArray(new String[0]));
         }
      }

      @JvmStatic
      @JvmName(
         name = "parse"
      )
      @Nullable
      public final MediaType parse(@NotNull String $this$toMediaTypeOrNull) {
         Intrinsics.checkNotNullParameter($this$toMediaTypeOrNull, "<this>");

         MediaType var2;
         try {
            var2 = this.get($this$toMediaTypeOrNull);
         } catch (IllegalArgumentException var4) {
            var2 = null;
         }

         return var2;
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "mediaType.toMediaType()",
   imports = {"okhttp3.MediaType.Companion.toMediaType"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_get"
      )
      @NotNull
      public final MediaType _deprecated_get/* $FF was: -deprecated_get*/(@NotNull String mediaType) {
         Intrinsics.checkNotNullParameter(mediaType, "mediaType");
         return this.get(mediaType);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "mediaType.toMediaTypeOrNull()",
   imports = {"okhttp3.MediaType.Companion.toMediaTypeOrNull"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_parse"
      )
      @Nullable
      public final MediaType _deprecated_parse/* $FF was: -deprecated_parse*/(@NotNull String mediaType) {
         Intrinsics.checkNotNullParameter(mediaType, "mediaType");
         return this.parse(mediaType);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
