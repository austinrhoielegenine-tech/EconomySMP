package net.dv8tion.jda.api.entities;

import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.localization.LocalizationUtils;

public class RoleConnectionMetadata implements SerializableData {
   public static final int MAX_NAME_LENGTH = 100;
   public static final int MAX_DESCRIPTION_LENGTH = 200;
   public static final int MAX_KEY_LENGTH = 50;
   public static final int MAX_RECORDS = 5;
   private final MetadataType type;
   private final String key;
   private final String name;
   private final String description;
   private final LocalizationMap nameLocalization = new LocalizationMap(RoleConnectionMetadata::checkName);
   private final LocalizationMap descriptionLocalization = new LocalizationMap(RoleConnectionMetadata::checkDescription);

   public RoleConnectionMetadata(@Nonnull MetadataType type, @Nonnull String name, @Nonnull String key, @Nonnull String description) {
      Checks.check(type != RoleConnectionMetadata.MetadataType.UNKNOWN, "Type must not be UNKNOWN");
      Checks.notNull(type, "Type");
      Checks.notNull(key, "Key");
      Checks.inRange(key, 1, 50, "Key");
      Checks.matches(key, Checks.LOWERCASE_ASCII_ALPHANUMERIC, "Key");
      checkName(name);
      checkDescription(description);
      this.type = type;
      this.name = name;
      this.key = key;
      this.description = description;
   }

   private static void checkName(String name) {
      Checks.notNull(name, "Name");
      Checks.inRange(name, 1, 100, "Name");
   }

   private static void checkDescription(String description) {
      Checks.notNull(description, "Description");
      Checks.inRange(description, 1, 200, "Description");
   }

   @Nonnull
   public MetadataType getType() {
      return this.type;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public String getKey() {
      return this.key;
   }

   @Nonnull
   public String getDescription() {
      return this.description;
   }

   @Nonnull
   public LocalizationMap getNameLocalizations() {
      return this.nameLocalization;
   }

   @Nonnull
   public LocalizationMap getDescriptionLocalizations() {
      return this.descriptionLocalization;
   }

   @Nonnull
   public RoleConnectionMetadata setNameLocalization(@Nonnull DiscordLocale locale, @Nonnull String name) {
      this.nameLocalization.setTranslation(locale, name);
      return this;
   }

   @Nonnull
   public RoleConnectionMetadata setNameLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.nameLocalization.setTranslations(map);
      return this;
   }

   @Nonnull
   public RoleConnectionMetadata setDescriptionLocalization(@Nonnull DiscordLocale locale, @Nonnull String description) {
      this.descriptionLocalization.setTranslation(locale, description);
      return this;
   }

   @Nonnull
   public RoleConnectionMetadata setDescriptionLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.descriptionLocalization.setTranslations(map);
      return this;
   }

   public String toString() {
      return (new EntityString(this)).setType(this.type).setName(this.name).addMetadata("key", this.key).toString();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof RoleConnectionMetadata)) {
         return false;
      } else if (this == obj) {
         return true;
      } else {
         RoleConnectionMetadata o = (RoleConnectionMetadata)obj;
         return this.type == o.type && this.key.equals(o.key) && this.name.equals(o.name) && this.description.equals(o.description);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.type, this.key, this.name, this.description});
   }

   @Nonnull
   public DataObject toData() {
      return DataObject.empty().put("type", this.type.value).put("name", this.name).put("key", this.key).put("description", this.description).put("name_localizations", this.nameLocalization).put("description_localizations", this.descriptionLocalization);
   }

   @Nonnull
   public static RoleConnectionMetadata fromData(@Nonnull DataObject data) {
      Checks.notNull(data, "Data");
      RoleConnectionMetadata metadata = new RoleConnectionMetadata(RoleConnectionMetadata.MetadataType.fromValue(data.getInt("type")), data.getString("name", (String)null), data.getString("key", (String)null), data.getString("description", (String)null));
      return metadata.setNameLocalizations(LocalizationUtils.mapFromProperty(data, "name_localizations")).setDescriptionLocalizations(LocalizationUtils.mapFromProperty(data, "description_localizations"));
   }

   public static enum MetadataType {
      INTEGER_LESS_THAN_OR_EQUAL(1),
      INTEGER_GREATER_THAN_OR_EQUAL(2),
      INTEGER_EQUALS(3),
      INTEGER_NOT_EQUALS(4),
      DATETIME_LESS_THAN_OR_EQUAL(5),
      DATETIME_GREATER_THAN_OR_EQUAL(6),
      BOOLEAN_EQUAL(7),
      BOOLEAN_NOT_EQUAL(8),
      UNKNOWN(-1);

      private final int value;

      private MetadataType(int value) {
         this.value = value;
      }

      public int getValue() {
         return this.value;
      }

      @Nonnull
      public static MetadataType fromValue(int value) {
         for(MetadataType type : values()) {
            if (type.value == value) {
               return type;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static MetadataType[] $values() {
         return new MetadataType[]{INTEGER_LESS_THAN_OR_EQUAL, INTEGER_GREATER_THAN_OR_EQUAL, INTEGER_EQUALS, INTEGER_NOT_EQUALS, DATETIME_LESS_THAN_OR_EQUAL, DATETIME_GREATER_THAN_OR_EQUAL, BOOLEAN_EQUAL, BOOLEAN_NOT_EQUAL, UNKNOWN};
      }
   }
}
