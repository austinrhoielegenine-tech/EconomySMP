package net.dv8tion.jda.api.requests.restaction;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.requests.RestAction;

public interface CommandCreateAction extends RestAction<Command>, SlashCommandData {
   @Nonnull
   @CheckReturnValue
   CommandCreateAction setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction addCheck(@Nonnull BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction deadline(long var1);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setLocalizationFunction(@Nonnull LocalizationFunction var1);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setNameLocalization(@Nonnull DiscordLocale var1, @Nonnull String var2);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setNameLocalizations(@Nonnull Map<DiscordLocale, String> var1);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setDescription(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setDescriptionLocalization(@Nonnull DiscordLocale var1, @Nonnull String var2);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setDescriptionLocalizations(@Nonnull Map<DiscordLocale, String> var1);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction addOptions(@Nonnull OptionData... var1);

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction addOptions(@Nonnull Collection<? extends OptionData> options) {
      return (CommandCreateAction)SlashCommandData.super.addOptions(options);
   }

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean required, boolean autoComplete) {
      return (CommandCreateAction)SlashCommandData.super.addOption(type, name, description, required, autoComplete);
   }

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean required) {
      return (CommandCreateAction)SlashCommandData.super.addOption(type, name, description, required);
   }

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description) {
      return (CommandCreateAction)SlashCommandData.super.addOption(type, name, description, false);
   }

   @Nonnull
   @CheckReturnValue
   CommandCreateAction addSubcommands(@Nonnull SubcommandData... var1);

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction addSubcommands(@Nonnull Collection<? extends SubcommandData> subcommands) {
      return (CommandCreateAction)SlashCommandData.super.addSubcommands(subcommands);
   }

   @Nonnull
   @CheckReturnValue
   CommandCreateAction addSubcommandGroups(@Nonnull SubcommandGroupData... var1);

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction addSubcommandGroups(@Nonnull Collection<? extends SubcommandGroupData> groups) {
      return (CommandCreateAction)SlashCommandData.super.addSubcommandGroups(groups);
   }

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setDefaultPermissions(@Nonnull DefaultMemberPermissions var1);

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction setContexts(@Nonnull InteractionContextType... contexts) {
      return (CommandCreateAction)SlashCommandData.super.setContexts(contexts);
   }

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setContexts(@Nonnull Collection<InteractionContextType> var1);

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction setIntegrationTypes(@Nonnull IntegrationType... integrationTypes) {
      return (CommandCreateAction)SlashCommandData.super.setIntegrationTypes(integrationTypes);
   }

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setIntegrationTypes(@Nonnull Collection<IntegrationType> var1);

   @Nonnull
   @CheckReturnValue
   CommandCreateAction setNSFW(boolean var1);
}
