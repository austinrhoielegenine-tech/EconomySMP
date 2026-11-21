package net.dv8tion.jda.api.interactions.commands.context;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

public interface ContextInteraction<T> extends CommandInteraction {
   @Nonnull
   ContextTarget getTargetType();

   @Nonnull
   T getTarget();

   public static enum ContextTarget {
      USER,
      MESSAGE;

      // $FF: synthetic method
      private static ContextTarget[] $values() {
         return new ContextTarget[]{USER, MESSAGE};
      }
   }
}
