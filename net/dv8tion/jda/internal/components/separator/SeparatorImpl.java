package net.dv8tion.jda.internal.components.separator;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class SeparatorImpl extends AbstractComponentImpl implements Separator, MessageTopLevelComponentUnion, ContainerChildComponentUnion {
   private final int uniqueId;
   private final Separator.Spacing spacing;
   private final boolean isDivider;

   public SeparatorImpl(DataObject obj) {
      this(obj.getInt("id", -1), Separator.Spacing.fromKey(obj.getInt("spacing", 1)), obj.getBoolean("divider", true));
   }

   public SeparatorImpl(Separator.Spacing spacing, boolean isDivider) {
      this(-1, spacing, isDivider);
      Checks.check(spacing != Separator.Spacing.UNKNOWN, "Spacing cannot be unknown");
   }

   private SeparatorImpl(int uniqueId, Separator.Spacing spacing, boolean isDivider) {
      this.uniqueId = uniqueId;
      this.spacing = spacing;
      this.isDivider = isDivider;
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.SEPARATOR;
   }

   @Nonnull
   public SeparatorImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new SeparatorImpl(uniqueId, this.spacing, this.isDivider);
   }

   @Nonnull
   public Separator withDivider(boolean divider) {
      return new SeparatorImpl(this.uniqueId, this.spacing, divider);
   }

   @Nonnull
   public Separator withSpacing(@Nonnull Separator.Spacing spacing) {
      Checks.notNull(spacing, "Spacing");
      Checks.check(spacing != Separator.Spacing.UNKNOWN, "Spacing cannot be unknown");
      return new SeparatorImpl(this.uniqueId, spacing, this.isDivider);
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   public boolean isDivider() {
      return this.isDivider;
   }

   @Nonnull
   public Separator.Spacing getSpacing() {
      return this.spacing;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty().put("type", this.getType().getKey()).put("divider", this.isDivider).put("spacing", this.spacing.getKey());
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      return json;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SeparatorImpl)) {
         return false;
      } else {
         SeparatorImpl separator = (SeparatorImpl)o;
         return this.uniqueId == separator.uniqueId && this.isDivider == separator.isDivider && this.spacing == separator.spacing;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.spacing, this.isDivider});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("divider", this.isDivider).addMetadata("spacing", this.spacing).toString();
   }
}
