package net.dv8tion.jda.internal.components.label;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.ModalTopLevelComponentUnion;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.label.LabelChildComponent;
import net.dv8tion.jda.api.components.label.LabelChildComponentUnion;
import net.dv8tion.jda.api.components.utils.ComponentDeserializer;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class LabelImpl extends AbstractComponentImpl implements Label, ModalTopLevelComponentUnion {
   private final int uniqueId;
   private final String label;
   private final String description;
   private final LabelChildComponentUnion child;

   public LabelImpl(@Nonnull ComponentDeserializer deserializer, @Nonnull DataObject object) {
      this(object.getInt("id", -1), object.getString("label"), object.getString("description", (String)null), (LabelChildComponentUnion)deserializer.deserializeAs(LabelChildComponentUnion.class, object.getObject("component")));
   }

   public LabelImpl(@Nonnull String label, @Nullable String description, @Nonnull LabelChildComponentUnion child) {
      this(-1, label, description, child);
   }

   private LabelImpl(int uniqueId, @Nonnull String label, @Nullable String description, @Nonnull LabelChildComponentUnion child) {
      this.uniqueId = uniqueId;
      this.label = label;
      this.description = description;
      this.child = child;
   }

   public static Label validated(@Nonnull String label, @Nullable String description, @Nonnull LabelChildComponent child) {
      Checks.notBlank(label, "Label");
      Checks.notLonger(label, 45, "Label");
      Checks.notNull(child, "Child");
      if (description != null) {
         Checks.notBlank(description, "Description");
         Checks.notLonger(description, 100, "Description");
      }

      LabelChildComponentUnion childUnion = (LabelChildComponentUnion)ComponentsUtil.safeUnionCast("child", child, LabelChildComponentUnion.class);
      return new LabelImpl(label, description, childUnion);
   }

   @Nonnull
   public Label withLabel(@Nonnull String label) {
      return validated(label, this.description, this.child);
   }

   @Nonnull
   public Label withDescription(@Nullable String description) {
      return validated(this.label, description, this.child);
   }

   @Nonnull
   public Label withChild(@Nonnull LabelChildComponent child) {
      return validated(this.label, this.description, child);
   }

   @Nonnull
   public LabelImpl withUniqueId(int uniqueId) {
      return new LabelImpl(uniqueId, this.label, this.description, this.child);
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.LABEL;
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getLabel() {
      return this.label;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nonnull
   public LabelChildComponentUnion getChild() {
      return this.child;
   }

   @Nonnull
   public DataObject toData() {
      DataObject obj = DataObject.empty().put("type", this.getType().getKey()).put("label", this.label).put("description", this.description).put("component", this.child);
      if (this.uniqueId >= 0) {
         obj.put("id", this.uniqueId);
      }

      return obj;
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("label", this.label).toString();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof LabelImpl)) {
         return false;
      } else {
         LabelImpl that = (LabelImpl)o;
         return this.uniqueId == that.uniqueId && Objects.equals(this.label, that.label) && Objects.equals(this.description, that.description) && Objects.equals(this.child, that.child);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.label, this.description, this.child});
   }
}
