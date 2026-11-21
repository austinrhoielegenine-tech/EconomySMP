package net.dv8tion.jda.api.components.selections;

import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.Checks;

public class SelectOption implements SerializableData {
   public static final int LABEL_MAX_LENGTH = 100;
   public static final int VALUE_MAX_LENGTH = 100;
   public static final int DESCRIPTION_MAX_LENGTH = 100;
   private final String label;
   private final String value;
   private final String description;
   private final boolean isDefault;
   private final EmojiUnion emoji;

   protected SelectOption(@Nonnull String label, @Nonnull String value) {
      this(label, value, (String)null, false, (Emoji)null);
   }

   protected SelectOption(@Nonnull String label, @Nonnull String value, @Nullable String description, boolean isDefault, @Nullable Emoji emoji) {
      Checks.notEmpty(label, "Label");
      Checks.notEmpty(value, "Value");
      Checks.notLonger(label, 100, "Label");
      Checks.notLonger(value, 100, "Value");
      if (description != null) {
         Checks.notLonger(description, 100, "Description");
      }

      this.label = label;
      this.value = value;
      this.description = description;
      this.isDefault = isDefault;
      this.emoji = (EmojiUnion)emoji;
   }

   @Nonnull
   @CheckReturnValue
   public static SelectOption of(@Nonnull String label, @Nonnull String value) {
      return new SelectOption(label, value);
   }

   @Nonnull
   @CheckReturnValue
   public SelectOption withLabel(@Nonnull String label) {
      return new SelectOption(label, this.value, this.description, this.isDefault, this.emoji);
   }

   @Nonnull
   @CheckReturnValue
   public SelectOption withValue(@Nonnull String value) {
      return new SelectOption(this.label, value, this.description, this.isDefault, this.emoji);
   }

   @Nonnull
   @CheckReturnValue
   public SelectOption withDescription(@Nullable String description) {
      return new SelectOption(this.label, this.value, description, this.isDefault, this.emoji);
   }

   @Nonnull
   @CheckReturnValue
   public SelectOption withDefault(boolean isDefault) {
      return new SelectOption(this.label, this.value, this.description, isDefault, this.emoji);
   }

   @Nonnull
   @CheckReturnValue
   public SelectOption withEmoji(@Nullable Emoji emoji) {
      return new SelectOption(this.label, this.value, this.description, this.isDefault, emoji);
   }

   @Nonnull
   public String getLabel() {
      return this.label;
   }

   @Nonnull
   public String getValue() {
      return this.value;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   public boolean isDefault() {
      return this.isDefault;
   }

   @Nullable
   public EmojiUnion getEmoji() {
      return this.emoji;
   }

   @Nonnull
   public DataObject toData() {
      DataObject object = DataObject.empty();
      object.put("label", this.label);
      object.put("value", this.value);
      object.put("default", this.isDefault);
      if (this.emoji != null) {
         object.put("emoji", this.emoji);
      }

      if (this.description != null && !this.description.isEmpty()) {
         object.put("description", this.description);
      }

      return object;
   }

   @Nonnull
   @CheckReturnValue
   public static SelectOption fromData(@Nonnull DataObject data) {
      Checks.notNull(data, "DataObject");
      return new SelectOption(data.getString("label"), data.getString("value"), data.getString("description", (String)null), data.getBoolean("default", false), (Emoji)data.optObject("emoji").map(EntityBuilder::createEmoji).orElse((Object)null));
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SelectOption)) {
         return false;
      } else {
         SelectOption that = (SelectOption)o;
         return this.isDefault == that.isDefault && Objects.equals(this.label, that.label) && Objects.equals(this.value, that.value) && Objects.equals(this.description, that.description) && Objects.equals(this.emoji, that.emoji);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.label, this.value, this.description, this.isDefault, this.emoji});
   }
}
