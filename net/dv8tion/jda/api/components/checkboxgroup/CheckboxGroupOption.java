package net.dv8tion.jda.api.components.checkboxgroup;

import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public final class CheckboxGroupOption implements SerializableData {
   public static final int LABEL_MAX_LENGTH = 100;
   public static final int VALUE_MAX_LENGTH = 100;
   public static final int DESCRIPTION_MAX_LENGTH = 100;
   private final String label;
   private final String value;
   private final String description;
   private final boolean isDefault;

   private CheckboxGroupOption(String label, String value, String description, boolean isDefault) {
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
   public static CheckboxGroupOption of(@Nonnull String label, @Nonnull String value) {
      return new CheckboxGroupOption(label, value, (String)null, false);
   }

   @Nonnull
   public static CheckboxGroupOption of(@Nonnull String label, @Nonnull String value, @Nullable String description) {
      return new CheckboxGroupOption(label, value, description, false);
   }

   @Nonnull
   public static CheckboxGroupOption of(@Nonnull String label, @Nonnull String value, @Nullable String description, boolean isDefault) {
      return new CheckboxGroupOption(label, value, description, isDefault);
   }

   @Nonnull
   @CheckReturnValue
   public CheckboxGroupOption withLabel(@Nonnull String label) {
      return new CheckboxGroupOption(label, this.value, this.description, this.isDefault);
   }

   @Nonnull
   @CheckReturnValue
   public CheckboxGroupOption withValue(@Nonnull String value) {
      return new CheckboxGroupOption(this.label, value, this.description, this.isDefault);
   }

   @Nonnull
   @CheckReturnValue
   public CheckboxGroupOption withDescription(@Nullable String description) {
      return new CheckboxGroupOption(this.label, this.value, description, this.isDefault);
   }

   @Nonnull
   @CheckReturnValue
   public CheckboxGroupOption withDefault(boolean isDefault) {
      return new CheckboxGroupOption(this.label, this.value, this.description, isDefault);
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
   public static CheckboxGroupOption fromData(@Nonnull DataObject data) {
      Checks.notNull(data, "DataObject");
      return new CheckboxGroupOption(data.getString("label"), data.getString("value"), data.getString("description", (String)null), data.getBoolean("default", false));
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
      } else if (!(o instanceof CheckboxGroupOption)) {
         return false;
      } else {
         CheckboxGroupOption that = (CheckboxGroupOption)o;
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
