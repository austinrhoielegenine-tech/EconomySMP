package net.dv8tion.jda.api.interactions.commands.build;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.localization.LocalizationUtils;
import org.jetbrains.annotations.Unmodifiable;

public interface SlashCommandData extends CommandData {
   @Nonnull
   SlashCommandData setLocalizationFunction(@Nonnull LocalizationFunction var1);

   @Nonnull
   SlashCommandData setName(@Nonnull String var1);

   @Nonnull
   SlashCommandData setNameLocalization(@Nonnull DiscordLocale var1, @Nonnull String var2);

   @Nonnull
   SlashCommandData setNameLocalizations(@Nonnull Map<DiscordLocale, String> var1);

   @Nonnull
   SlashCommandData setDefaultPermissions(@Nonnull DefaultMemberPermissions var1);

   @Nonnull
   default SlashCommandData setContexts(@Nonnull InteractionContextType... contexts) {
      return (SlashCommandData)CommandData.super.setContexts(contexts);
   }

   @Nonnull
   SlashCommandData setContexts(@Nonnull Collection<InteractionContextType> var1);

   @Nonnull
   default SlashCommandData setIntegrationTypes(@Nonnull IntegrationType... integrationTypes) {
      return (SlashCommandData)CommandData.super.setIntegrationTypes(integrationTypes);
   }

   @Nonnull
   SlashCommandData setIntegrationTypes(@Nonnull Collection<IntegrationType> var1);

   @Nonnull
   SlashCommandData setNSFW(boolean var1);

   @Nonnull
   SlashCommandData setDescription(@Nonnull String var1);

   @Nonnull
   SlashCommandData setDescriptionLocalization(@Nonnull DiscordLocale var1, @Nonnull String var2);

   @Nonnull
   SlashCommandData setDescriptionLocalizations(@Nonnull Map<DiscordLocale, String> var1);

   @Nonnull
   String getDescription();

   @Nonnull
   LocalizationMap getDescriptionLocalizations();

   boolean removeOptions(@Nonnull Predicate<? super OptionData> var1);

   default boolean removeOptionByName(@Nonnull String name) {
      return this.removeOptions((option) -> option.getName().equals(name));
   }

   boolean removeSubcommands(@Nonnull Predicate<? super SubcommandData> var1);

   default boolean removeSubcommandByName(@Nonnull String name) {
      return this.removeSubcommands((subcommand) -> subcommand.getName().equals(name));
   }

   boolean removeSubcommandGroups(@Nonnull Predicate<? super SubcommandGroupData> var1);

   default boolean removeSubcommandGroupByName(@Nonnull String name) {
      return this.removeSubcommandGroups((group) -> group.getName().equals(name));
   }

   @Nonnull
   @Unmodifiable List<SubcommandData> getSubcommands();

   @Nonnull
   @Unmodifiable List<SubcommandGroupData> getSubcommandGroups();

   @Nonnull
   @Unmodifiable List<OptionData> getOptions();

   @Nonnull
   SlashCommandData addOptions(@Nonnull OptionData... var1);

   @Nonnull
   default SlashCommandData addOptions(@Nonnull Collection<? extends OptionData> options) {
      Checks.noneNull(options, "Option");
      return this.addOptions((OptionData[])options.toArray(new OptionData[0]));
   }

   @Nonnull
   default SlashCommandData addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean required, boolean autoComplete) {
      return this.addOptions((new OptionData(type, name, description)).setRequired(required).setAutoComplete(autoComplete));
   }

   @Nonnull
   default SlashCommandData addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean required) {
      return this.addOption(type, name, description, required, false);
   }

   @Nonnull
   default SlashCommandData addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description) {
      return this.addOption(type, name, description, false);
   }

   @Nonnull
   SlashCommandData addSubcommands(@Nonnull SubcommandData... var1);

   @Nonnull
   default SlashCommandData addSubcommands(@Nonnull Collection<? extends SubcommandData> subcommands) {
      Checks.noneNull(subcommands, "Subcommands");
      return this.addSubcommands((SubcommandData[])subcommands.toArray(new SubcommandData[0]));
   }

   @Nonnull
   SlashCommandData addSubcommandGroups(@Nonnull SubcommandGroupData... var1);

   @Nonnull
   default SlashCommandData addSubcommandGroups(@Nonnull Collection<? extends SubcommandGroupData> groups) {
      Checks.noneNull(groups, "SubcommandGroups");
      return this.addSubcommandGroups((SubcommandGroupData[])groups.toArray(new SubcommandGroupData[0]));
   }

   @Nonnull
   static SlashCommandData fromCommand(@Nonnull Command command) {
      Checks.notNull(command, "Command");
      if (command.getType() != Command.Type.SLASH) {
         throw new IllegalArgumentException("Cannot convert command of type " + command.getType() + " to SlashCommandData!");
      } else {
         CommandDataImpl data = new CommandDataImpl(command.getName(), command.getDescription());
         data.setContexts(command.getContexts());
         data.setIntegrationTypes(command.getIntegrationTypes());
         data.setNSFW(command.isNSFW());
         data.setDefaultPermissions(command.getDefaultPermissions());
         data.setNameLocalizations(command.getNameLocalizations().toMap());
         data.setDescriptionLocalizations(command.getDescriptionLocalizations().toMap());
         Stream var10000 = command.getOptions().stream().map(OptionData::fromOption);
         Objects.requireNonNull(data);
         var10000.forEach((xva$0) -> data.addOptions(xva$0));
         var10000 = command.getSubcommands().stream().map(SubcommandData::fromSubcommand);
         Objects.requireNonNull(data);
         var10000.forEach((xva$0) -> data.addSubcommands(xva$0));
         var10000 = command.getSubcommandGroups().stream().map(SubcommandGroupData::fromGroup);
         Objects.requireNonNull(data);
         var10000.forEach((xva$0) -> data.addSubcommandGroups(xva$0));
         return data;
      }
   }

   @Nonnull
   static SlashCommandData fromData(@Nonnull DataObject object) {
      Checks.notNull(object, "DataObject");
      String name = object.getString("name");
      Command.Type commandType = Command.Type.fromId(object.getInt("type", 1));
      if (commandType != Command.Type.SLASH) {
         throw new IllegalArgumentException("Cannot convert command of type " + commandType + " to SlashCommandData!");
      } else {
         String description = object.getString("description");
         DataArray options = (DataArray)object.optArray("options").orElseGet(DataArray::empty);
         CommandDataImpl command = new CommandDataImpl(name, description);
         if (!object.isNull("contexts")) {
            command.setContexts((Collection)object.getArray("contexts").stream(DataArray::getString).map(InteractionContextType::fromKey).collect(Helpers.toUnmodifiableEnumSet(InteractionContextType.class)));
         } else {
            command.setContexts(Helpers.unmodifiableEnumSet(InteractionContextType.GUILD, InteractionContextType.BOT_DM));
         }

         if (!object.isNull("integration_types")) {
            command.setIntegrationTypes((Collection)object.getArray("integration_types").stream(DataArray::getString).map(IntegrationType::fromKey).collect(Helpers.toUnmodifiableEnumSet(IntegrationType.class)));
         } else {
            command.setIntegrationTypes(Helpers.unmodifiableEnumSet(IntegrationType.GUILD_INSTALL));
         }

         command.setNSFW(object.getBoolean("nsfw"));
         command.setDefaultPermissions(object.isNull("default_member_permissions") ? DefaultMemberPermissions.ENABLED : DefaultMemberPermissions.enabledFor(object.getLong("default_member_permissions")));
         command.setNameLocalizations(LocalizationUtils.mapFromProperty(object, "name_localizations"));
         command.setDescriptionLocalizations(LocalizationUtils.mapFromProperty(object, "description_localizations"));
         options.stream(DataArray::getObject).forEach((opt) -> {
            OptionType type = OptionType.fromKey(opt.getInt("type"));
            switch (type) {
               case SUB_COMMAND:
                  command.addSubcommands(SubcommandData.fromData(opt));
                  break;
               case SUB_COMMAND_GROUP:
                  command.addSubcommandGroups(SubcommandGroupData.fromData(opt));
                  break;
               default:
                  command.addOptions(OptionData.fromData(opt));
            }

         });
         return command;
      }
   }
}
