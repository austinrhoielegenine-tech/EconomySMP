package okhttp3.internal;

import java.util.concurrent.atomic.AtomicReference;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000 \n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a=\u0010\u0000\u001a\u0002H\u0001\"\b\b\u0000\u0010\u0001*\u00020\u0002*\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00010\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u0002H\u00010\bH\u0000¢\u0006\u0002\u0010\t¨\u0006\n"},
   d2 = {"computeIfAbsent", "T", "", "Ljava/util/concurrent/atomic/AtomicReference;", "Lokhttp3/internal/Tags;", "type", "Lkotlin/reflect/KClass;", "compute", "Lkotlin/Function0;", "(Ljava/util/concurrent/atomic/AtomicReference;Lkotlin/reflect/KClass;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "okhttp"}
)
public final class TagsKt {
   @NotNull
   public static final <T> T computeIfAbsent(@NotNull AtomicReference<Tags> $this$computeIfAbsent, @NotNull KClass<T> type, @NotNull Function0<? extends T> compute) {
      Intrinsics.checkNotNullParameter($this$computeIfAbsent, "<this>");
      Intrinsics.checkNotNullParameter(type, "type");
      Intrinsics.checkNotNullParameter(compute, "compute");
      Object computed = null;

      Tags tags;
      Tags newTags;
      do {
         tags = (Tags)$this$computeIfAbsent.get();
         Object existing = tags.get(type);
         if (existing != null) {
            return (T)existing;
         }

         if (computed == null) {
            computed = compute.invoke();
         }

         newTags = tags.plus(type, computed);
      } while(!$this$computeIfAbsent.compareAndSet(tags, newTags));

      return (T)computed;
   }
}
