package net.dv8tion.jda.internal.components.textdisplay;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.ModalTopLevelComponentUnion;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.section.SectionContentComponentUnion;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class TextDisplayImpl extends AbstractComponentImpl implements TextDisplay, MessageTopLevelComponentUnion, ModalTopLevelComponentUnion, ContainerChildComponentUnion, SectionContentComponentUnion {
   private final int uniqueId;
   private final String content;

   public TextDisplayImpl(DataObject data) {
      this(data.getInt("id", -1), data.getString("content"));
   }

   public TextDisplayImpl(String content) {
      this(-1, content);
   }

   private TextDisplayImpl(int uniqueId, String content) {
      this.content = content;
      this.uniqueId = uniqueId;
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.TEXT_DISPLAY;
   }

   @Nonnull
   public TextDisplayImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new TextDisplayImpl(uniqueId, this.content);
   }

   @Nonnull
   public TextDisplay withContent(@Nonnull String content) {
      Checks.notBlank(content, "Content");
      return new TextDisplayImpl(this.uniqueId, content);
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getContent() {
      return this.content;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty().put("type", this.getType().getKey()).put("content", this.content);
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      return json;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TextDisplayImpl)) {
         return false;
      } else {
         TextDisplayImpl that = (TextDisplayImpl)o;
         return this.uniqueId == that.uniqueId && Objects.equals(this.content, that.content);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.content});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("content", this.content).toString();
   }
}
