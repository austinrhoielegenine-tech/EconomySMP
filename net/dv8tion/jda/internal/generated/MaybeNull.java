package net.dv8tion.jda.internal.generated;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.EntityString;

@JsonSerialize(
   using = MaybeNullSerializer.class
)
@JsonDeserialize(
   using = MaybeNullDeserializer.class
)
public class MaybeNull<T> {
   private static final MaybeNull<?> empty = new MaybeNull((Object)null);
   private final T value;

   public MaybeNull(T value) {
      this.value = value;
   }

   public T value() {
      return this.value;
   }

   public boolean isPresent() {
      return this.value != null;
   }

   @Nonnull
   public static <T> MaybeNull<T> empty() {
      return empty;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof MaybeNull)) {
         return false;
      } else {
         MaybeNull<?> other = (MaybeNull)obj;
         return Objects.equals(this.value, other.value);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.value});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("value", this.value).toString();
   }
}
