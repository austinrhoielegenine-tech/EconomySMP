package net.dv8tion.jda.api.interactions.modals;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.SelectMenuMentions;
import net.dv8tion.jda.internal.interactions.InteractionImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class ModalMapping {
   private final InteractionImpl interaction;
   private final String customId;
   private final int uniqueId;
   private final DataObject resolved;
   private final DataObject value;
   private final Component.Type type;

   public ModalMapping(InteractionImpl interaction, DataObject resolved, DataObject object) {
      this.interaction = interaction;
      this.uniqueId = object.getInt("id");
      this.customId = object.getString("custom_id");
      this.type = Component.Type.fromKey(object.getInt("type"));
      this.resolved = resolved;
      this.value = object;
   }

   @Nonnull
   public String getCustomId() {
      return this.customId;
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public Component.Type getType() {
      return this.type;
   }

   @Nonnull
   public String getAsString() {
      switch (this.type) {
         case TEXT_INPUT:
            return this.value.getString("value");
         case RADIO_GROUP:
            if (this.value.isNull("value")) {
               throw new IllegalStateException("Cannot get a non-null string from an optional radio group, use 'getAsOptionalString()' instead");
            }

            return this.value.getString("value");
         default:
            throw this.newTypeError("String");
      }
   }

   @Nullable
   public String getAsOptionalString() {
      switch (this.type) {
         case TEXT_INPUT:
            String value = this.value.getString("value");
            if (value.isEmpty()) {
               return null;
            }

            return value;
         case RADIO_GROUP:
            if (this.value.isNull("value")) {
               return null;
            }

            return this.value.getString("value");
         default:
            throw this.newTypeError("String");
      }
   }

   public boolean getAsBoolean() {
      if (this.type != Component.Type.CHECKBOX) {
         this.typeError("boolean");
      }

      return this.value.getBoolean("value");
   }

   @Nonnull
   public List<String> getAsStringList() {
      if (this.type != Component.Type.STRING_SELECT && !this.type.isEntitySelectMenu() && this.type != Component.Type.CHECKBOX_GROUP) {
         this.typeError("List<String>");
      }

      return (List)this.value.getArray("values").stream(DataArray::getString).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public List<Long> getAsLongList() {
      if (!this.type.isEntitySelectMenu()) {
         this.typeError("List<Long>");
      }

      return (List)this.value.getArray("values").stream(DataArray::getLong).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public Mentions getAsMentions() {
      if (!this.type.isEntitySelectMenu()) {
         this.typeError("Mentions");
      }

      return new SelectMenuMentions(this.interaction.getJDA(), this.interaction.getInteractionEntityBuilder(), this.interaction.getGuild(), this.resolved, this.value.getArray("values"));
   }

   @Nonnull
   public List<Message.Attachment> getAsAttachmentList() {
      if (this.type != Component.Type.FILE_UPLOAD) {
         this.typeError("List<Message.Attachment>");
      }

      if (this.resolved.isNull("attachments")) {
         return Collections.emptyList();
      } else {
         DataObject attachments = this.resolved.getObject("attachments");
         EntityBuilder entityBuilder = this.interaction.getJDA().getEntityBuilder();
         return (List)this.value.getArray("values").stream(DataArray::getString).map((id) -> entityBuilder.createMessageAttachment(attachments.getObject(id))).collect(Helpers.toUnmodifiableList());
      }
   }

   public String toString() {
      return (new EntityString(this)).setType(this.getType()).addMetadata("customId", this.customId).toString();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ModalMapping)) {
         return false;
      } else {
         ModalMapping that = (ModalMapping)o;
         return this.type == that.type && Objects.equals(this.customId, that.customId) && this.uniqueId == that.uniqueId && Objects.equals(this.value, that.value);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.customId, this.uniqueId, this.value, this.type});
   }

   private void typeError(String targetType) {
      throw new IllegalStateException("ModalMapping of type " + this.getType() + " can not be represented as " + targetType + "!");
   }

   private RuntimeException newTypeError(String targetType) {
      return new IllegalStateException("ModalMapping of type " + this.getType() + " can not be represented as " + targetType + "!");
   }
}
