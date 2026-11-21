package net.dv8tion.jda.api.interactions.commands.build;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.localization.LocalizationUtils;
import org.jetbrains.annotations.UnmodifiableView;

public interface CommandData extends SerializableData {
   int MAX_NAME_LENGTH = 32;
   int MAX_DESCRIPTION_LENGTH = 100;
   int MAX_OPTIONS = 25;

   @Nonnull
   CommandData setLocalizationFunction(@Nonnull LocalizationFunction var1);

   @Nonnull
   CommandData setName(@Nonnull String var1);

   @Nonnull
   CommandData setNameLocalization(@Nonnull DiscordLocale var1, @Nonnull String var2);

   @Nonnull
   CommandData setNameLocalizations(@Nonnull Map<DiscordLocale, String> var1);

   @Nonnull
   CommandData setDefaultPermissions(@Nonnull DefaultMemberPermissions var1);

   @Nonnull
   default CommandData setContexts(@Nonnull InteractionContextType... contexts) {
      Checks.notEmpty((Object[])contexts, "Contexts");
      return this.setContexts((Collection)Arrays.asList(contexts));
   }

   @Nonnull
   CommandData setContexts(@Nonnull Collection<InteractionContextType> var1);

   @Nonnull
   default CommandData setIntegrationTypes(@Nonnull IntegrationType... integrationTypes) {
      Checks.notEmpty((Object[])integrationTypes, "Integration types");
      return this.setIntegrationTypes((Collection)Arrays.asList(integrationTypes));
   }

   @Nonnull
   CommandData setIntegrationTypes(@Nonnull Collection<IntegrationType> var1);

   @Nonnull
   CommandData setNSFW(boolean var1);

   @Nonnull
   String getName();

   @Nonnull
   LocalizationMap getNameLocalizations();

   @Nonnull
   Command.Type getType();

   @Nonnull
   DefaultMemberPermissions getDefaultPermissions();

   @Nonnull
   @UnmodifiableView Set<InteractionContextType> getContexts();

   @Nonnull
   @UnmodifiableView Set<IntegrationType> getIntegrationTypes();

   boolean isNSFW();

   @Nonnull
   static CommandData fromCommand(@Nonnull Command command) {
      Checks.notNull(command, "Command");
      if (command.getType() != Command.Type.SLASH) {
         CommandDataImpl data = new CommandDataImpl(command.getType(), command.getName());
         return data.setDefaultPermissions(command.getDefaultPermissions()).setContexts(command.getContexts()).setIntegrationTypes(command.getIntegrationTypes()).setNSFW(command.isNSFW()).setNameLocalizations(command.getNameLocalizations().toMap()).setDescriptionLocalizations(command.getDescriptionLocalizations().toMap());
      } else {
         return SlashCommandData.fromCommand(command);
      }
   }

   @Nonnull
   static CommandData fromData(@Nonnull DataObject object) {
      Checks.notNull(object, "DataObject");
      String name = object.getString("name");
      Command.Type commandType = Command.Type.fromId(object.getInt("type", 1));
      if (commandType != Command.Type.SLASH) {
         CommandDataImpl data = new CommandDataImpl(commandType, name);
         if (!object.isNull("default_member_permissions")) {
            long defaultPermissions = object.getLong("default_member_permissions");
            data.setDefaultPermissions(defaultPermissions == 0L ? DefaultMemberPermissions.DISABLED : DefaultMemberPermissions.enabledFor(defaultPermissions));
         }

         if (!object.isNull("contexts")) {
            data.setContexts((Collection)object.getArray("contexts").stream(DataArray::getString).map(InteractionContextType::fromKey).collect(Helpers.toUnmodifiableEnumSet(InteractionContextType.class)));
         } else {
            data.setContexts(Helpers.unmodifiableEnumSet(InteractionContextType.GUILD, InteractionContextType.BOT_DM));
         }

         if (!object.isNull("integration_types")) {
            data.setIntegrationTypes((Collection)object.getArray("integration_types").stream(DataArray::getString).map(IntegrationType::fromKey).collect(Helpers.toUnmodifiableEnumSet(IntegrationType.class)));
         } else {
            data.setIntegrationTypes(Helpers.unmodifiableEnumSet(IntegrationType.GUILD_INSTALL));
         }

         data.setNSFW(object.getBoolean("nsfw"));
         data.setNameLocalizations(LocalizationUtils.mapFromProperty(object, "name_localizations"));
         data.setDescriptionLocalizations(LocalizationUtils.mapFromProperty(object, "description_localizations"));
         return data;
      } else {
         return SlashCommandData.fromData(object);
      }
   }
}
