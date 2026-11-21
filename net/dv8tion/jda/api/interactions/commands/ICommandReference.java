package net.dv8tion.jda.api.interactions.commands;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.IMentionable;

public interface ICommandReference extends IMentionable {
   @Nonnull
   String getName();

   @Nonnull
   String getFullCommandName();

   @Nonnull
   default String getAsMention() {
      return "</" + this.getFullCommandName() + ":" + this.getIdLong() + ">";
   }
}
