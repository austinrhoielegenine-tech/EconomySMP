package net.dv8tion.jda.internal.components.checkboxgroup;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroup;
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroupOption;
import net.dv8tion.jda.api.components.label.LabelChildComponentUnion;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public class CheckboxGroupImpl extends AbstractComponentImpl implements CheckboxGroup, LabelChildComponentUnion {
   private final int uniqueId;
   private final String customId;
   private final List<CheckboxGroupOption> options;
   private final int minValues;
   private final int maxValues;
   private final boolean required;

   public CheckboxGroupImpl(int uniqueId, String customId, List<CheckboxGroupOption> options, int minValues, int maxValues, boolean required) {
      this.uniqueId = uniqueId;
      this.customId = customId;
      this.options = Helpers.<CheckboxGroupOption>copyAsUnmodifiableList(options);
      this.minValues = minValues;
      this.maxValues = maxValues;
      this.required = required;
   }

   public CheckboxGroupImpl(DataObject data) {
      this.uniqueId = data.getInt("id", -1);
      this.customId = data.getString("custom_id");
      this.options = (List)data.getArray("options").stream(DataArray::getObject).map(CheckboxGroupOption::fromData).collect(Helpers.toUnmodifiableList());
      this.minValues = data.getInt("min_values", -1);
      this.maxValues = data.getInt("max_values", -1);
      this.required = data.getBoolean("required", true);
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.CHECKBOX_GROUP;
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getCustomId() {
      return this.customId;
   }

   @Nonnull
   public @Unmodifiable List<CheckboxGroupOption> getOptions() {
      return this.options;
   }

   public int getMinValues() {
      return this.minValues;
   }

   public int getMaxValues() {
      return this.maxValues;
   }

   public boolean isRequired() {
      return this.required;
   }

   @Nonnull
   public CheckboxGroupImpl withUniqueId(int uniqueId) {
      return new CheckboxGroupImpl(uniqueId, this.customId, this.options, this.minValues, this.maxValues, this.required);
   }

   @Nonnull
   public DataObject toData() {
      DataObject object = DataObject.empty().put("type", this.getType().getKey()).put("custom_id", this.customId).put("options", DataArray.fromCollection(this.options));
      if (this.uniqueId != -1) {
         object.put("id", this.uniqueId);
      }

      if (this.minValues != -1) {
         object.put("min_values", this.minValues);
      }

      if (this.maxValues != -1) {
         object.put("max_values", this.maxValues);
      }

      if (!this.required) {
         object.put("required", false);
      }

      return object;
   }

   public final boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CheckboxGroupImpl)) {
         return false;
      } else {
         CheckboxGroupImpl that = (CheckboxGroupImpl)o;
         return this.uniqueId == that.uniqueId && this.customId.equals(that.customId) && this.options.equals(that.options) && this.minValues == that.minValues && this.maxValues == that.maxValues && this.required == that.required;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.customId, this.options, this.minValues, this.maxValues, this.required});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("custom_id", this.customId).addMetadata("required", this.required).toString();
   }
}
