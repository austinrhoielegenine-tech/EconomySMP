package net.dv8tion.jda.api.interactions;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.utils.EntityString;

public class AutoCompleteQuery {
   private final String name;
   private final String value;
   private final OptionType type;

   public AutoCompleteQuery(@Nonnull OptionMapping option) {
      this.name = option.getName();
      this.value = option.getAsString();
      this.type = option.getType();
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public String getValue() {
      return this.value;
   }

   @Nonnull
   public OptionType getType() {
      return this.type;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.value, this.type});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof AutoCompleteQuery)) {
         return false;
      } else {
         AutoCompleteQuery query = (AutoCompleteQuery)obj;
         return this.type == query.type && this.name.equals(query.name) && this.value.equals(query.value);
      }
   }

   public String toString() {
      return (new EntityString(this)).setType(this.getType()).addMetadata("name", this.getName()).addMetadata("value", this.getValue()).toString();
   }
}
