package okio;

import java.util.List;
import java.util.RandomAccess;
import kotlin.Metadata;
import kotlin.collections.AbstractList;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0007\u0018\u0000 \u0017*\b\b\u0000\u0010\u0001*\u00020\u00022\b\u0012\u0004\u0012\u0002H\u00010\u00032\u00060\u0004j\u0002`\u0005:\u0001\u0017B\u001d\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00028\u00000\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u0016\u0010\u0014\u001a\u00028\u00002\u0006\u0010\u0015\u001a\u00020\u0011H\u0096\u0002¢\u0006\u0002\u0010\u0016R\u0014\u0010\b\u001a\u00020\tX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00028\u00000\u0007X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\u00020\u00118VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u0018"},
   d2 = {"Lokio/TypedOptions;", "T", "", "Lkotlin/collections/AbstractList;", "Ljava/util/RandomAccess;", "Lkotlin/collections/RandomAccess;", "list", "", "options", "Lokio/Options;", "<init>", "(Ljava/util/List;Lokio/Options;)V", "getOptions$okio", "()Lokio/Options;", "getList$okio", "()Ljava/util/List;", "size", "", "getSize", "()I", "get", "index", "(I)Ljava/lang/Object;", "Companion", "okio"}
)
public final class TypedOptions<T> extends AbstractList<T> implements RandomAccess {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Options options;
   @NotNull
   private final List<T> list;

   public TypedOptions(@NotNull List<? extends T> list, @NotNull Options options) {
      Intrinsics.checkNotNullParameter(list, "list");
      Intrinsics.checkNotNullParameter(options, "options");
      super();
      this.options = options;
      this.list = CollectionsKt.toList((Iterable)list);
      if (this.list.size() != this.options.size()) {
         String var3 = "Failed requirement.";
         throw new IllegalArgumentException(var3.toString());
      }
   }

   @NotNull
   public final Options getOptions$okio() {
      return this.options;
   }

   @NotNull
   public final List<T> getList$okio() {
      return this.list;
   }

   public int getSize() {
      return this.list.size();
   }

   @NotNull
   public T get(int index) {
      return (T)this.list.get(index);
   }

   @JvmStatic
   @NotNull
   public static final <T> TypedOptions<T> of(@NotNull Iterable<? extends T> values, @NotNull Function1<? super T, ? extends ByteString> encode) {
      return Companion.<T>of(values, encode);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u001c\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J>\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00060\u0005\"\b\b\u0001\u0010\u0006*\u00020\u00012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u0002H\u00060\b2\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u0002H\u0006\u0012\u0004\u0012\u00020\u000b0\nH\u0087\bø\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\f"},
      d2 = {"Lokio/TypedOptions$Companion;", "", "<init>", "()V", "of", "Lokio/TypedOptions;", "T", "values", "", "encode", "Lkotlin/Function1;", "Lokio/ByteString;", "okio"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final <T> TypedOptions<T> of(@NotNull Iterable<? extends T> values, @NotNull Function1<? super T, ? extends ByteString> encode) {
         Intrinsics.checkNotNullParameter(values, "values");
         Intrinsics.checkNotNullParameter(encode, "encode");
         int $i$f$of = 0;
         List list = CollectionsKt.toList(values);
         Options.Companion var10000 = Options.Companion;
         int var6 = 0;
         int var7 = list.size();
         ByteString[] var8 = new ByteString[var7];

         Options.Companion var10;
         for(var10 = var10000; var6 < var7; ++var6) {
            var8[var6] = (ByteString)encode.invoke(list.get(var6));
         }

         Options options = var10.of(var8);
         return new TypedOptions<T>(list, options);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
