package net.dv8tion.jda.api.components.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;

public interface IDisableable extends Component {
   boolean isDisabled();

   default boolean isEnabled() {
      return !this.isDisabled();
   }

   @Nonnull
   IDisableable withUniqueId(int var1);

   @Nonnull
   @CheckReturnValue
   IDisableable withDisabled(boolean var1);

   @Nonnull
   @CheckReturnValue
   default IDisableable asDisabled() {
      return this.withDisabled(true);
   }

   @Nonnull
   @CheckReturnValue
   default IDisableable asEnabled() {
      return this.withDisabled(false);
   }
}
