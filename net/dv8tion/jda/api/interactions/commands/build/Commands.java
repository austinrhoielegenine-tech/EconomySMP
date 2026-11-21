package net.dv8tion.jda.api.interactions.commands.build;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.utils.Checks;

public class Commands {
   public static final int MAX_SLASH_COMMANDS = 100;
   public static final int MAX_USER_COMMANDS = 15;
   public static final int MAX_MESSAGE_COMMANDS = 15;

   @Nonnull
   public static SlashCommandData slash(@Nonnull String name, @Nonnull String description) {
      return new CommandDataImpl(name, description);
   }

   @Nonnull
   public static CommandData message(@Nonnull String name) {
      return new CommandDataImpl(Command.Type.MESSAGE, name);
   }

   @Nonnull
   public static CommandData user(@Nonnull String name) {
      return new CommandDataImpl(Command.Type.USER, name);
   }

   @Nonnull
   public static CommandData context(@Nonnull Command.Type type, @Nonnull String name) {
      return new CommandDataImpl(type, name);
   }

   @Nonnull
   public static List<CommandData> fromList(@Nonnull DataArray array) {
      Checks.notNull(array, "DataArray");
      return (List)array.stream(DataArray::getObject).map(CommandData::fromData).collect(Collectors.toList());
   }

   @Nonnull
   public static List<CommandData> fromList(@Nonnull Collection<? extends DataObject> collection) {
      Checks.noneNull(collection, "CommandData");
      return fromList(DataArray.fromCollection(collection));
   }
}
