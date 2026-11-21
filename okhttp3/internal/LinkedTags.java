package okhttp3.internal;

import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0003B%\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u0005\u0012\u0006\u0010\u0006\u001a\u00028\u0000\u0012\u0006\u0010\u0007\u001a\u00020\u0003¢\u0006\u0004\b\b\u0010\tJ/\u0010\u000b\u001a\u00020\u0003\"\b\b\u0001\u0010\f*\u00020\u00022\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\f0\u00052\b\u0010\u0006\u001a\u0004\u0018\u0001H\fH\u0016¢\u0006\u0002\u0010\rJ(\u0010\u000e\u001a\u0004\u0018\u0001H\f\"\b\b\u0001\u0010\f*\u00020\u00022\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\f0\u0005H\u0096\u0002¢\u0006\u0002\u0010\u000fJ\b\u0010\u0010\u001a\u00020\u0011H\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00028\u0000X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\nR\u000e\u0010\u0007\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"},
   d2 = {"Lokhttp3/internal/LinkedTags;", "K", "", "Lokhttp3/internal/Tags;", "key", "Lkotlin/reflect/KClass;", "value", "next", "<init>", "(Lkotlin/reflect/KClass;Ljava/lang/Object;Lokhttp3/internal/Tags;)V", "Ljava/lang/Object;", "plus", "T", "(Lkotlin/reflect/KClass;Ljava/lang/Object;)Lokhttp3/internal/Tags;", "get", "(Lkotlin/reflect/KClass;)Ljava/lang/Object;", "toString", "", "okhttp"}
)
final class LinkedTags<K> extends Tags {
   @NotNull
   private final KClass<K> key;
   @NotNull
   private final K value;
   @NotNull
   private final Tags next;

   public LinkedTags(@NotNull KClass<K> key, @NotNull K value, @NotNull Tags next) {
      Intrinsics.checkNotNullParameter(key, "key");
      Intrinsics.checkNotNullParameter(value, "value");
      Intrinsics.checkNotNullParameter(next, "next");
      super((DefaultConstructorMarker)null);
      this.key = key;
      this.value = (K)value;
      this.next = next;
   }

   @NotNull
   public <T> Tags plus(@NotNull KClass<T> key, @Nullable T value) {
      Intrinsics.checkNotNullParameter(key, "key");
      Tags var10000;
      if (Intrinsics.areEqual((Object)key, (Object)this.key)) {
         var10000 = this.next;
      } else {
         Tags nextMinusKey = this.next.plus(key, (Object)null);
         var10000 = nextMinusKey == this.next ? this : new LinkedTags(this.key, this.value, nextMinusKey);
      }

      Tags thisMinusKey = var10000;
      return value != null ? (Tags)(new LinkedTags(key, value, thisMinusKey)) : thisMinusKey;
   }

   @Nullable
   public <T> T get(@NotNull KClass<T> key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return (T)(Intrinsics.areEqual((Object)key, (Object)this.key) ? JvmClassMappingKt.getJavaClass(key).cast(this.value) : this.next.get(key));
   }

   @NotNull
   public String toString() {
      return CollectionsKt.joinToString$default((Iterable)CollectionsKt.reversed((Iterable)SequencesKt.toList(SequencesKt.generateSequence(this, LinkedTags::toString$lambda$0))), (CharSequence)null, (CharSequence)"{", (CharSequence)"}", 0, (CharSequence)null, LinkedTags::toString$lambda$1, 25, (Object)null);
   }

   private static final LinkedTags toString$lambda$0(LinkedTags it) {
      Intrinsics.checkNotNullParameter(it, "it");
      Tags var1 = it.next;
      return var1 instanceof LinkedTags ? (LinkedTags)var1 : null;
   }

   private static final CharSequence toString$lambda$1(LinkedTags it) {
      Intrinsics.checkNotNullParameter(it, "it");
      return (CharSequence)("" + it.key + '=' + it.value);
   }
}
