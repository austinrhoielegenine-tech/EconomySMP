package net.dv8tion.jda.internal.interactions.command;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public interface CommandInteractionPayloadMixin extends CommandInteractionPayload {
   CommandInteractionPayload getCommandPayload();

   @Nonnull
   default Command.Type getCommandType() {
      return this.getCommandPayload().getCommandType();
   }

   @Nonnull
   default String getName() {
      return this.getCommandPayload().getName();
   }

   @Nullable
   default String getSubcommandName() {
      return this.getCommandPayload().getSubcommandName();
   }

   @Nullable
   default String getSubcommandGroup() {
      return this.getCommandPayload().getSubcommandGroup();
   }

   default long getCommandIdLong() {
      return this.getCommandPayload().getCommandIdLong();
   }

   @Nonnull
   default List<OptionMapping> getOptions() {
      return this.getCommandPayload().getOptions();
   }

   default boolean isGuildCommand() {
      return this.getCommandPayload().isGuildCommand();
   }
}
