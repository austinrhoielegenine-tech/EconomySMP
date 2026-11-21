package net.dv8tion.jda.api.interactions.commands;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.DataType;
import net.dv8tion.jda.internal.interactions.command.CommandImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.localization.LocalizationUtils;
import org.jetbrains.annotations.Unmodifiable;

public interface Command extends ISnowflake, ICommandReference {
   @Nonnull
   @CheckReturnValue
   RestAction<Void> delete();

   @Nonnull
   @CheckReturnValue
   CommandEditAction editCommand();

   @Nonnull
   @CheckReturnValue
   RestAction<List<IntegrationPrivilege>> retrievePrivileges(@Nonnull Guild var1);

   @Nonnull
   JDA getJDA();

   @Nonnull
   Type getType();

   @Nonnull
   String getName();

   @Nonnull
   LocalizationMap getNameLocalizations();

   @Nonnull
   String getDescription();

   @Nonnull
   LocalizationMap getDescriptionLocalizations();

   @Nonnull
   @Unmodifiable List<Option> getOptions();

   @Nonnull
   @Unmodifiable List<Subcommand> getSubcommands();

   @Nonnull
   @Unmodifiable List<SubcommandGroup> getSubcommandGroups();

   long getApplicationIdLong();

   @Nonnull
   default String getApplicationId() {
      return Long.toUnsignedString(this.getApplicationIdLong());
   }

   long getVersion();

   @Nonnull
   default OffsetDateTime getTimeModified() {
      return TimeUtil.getTimeCreated(this.getVersion());
   }

   @Nonnull
   DefaultMemberPermissions getDefaultPermissions();

   @Nonnull
   EnumSet<InteractionContextType> getContexts();

   @Nonnull
   EnumSet<IntegrationType> getIntegrationTypes();

   boolean isNSFW();

   public static enum Type {
      UNKNOWN(-1),
      SLASH(1),
      USER(2),
      MESSAGE(3);

      private final int id;

      private Type(int id) {
         this.id = id;
      }

      @Nonnull
      public static Type fromId(int id) {
         for(Type type : values()) {
            if (type.id == id) {
               return type;
            }
         }

         return UNKNOWN;
      }

      public int getId() {
         return this.id;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{UNKNOWN, SLASH, USER, MESSAGE};
      }
   }

   public static class Choice {
      public static final int MAX_NAME_LENGTH = 100;
      public static final int MAX_STRING_VALUE_LENGTH = 100;
      private String name;
      private final LocalizationMap nameLocalizations = new LocalizationMap(this::checkName);
      private long intValue = 0L;
      private double doubleValue = Double.NaN;
      private String stringValue = null;
      private OptionType type;

      public Choice(@Nonnull String name, long value) {
         this.setName(name);
         this.setIntValue(value);
      }

      public Choice(@Nonnull String name, double value) {
         this.setName(name);
         this.setDoubleValue(value);
      }

      public Choice(@Nonnull String name, @Nonnull String value) {
         this.setName(name);
         this.setStringValue(value);
      }

      public Choice(@Nonnull DataObject json) {
         Checks.notNull(json, "DataObject");
         this.name = json.getString("name");
         if (json.isType("value", DataType.INT)) {
            this.setIntValue(json.getLong("value"));
         } else if (json.isType("value", DataType.FLOAT)) {
            this.setDoubleValue(json.getDouble("value"));
         } else {
            this.setStringValue(json.getString("value"));
         }

         this.setNameLocalizations(LocalizationUtils.mapFromProperty(json, "name_localizations"));
      }

      @Nonnull
      public String getName() {
         return this.name;
      }

      @Nonnull
      public Choice setName(@Nonnull String name) {
         this.checkName(name);
         this.name = name;
         return this;
      }

      @Nonnull
      public LocalizationMap getNameLocalizations() {
         return this.nameLocalizations;
      }

      @Nonnull
      public Choice setNameLocalization(@Nonnull DiscordLocale locale, @Nonnull String name) {
         this.nameLocalizations.setTranslation(locale, name);
         return this;
      }

      @Nonnull
      public Choice setNameLocalizations(@Nonnull Map<DiscordLocale, String> map) {
         this.nameLocalizations.setTranslations(map);
         return this;
      }

      public double getAsDouble() {
         return this.doubleValue;
      }

      public long getAsLong() {
         return this.intValue;
      }

      @Nonnull
      public String getAsString() {
         return this.stringValue;
      }

      @Nonnull
      public OptionType getType() {
         return this.type;
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.stringValue});
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Choice)) {
            return false;
         } else {
            Choice other = (Choice)obj;
            return Objects.equals(other.name, this.name) && Objects.equals(other.stringValue, this.stringValue);
         }
      }

      public String toString() {
         return (new EntityString(this)).setName(this.name).addMetadata("value", this.stringValue).toString();
      }

      private void setIntValue(long value) {
         this.doubleValue = (double)value;
         this.intValue = value;
         this.stringValue = Long.toString(value);
         this.type = OptionType.INTEGER;
      }

      private void setDoubleValue(double value) {
         this.doubleValue = value;
         this.intValue = (long)value;
         this.stringValue = Double.toString(value);
         this.type = OptionType.NUMBER;
      }

      private void setStringValue(@Nonnull String value) {
         Checks.notLonger(value, 100, "Choice string value");
         this.doubleValue = Double.NaN;
         this.intValue = 0L;
         this.stringValue = value;
         this.type = OptionType.STRING;
      }

      private void checkName(@Nonnull String name) {
         Checks.notEmpty(name, "Choice name");
         Checks.notLonger(name, 100, "Choice name");
      }

      @Nonnull
      public DataObject toData(@Nonnull OptionType optionType) {
         Object value;
         if (optionType == OptionType.INTEGER) {
            value = this.getAsLong();
         } else if (optionType == OptionType.STRING) {
            value = this.getAsString();
         } else {
            if (optionType != OptionType.NUMBER) {
               throw new IllegalArgumentException("Cannot transform choice into data for type " + optionType);
            }

            value = this.getAsDouble();
         }

         return DataObject.empty().put("name", this.name).put("value", value).put("name_localizations", this.nameLocalizations);
      }
   }

   public static class Option {
      private final String name;
      private final String description;
      private final LocalizationMap nameLocalizations;
      private final LocalizationMap descriptionLocalizations;
      private final int type;
      private final boolean required;
      private final boolean autoComplete;
      private final Set<ChannelType> channelTypes;
      private final List<Choice> choices;
      private Number minValue;
      private Number maxValue;
      private Integer minLength;
      private Integer maxLength;

      public Option(@Nonnull DataObject json) {
         this.name = json.getString("name");
         this.nameLocalizations = LocalizationUtils.unmodifiableFromProperty(json, "name_localizations");
         this.description = json.getString("description");
         this.descriptionLocalizations = LocalizationUtils.unmodifiableFromProperty(json, "description_localizations");
         this.type = json.getInt("type");
         this.required = json.getBoolean("required");
         this.autoComplete = json.getBoolean("autocomplete");
         this.channelTypes = Collections.unmodifiableSet((Set)json.optArray("channel_types").map((it) -> (Set)it.stream(DataArray::getInt).map(ChannelType::fromId).collect(Collectors.toSet())).orElse(Collections.emptySet()));
         this.choices = (List)json.optArray("choices").map((it) -> (List)it.stream(DataArray::getObject).map(Choice::new).collect(Collectors.toList())).orElse(Collections.emptyList());
         if (!json.isNull("min_value")) {
            this.minValue = json.getDouble("min_value");
         }

         if (!json.isNull("max_value")) {
            this.maxValue = json.getDouble("max_value");
         }

         if (!json.isNull("min_length")) {
            this.minLength = json.getInt("min_length");
         }

         if (!json.isNull("max_length")) {
            this.maxLength = json.getInt("max_length");
         }

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

      public int getTypeRaw() {
         return this.type;
      }

      public boolean isRequired() {
         return this.required;
      }

      public boolean isAutoComplete() {
         return this.autoComplete;
      }

      @Nonnull
      public OptionType getType() {
         return OptionType.fromKey(this.type);
      }

      @Nonnull
      public @Unmodifiable Set<ChannelType> getChannelTypes() {
         return this.channelTypes;
      }

      @Nullable
      public Number getMinValue() {
         return this.minValue;
      }

      @Nullable
      public Number getMaxValue() {
         return this.maxValue;
      }

      @Nullable
      public Integer getMinLength() {
         return this.minLength;
      }

      @Nullable
      public Integer getMaxLength() {
         return this.maxLength;
      }

      @Nonnull
      public @Unmodifiable List<Choice> getChoices() {
         return this.choices;
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.description, this.type, this.choices, this.channelTypes, this.minValue, this.maxValue, this.minLength, this.maxLength, this.required, this.autoComplete});
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Option)) {
            return false;
         } else {
            Option other = (Option)obj;
            return Objects.equals(other.name, this.name) && Objects.equals(other.description, this.description) && Objects.equals(other.choices, this.choices) && Objects.equals(other.channelTypes, this.channelTypes) && Objects.equals(other.minValue, this.minValue) && Objects.equals(other.maxValue, this.maxValue) && Objects.equals(other.minLength, this.minLength) && Objects.equals(other.maxLength, this.maxLength) && other.required == this.required && other.autoComplete == this.autoComplete && other.type == this.type;
         }
      }

      public String toString() {
         return (new EntityString(this)).setType(this.getType()).addMetadata("name", this.name).toString();
      }
   }

   public static class Subcommand implements ICommandReference {
      private final ICommandReference parentCommand;
      private final String name;
      private final String description;
      private final LocalizationMap nameLocalizations;
      private final LocalizationMap descriptionLocalizations;
      private final List<Option> options;

      public Subcommand(ICommandReference parentCommand, DataObject json) {
         this.parentCommand = parentCommand;
         this.name = json.getString("name");
         this.nameLocalizations = LocalizationUtils.unmodifiableFromProperty(json, "name_localizations");
         this.description = json.getString("description");
         this.descriptionLocalizations = LocalizationUtils.unmodifiableFromProperty(json, "description_localizations");
         this.options = CommandImpl.<Option>parseOptions(json, CommandImpl.OPTION_TEST, Option::new);
      }

      public long getIdLong() {
         return this.parentCommand.getIdLong();
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
         return this.parentCommand.getFullCommandName() + " " + this.getName();
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
      public @Unmodifiable List<Option> getOptions() {
         return this.options;
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.description, this.options});
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Subcommand)) {
            return false;
         } else {
            Subcommand other = (Subcommand)obj;
            return Objects.equals(other.name, this.name) && Objects.equals(other.description, this.description) && Objects.equals(other.options, this.options);
         }
      }

      public String toString() {
         return (new EntityString(this)).addMetadata("name", this.name).toString();
      }
   }

   public static class SubcommandGroup implements ICommandReference {
      private final Command parentCommand;
      private final String name;
      private final String description;
      private final LocalizationMap nameLocalizations;
      private final LocalizationMap descriptionLocalizations;
      private final List<Subcommand> subcommands;

      public SubcommandGroup(Command parentCommand, DataObject json) {
         this.parentCommand = parentCommand;
         this.name = json.getString("name");
         this.nameLocalizations = LocalizationUtils.unmodifiableFromProperty(json, "name_localizations");
         this.description = json.getString("description");
         this.descriptionLocalizations = LocalizationUtils.unmodifiableFromProperty(json, "description_localizations");
         this.subcommands = CommandImpl.<Subcommand>parseOptions(json, CommandImpl.SUBCOMMAND_TEST, (o) -> new Subcommand(this, o));
      }

      public long getIdLong() {
         return this.parentCommand.getIdLong();
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
         return this.parentCommand.getFullCommandName() + " " + this.getName();
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
      public @Unmodifiable List<Subcommand> getSubcommands() {
         return this.subcommands;
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.description, this.subcommands});
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof SubcommandGroup)) {
            return false;
         } else {
            SubcommandGroup other = (SubcommandGroup)obj;
            return Objects.equals(other.name, this.name) && Objects.equals(other.description, this.description) && Objects.equals(other.subcommands, this.subcommands);
         }
      }

      public String toString() {
         return (new EntityString(this)).addMetadata("name", this.name).toString();
      }
   }
}
