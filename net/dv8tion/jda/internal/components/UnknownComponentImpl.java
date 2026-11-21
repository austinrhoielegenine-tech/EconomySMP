package net.dv8tion.jda.internal.components;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.ModalTopLevelComponentUnion;
import net.dv8tion.jda.api.components.UnknownComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponentUnion;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponentUnion;
import net.dv8tion.jda.api.components.section.SectionContentComponentUnion;
import net.dv8tion.jda.api.utils.data.DataObject;

public class UnknownComponentImpl extends AbstractComponentImpl implements UnknownComponent, MessageTopLevelComponentUnion, ModalTopLevelComponentUnion, ActionRowChildComponentUnion, SectionContentComponentUnion, SectionAccessoryComponentUnion, ContainerChildComponentUnion {
   private final DataObject data;

   public UnknownComponentImpl(DataObject data) {
      this.data = data;
   }

   @Nonnull
   public UnknownComponentImpl withUniqueId(int uniqueId) {
      throw new UnsupportedOperationException("Cannot modify an unknown component");
   }

   public int getUniqueId() {
      return this.data.getInt("id");
   }

   @Nonnull
   public DataObject toData() {
      return this.data;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof UnknownComponentImpl)) {
         return false;
      } else {
         UnknownComponentImpl that = (UnknownComponentImpl)o;
         return Objects.equals(this.data, that.data);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.data);
   }

   public String toString() {
      return "UnknownComponent(data=" + this.data + ")";
   }
}
