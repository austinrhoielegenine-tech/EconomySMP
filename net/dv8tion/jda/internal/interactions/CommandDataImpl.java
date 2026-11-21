package net.dv8tion.jda.internal.interactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.interactions.command.localization.LocalizationMapper;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class CommandDataImpl implements SlashCommandData {
   protected final List<SerializableData> options = new ArrayList(25);
   protected String name;
   protected String description = "";
   private LocalizationMapper localizationMapper;
   private final LocalizationMap nameLocalizations = new LocalizationMap(this::checkName);
   private final LocalizationMap descriptionLocalizations = new LocalizationMap(this::checkDescription);
   private boolean allowSubcommands = true;
   private boolean allowOption = true;
   private boolean allowRequired = true;
   private EnumSet<InteractionContextType> contexts;
   private EnumSet<IntegrationType> integrationTypes;
   private boolean nsfw;
   private DefaultMemberPermissions defaultMemberPermissions;
   private final Command.Type type;

   public CommandDataImpl(@Nonnull String name, @Nonnull String description) {
      this.contexts = EnumSet.of(InteractionContextType.GUILD, InteractionContextType.BOT_DM);
      this.integrationTypes = EnumSet.of(IntegrationType.GUILD_INSTALL);
      this.nsfw = false;
      this.defaultMemberPermissions = DefaultMemberPermissions.ENABLED;
      this.type = Command.Type.SLASH;
      this.setName(name);
      this.setDescription(description);
   }

   public CommandDataImpl(@Nonnull Command.Type type, @Nonnull String name) {
      this.contexts = EnumSet.of(InteractionContextType.GUILD, InteractionContextType.BOT_DM);
      this.integrationTypes = EnumSet.of(IntegrationType.GUILD_INSTALL);
      this.nsfw = false;
      this.defaultMemberPermissions = DefaultMemberPermissions.ENABLED;
      this.type = type;
      Checks.notNull(type, "Command Type");
      Checks.check(type != Command.Type.SLASH, "Cannot create slash command without description. Use `new CommandDataImpl(name, description)` instead.");
      this.setName(name);
   }

   public static CommandDataImpl of(@Nonnull Command.Type type, @Nonnull String name, @Nullable String description) {
      return type == Command.Type.SLASH ? new CommandDataImpl(name, description) : new CommandDataImpl(type, name);
   }

   protected void checkType(Command.Type required, String action) {
      if (required != this.type) {
         throw new IllegalStateException("Cannot " + action + " for commands of type " + this.type);
      }
   }

   public void checkName(@Nonnull String name) {
      Checks.inRange(name, 1, 32, "Name");
      if (this.type == Command.Type.SLASH) {
         Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Name");
         Checks.isLowercase(name, "Name");
      }

   }

   public void checkDescription(@Nonnull String description) {
      this.checkType(Command.Type.SLASH, "set description");
      Checks.inRange(description, 1, 100, "Description");
   }

   @Nonnull
   public DataObject toData() {
      DataArray options = DataArray.fromCollection(this.options);
      if (this.localizationMapper != null) {
         this.localizationMapper.localizeCommand(this, options);
      }

      DataObject json = DataObject.empty().put("type", this.type.getId()).put("name", this.name).put("nsfw", this.nsfw).put("options", options).put("contexts", this.contexts.stream().map(InteractionContextType::getType).collect(Collectors.toList())).put("integration_types", this.integrationTypes.stream().map(IntegrationType::getType).collect(Collectors.toList())).put("default_member_permissions", this.defaultMemberPermissions == DefaultMemberPermissions.ENABLED ? null : Long.toUnsignedString(this.defaultMemberPermissions.getPermissionsRaw())).put("name_localizations", this.nameLocalizations);
      if (this.type == Command.Type.SLASH) {
         json.put("description", this.description).put("description_localizations", this.descriptionLocalizations);
      }

      return json;
   }

   @Nonnull
   public Command.Type getType() {
      return this.type;
   }

   @Nonnull
   public DefaultMemberPermissions getDefaultPermissions() {
      return this.defaultMemberPermissions;
   }

   @Nonnull
   public Set<InteractionContextType> getContexts() {
      return Collections.unmodifiableSet(this.contexts);
   }

   @Nonnull
   public Set<IntegrationType> getIntegrationTypes() {
      return this.integrationTypes;
   }

   public boolean isNSFW() {
      return this.nsfw;
   }

   @Nonnull
   public List<OptionData> getOptions() {
      Stream var10000 = this.options.stream();
      Objects.requireNonNull(OptionData.class);
      var10000 = var10000.filter(OptionData.class::isInstance);
      Objects.requireNonNull(OptionData.class);
      return (List)var10000.map(OptionData.class::cast).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public List<SubcommandData> getSubcommands() {
      Stream var10000 = this.options.stream();
      Objects.requireNonNull(SubcommandData.class);
      var10000 = var10000.filter(SubcommandData.class::isInstance);
      Objects.requireNonNull(SubcommandData.class);
      return (List)var10000.map(SubcommandData.class::cast).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public List<SubcommandGroupData> getSubcommandGroups() {
      Stream var10000 = this.options.stream();
      Objects.requireNonNull(SubcommandGroupData.class);
      var10000 = var10000.filter(SubcommandGroupData.class::isInstance);
      Objects.requireNonNull(SubcommandGroupData.class);
      return (List)var10000.map(SubcommandGroupData.class::cast).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public CommandDataImpl setDefaultPermissions(@Nonnull DefaultMemberPermissions permissions) {
      Checks.notNull(permissions, "Permissions");
      this.defaultMemberPermissions = permissions;
      return this;
   }

   @Nonnull
   public CommandDataImpl setContexts(@Nonnull Collection<InteractionContextType> contexts) {
      Checks.notEmpty(contexts, "Contexts");
      this.contexts = Helpers.<InteractionContextType>copyEnumSet(InteractionContextType.class, contexts);
      return this;
   }

   @Nonnull
   public CommandDataImpl setIntegrationTypes(@Nonnull Collection<IntegrationType> integrationTypes) {
      Checks.notEmpty((Collection)this.contexts, "Contexts");
      this.integrationTypes = Helpers.<IntegrationType>copyEnumSet(IntegrationType.class, integrationTypes);
      return this;
   }

   @Nonnull
   public CommandDataImpl setNSFW(boolean nsfw) {
      this.nsfw = nsfw;
      return this;
   }

   @Nonnull
   public CommandDataImpl addOptions(@Nonnull OptionData... options) {
      Checks.noneNull(options, "Option");
      if (options.length == 0) {
         return this;
      } else {
         this.checkType(Command.Type.SLASH, "add options");
         Checks.check(options.length + this.options.size() <= 25, "Cannot have more than %d options for a command!", (int)25);
         Checks.check(this.allowOption, "You cannot mix options with subcommands/groups.");
         boolean allowRequired = this.allowRequired;

         for(OptionData option : options) {
            Checks.check(option.getType() != OptionType.SUB_COMMAND, "Cannot add a subcommand with addOptions(...). Use addSubcommands(...) instead!");
            Checks.check(option.getType() != OptionType.SUB_COMMAND_GROUP, "Cannot add a subcommand group with addOptions(...). Use addSubcommandGroups(...) instead!");
            Checks.check(allowRequired || !option.isRequired(), "Cannot add required options after non-required options!");
            allowRequired = option.isRequired();
         }

         Checks.checkUnique(Stream.concat(this.getOptions().stream(), Arrays.stream(options)).map(OptionData::getName), "Cannot have multiple options with the same name. Name: \"%s\" appeared %d times!", (count, value) -> new Object[]{value, count});
         this.allowSubcommands = false;
         this.allowRequired = allowRequired;
         Collections.addAll(this.options, options);
         return this;
      }
   }

   @Nonnull
   public CommandDataImpl addSubcommands(@Nonnull SubcommandData... subcommands) {
      Checks.noneNull(subcommands, "Subcommands");
      if (subcommands.length == 0) {
         return this;
      } else {
         this.checkType(Command.Type.SLASH, "add subcommands");
         if (!this.allowSubcommands) {
            throw new IllegalArgumentException("You cannot mix options with subcommands/groups.");
         } else {
            Checks.check(subcommands.length + this.options.size() <= 25, "Cannot have more than %d subcommands for a command!", (int)25);
            Checks.checkUnique(Stream.concat(this.getSubcommands().stream(), Arrays.stream(subcommands)).map(SubcommandData::getName), "Cannot have multiple subcommands with the same name. Name: \"%s\" appeared %d times!", (count, value) -> new Object[]{value, count});
            this.allowOption = false;
            Collections.addAll(this.options, subcommands);
            return this;
         }
      }
   }

   @Nonnull
   public CommandDataImpl addSubcommandGroups(@Nonnull SubcommandGroupData... groups) {
      Checks.noneNull(groups, "SubcommandGroups");
      if (groups.length == 0) {
         return this;
      } else {
         this.checkType(Command.Type.SLASH, "add subcommand groups");
         if (!this.allowSubcommands) {
            throw new IllegalArgumentException("You cannot mix options with subcommands/groups.");
         } else {
            Checks.check(groups.length + this.options.size() <= 25, "Cannot have more than %d subcommand groups for a command!", (int)25);
            Checks.checkUnique(Stream.concat(this.getSubcommandGroups().stream(), Arrays.stream(groups)).map(SubcommandGroupData::getName), "Cannot have multiple subcommand groups with the same name. Name: \"%s\" appeared %d times!", (count, value) -> new Object[]{value, count});
            this.allowOption = false;
            Collections.addAll(this.options, groups);
            return this;
         }
      }
   }

   @Nonnull
   public CommandDataImpl setLocalizationFunction(@Nonnull LocalizationFunction localizationFunction) {
      Checks.notNull(localizationFunction, "Localization function");
      this.localizationMapper = LocalizationMapper.fromFunction(localizationFunction);
      return this;
   }

   @Nonnull
   public CommandDataImpl setName(@Nonnull String name) {
      this.checkName(name);
      this.name = name;
      return this;
   }

   @Nonnull
   public CommandDataImpl setNameLocalization(@Nonnull DiscordLocale locale, @Nonnull String name) {
      this.nameLocalizations.setTranslation(locale, name);
      return this;
   }

   @Nonnull
   public CommandDataImpl setNameLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.nameLocalizations.setTranslations(map);
      return this;
   }

   @Nonnull
   public CommandDataImpl setDescription(@Nonnull String description) {
      this.checkDescription(description);
      this.description = description;
      return this;
   }

   @Nonnull
   public CommandDataImpl setDescriptionLocalization(@Nonnull DiscordLocale locale, @Nonnull String description) {
      this.descriptionLocalizations.setTranslation(locale, description);
      return this;
   }

   @Nonnull
   public CommandDataImpl setDescriptionLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.descriptionLocalizations.setTranslations(map);
      return this;
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
   public String getDescription() {
      return this.description;
   }

   @Nonnull
   public LocalizationMap getDescriptionLocalizations() {
      return this.descriptionLocalizations;
   }

   public boolean removeOptions(@Nonnull Predicate<? super OptionData> condition) {
      Checks.notNull(condition, "Condition");
      boolean modified = this.options.removeIf((o) -> o instanceof OptionData && condition.test((OptionData)o));
      if (modified) {
         this.updateAllowedOptions();
      }

      return modified;
   }

   public boolean removeSubcommands(@Nonnull Predicate<? super SubcommandData> condition) {
      Checks.notNull(condition, "Condition");
      boolean modified = this.options.removeIf((o) -> o instanceof SubcommandData && condition.test((SubcommandData)o));
      if (modified) {
         this.updateAllowedOptions();
      }

      return modified;
   }

   public boolean removeSubcommandGroups(@Nonnull Predicate<? super SubcommandGroupData> condition) {
      Checks.notNull(condition, "Condition");
      boolean modified = this.options.removeIf((o) -> o instanceof SubcommandGroupData && condition.test((SubcommandGroupData)o));
      if (modified) {
         this.updateAllowedOptions();
      }

      return modified;
   }

   public void removeAllOptions() {
      this.options.clear();
      this.updateAllowedOptions();
   }

   private void updateAllowedOptions() {
      if (this.options.isEmpty()) {
         this.allowRequired = this.allowOption = this.allowSubcommands = true;
      } else {
         SerializableData last = (SerializableData)this.options.get(this.options.size() - 1);
         this.allowOption = last instanceof OptionData;
         this.allowRequired = this.allowOption && ((OptionData)last).isRequired();
         this.allowSubcommands = !this.allowOption;
      }
   }
}
