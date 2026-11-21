package net.dv8tion.jda.api.utils;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import okhttp3.MultipartBody;

public class AttachmentUpdate implements AttachedFile, ISnowflake {
   private final long id;
   private final String name;

   protected AttachmentUpdate(long id, String name) {
      this.id = id;
      this.name = name;
   }

   @Nonnull
   public static AttachmentUpdate fromAttachment(long id) {
      return new AttachmentUpdate(id, (String)null);
   }

   @Nonnull
   public static AttachmentUpdate fromAttachment(@Nonnull String id) {
      return fromAttachment(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   public static AttachmentUpdate fromAttachment(@Nonnull Message.Attachment attachment) {
      Checks.notNull(attachment, "Attachment");
      return new AttachmentUpdate(attachment.getIdLong(), attachment.getFileName());
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   public long getIdLong() {
      return this.id;
   }

   public void addPart(@Nonnull MultipartBody.Builder builder, int index) {
   }

   @Nonnull
   public DataObject toAttachmentData(int index) {
      DataObject object = DataObject.empty().put("id", this.getId());
      if (this.name != null) {
         object.put("filename", this.name);
      }

      return object;
   }

   public void close() {
   }

   public void forceClose() {
   }

   public boolean equals(Object o) {
      if (!(o instanceof AttachmentUpdate)) {
         return false;
      } else {
         AttachmentUpdate that = (AttachmentUpdate)o;
         return this.id == that.id && Objects.equals(this.name, that.name);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.name});
   }

   public String toString() {
      EntityString entityString = (new EntityString("AttachedFile")).setType("Attachment");
      if (this.name != null) {
         entityString.setName(this.name);
      }

      return entityString.toString();
   }
}
