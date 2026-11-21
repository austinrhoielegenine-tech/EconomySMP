package net.dv8tion.jda.internal.components.selections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponentUnion;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.utils.EntityString;

public abstract class SelectMenuImpl extends AbstractComponentImpl implements SelectMenu, ActionRowChildComponentUnion {
   protected final String id;
   protected final String placeholder;
   protected final int uniqueId;
   protected final int minValues;
   protected final int maxValues;
   protected final boolean disabled;
   protected final Boolean required;

   public SelectMenuImpl(DataObject data) {
      this(data.getString("custom_id"), data.getInt("id", -1), data.getString("placeholder", (String)null), data.getInt("min_values", 1), data.getInt("max_values", 1), data.getBoolean("disabled"), data.isNull("required") ? null : data.getBoolean("required"));
   }

   public SelectMenuImpl(String id, int uniqueId, String placeholder, int minValues, int maxValues, boolean disabled, Boolean required) {
      this.id = id;
      this.uniqueId = uniqueId;
      this.placeholder = placeholder;
      this.minValues = minValues;
      this.maxValues = maxValues;
      this.disabled = disabled;
      this.required = required;
   }

   @Nonnull
   public abstract SelectMenuImpl withUniqueId(int var1);

   @Nonnull
   public String getCustomId() {
      return this.id;
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nullable
   public String getPlaceholder() {
      return this.placeholder;
   }

   public int getMinValues() {
      return this.minValues;
   }

   public int getMaxValues() {
      return this.maxValues;
   }

   public boolean isDisabled() {
      return this.disabled;
   }

   public Boolean isRequired() {
      return this.required;
   }

   @Nonnull
   public DataObject toData() {
      DataObject data = DataObject.empty();
      data.put("custom_id", this.id);
      if (this.uniqueId >= 0) {
         data.put("id", this.uniqueId);
      }

      data.put("min_values", this.minValues);
      data.put("max_values", this.maxValues);
      data.put("disabled", this.disabled);
      if (this.placeholder != null) {
         data.put("placeholder", this.placeholder);
      }

      if (this.required != null) {
         data.put("required", this.required);
      }

      return data;
   }

   public String toString() {
      return (new EntityString(SelectMenu.class)).setType(this.getType()).addMetadata("id", this.uniqueId).addMetadata("custom id", this.id).addMetadata("placeholder", this.placeholder).toString();
   }
}
