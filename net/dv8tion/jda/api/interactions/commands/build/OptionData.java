package net.dv8tion.jda.api.interactions.commands.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.DataType;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.localization.LocalizationUtils;
import org.jetbrains.annotations.Unmodifiable;

public class OptionData implements SerializableData {
   public static final double MAX_POSITIVE_NUMBER = 9.007199254740991E15;
   public static final double MIN_NEGATIVE_NUMBER = -9.007199254740991E15;
   public static final int MAX_NAME_LENGTH = 32;
   public static final int MAX_CHOICE_NAME_LENGTH = 100;
   public static final int MAX_DESCRIPTION_LENGTH = 100;
   public static final int MAX_CHOICE_VALUE_LENGTH = 100;
   public static final int MAX_CHOICES = 25;
   public static final int MAX_STRING_OPTION_LENGTH = 6000;
   private final OptionType type;
   private String name;
   private String description;
   private final LocalizationMap nameLocalizations;
   private final LocalizationMap descriptionLocalizations;
   private boolean isRequired;
   private boolean isAutoComplete;
   private final EnumSet<ChannelType> channelTypes;
   private Number minValue;
   private Number maxValue;
   private Integer minLength;
   private Integer maxLength;
   private List<Command.Choice> choices;

   public OptionData(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description) {
      this(type, name, description, false);
   }

   public OptionData(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean isRequired) {
      this(type, name, description, isRequired, false);
   }

   public OptionData(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean isRequired, boolean isAutoComplete) {
      this.nameLocalizations = new LocalizationMap(this::checkName);
      this.descriptionLocalizations = new LocalizationMap(this::checkDescription);
      this.channelTypes = EnumSet.noneOf(ChannelType.class);
      Checks.notNull(type, "Type");
      Checks.check(type != OptionType.UNKNOWN, "Cannot make option of unknown type!");
      Checks.check(type != OptionType.SUB_COMMAND, "Cannot make a subcommand group with OptionData. Use addSubcommands(...) instead!");
      Checks.check(type != OptionType.SUB_COMMAND_GROUP, "Cannot make a subcommand group with OptionData. Use addSubcommandGroups(...) instead!");
      this.type = type;
      this.setName(name);
      this.setDescription(description);
      this.setRequired(isRequired);
      if (type.canSupportChoices()) {
         this.choices = new ArrayList();
      }

      this.setAutoComplete(isAutoComplete);
   }

   protected void checkName(@Nonnull String name) {
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 32, "Name");
      Checks.isLowercase(name, "Name");
      Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Name");
   }

   protected void checkDescription(@Nonnull String description) {
      Checks.notEmpty(description, "Description");
      Checks.notLonger(description, 100, "Description");
   }

   @Nonnull
   public OptionType getType() {
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
   public String getDescription() {
      return this.description;
   }

   @Nonnull
   public LocalizationMap getDescriptionLocalizations() {
      return this.descriptionLocalizations;
   }

   public boolean isRequired() {
      return this.isRequired;
   }

   public boolean isAutoComplete() {
      return this.isAutoComplete;
   }

   @Nonnull
   public EnumSet<ChannelType> getChannelTypes() {
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
   public @Unmodifiable List<Command.Choice> getChoices() {
      return this.choices != null && !this.choices.isEmpty() ? Collections.unmodifiableList(this.choices) : Collections.emptyList();
   }

   @Nonnull
   public OptionData setName(@Nonnull String name) {
      this.checkName(name);
      this.name = name;
      return this;
   }

   @Nonnull
   public OptionData setNameLocalization(@Nonnull DiscordLocale locale, @Nonnull String name) {
      this.nameLocalizations.setTranslation(locale, name);
      return this;
   }

   @Nonnull
   public OptionData setNameLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.nameLocalizations.setTranslations(map);
      return this;
   }

   @Nonnull
   public OptionData setDescription(@Nonnull String description) {
      this.checkDescription(description);
      this.description = description;
      return this;
   }

   @Nonnull
   public OptionData setDescriptionLocalization(@Nonnull DiscordLocale locale, @Nonnull String description) {
      this.descriptionLocalizations.setTranslation(locale, description);
      return this;
   }

   @Nonnull
   public OptionData setDescriptionLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.descriptionLocalizations.setTranslations(map);
      return this;
   }

   @Nonnull
   public OptionData setRequired(boolean required) {
      this.isRequired = required;
      return this;
   }

   @Nonnull
   public OptionData setAutoComplete(boolean autoComplete) {
      if (autoComplete) {
         if (this.choices == null || !this.type.canSupportChoices()) {
            throw new IllegalStateException("Cannot enable auto-complete for options of type " + this.type);
         }

         if (!this.choices.isEmpty()) {
            throw new IllegalStateException("Cannot enable auto-complete for options with choices");
         }
      }

      this.isAutoComplete = autoComplete;
      return this;
   }

   @Nonnull
   public OptionData setChannelTypes(@Nonnull ChannelType... channelTypes) {
      Checks.noneNull(channelTypes, "ChannelTypes");
      return this.setChannelTypes((Collection)Arrays.asList(channelTypes));
   }

   @Nonnull
   public OptionData setChannelTypes(@Nonnull Collection<ChannelType> channelTypes) {
      if (this.type != OptionType.CHANNEL) {
         throw new IllegalArgumentException("Can only apply channel type restriction to options of type CHANNEL");
      } else {
         Checks.notNull(channelTypes, "ChannelType collection");
         Checks.noneNull(channelTypes, "ChannelType");

         for(ChannelType channelType : channelTypes) {
            if (!channelType.isGuild()) {
               throw new IllegalArgumentException("Provided channel type is not a guild channel type. Provided: " + channelType);
            }
         }

         this.channelTypes.clear();
         this.channelTypes.addAll(channelTypes);
         return this;
      }
   }

   @Nonnull
   public OptionData setMinValue(long value) {
      if (this.type != OptionType.INTEGER && this.type != OptionType.NUMBER) {
         throw new IllegalArgumentException("Can only set min and max long value for options of type INTEGER or NUMBER");
      } else {
         Checks.check((double)value >= -9.007199254740991E15, "Long value may not be less than %f", (Object)-9.007199254740991E15);
         this.minValue = value;
         return this;
      }
   }

   @Nonnull
   public OptionData setMinValue(double value) {
      if (this.type != OptionType.NUMBER) {
         throw new IllegalArgumentException("Can only set min double value for options of type NUMBER");
      } else {
         Checks.check(value >= -9.007199254740991E15, "Double value may not be less than %f", (Object)-9.007199254740991E15);
         this.minValue = value;
         return this;
      }
   }

   @Nonnull
   public OptionData setMaxValue(long value) {
      if (this.type != OptionType.INTEGER && this.type != OptionType.NUMBER) {
         throw new IllegalArgumentException("Can only set min and max long value for options of type INTEGER or NUMBER");
      } else {
         Checks.check((double)value <= 9.007199254740991E15, "Long value may not be greater than %f", (Object)9.007199254740991E15);
         this.maxValue = value;
         return this;
      }
   }

   @Nonnull
   public OptionData setMaxValue(double value) {
      if (this.type != OptionType.NUMBER) {
         throw new IllegalArgumentException("Can only set max double value for options of type NUMBER");
      } else {
         Checks.check(value <= 9.007199254740991E15, "Double value may not be greater than %f", (Object)9.007199254740991E15);
         this.maxValue = value;
         return this;
      }
   }

   @Nonnull
   public OptionData setRequiredRange(long minValue, long maxValue) {
      if (this.type != OptionType.INTEGER && this.type != OptionType.NUMBER) {
         throw new IllegalArgumentException("Can only set min and max long value for options of type INTEGER or NUMBER");
      } else {
         Checks.check((double)minValue >= -9.007199254740991E15, "Long value may not be less than %f", (Object)-9.007199254740991E15);
         Checks.check((double)maxValue <= 9.007199254740991E15, "Long value may not be greater than %f", (Object)9.007199254740991E15);
         this.minValue = minValue;
         this.maxValue = maxValue;
         return this;
      }
   }

   @Nonnull
   public OptionData setRequiredRange(double minValue, double maxValue) {
      if (this.type != OptionType.NUMBER) {
         throw new IllegalArgumentException("Can only set min and max double value for options of type NUMBER");
      } else {
         Checks.check(minValue >= -9.007199254740991E15, "Double value may not be less than %f", (Object)-9.007199254740991E15);
         Checks.check(maxValue <= 9.007199254740991E15, "Double value may not be greater than %f", (Object)9.007199254740991E15);
         this.minValue = minValue;
         this.maxValue = maxValue;
         return this;
      }
   }

   @Nonnull
   public OptionData setMinLength(int minLength) {
      if (this.type != OptionType.STRING) {
         throw new IllegalArgumentException("Can only set min length for options of type STRING");
      } else {
         Checks.positive(minLength, "Min length");
         this.minLength = minLength;
         return this;
      }
   }

   @Nonnull
   public OptionData setMaxLength(int maxLength) {
      if (this.type != OptionType.STRING) {
         throw new IllegalArgumentException("Can only set max length for options of type STRING");
      } else {
         Checks.positive(maxLength, "Max length");
         Checks.check(maxLength <= 6000, "Max length must not be greater than %d. Provided: %d", 6000, maxLength);
         this.maxLength = maxLength;
         return this;
      }
   }

   @Nonnull
   public OptionData setRequiredLength(int minLength, int maxLength) {
      if (this.type != OptionType.STRING) {
         throw new IllegalArgumentException("Can only set min and max length for options of type STRING");
      } else {
         Checks.check(minLength <= maxLength, "Min length must not be greater than max length. Provided: %d > %d", minLength, maxLength);
         this.setMinLength(minLength);
         this.setMaxLength(maxLength);
         return this;
      }
   }

   @Nonnull
   public OptionData addChoice(@Nonnull String name, double value) {
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      Checks.check(value >= -9.007199254740991E15, "Double value may not be less than %f", (Object)-9.007199254740991E15);
      Checks.check(value <= 9.007199254740991E15, "Double value may not be greater than %f", (Object)9.007199254740991E15);
      if (this.isAutoComplete) {
         throw new IllegalStateException("Cannot add choices to auto-complete options");
      } else if (this.type != OptionType.NUMBER) {
         throw new IllegalArgumentException("Cannot add double choice for OptionType." + this.type);
      } else {
         Checks.check(this.choices.size() < 25, "Cannot have more than 25 choices for an option!");
         this.choices.add(new Command.Choice(name, value));
         return this;
      }
   }

   @Nonnull
   public OptionData addChoice(@Nonnull String name, long value) {
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      Checks.check((double)value >= -9.007199254740991E15, "Long value may not be less than %f", (Object)-9.007199254740991E15);
      Checks.check((double)value <= 9.007199254740991E15, "Long value may not be greater than %f", (Object)9.007199254740991E15);
      if (this.isAutoComplete) {
         throw new IllegalStateException("Cannot add choices to auto-complete options");
      } else if (this.type != OptionType.INTEGER) {
         throw new IllegalArgumentException("Cannot add long choice for OptionType." + this.type);
      } else {
         Checks.check(this.choices.size() < 25, "Cannot have more than 25 choices for an option!");
         this.choices.add(new Command.Choice(name, value));
         return this;
      }
   }

   @Nonnull
   public OptionData addChoice(@Nonnull String name, @Nonnull String value) {
      Checks.notEmpty(name, "Name");
      Checks.notEmpty(value, "Value");
      Checks.notLonger(name, 100, "Name");
      Checks.notLonger(value, 100, "Value");
      if (this.isAutoComplete) {
         throw new IllegalStateException("Cannot add choices to auto-complete options");
      } else if (this.type != OptionType.STRING) {
         throw new IllegalArgumentException("Cannot add string choice for OptionType." + this.type);
      } else {
         Checks.check(this.choices.size() < 25, "Cannot have more than 25 choices for an option!");
         this.choices.add(new Command.Choice(name, value));
         return this;
      }
   }

   @Nonnull
   public OptionData addChoices(@Nonnull Command.Choice... choices) {
      Checks.noneNull(choices, "Choices");
      return this.addChoices((Collection)Arrays.asList(choices));
   }

   @Nonnull
   public OptionData addChoices(@Nonnull Collection<? extends Command.Choice> choices) {
      Checks.notNull(choices, "Choices");
      if (choices.size() == 0) {
         return this;
      } else if (this.choices != null && this.type.canSupportChoices()) {
         Checks.noneNull(choices, "Choices");
         if (this.isAutoComplete) {
            throw new IllegalStateException("Cannot add choices to auto-complete options");
         } else {
            Checks.check(choices.size() + this.choices.size() <= 25, "Cannot have more than 25 choices for one option!");
            this.choices.addAll(choices);
            return this;
         }
      } else {
         throw new IllegalStateException("Cannot add choices for an option of type " + this.type);
      }
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty().put("type", this.type.getKey()).put("name", this.name).put("name_localizations", this.nameLocalizations).put("description", this.description).put("description_localizations", this.descriptionLocalizations);
      if (this.type != OptionType.SUB_COMMAND && this.type != OptionType.SUB_COMMAND_GROUP) {
         json.put("required", this.isRequired);
         json.put("autocomplete", this.isAutoComplete);
      }

      if (this.choices != null && !this.choices.isEmpty()) {
         json.put("choices", DataArray.fromCollection((Collection)this.choices.stream().map((choice) -> choice.toData(this.type)).collect(Collectors.toList())));
      }

      if (this.type == OptionType.CHANNEL && !this.channelTypes.isEmpty()) {
         json.put("channel_types", this.channelTypes.stream().map(ChannelType::getId).collect(Collectors.toList()));
      }

      if (this.type == OptionType.INTEGER || this.type == OptionType.NUMBER) {
         if (this.minValue != null) {
            json.put("min_value", this.minValue);
         }

         if (this.maxValue != null) {
            json.put("max_value", this.maxValue);
         }
      }

      if (this.type == OptionType.STRING) {
         if (this.minLength != null) {
            json.put("min_length", this.minLength);
         }

         if (this.maxLength != null) {
            json.put("max_length", this.maxLength);
         }
      }

      return json;
   }

   @Nonnull
   public static OptionData fromData(@Nonnull DataObject json) {
      String name = json.getString("name");
      String description = json.getString("description");
      OptionType type = OptionType.fromKey(json.getInt("type"));
      OptionData option = new OptionData(type, name, description);
      option.setRequired(json.getBoolean("required"));
      option.setAutoComplete(json.getBoolean("autocomplete"));
      if (type == OptionType.INTEGER || type == OptionType.NUMBER) {
         if (!json.isNull("min_value")) {
            if (json.isType("min_value", DataType.INT)) {
               option.setMinValue(json.getLong("min_value"));
            } else if (json.isType("min_value", DataType.FLOAT)) {
               option.setMinValue(json.getDouble("min_value"));
            }
         }

         if (!json.isNull("max_value")) {
            if (json.isType("max_value", DataType.INT)) {
               option.setMaxValue(json.getLong("max_value"));
            } else if (json.isType("max_value", DataType.FLOAT)) {
               option.setMaxValue(json.getDouble("max_value"));
            }
         }
      }

      if (type == OptionType.CHANNEL) {
         option.setChannelTypes((Collection)json.optArray("channel_types").map((it) -> (Set)it.stream(DataArray::getInt).map(ChannelType::fromId).collect(Collectors.toSet())).orElse(Collections.emptySet()));
      }

      if (type == OptionType.STRING) {
         if (!json.isNull("min_length")) {
            option.setMinLength(json.getInt("min_length"));
         }

         if (!json.isNull("max_length")) {
            option.setMaxLength(json.getInt("max_length"));
         }
      }

      json.optArray("choices").ifPresent((choices1) -> option.addChoices((Collection)choices1.stream(DataArray::getObject).map(Command.Choice::new).collect(Collectors.toList())));
      option.setNameLocalizations(LocalizationUtils.mapFromProperty(json, "name_localizations"));
      option.setDescriptionLocalizations(LocalizationUtils.mapFromProperty(json, "description_localizations"));
      return option;
   }

   @Nonnull
   public static OptionData fromOption(@Nonnull Command.Option option) {
      Checks.notNull(option, "Option");
      OptionData data = new OptionData(option.getType(), option.getName(), option.getDescription());
      data.setRequired(option.isRequired());
      data.setAutoComplete(option.isAutoComplete());
      data.addChoices((Collection)option.getChoices());
      data.setNameLocalizations(option.getNameLocalizations().toMap());
      data.setDescriptionLocalizations(option.getDescriptionLocalizations().toMap());
      Number min = option.getMinValue();
      Number max = option.getMaxValue();
      Integer minLength = option.getMinLength();
      Integer maxLength = option.getMaxLength();
      switch (option.getType()) {
         case CHANNEL:
            data.setChannelTypes((Collection)option.getChannelTypes());
            break;
         case NUMBER:
            if (min != null) {
               data.setMinValue(min.doubleValue());
            }

            if (max != null) {
               data.setMaxValue(max.doubleValue());
            }
            break;
         case INTEGER:
            if (min != null) {
               data.setMinValue(min.longValue());
            }

            if (max != null) {
               data.setMaxValue(max.longValue());
            }
            break;
         case STRING:
            if (minLength != null) {
               data.setMinLength(minLength);
            }

            if (maxLength != null) {
               data.setMaxLength(maxLength);
            }
      }

      return data;
   }
}
