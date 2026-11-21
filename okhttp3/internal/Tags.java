package okhttp3.internal;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b0\u0018\u00002\u00020\u0001B\t\b\u0004¢\u0006\u0004\b\u0002\u0010\u0003J/\u0010\u0004\u001a\u00020\u0000\"\b\b\u0000\u0010\u0005*\u00020\u00012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u0002H\u00050\u00072\b\u0010\b\u001a\u0004\u0018\u0001H\u0005H&¢\u0006\u0002\u0010\tJ(\u0010\n\u001a\u0004\u0018\u0001H\u0005\"\b\b\u0000\u0010\u0005*\u00020\u00012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u0002H\u00050\u0007H¦\u0002¢\u0006\u0002\u0010\u000b\u0082\u0001\u0002\f\r¨\u0006\u000e"},
   d2 = {"Lokhttp3/internal/Tags;", "", "<init>", "()V", "plus", "T", "key", "Lkotlin/reflect/KClass;", "value", "(Lkotlin/reflect/KClass;Ljava/lang/Object;)Lokhttp3/internal/Tags;", "get", "(Lkotlin/reflect/KClass;)Ljava/lang/Object;", "Lokhttp3/internal/EmptyTags;", "Lokhttp3/internal/LinkedTags;", "okhttp"}
)
public abstract class Tags {
   private Tags() {
   }

   @NotNull
   public abstract <T> Tags plus(@NotNull KClass<T> var1, @Nullable T var2);

   @Nullable
   public abstract <T> T get(@NotNull KClass<T> var1);

   // $FF: synthetic method
   public Tags(DefaultConstructorMarker $constructor_marker) {
      this();
   }
}
