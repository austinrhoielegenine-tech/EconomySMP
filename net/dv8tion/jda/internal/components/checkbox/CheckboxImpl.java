package net.dv8tion.jda.internal.components.checkbox;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.checkbox.Checkbox;
import net.dv8tion.jda.api.components.label.LabelChildComponentUnion;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class CheckboxImpl extends AbstractComponentImpl implements Checkbox, LabelChildComponentUnion {
   private final int uniqueId;
   private final String customId;
   private final boolean isDefault;

   public CheckboxImpl(int uniqueId, String customId, boolean isDefault) {
      this.uniqueId = uniqueId;
      this.customId = customId;
      this.isDefault = isDefault;
   }

   public CheckboxImpl(DataObject data) {
      this.uniqueId = data.getInt("id", -1);
      this.customId = data.getString("custom_id");
      this.isDefault = data.getBoolean("default", false);
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.CHECKBOX;
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getCustomId() {
      return this.customId;
   }

   public boolean isDefault() {
      return this.isDefault;
   }

   @Nonnull
   public CheckboxImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new CheckboxImpl(uniqueId, this.customId, this.isDefault);
   }

   @Nonnull
   public CheckboxImpl withCustomId(@Nonnull String customId) {
      Checks.notBlank(customId, "Custom ID");
      Checks.notLonger(customId, 100, "Custom ID");
      return new CheckboxImpl(this.uniqueId, customId, this.isDefault);
   }

   @Nonnull
   public CheckboxImpl withDefault(boolean isDefault) {
      return new CheckboxImpl(this.uniqueId, this.customId, isDefault);
   }

   @Nonnull
   public DataObject toData() {
      DataObject object = DataObject.empty().put("type", this.getType().getKey()).put("custom_id", this.customId);
      if (this.uniqueId >= 0) {
         object.put("id", this.uniqueId);
      }

      if (this.isDefault) {
         object.put("default", true);
      }

      return object;
   }

   public final boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CheckboxImpl)) {
         return false;
      } else {
         CheckboxImpl that = (CheckboxImpl)o;
         return this.uniqueId == that.uniqueId && this.customId.equals(that.customId) && this.isDefault == that.isDefault;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.customId, this.isDefault});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.customId).addMetadata("checked", this.isDefault).toString();
   }
}
