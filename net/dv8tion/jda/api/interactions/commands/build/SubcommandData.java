package net.dv8tion.jda.api.interactions.commands.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.localization.LocalizationUtils;
import org.jetbrains.annotations.Unmodifiable;

public class SubcommandData implements SerializableData {
   protected final List<OptionData> options = new ArrayList(25);
   protected String name;
   protected String description;
   private final LocalizationMap nameLocalizations = new LocalizationMap(this::checkName);
   private final LocalizationMap descriptionLocalizations = new LocalizationMap(this::checkDescription);
   private boolean allowRequired = true;

   public SubcommandData(@Nonnull String name, @Nonnull String description) {
      this.setName(name);
      this.setDescription(description);
   }

   protected void checkName(@Nonnull String name) {
      Checks.inRange(name, 1, 32, "Name");
      Checks.isLowercase(name, "Name");
      Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Name");
   }

   protected void checkDescription(@Nonnull String description) {
      Checks.inRange(description, 1, 100, "Description");
   }

   @Nonnull
   public SubcommandData setName(@Nonnull String name) {
      this.checkName(name);
      this.name = name;
      return this;
   }

   @Nonnull
   public SubcommandData setNameLocalization(@Nonnull DiscordLocale locale, @Nonnull String name) {
      this.nameLocalizations.setTranslation(locale, name);
      return this;
   }

   @Nonnull
   public SubcommandData setNameLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.nameLocalizations.setTranslations(map);
      return this;
   }

   @Nonnull
   public SubcommandData setDescription(@Nonnull String description) {
      this.checkDescription(description);
      this.description = description;
      return this;
   }

   @Nonnull
   public SubcommandData setDescriptionLocalization(@Nonnull DiscordLocale locale, @Nonnull String description) {
      this.descriptionLocalizations.setTranslation(locale, description);
      return this;
   }

   @Nonnull
   public SubcommandData setDescriptionLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.descriptionLocalizations.setTranslations(map);
      return this;
   }

   public boolean removeOptions(@Nonnull Predicate<? super OptionData> condition) {
      Checks.notNull(condition, "Condition");
      return this.options.removeIf(condition);
   }

   public boolean removeOptionByName(@Nonnull String name) {
      return this.removeOptions((option) -> option.getName().equals(name));
   }

   @Nonnull
   public SubcommandData addOptions(@Nonnull OptionData... options) {
      Checks.noneNull(options, "Option");
      Checks.check(options.length + this.options.size() <= 25, "Cannot have more than %d options for a subcommand!", (int)25);
      boolean allowRequired = this.allowRequired;

      for(OptionData option : options) {
         Checks.check(option.getType() != OptionType.SUB_COMMAND, "Cannot add a subcommand to a subcommand!");
         Checks.check(option.getType() != OptionType.SUB_COMMAND_GROUP, "Cannot add a subcommand group to a subcommand!");
         Checks.check(allowRequired || !option.isRequired(), "Cannot add required options after non-required options!");
         allowRequired = option.isRequired();
      }

      Checks.checkUnique(Stream.concat(this.getOptions().stream(), Arrays.stream(options)).map(OptionData::getName), "Cannot have multiple options with the same name. Name: \"%s\" appeared %d times!", (count, value) -> new Object[]{value, count});
      this.allowRequired = allowRequired;
      this.options.addAll(Arrays.asList(options));
      return this;
   }

   @Nonnull
   public SubcommandData addOptions(@Nonnull Collection<? extends OptionData> options) {
      Checks.noneNull(options, "Options");
      return this.addOptions((OptionData[])options.toArray(new OptionData[0]));
   }

   @Nonnull
   public SubcommandData addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean required, boolean autoComplete) {
      return this.addOptions((new OptionData(type, name, description)).setRequired(required).setAutoComplete(autoComplete));
   }

   @Nonnull
   public SubcommandData addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean required) {
      return this.addOption(type, name, description, required, false);
   }

   @Nonnull
   public SubcommandData addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description) {
      return this.addOption(type, name, description, false);
   }

   @Nonnull
   public @Unmodifiable List<OptionData> getOptions() {
      return Collections.unmodifiableList(this.options);
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

   @Nonnull
   public DataObject toData() {
      return DataObject.empty().put("type", OptionType.SUB_COMMAND.getKey()).put("name", this.name).put("name_localizations", this.nameLocalizations).put("description", this.description).put("description_localizations", this.descriptionLocalizations).put("options", DataArray.fromCollection(this.options));
   }

   @Nonnull
   public static SubcommandData fromData(@Nonnull DataObject json) {
      String name = json.getString("name");
      String description = json.getString("description");
      SubcommandData sub = new SubcommandData(name, description);
      json.optArray("options").ifPresent((arr) -> {
         Stream var10000 = arr.stream(DataArray::getObject).map(OptionData::fromData);
         Objects.requireNonNull(sub);
         var10000.forEach((xva$0) -> sub.addOptions(xva$0));
      });
      sub.setNameLocalizations(LocalizationUtils.mapFromProperty(json, "name_localizations"));
      sub.setDescriptionLocalizations(LocalizationUtils.mapFromProperty(json, "description_localizations"));
      return sub;
   }

   @Nonnull
   public static SubcommandData fromSubcommand(@Nonnull Command.Subcommand subcommand) {
      Checks.notNull(subcommand, "Subcommand");
      SubcommandData data = new SubcommandData(subcommand.getName(), subcommand.getDescription());
      data.setNameLocalizations(subcommand.getNameLocalizations().toMap());
      data.setDescriptionLocalizations(subcommand.getDescriptionLocalizations().toMap());
      Stream var10000 = subcommand.getOptions().stream().map(OptionData::fromOption);
      Objects.requireNonNull(data);
      var10000.forEach((xva$0) -> data.addOptions(xva$0));
      return data;
   }
}
