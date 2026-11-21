package net.dv8tion.jda.api.components.checkbox;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.attribute.ICustomId;
import net.dv8tion.jda.api.components.label.LabelChildComponent;
import net.dv8tion.jda.internal.components.checkbox.CheckboxImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface Checkbox extends ICustomId, LabelChildComponent {
   int CUSTOM_ID_MAX_LENGTH = 100;

   @Nonnull
   @CheckReturnValue
   Checkbox withUniqueId(int var1);

   @Nonnull
   @CheckReturnValue
   Checkbox withCustomId(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   Checkbox withDefault(boolean var1);

   @Nonnull
   String getCustomId();

   boolean isDefault();

   @Nonnull
   static Checkbox of(@Nonnull String customId) {
      Checks.notBlank(customId, "Custom ID");
      Checks.notLonger(customId, 100, "Custom ID");
      return new CheckboxImpl(-1, customId, false);
   }

   @Nonnull
   static Checkbox of(@Nonnull String customId, boolean isDefault) {
      Checks.notBlank(customId, "Custom ID");
      Checks.notLonger(customId, 100, "Custom ID");
      return new CheckboxImpl(-1, customId, isDefault);
   }
}
