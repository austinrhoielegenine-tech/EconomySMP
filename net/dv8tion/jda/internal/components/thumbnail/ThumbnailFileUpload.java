package net.dv8tion.jda.internal.components.thumbnail;

import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.ResolvedMedia;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponentUnion;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.entities.FileContainerMixin;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class ThumbnailFileUpload extends AbstractComponentImpl implements Thumbnail, SectionAccessoryComponentUnion, FileContainerMixin {
   private final int uniqueId;
   private final FileUpload file;
   private final String description;
   private final boolean spoiler;

   public ThumbnailFileUpload(FileUpload file) {
      this(-1, file, (String)null, false);
   }

   public ThumbnailFileUpload(int uniqueId, FileUpload file, String description, boolean spoiler) {
      this.uniqueId = uniqueId;
      this.file = file;
      this.description = description;
      this.spoiler = spoiler;
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.THUMBNAIL;
   }

   @Nonnull
   public ThumbnailFileUpload withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new ThumbnailFileUpload(uniqueId, this.file, this.description, this.spoiler);
   }

   @Nonnull
   public Thumbnail withDescription(@Nullable String description) {
      if (description != null) {
         Checks.notBlank(description, "Description");
         Checks.notLonger(description, 1024, "Description");
      }

      return new ThumbnailFileUpload(this.uniqueId, this.file, description, this.spoiler);
   }

   @Nonnull
   public Thumbnail withSpoiler(boolean spoiler) {
      return new ThumbnailFileUpload(this.uniqueId, this.file, this.description, spoiler);
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getUrl() {
      return "attachment://" + this.file.getName();
   }

   public Stream<FileUpload> getFiles() {
      return Stream.of(this.file);
   }

   @Nullable
   public ResolvedMedia getResolvedMedia() {
      return null;
   }

   @Nullable
   public String getDescription() {
      return this.description != null ? this.description : this.file.getDescription();
   }

   public boolean isSpoiler() {
      return this.spoiler;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty().put("type", this.getType().getKey()).put("media", DataObject.empty().put("url", this.getUrl())).put("spoiler", this.spoiler);
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      if (this.getDescription() != null) {
         json.put("description", this.getDescription());
      }

      return json;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ThumbnailFileUpload)) {
         return false;
      } else {
         ThumbnailFileUpload thumbnail = (ThumbnailFileUpload)o;
         return this.uniqueId == thumbnail.uniqueId && this.spoiler == thumbnail.spoiler && Objects.equals(this.file, thumbnail.file) && Objects.equals(this.description, thumbnail.description);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.file, this.description, this.spoiler});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("file", this.file).addMetadata("spoiler", this.spoiler).addMetadata("description", this.description).toString();
   }
}
