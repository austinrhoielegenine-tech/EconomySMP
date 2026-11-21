package net.dv8tion.jda.api.requests.restaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;

public interface CommandEditAction extends RestAction<Command> {
   @Nonnull
   @CheckReturnValue
   CommandEditAction setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   CommandEditAction addCheck(@Nonnull BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   CommandEditAction timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   CommandEditAction deadline(long var1);

   @Nonnull
   @CheckReturnValue
   CommandEditAction apply(@Nonnull CommandData var1);

   @Nonnull
   @CheckReturnValue
   CommandEditAction setName(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   default CommandEditAction setContexts(@Nonnull InteractionContextType... contexts) {
      return this.setContexts((Collection)Arrays.asList(contexts));
   }

   @Nonnull
   @CheckReturnValue
   CommandEditAction setContexts(@Nonnull Collection<InteractionContextType> var1);

   @Nonnull
   @CheckReturnValue
   default CommandEditAction setIntegrationTypes(@Nonnull IntegrationType... integrationTypes) {
      return this.setIntegrationTypes((Collection)Arrays.asList(integrationTypes));
   }

   @Nonnull
   @CheckReturnValue
   CommandEditAction setIntegrationTypes(@Nonnull Collection<IntegrationType> var1);

   @Nonnull
   @CheckReturnValue
   CommandEditAction setNSFW(boolean var1);

   @Nonnull
   @CheckReturnValue
   CommandEditAction setDefaultPermissions(@Nonnull DefaultMemberPermissions var1);

   @Nonnull
   @CheckReturnValue
   CommandEditAction setDescription(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   CommandEditAction clearOptions();

   @Nonnull
   @CheckReturnValue
   CommandEditAction addOptions(@Nonnull OptionData... var1);

   @Nonnull
   @CheckReturnValue
   default CommandEditAction addOptions(@Nonnull Collection<? extends OptionData> options) {
      Checks.noneNull(options, "Options");
      return this.addOptions((OptionData[])options.toArray(new OptionData[0]));
   }

   @Nonnull
   @CheckReturnValue
   default CommandEditAction addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean required) {
      return this.addOptions((new OptionData(type, name, description)).setRequired(required));
   }

   @Nonnull
   @CheckReturnValue
   default CommandEditAction addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description) {
      return this.addOption(type, name, description, false);
   }

   @Nonnull
   @CheckReturnValue
   CommandEditAction addSubcommands(@Nonnull SubcommandData... var1);

   @Nonnull
   @CheckReturnValue
   default CommandEditAction addSubcommands(@Nonnull Collection<? extends SubcommandData> subcommands) {
      Checks.noneNull(subcommands, "Subcommands");
      return this.addSubcommands((SubcommandData[])subcommands.toArray(new SubcommandData[0]));
   }

   @Nonnull
   @CheckReturnValue
   CommandEditAction addSubcommandGroups(@Nonnull SubcommandGroupData... var1);

   @Nonnull
   @CheckReturnValue
   default CommandEditAction addSubcommandGroups(@Nonnull Collection<? extends SubcommandGroupData> groups) {
      Checks.noneNull(groups, "SubcommandGroups");
      return this.addSubcommandGroups((SubcommandGroupData[])groups.toArray(new SubcommandGroupData[0]));
   }
}
