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

public class SubcommandGroupData implements SerializableData {
   private final List<SubcommandData> subcommands = new ArrayList(25);
   private String name;
   private String description;
   private final LocalizationMap nameLocalizations = new LocalizationMap(this::checkName);
   private final LocalizationMap descriptionLocalizations = new LocalizationMap(this::checkDescription);

   public SubcommandGroupData(@Nonnull String name, @Nonnull String description) {
      Checks.inRange(name, 1, 32, "Name");
      Checks.isLowercase(name, "Name");
      Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Name");
      Checks.inRange(description, 1, 100, "Description");
      this.name = name;
      this.description = description;
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
   public SubcommandGroupData setName(@Nonnull String name) {
      this.checkName(name);
      this.name = name;
      return this;
   }

   @Nonnull
   public SubcommandGroupData setNameLocalization(@Nonnull DiscordLocale locale, @Nonnull String name) {
      this.nameLocalizations.setTranslation(locale, name);
      return this;
   }

   @Nonnull
   public SubcommandGroupData setNameLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.nameLocalizations.setTranslations(map);
      return this;
   }

   @Nonnull
   public SubcommandGroupData setDescription(@Nonnull String description) {
      this.checkDescription(description);
      this.description = description;
      return this;
   }

   @Nonnull
   public SubcommandGroupData setDescriptionLocalization(@Nonnull DiscordLocale locale, @Nonnull String description) {
      this.descriptionLocalizations.setTranslation(locale, description);
      return this;
   }

   @Nonnull
   public SubcommandGroupData setDescriptionLocalizations(@Nonnull Map<DiscordLocale, String> map) {
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

   public boolean removeSubcommand(@Nonnull Predicate<? super SubcommandData> condition) {
      Checks.notNull(condition, "Condition");
      return this.subcommands.removeIf(condition);
   }

   public boolean removeSubcommandByName(@Nonnull String name) {
      return this.removeSubcommand((subcommand) -> subcommand.getName().equals(name));
   }

   @Nonnull
   public @Unmodifiable List<SubcommandData> getSubcommands() {
      return Collections.unmodifiableList(this.subcommands);
   }

   @Nonnull
   public SubcommandGroupData addSubcommands(@Nonnull SubcommandData... subcommands) {
      Checks.noneNull(subcommands, "Subcommand");
      Checks.check(subcommands.length + this.subcommands.size() <= 25, "Cannot have more than %d subcommands in one group!", (int)25);
      Checks.checkUnique(Stream.concat(this.getSubcommands().stream(), Arrays.stream(subcommands)).map(SubcommandData::getName), "Cannot have multiple subcommands with the same name. Name: \"%s\" appeared %d times!", (count, value) -> new Object[]{value, count});
      this.subcommands.addAll(Arrays.asList(subcommands));
      return this;
   }

   @Nonnull
   public SubcommandGroupData addSubcommands(@Nonnull Collection<? extends SubcommandData> subcommands) {
      Checks.noneNull(subcommands, "Subcommands");
      return this.addSubcommands((SubcommandData[])subcommands.toArray(new SubcommandData[0]));
   }

   @Nonnull
   public DataObject toData() {
      return DataObject.empty().put("type", OptionType.SUB_COMMAND_GROUP.getKey()).put("name", this.name).put("name_localizations", this.nameLocalizations).put("description", this.description).put("description_localizations", this.descriptionLocalizations).put("options", DataArray.fromCollection(this.subcommands));
   }

   @Nonnull
   public static SubcommandGroupData fromData(@Nonnull DataObject json) {
      String name = json.getString("name");
      String description = json.getString("description");
      SubcommandGroupData group = new SubcommandGroupData(name, description);
      json.optArray("options").ifPresent((arr) -> {
         Stream var10000 = arr.stream(DataArray::getObject).map(SubcommandData::fromData);
         Objects.requireNonNull(group);
         var10000.forEach((xva$0) -> group.addSubcommands(xva$0));
      });
      group.setNameLocalizations(LocalizationUtils.mapFromProperty(json, "name_localizations"));
      group.setDescriptionLocalizations(LocalizationUtils.mapFromProperty(json, "description_localizations"));
      return group;
   }

   @Nonnull
   public static SubcommandGroupData fromGroup(@Nonnull Command.SubcommandGroup group) {
      Checks.notNull(group, "Subcommand Group");
      SubcommandGroupData data = new SubcommandGroupData(group.getName(), group.getDescription());
      data.setNameLocalizations(group.getNameLocalizations().toMap());
      data.setDescriptionLocalizations(group.getDescriptionLocalizations().toMap());
      Stream var10000 = group.getSubcommands().stream().map(SubcommandData::fromSubcommand);
      Objects.requireNonNull(data);
      var10000.forEach((xva$0) -> data.addSubcommands(xva$0));
      return data;
   }
}
