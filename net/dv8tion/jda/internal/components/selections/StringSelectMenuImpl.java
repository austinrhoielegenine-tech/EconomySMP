package net.dv8tion.jda.internal.components.selections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.label.LabelChildComponentUnion;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

public class StringSelectMenuImpl extends SelectMenuImpl implements StringSelectMenu, LabelChildComponentUnion {
   private final List<SelectOption> options;

   public StringSelectMenuImpl(DataObject data) {
      super(data);
      this.options = parseOptions(data.getArray("options"));
   }

   public StringSelectMenuImpl(String id, int uniqueId, String placeholder, int minValues, int maxValues, boolean disabled, List<SelectOption> options, Boolean required) {
      super(id, uniqueId, placeholder, minValues, maxValues, disabled, required);
      this.options = options;
   }

   private static List<SelectOption> parseOptions(DataArray array) {
      List<SelectOption> options = new ArrayList(array.length());
      Stream var10000 = array.stream(DataArray::getObject).map(SelectOption::fromData);
      Objects.requireNonNull(options);
      var10000.forEach(options::add);
      return options;
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.STRING_SELECT;
   }

   @Nonnull
   public StringSelectMenuImpl withUniqueId(int uniqueId) {
      return (StringSelectMenuImpl)((StringSelectMenu.Builder)this.createCopy().setUniqueId(uniqueId)).build();
   }

   @Nonnull
   public List<SelectOption> getOptions() {
      return Collections.unmodifiableList(this.options);
   }

   @Nonnull
   public DataObject toData() {
      return super.toData().put("type", Component.Type.STRING_SELECT.getKey()).put("options", DataArray.fromCollection(this.options));
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.placeholder, this.minValues, this.maxValues, this.disabled, this.options});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof StringSelectMenu)) {
         return false;
      } else {
         StringSelectMenu other = (StringSelectMenu)obj;
         return Objects.equals(this.id, other.getCustomId()) && Objects.equals(this.placeholder, other.getPlaceholder()) && this.minValues == other.getMinValues() && this.maxValues == other.getMaxValues() && this.disabled == other.isDisabled() && Objects.equals(this.options, other.getOptions()) && Objects.equals(this.required, other.isRequired());
      }
   }
}
