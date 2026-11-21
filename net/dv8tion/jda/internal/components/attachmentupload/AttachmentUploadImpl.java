package net.dv8tion.jda.internal.components.attachmentupload;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.label.LabelChildComponentUnion;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class AttachmentUploadImpl extends AbstractComponentImpl implements AttachmentUpload, LabelChildComponentUnion {
   protected final int uniqueId;
   protected final String customId;
   protected final int minValues;
   protected final int maxValues;
   protected final boolean required;

   public AttachmentUploadImpl(DataObject data) {
      this(data.getInt("id", -1), data.getString("custom_id"), data.getInt("min_values", 1), data.getInt("max_values", 1), data.getBoolean("required", true));
   }

   public AttachmentUploadImpl(int uniqueId, String customId, int minValues, int maxValues, boolean required) {
      this.uniqueId = uniqueId;
      this.customId = customId;
      this.minValues = minValues;
      this.maxValues = maxValues;
      this.required = required;
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.FILE_UPLOAD;
   }

   @Nonnull
   public AttachmentUploadImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new AttachmentUploadImpl(uniqueId, this.customId, this.minValues, this.maxValues, this.required);
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getCustomId() {
      return this.customId;
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
   public DataObject toData() {
      DataObject json = DataObject.empty().put("type", this.getType().getKey()).put("custom_id", this.customId).put("required", this.required).put("min_values", this.minValues).put("max_values", this.maxValues);
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      return json;
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("custom_id", this.customId).addMetadata("required", this.required).toString();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AttachmentUploadImpl)) {
         return false;
      } else {
         AttachmentUploadImpl that = (AttachmentUploadImpl)o;
         return this.uniqueId == that.uniqueId && this.minValues == that.minValues && this.maxValues == that.maxValues && this.required == that.required && Objects.equals(this.customId, that.customId);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.customId, this.minValues, this.maxValues, this.required});
   }
}
