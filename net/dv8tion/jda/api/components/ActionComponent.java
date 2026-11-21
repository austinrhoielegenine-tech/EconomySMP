package net.dv8tion.jda.api.components;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.attribute.ICustomId;
import net.dv8tion.jda.api.components.attribute.IDisableable;

public interface ActionComponent extends IDisableable, ICustomId {
   @Nonnull
   ActionComponent withUniqueId(int var1);

   boolean isDisabled();

   @Nonnull
   @CheckReturnValue
   default ActionComponent asDisabled() {
      return (ActionComponent)IDisableable.super.asDisabled();
   }

   @Nonnull
   @CheckReturnValue
   default ActionComponent asEnabled() {
      return (ActionComponent)IDisableable.super.asEnabled();
   }

   @Nonnull
   @CheckReturnValue
   ActionComponent withDisabled(boolean var1);
}
