package okhttp3.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.CharsKt;
import kotlin.text.StringsKt;
import okhttp3.Headers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000X\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010(\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\f\n\u0002\b\u0004\n\u0002\u0010$\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u0014\u0010\u0005\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007*\u00020\u00022\u0006\u0010\b\u001a\u00020\u0001H\u0000\u001a\u001e\u0010\t\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00010\u000b0\n*\u00020\u0002H\u0000\u001a\f\u0010\f\u001a\u00020\r*\u00020\u0002H\u0000\u001a\u0016\u0010\u000e\u001a\u00020\u000f*\u00020\u00022\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0000\u001a\f\u0010\u0012\u001a\u00020\u0004*\u00020\u0002H\u0000\u001a\f\u0010\u0013\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a%\u0010\u0014\u001a\u0004\u0018\u00010\u00012\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u00162\u0006\u0010\b\u001a\u00020\u0001H\u0000¢\u0006\u0002\u0010\u0017\u001a\u001c\u0010\u0018\u001a\u00020\r*\u00020\r2\u0006\u0010\b\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u0001H\u0000\u001a\u0014\u0010\u001a\u001a\u00020\r*\u00020\r2\u0006\u0010\u001b\u001a\u00020\u0002H\u0000\u001a\u001c\u0010\u001c\u001a\u00020\r*\u00020\r2\u0006\u0010\b\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u0001H\u0000\u001a\u0014\u0010\u001d\u001a\u00020\r*\u00020\r2\u0006\u0010\b\u001a\u00020\u0001H\u0000\u001a\u001c\u0010\u001e\u001a\u00020\r*\u00020\r2\u0006\u0010\b\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u0001H\u0000\u001a\u0016\u0010\u001f\u001a\u0004\u0018\u00010\u0001*\u00020\r2\u0006\u0010\b\u001a\u00020\u0001H\u0000\u001a\f\u0010 \u001a\u00020\u0002*\u00020\rH\u0000\u001a\u0010\u0010!\u001a\u00020\"2\u0006\u0010\b\u001a\u00020\u0001H\u0000\u001a\u0018\u0010#\u001a\u00020\"2\u0006\u0010\u0019\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\u0001H\u0000\u001a\f\u0010$\u001a\u00020\u0001*\u00020%H\u0002\u001a!\u0010&\u001a\u00020\u00022\u0012\u0010'\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00010\u0016\"\u00020\u0001H\u0000¢\u0006\u0002\u0010(\u001a\u0018\u0010)\u001a\u00020\u0002*\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00010*H\u0000¨\u0006+"},
   d2 = {"commonName", "", "Lokhttp3/Headers;", "index", "", "commonValue", "commonValues", "", "name", "commonIterator", "", "Lkotlin/Pair;", "commonNewBuilder", "Lokhttp3/Headers$Builder;", "commonEquals", "", "other", "", "commonHashCode", "commonToString", "commonHeadersGet", "namesAndValues", "", "([Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", "commonAdd", "value", "commonAddAll", "headers", "commonAddLenient", "commonRemoveAll", "commonSet", "commonGet", "commonBuild", "headersCheckName", "", "headersCheckValue", "charCode", "", "commonHeadersOf", "inputNamesAndValues", "([Ljava/lang/String;)Lokhttp3/Headers;", "commonToHeaders", "", "okhttp"}
)
@SourceDebugExtension({"SMAP\n-HeadersCommon.kt\nKotlin\n*S Kotlin\n*F\n+ 1 -HeadersCommon.kt\nokhttp3/internal/_HeadersCommonKt\n+ 2 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,207:1\n242#2:208\n37#3,2:209\n1#4:211\n*S KotlinDebug\n*F\n+ 1 -HeadersCommon.kt\nokhttp3/internal/_HeadersCommonKt\n*L\n36#1:208\n136#1:209,2\n*E\n"})
public final class _HeadersCommonKt {
   @NotNull
   public static final String commonName(@NotNull Headers $this$commonName, int index) {
      Intrinsics.checkNotNullParameter($this$commonName, "<this>");
      return (String)ArraysKt.getOrNull($this$commonName.getNamesAndValues$okhttp(), index * 2);
   }

   @NotNull
   public static final String commonValue(@NotNull Headers $this$commonValue, int index) {
      Intrinsics.checkNotNullParameter($this$commonValue, "<this>");
      return (String)ArraysKt.getOrNull($this$commonValue.getNamesAndValues$okhttp(), index * 2 + 1);
   }

   @NotNull
   public static final List<String> commonValues(@NotNull Headers $this$commonValues, @NotNull String name) {
      Intrinsics.checkNotNullParameter($this$commonValues, "<this>");
      Intrinsics.checkNotNullParameter(name, "name");
      List result = null;
      int i = 0;

      for(int var4 = $this$commonValues.size(); i < var4; ++i) {
         if (StringsKt.equals(name, $this$commonValues.name(i), true)) {
            if (result == null) {
               result = (List)(new ArrayList(2));
            }

            result.add($this$commonValues.value(i));
         }
      }

      List var10000;
      if (result != null) {
         int $i$f$unmodifiable = 0;
         var10000 = Collections.unmodifiableList(result);
         Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableList(...)");
      } else {
         var10000 = null;
      }

      if (var10000 == null) {
         var10000 = CollectionsKt.emptyList();
      }

      return var10000;
   }

   @NotNull
   public static final Iterator<Pair<String, String>> commonIterator(@NotNull Headers $this$commonIterator) {
      Intrinsics.checkNotNullParameter($this$commonIterator, "<this>");
      int var1 = 0;
      int var2 = $this$commonIterator.size();

      Pair[] var3;
      for(var3 = new Pair[var2]; var1 < var2; ++var1) {
         var3[var1] = TuplesKt.to($this$commonIterator.name(var1), $this$commonIterator.value(var1));
      }

      return ArrayIteratorKt.<Pair<String, String>>iterator(var3);
   }

   @NotNull
   public static final Headers.Builder commonNewBuilder(@NotNull Headers $this$commonNewBuilder) {
      Intrinsics.checkNotNullParameter($this$commonNewBuilder, "<this>");
      Headers.Builder result = new Headers.Builder();
      CollectionsKt.addAll((Collection)result.getNamesAndValues$okhttp(), $this$commonNewBuilder.getNamesAndValues$okhttp());
      return result;
   }

   public static final boolean commonEquals(@NotNull Headers $this$commonEquals, @Nullable Object other) {
      Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
      return other instanceof Headers && Arrays.equals($this$commonEquals.getNamesAndValues$okhttp(), ((Headers)other).getNamesAndValues$okhttp());
   }

   public static final int commonHashCode(@NotNull Headers $this$commonHashCode) {
      Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
      return Arrays.hashCode($this$commonHashCode.getNamesAndValues$okhttp());
   }

   @NotNull
   public static final String commonToString(@NotNull Headers $this$commonToString) {
      Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
      StringBuilder var1 = new StringBuilder();
      StringBuilder $this$commonToString_u24lambda_u240 = var1;
      int var3 = 0;
      int i = 0;

      for(int var5 = $this$commonToString.size(); i < var5; ++i) {
         String name = $this$commonToString.name(i);
         String value = $this$commonToString.value(i);
         $this$commonToString_u24lambda_u240.append(name);
         $this$commonToString_u24lambda_u240.append(": ");
         $this$commonToString_u24lambda_u240.append(_UtilCommonKt.isSensitiveHeader(name) ? "██" : value);
         $this$commonToString_u24lambda_u240.append("\n");
      }

      return var1.toString();
   }

   @Nullable
   public static final String commonHeadersGet(@NotNull String[] namesAndValues, @NotNull String name) {
      Intrinsics.checkNotNullParameter(namesAndValues, "namesAndValues");
      Intrinsics.checkNotNullParameter(name, "name");
      int var2 = namesAndValues.length - 2;
      int i = var2;
      int var4 = ProgressionUtilKt.getProgressionLastElement(var2, 0, -2);
      if (var4 <= var2) {
         while(true) {
            if (StringsKt.equals(name, namesAndValues[i], true)) {
               return namesAndValues[i + 1];
            }

            if (i == var4) {
               break;
            }

            i -= 2;
         }
      }

      return null;
   }

   @NotNull
   public static final Headers.Builder commonAdd(@NotNull Headers.Builder $this$commonAdd, @NotNull String name, @NotNull String value) {
      Intrinsics.checkNotNullParameter($this$commonAdd, "<this>");
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(value, "value");
      int var5 = 0;
      headersCheckName(name);
      headersCheckValue(value, name);
      commonAddLenient($this$commonAdd, name, value);
      return $this$commonAdd;
   }

   @NotNull
   public static final Headers.Builder commonAddAll(@NotNull Headers.Builder $this$commonAddAll, @NotNull Headers headers) {
      Intrinsics.checkNotNullParameter($this$commonAddAll, "<this>");
      Intrinsics.checkNotNullParameter(headers, "headers");
      Headers.Builder $this$commonAddAll_u24lambda_u240 = $this$commonAddAll;
      int var4 = 0;
      int i = 0;

      for(int var6 = headers.size(); i < var6; ++i) {
         commonAddLenient($this$commonAddAll_u24lambda_u240, headers.name(i), headers.value(i));
      }

      return $this$commonAddAll;
   }

   @NotNull
   public static final Headers.Builder commonAddLenient(@NotNull Headers.Builder $this$commonAddLenient, @NotNull String name, @NotNull String value) {
      Intrinsics.checkNotNullParameter($this$commonAddLenient, "<this>");
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(value, "value");
      int var5 = 0;
      $this$commonAddLenient.getNamesAndValues$okhttp().add(name);
      $this$commonAddLenient.getNamesAndValues$okhttp().add(StringsKt.trim((CharSequence)value).toString());
      return $this$commonAddLenient;
   }

   @NotNull
   public static final Headers.Builder commonRemoveAll(@NotNull Headers.Builder $this$commonRemoveAll, @NotNull String name) {
      Intrinsics.checkNotNullParameter($this$commonRemoveAll, "<this>");
      Intrinsics.checkNotNullParameter(name, "name");
      Headers.Builder $this$commonRemoveAll_u24lambda_u240 = $this$commonRemoveAll;
      int var4 = 0;

      for(int i = 0; i < $this$commonRemoveAll_u24lambda_u240.getNamesAndValues$okhttp().size(); i += 2) {
         if (StringsKt.equals(name, (String)$this$commonRemoveAll_u24lambda_u240.getNamesAndValues$okhttp().get(i), true)) {
            $this$commonRemoveAll_u24lambda_u240.getNamesAndValues$okhttp().remove(i);
            $this$commonRemoveAll_u24lambda_u240.getNamesAndValues$okhttp().remove(i);
            i -= 2;
         }
      }

      return $this$commonRemoveAll;
   }

   @NotNull
   public static final Headers.Builder commonSet(@NotNull Headers.Builder $this$commonSet, @NotNull String name, @NotNull String value) {
      Intrinsics.checkNotNullParameter($this$commonSet, "<this>");
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(value, "value");
      int var5 = 0;
      headersCheckName(name);
      headersCheckValue(value, name);
      $this$commonSet.removeAll(name);
      commonAddLenient($this$commonSet, name, value);
      return $this$commonSet;
   }

   @Nullable
   public static final String commonGet(@NotNull Headers.Builder $this$commonGet, @NotNull String name) {
      Intrinsics.checkNotNullParameter($this$commonGet, "<this>");
      Intrinsics.checkNotNullParameter(name, "name");
      int var2 = $this$commonGet.getNamesAndValues$okhttp().size() - 2;
      int i = var2;
      int var4 = ProgressionUtilKt.getProgressionLastElement(var2, 0, -2);
      if (var4 <= var2) {
         while(true) {
            if (StringsKt.equals(name, (String)$this$commonGet.getNamesAndValues$okhttp().get(i), true)) {
               return (String)$this$commonGet.getNamesAndValues$okhttp().get(i + 1);
            }

            if (i == var4) {
               break;
            }

            i -= 2;
         }
      }

      return null;
   }

   @NotNull
   public static final Headers commonBuild(@NotNull Headers.Builder $this$commonBuild) {
      Intrinsics.checkNotNullParameter($this$commonBuild, "<this>");
      Collection $this$toTypedArray$iv = (Collection)$this$commonBuild.getNamesAndValues$okhttp();
      int $i$f$toTypedArray = 0;
      return new Headers((String[])$this$toTypedArray$iv.toArray(new String[0]));
   }

   public static final void headersCheckName(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      if (((CharSequence)name).length() <= 0) {
         int var5 = 0;
         String var6 = "name is empty";
         throw new IllegalArgumentException(var6.toString());
      } else {
         int i = 0;

         for(int var2 = ((CharSequence)name).length(); i < var2; ++i) {
            char c = name.charAt(i);
            if (!('!' <= c ? c < 127 : false)) {
               int var4 = 0;
               String var7 = "Unexpected char 0x" + charCode(c) + " at " + i + " in header name: " + name;
               throw new IllegalArgumentException(var7.toString());
            }
         }

      }
   }

   public static final void headersCheckValue(@NotNull String value, @NotNull String name) {
      Intrinsics.checkNotNullParameter(value, "value");
      Intrinsics.checkNotNullParameter(name, "name");
      int i = 0;

      for(int var3 = ((CharSequence)value).length(); i < var3; ++i) {
         char c = value.charAt(i);
         if (c != '\t' && !(' ' <= c ? c < 127 : false)) {
            int var5 = 0;
            String var6 = "Unexpected char 0x" + charCode(c) + " at " + i + " in " + name + " value" + (_UtilCommonKt.isSensitiveHeader(name) ? "" : ": " + value);
            throw new IllegalArgumentException(var6.toString());
         }
      }

   }

   private static final String charCode(char $this$charCode) {
      String var10000 = Integer.toString($this$charCode, CharsKt.checkRadix(16));
      Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
      String it = var10000;
      int var2 = 0;
      return it.length() < 2 ? '0' + it : it;
   }

   @NotNull
   public static final Headers commonHeadersOf(@NotNull String... inputNamesAndValues) {
      Intrinsics.checkNotNullParameter(inputNamesAndValues, "inputNamesAndValues");
      if (inputNamesAndValues.length % 2 != 0) {
         int var8 = 0;
         String var9 = "Expected alternating header names and values";
         throw new IllegalArgumentException(var9.toString());
      } else {
         String[] namesAndValues = (String[])Arrays.copyOf(inputNamesAndValues, inputNamesAndValues.length);
         int i = 0;

         for(int var3 = namesAndValues.length; i < var3; ++i) {
            if (namesAndValues[i] == null) {
               int var5 = 0;
               String name = "Headers cannot be null";
               throw new IllegalArgumentException(name.toString());
            }

            namesAndValues[i] = StringsKt.trim((CharSequence)inputNamesAndValues[i]).toString();
         }

         i = namesAndValues.length + -1;
         int i = 0;
         int var4 = ProgressionUtilKt.getProgressionLastElement(0, i, 2);
         if (i <= var4) {
            while(true) {
               String name = namesAndValues[i];
               String value = namesAndValues[i + 1];
               headersCheckName(name);
               headersCheckValue(value, name);
               if (i == var4) {
                  break;
               }

               i += 2;
            }
         }

         return new Headers(namesAndValues);
      }
   }

   @NotNull
   public static final Headers commonToHeaders(@NotNull Map<String, String> $this$commonToHeaders) {
      Intrinsics.checkNotNullParameter($this$commonToHeaders, "<this>");
      String[] namesAndValues = new String[$this$commonToHeaders.size() * 2];
      int i = 0;

      for(Map.Entry var4 : $this$commonToHeaders.entrySet()) {
         String k = (String)var4.getKey();
         String v = (String)var4.getValue();
         String name = StringsKt.trim((CharSequence)k).toString();
         String value = StringsKt.trim((CharSequence)v).toString();
         headersCheckName(name);
         headersCheckValue(value, name);
         namesAndValues[i] = name;
         namesAndValues[i + 1] = value;
         i += 2;
      }

      return new Headers(namesAndValues);
   }
}
