package net.dv8tion.jda.internal.components.mediagallery;

import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.ResolvedMedia;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.entities.FileContainerMixin;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class MediaGalleryItemFileUpload implements MediaGalleryItem, FileContainerMixin, SerializableData {
   private final FileUpload file;
   private final String description;
   private final boolean spoiler;

   public MediaGalleryItemFileUpload(FileUpload upload) {
      this(upload, (String)null, false);
   }

   public MediaGalleryItemFileUpload(FileUpload file, String description, boolean spoiler) {
      this.file = file;
      this.description = description;
      this.spoiler = spoiler;
   }

   @Nonnull
   public MediaGalleryItem withDescription(@Nullable String description) {
      if (description != null) {
         Checks.notBlank(description, "Description");
         Checks.notLonger(description, 1024, "Description");
      }

      return new MediaGalleryItemFileUpload(this.file, description, this.spoiler);
   }

   @Nonnull
   public MediaGalleryItem withSpoiler(boolean spoiler) {
      return new MediaGalleryItemFileUpload(this.file, this.description, spoiler);
   }

   @Nonnull
   public String getUrl() {
      return "attachment://" + this.file.getName();
   }

   @Nullable
   public ResolvedMedia getResolvedMedia() {
      return null;
   }

   public Stream<FileUpload> getFiles() {
      return Stream.of(this.file);
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
      return DataObject.empty().put("media", DataObject.empty().put("url", this.getUrl())).put("description", this.getDescription()).put("spoiler", this.isSpoiler());
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MediaGalleryItemFileUpload)) {
         return false;
      } else {
         MediaGalleryItemFileUpload that = (MediaGalleryItemFileUpload)o;
         return this.spoiler == that.spoiler && Objects.equals(this.file, that.file) && Objects.equals(this.description, that.description);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.file, this.description, this.spoiler});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("file", this.file).addMetadata("spoiler", this.spoiler).addMetadata("description", this.getDescription()).toString();
   }
}
