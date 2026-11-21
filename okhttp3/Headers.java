package okhttp3;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.jvm.internal.markers.KMappedMarker;
import kotlin.text.StringsKt;
import okhttp3.internal._HeadersCommonKt;
import okhttp3.internal.http.DateFormattingKt;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\"\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010(\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0003\u0018\u0000 *2\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00020\u0001:\u0002)*B\u0017\b\u0000\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u00032\u0006\u0010\f\u001a\u00020\u0003H\u0086\u0002J\u0010\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\f\u001a\u00020\u0003J\u0012\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\f\u001a\u00020\u0003H\u0007J\r\u0010\u0011\u001a\u00020\u0012H\u0007¢\u0006\u0002\b\u0014J\u000e\u0010\f\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0012J\u000e\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0012J\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00030\u0018J\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00030\u001a2\u0006\u0010\f\u001a\u00020\u0003J\u0006\u0010\u001b\u001a\u00020\u001cJ\u001b\u0010\u001d\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00020\u001eH\u0096\u0002J\u0006\u0010\u001f\u001a\u00020 J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010$H\u0096\u0002J\b\u0010%\u001a\u00020\u0012H\u0016J\b\u0010&\u001a\u00020\u0003H\u0016J\u0018\u0010'\u001a\u0014\u0012\u0004\u0012\u00020\u0003\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u001a0(R\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005X\u0080\u0004¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0011\u001a\u00020\u00128G¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0013¨\u0006+"},
   d2 = {"Lokhttp3/Headers;", "", "Lkotlin/Pair;", "", "namesAndValues", "", "<init>", "([Ljava/lang/String;)V", "getNamesAndValues$okhttp", "()[Ljava/lang/String;", "[Ljava/lang/String;", "get", "name", "getDate", "Ljava/util/Date;", "getInstant", "Ljava/time/Instant;", "size", "", "()I", "-deprecated_size", "index", "value", "names", "", "values", "", "byteCount", "", "iterator", "", "newBuilder", "Lokhttp3/Headers$Builder;", "equals", "", "other", "", "hashCode", "toString", "toMultimap", "", "Builder", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nHeaders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Headers.kt\nokhttp3/Headers\n+ 2 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n*L\n1#1,359:1\n246#2:360\n*S KotlinDebug\n*F\n+ 1 Headers.kt\nokhttp3/Headers\n*L\n110#1:360\n*E\n"})
public final class Headers implements Iterable<Pair<? extends String, ? extends String>>, KMappedMarker {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final String[] namesAndValues;
   @JvmField
   @NotNull
   public static final Headers EMPTY = new Headers(new String[0]);

   public Headers(@NotNull String[] namesAndValues) {
      Intrinsics.checkNotNullParameter(namesAndValues, "namesAndValues");
      super();
      this.namesAndValues = namesAndValues;
   }

   @NotNull
   public final String[] getNamesAndValues$okhttp() {
      return this.namesAndValues;
   }

   @Nullable
   public final String get(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      return _HeadersCommonKt.commonHeadersGet(this.namesAndValues, name);
   }

   @Nullable
   public final Date getDate(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      String var10000 = this.get(name);
      return var10000 != null ? DateFormattingKt.toHttpDateOrNull(var10000) : null;
   }

   @IgnoreJRERequirement
   @Nullable
   public final Instant getInstant(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      Date var10000 = this.getDate(name);
      return var10000 != null ? var10000.toInstant() : null;
   }

   @JvmName(
      name = "size"
   )
   public final int size() {
      return this.namesAndValues.length / 2;
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
   public final String name(int index) {
      return _HeadersCommonKt.commonName(this, index);
   }

   @NotNull
   public final String value(int index) {
      return _HeadersCommonKt.commonValue(this, index);
   }

   @NotNull
   public final Set<String> names() {
      TreeSet result = new TreeSet(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
      int i = 0;

      for(int var3 = this.size(); i < var3; ++i) {
         result.add(this.name(i));
      }

      Set $this$unmodifiable$iv = (Set)result;
      int $i$f$unmodifiable = 0;
      Set var10000 = Collections.unmodifiableSet($this$unmodifiable$iv);
      Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableSet(...)");
      return var10000;
   }

   @NotNull
   public final List<String> values(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      return _HeadersCommonKt.commonValues(this, name);
   }

   public final long byteCount() {
      long result = (long)(this.namesAndValues.length * 2);
      int i = 0;

      for(int var4 = this.namesAndValues.length; i < var4; ++i) {
         result += (long)this.namesAndValues[i].length();
      }

      return result;
   }

   @NotNull
   public Iterator<Pair<String, String>> iterator() {
      return _HeadersCommonKt.commonIterator(this);
   }

   @NotNull
   public final Builder newBuilder() {
      return _HeadersCommonKt.commonNewBuilder(this);
   }

   public boolean equals(@Nullable Object other) {
      return _HeadersCommonKt.commonEquals(this, other);
   }

   public int hashCode() {
      return _HeadersCommonKt.commonHashCode(this);
   }

   @NotNull
   public String toString() {
      return _HeadersCommonKt.commonToString(this);
   }

   @NotNull
   public final Map<String, List<String>> toMultimap() {
      TreeMap result = new TreeMap(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
      int i = 0;

      for(int var3 = this.size(); i < var3; ++i) {
         String var5 = this.name(i);
         Locale var10000 = Locale.US;
         Intrinsics.checkNotNullExpressionValue(var10000, "US");
         String var7 = var5.toLowerCase(var10000);
         Intrinsics.checkNotNullExpressionValue(var7, "toLowerCase(...)");
         String name = var7;
         List values = (List)result.get(name);
         if (values == null) {
            values = (List)(new ArrayList(2));
            ((Map)result).put(name, values);
         }

         values.add(this.value(i));
      }

      return (Map)result;
   }

   @JvmStatic
   @JvmName(
      name = "of"
   )
   @NotNull
   public static final Headers of(@NotNull String... namesAndValues) {
      return Companion.of(namesAndValues);
   }

   @JvmStatic
   @JvmName(
      name = "of"
   )
   @NotNull
   public static final Headers of(@NotNull Map<String, String> $this$of) {
      return Companion.of($this$of);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\t\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0006H\u0000¢\u0006\u0002\b\u000bJ\u000e\u0010\f\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0006J\u0016\u0010\f\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0006J\u0016\u0010\u000f\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0006J\u000e\u0010\u0010\u001a\u00020\u00002\u0006\u0010\u0011\u001a\u00020\u0012J\u0016\u0010\f\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0013J\u0018\u0010\f\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0014H\u0007J\u0019\u0010\u0015\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0013H\u0086\u0002J\u0019\u0010\u0015\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0014H\u0087\u0002J\u001d\u0010\t\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0006H\u0000¢\u0006\u0002\b\u000bJ\u000e\u0010\u0016\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u0006J\u0019\u0010\u0015\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0006H\u0086\u0002J\u0013\u0010\u0017\u001a\u0004\u0018\u00010\u00062\u0006\u0010\r\u001a\u00020\u0006H\u0086\u0002J\u0006\u0010\u0018\u001a\u00020\u0012R\u001a\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0019"},
      d2 = {"Lokhttp3/Headers$Builder;", "", "<init>", "()V", "namesAndValues", "", "", "getNamesAndValues$okhttp", "()Ljava/util/List;", "addLenient", "line", "addLenient$okhttp", "add", "name", "value", "addUnsafeNonAscii", "addAll", "headers", "Lokhttp3/Headers;", "Ljava/util/Date;", "Ljava/time/Instant;", "set", "removeAll", "get", "build", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHeaders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Headers.kt\nokhttp3/Headers$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,359:1\n1#2:360\n*E\n"})
   public static final class Builder {
      @NotNull
      private final List<String> namesAndValues = (List)(new ArrayList(20));

      @NotNull
      public final List<String> getNamesAndValues$okhttp() {
         return this.namesAndValues;
      }

      @NotNull
      public final Builder addLenient$okhttp(@NotNull String line) {
         Intrinsics.checkNotNullParameter(line, "line");
         Builder $this$addLenient_u24lambda_u240 = this;
         int var4 = 0;
         int index = StringsKt.indexOf$default((CharSequence)line, ':', 1, false, 4, (Object)null);
         if (index != -1) {
            String var10001 = line.substring(0, index);
            Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
            String var10002 = line.substring(index + 1);
            Intrinsics.checkNotNullExpressionValue(var10002, "substring(...)");
            $this$addLenient_u24lambda_u240.addLenient$okhttp(var10001, var10002);
         } else if (line.charAt(0) == ':') {
            String var6 = line.substring(1);
            Intrinsics.checkNotNullExpressionValue(var6, "substring(...)");
            $this$addLenient_u24lambda_u240.addLenient$okhttp("", var6);
         } else {
            $this$addLenient_u24lambda_u240.addLenient$okhttp("", line);
         }

         return this;
      }

      @NotNull
      public final Builder add(@NotNull String line) {
         Intrinsics.checkNotNullParameter(line, "line");
         Builder $this$add_u24lambda_u240 = this;
         int var4 = 0;
         int index = StringsKt.indexOf$default((CharSequence)line, ':', 0, false, 6, (Object)null);
         if (index == -1) {
            int var6 = 0;
            String var7 = "Unexpected header: " + line;
            throw new IllegalArgumentException(var7.toString());
         } else {
            String var10001 = line.substring(0, index);
            Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
            var10001 = StringsKt.trim((CharSequence)var10001).toString();
            String var10002 = line.substring(index + 1);
            Intrinsics.checkNotNullExpressionValue(var10002, "substring(...)");
            $this$add_u24lambda_u240.add(var10001, var10002);
            return this;
         }
      }

      @NotNull
      public final Builder add(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         return _HeadersCommonKt.commonAdd(this, name, value);
      }

      @NotNull
      public final Builder addUnsafeNonAscii(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Builder $this$addUnsafeNonAscii_u24lambda_u240 = this;
         int var5 = 0;
         _HeadersCommonKt.headersCheckName(name);
         $this$addUnsafeNonAscii_u24lambda_u240.addLenient$okhttp(name, value);
         return this;
      }

      @NotNull
      public final Builder addAll(@NotNull Headers headers) {
         Intrinsics.checkNotNullParameter(headers, "headers");
         return _HeadersCommonKt.commonAddAll(this, headers);
      }

      @NotNull
      public final Builder add(@NotNull String name, @NotNull Date value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         return this.add(name, DateFormattingKt.toHttpDateString(value));
      }

      @IgnoreJRERequirement
      @NotNull
      public final Builder add(@NotNull String name, @NotNull Instant value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Date var10002 = Date.from(value);
         Intrinsics.checkNotNullExpressionValue(var10002, "from(...)");
         return this.add(name, var10002);
      }

      @NotNull
      public final Builder set(@NotNull String name, @NotNull Date value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         return this.set(name, DateFormattingKt.toHttpDateString(value));
      }

      @IgnoreJRERequirement
      @NotNull
      public final Builder set(@NotNull String name, @NotNull Instant value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Date var10002 = Date.from(value);
         Intrinsics.checkNotNullExpressionValue(var10002, "from(...)");
         return this.set(name, var10002);
      }

      @NotNull
      public final Builder addLenient$okhttp(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         return _HeadersCommonKt.commonAddLenient(this, name, value);
      }

      @NotNull
      public final Builder removeAll(@NotNull String name) {
         Intrinsics.checkNotNullParameter(name, "name");
         return _HeadersCommonKt.commonRemoveAll(this, name);
      }

      @NotNull
      public final Builder set(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         return _HeadersCommonKt.commonSet(this, name, value);
      }

      @Nullable
      public final String get(@NotNull String name) {
         Intrinsics.checkNotNullParameter(name, "name");
         return _HeadersCommonKt.commonGet(this, name);
      }

      @NotNull
      public final Headers build() {
         return _HeadersCommonKt.commonBuild(this);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J#\u0010\u0006\u001a\u00020\u00052\u0012\u0010\u0007\u001a\n\u0012\u0006\b\u0001\u0012\u00020\t0\b\"\u00020\tH\u0007¢\u0006\u0004\b\n\u0010\u000bJ#\u0010\n\u001a\u00020\u00052\u0012\u0010\u0007\u001a\n\u0012\u0006\b\u0001\u0012\u00020\t0\b\"\u00020\tH\u0007¢\u0006\u0004\b\f\u0010\u000bJ\u001d\u0010\r\u001a\u00020\u0005*\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\u000eH\u0007¢\u0006\u0002\b\nJ!\u0010\n\u001a\u00020\u00052\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\u000eH\u0007¢\u0006\u0002\b\fR\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
      d2 = {"Lokhttp3/Headers$Companion;", "", "<init>", "()V", "EMPTY", "Lokhttp3/Headers;", "headersOf", "namesAndValues", "", "", "of", "([Ljava/lang/String;)Lokhttp3/Headers;", "-deprecated_of", "toHeaders", "", "headers", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @JvmName(
         name = "of"
      )
      @NotNull
      public final Headers of(@NotNull String... namesAndValues) {
         Intrinsics.checkNotNullParameter(namesAndValues, "namesAndValues");
         return _HeadersCommonKt.commonHeadersOf((String[])Arrays.copyOf(namesAndValues, namesAndValues.length));
      }

      /** @deprecated */
      @Deprecated(
         message = "function name changed",
         replaceWith = @ReplaceWith(
   expression = "headersOf(*namesAndValues)",
   imports = {}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_of"
      )
      @NotNull
      public final Headers _deprecated_of/* $FF was: -deprecated_of*/(@NotNull String... namesAndValues) {
         Intrinsics.checkNotNullParameter(namesAndValues, "namesAndValues");
         return this.of((String[])Arrays.copyOf(namesAndValues, namesAndValues.length));
      }

      @JvmStatic
      @JvmName(
         name = "of"
      )
      @NotNull
      public final Headers of(@NotNull Map<String, String> $this$toHeaders) {
         Intrinsics.checkNotNullParameter($this$toHeaders, "<this>");
         return _HeadersCommonKt.commonToHeaders($this$toHeaders);
      }

      /** @deprecated */
      @Deprecated(
         message = "function moved to extension",
         replaceWith = @ReplaceWith(
   expression = "headers.toHeaders()",
   imports = {}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_of"
      )
      @NotNull
      public final Headers _deprecated_of/* $FF was: -deprecated_of*/(@NotNull Map<String, String> headers) {
         Intrinsics.checkNotNullParameter(headers, "headers");
         return this.of(headers);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
