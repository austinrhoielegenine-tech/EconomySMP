package net.dv8tion.jda.internal.interactions.command;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.CommandEditActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.localization.LocalizationUtils;

public class CommandImpl implements Command {
   public static final EnumSet<OptionType> OPTIONS;
   public static final Predicate<DataObject> OPTION_TEST;
   public static final Predicate<DataObject> SUBCOMMAND_TEST;
   public static final Predicate<DataObject> GROUP_TEST;
   private final JDAImpl api;
   private final Guild guild;
   private final String name;
   private final String description;
   private final LocalizationMap nameLocalizations;
   private final LocalizationMap descriptionLocalizations;
   private final List<Command.Option> options;
   private final List<Command.SubcommandGroup> groups;
   private final List<Command.Subcommand> subcommands;
   private final long id;
   private final long guildId;
   private final long applicationId;
   private final long version;
   private final boolean nsfw;
   private final Set<InteractionContextType> contexts;
   private final Set<IntegrationType> integrationTypes;
   private final Command.Type type;
   private final DefaultMemberPermissions defaultMemberPermissions;

   public CommandImpl(JDAImpl api, Guild guild, DataObject json) {
      this.api = api;
      this.guild = guild;
      this.name = json.getString("name");
      this.nameLocalizations = LocalizationUtils.unmodifiableFromProperty(json, "name_localizations");
      this.description = json.getString("description", "");
      this.descriptionLocalizations = LocalizationUtils.unmodifiableFromProperty(json, "description_localizations");
      this.type = Command.Type.fromId(json.getInt("type", 1));
      this.id = json.getUnsignedLong("id");
      this.guildId = guild != null ? guild.getIdLong() : 0L;
      this.applicationId = json.getUnsignedLong("application_id", api.getSelfUser().getApplicationIdLong());
      this.options = parseOptions(json, OPTION_TEST, Command.Option::new);
      this.groups = parseOptions(json, GROUP_TEST, (o) -> new Command.SubcommandGroup(this, o));
      this.subcommands = parseOptions(json, SUBCOMMAND_TEST, (o) -> new Command.Subcommand(this, o));
      this.version = json.getUnsignedLong("version", this.id);
      this.defaultMemberPermissions = json.isNull("default_member_permissions") ? DefaultMemberPermissions.ENABLED : DefaultMemberPermissions.enabledFor(json.getLong("default_member_permissions"));
      if (!json.isNull("contexts")) {
         this.contexts = (Set)json.getArray("contexts").stream(DataArray::getString).map(InteractionContextType::fromKey).collect(Helpers.toUnmodifiableEnumSet(InteractionContextType.class));
      } else if (this.guildId != 0L) {
         this.contexts = Helpers.<InteractionContextType>unmodifiableEnumSet(InteractionContextType.GUILD);
      } else {
         boolean dmPermission = json.getBoolean("dm_permission", true);
         this.contexts = dmPermission ? Helpers.unmodifiableEnumSet(InteractionContextType.GUILD, InteractionContextType.BOT_DM) : Helpers.unmodifiableEnumSet(InteractionContextType.GUILD);
      }

      if (!json.isNull("integration_types")) {
         this.integrationTypes = (Set)json.getArray("integration_types").stream(DataArray::getString).map(IntegrationType::fromKey).collect(Helpers.toUnmodifiableEnumSet(IntegrationType.class));
      } else {
         this.integrationTypes = Helpers.<IntegrationType>unmodifiableEnumSet(IntegrationType.GUILD_INSTALL);
      }

      this.nsfw = json.getBoolean("nsfw");
   }

   public static <T> List<T> parseOptions(DataObject json, Predicate<DataObject> test, Function<DataObject, T> transform) {
      return (List)json.optArray("options").map((arr) -> (List)arr.stream(DataArray::getObject).filter(test).map(transform).collect(Collectors.toList())).orElse(Collections.emptyList());
   }

   @Nonnull
   public RestAction<Void> delete() {
      this.checkSelfUser("Cannot delete a command from another bot!");
      String appId = this.getJDA().getSelfUser().getApplicationId();
      Route.CompiledRoute route;
      if (this.guildId != 0L) {
         route = Route.Interactions.DELETE_GUILD_COMMAND.compile(appId, Long.toUnsignedString(this.guildId), this.getId());
      } else {
         route = Route.Interactions.DELETE_COMMAND.compile(appId, this.getId());
      }

      return new RestActionImpl<Void>(this.api, route);
   }

   @Nonnull
   public CommandEditAction editCommand() {
      this.checkSelfUser("Cannot edit a command from another bot!");
      return this.guild == null ? new CommandEditActionImpl(this.api, this.type, this.getId()) : new CommandEditActionImpl(this.guild, this.type, this.getId());
   }

   @Nonnull
   public RestAction<List<IntegrationPrivilege>> retrievePrivileges(@Nonnull Guild guild) {
      this.checkSelfUser("Cannot retrieve privileges for a command from another bot!");
      Checks.notNull(guild, "Guild");
      return guild.retrieveIntegrationPrivilegesById(this.id);
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public Command.Type getType() {
      return this.type;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public LocalizationMap getNameLocalizations() {
      return this.nameLocalizations;
   }

   @Nonnull
   public String getFullCommandName() {
      return this.name;
   }

   @Nonnull
   public String getDescription() {
      return this.description;
   }

   @Nonnull
   public LocalizationMap getDescriptionLocalizations() {
      return this.descriptionLocalizations;
   }

   @Nonnull
   public List<Command.Option> getOptions() {
      return this.options;
   }

   @Nonnull
   public List<Command.Subcommand> getSubcommands() {
      return this.subcommands;
   }

   @Nonnull
   public List<Command.SubcommandGroup> getSubcommandGroups() {
      return this.groups;
   }

   public long getApplicationIdLong() {
      return this.applicationId;
   }

   public long getVersion() {
      return this.version;
   }

   @Nonnull
   public DefaultMemberPermissions getDefaultPermissions() {
      return this.defaultMemberPermissions;
   }

   @Nonnull
   public EnumSet<InteractionContextType> getContexts() {
      return Helpers.<InteractionContextType>copyEnumSet(InteractionContextType.class, this.contexts);
   }

   @Nonnull
   public EnumSet<IntegrationType> getIntegrationTypes() {
      return Helpers.<IntegrationType>copyEnumSet(IntegrationType.class, this.integrationTypes);
   }

   public boolean isNSFW() {
      return this.nsfw;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public String getAsMention() {
      if (this.getType() != Command.Type.SLASH) {
         throw new IllegalStateException("Only slash commands can be mentioned");
      } else {
         return Command.super.getAsMention();
      }
   }

   public String toString() {
      return (new EntityString(this)).setType(this.getType()).setName(this.getName()).toString();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Command)) {
         return false;
      } else {
         return this.id == ((Command)obj).getIdLong();
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   private void checkSelfUser(String s) {
      if (this.applicationId != this.api.getSelfUser().getApplicationIdLong()) {
         throw new IllegalStateException(s);
      }
   }

   static {
      OPTIONS = EnumSet.complementOf(EnumSet.of(OptionType.SUB_COMMAND, OptionType.SUB_COMMAND_GROUP));
      OPTION_TEST = (it) -> OPTIONS.contains(OptionType.fromKey(it.getInt("type")));
      SUBCOMMAND_TEST = (it) -> OptionType.fromKey(it.getInt("type")) == OptionType.SUB_COMMAND;
      GROUP_TEST = (it) -> OptionType.fromKey(it.getInt("type")) == OptionType.SUB_COMMAND_GROUP;
   }
}
