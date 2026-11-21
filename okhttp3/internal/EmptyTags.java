package okhttp3.internal;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J/\u0010\u0004\u001a\u00020\u0001\"\b\b\u0000\u0010\u0005*\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u0002H\u00050\b2\b\u0010\t\u001a\u0004\u0018\u0001H\u0005H\u0016¢\u0006\u0002\u0010\nJ(\u0010\u000b\u001a\u0004\u0018\u0001H\u0005\"\b\b\u0000\u0010\u0005*\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u0002H\u00050\bH\u0096\u0002¢\u0006\u0002\u0010\fJ\b\u0010\r\u001a\u00020\u000eH\u0016¨\u0006\u000f"},
   d2 = {"Lokhttp3/internal/EmptyTags;", "Lokhttp3/internal/Tags;", "<init>", "()V", "plus", "T", "", "key", "Lkotlin/reflect/KClass;", "value", "(Lkotlin/reflect/KClass;Ljava/lang/Object;)Lokhttp3/internal/Tags;", "get", "(Lkotlin/reflect/KClass;)Ljava/lang/Object;", "toString", "", "okhttp"}
)
public final class EmptyTags extends Tags {
   @NotNull
   public static final EmptyTags INSTANCE = new EmptyTags();

   private EmptyTags() {
      super((DefaultConstructorMarker)null);
   }

   @NotNull
   public <T> Tags plus(@NotNull KClass<T> key, @Nullable T value) {
      Intrinsics.checkNotNullParameter(key, "key");
      return value != null ? (Tags)(new LinkedTags(key, value, this)) : (Tags)this;
   }

   @Nullable
   public <T> T get(@NotNull KClass<T> key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return null;
   }

   @NotNull
   public String toString() {
      return "{}";
   }
}
