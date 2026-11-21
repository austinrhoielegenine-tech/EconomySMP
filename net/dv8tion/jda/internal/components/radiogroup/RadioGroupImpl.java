package net.dv8tion.jda.internal.components.radiogroup;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.label.LabelChildComponentUnion;
import net.dv8tion.jda.api.components.radiogroup.RadioGroup;
import net.dv8tion.jda.api.components.radiogroup.RadioGroupOption;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public class RadioGroupImpl extends AbstractComponentImpl implements RadioGroup, LabelChildComponentUnion {
   private final int uniqueId;
   private final String customId;
   private final List<RadioGroupOption> options;
   private final boolean required;

   public RadioGroupImpl(int uniqueId, String customId, List<RadioGroupOption> options, boolean required) {
      this.uniqueId = uniqueId;
      this.customId = customId;
      this.options = Helpers.<RadioGroupOption>copyAsUnmodifiableList(options);
      this.required = required;
   }

   public RadioGroupImpl(DataObject data) {
      this.uniqueId = data.getInt("id", -1);
      this.customId = data.getString("custom_id");
      this.options = (List)data.getArray("options").stream(DataArray::getObject).map(RadioGroupOption::fromData).collect(Helpers.toUnmodifiableList());
      this.required = data.getBoolean("required", true);
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.RADIO_GROUP;
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getCustomId() {
      return this.customId;
   }

   @Nonnull
   public @Unmodifiable List<RadioGroupOption> getOptions() {
      return this.options;
   }

   public boolean isRequired() {
      return this.required;
   }

   @Nonnull
   public RadioGroupImpl withUniqueId(int uniqueId) {
      return new RadioGroupImpl(uniqueId, this.customId, this.options, this.required);
   }

   @Nonnull
   public DataObject toData() {
      DataObject object = DataObject.empty().put("type", this.getType().getKey()).put("custom_id", this.customId).put("options", DataArray.fromCollection(this.options));
      if (this.uniqueId != -1) {
         object.put("id", this.uniqueId);
      }

      if (!this.required) {
         object.put("required", false);
      }

      return object;
   }

   public final boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof RadioGroupImpl)) {
         return false;
      } else {
         RadioGroupImpl that = (RadioGroupImpl)o;
         return this.uniqueId == that.uniqueId && this.customId.equals(that.customId) && this.options.equals(that.options) && this.required == that.required;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.customId, this.options, this.required});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("custom_id", this.customId).addMetadata("required", this.required).toString();
   }
}
