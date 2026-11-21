package net.dv8tion.jda.api.components.radiogroup;

import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public final class RadioGroupOption implements SerializableData {
   public static final int LABEL_MAX_LENGTH = 100;
   public static final int VALUE_MAX_LENGTH = 100;
   public static final int DESCRIPTION_MAX_LENGTH = 100;
   private final String label;
   private final String value;
   private final String description;
   private final boolean isDefault;

   private RadioGroupOption(String label, String value, String description, boolean isDefault) {
      Checks.notBlank(label, "Label");
      Checks.notBlank(value, "Value");
      if (description != null) {
         Checks.notBlank(description, "Description");
      }

      this.label = label;
      this.value = value;
      this.description = description;
      this.isDefault = isDefault;
   }

   @Nonnull
   public static RadioGroupOption of(@Nonnull String label, @Nonnull String value) {
      return new RadioGroupOption(label, value, (String)null, false);
   }

   @Nonnull
   public static RadioGroupOption of(@Nonnull String label, @Nonnull String value, @Nullable String description) {
      return new RadioGroupOption(label, value, description, false);
   }

   @Nonnull
   public static RadioGroupOption of(@Nonnull String label, @Nonnull String value, @Nullable String description, boolean isDefault) {
      return new RadioGroupOption(label, value, description, isDefault);
   }

   @Nonnull
   @CheckReturnValue
   public RadioGroupOption withLabel(@Nonnull String label) {
      return new RadioGroupOption(label, this.value, this.description, this.isDefault);
   }

   @Nonnull
   @CheckReturnValue
   public RadioGroupOption withValue(@Nonnull String value) {
      return new RadioGroupOption(this.label, value, this.description, this.isDefault);
   }

   @Nonnull
   @CheckReturnValue
   public RadioGroupOption withDescription(@Nullable String description) {
      return new RadioGroupOption(this.label, this.value, description, this.isDefault);
   }

   @Nonnull
   @CheckReturnValue
   public RadioGroupOption withDefault(boolean isDefault) {
      return new RadioGroupOption(this.label, this.value, this.description, isDefault);
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

   @Nonnull
   @CheckReturnValue
   public static RadioGroupOption fromData(@Nonnull DataObject data) {
      Checks.notNull(data, "DataObject");
      return new RadioGroupOption(data.getString("label"), data.getString("value"), data.getString("description", (String)null), data.getBoolean("default", false));
   }

   @Nonnull
   public DataObject toData() {
      DataObject object = DataObject.empty().put("label", this.label).put("value", this.value);
      if (this.description != null) {
         object.put("description", this.description);
      }

      if (this.isDefault) {
         object.put("default", true);
      }

      return object;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof RadioGroupOption)) {
         return false;
      } else {
         RadioGroupOption that = (RadioGroupOption)o;
         return this.label.equals(that.label) && this.value.equals(that.value) && Objects.equals(this.description, that.description) && this.isDefault == that.isDefault;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.label, this.value, this.description, this.isDefault});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("value", this.value).toString();
   }
}
