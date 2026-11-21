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
import net.dv8tion.jda.internal.components.ResolvedMediaImpl;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.entities.FileContainerMixin;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class MediaGalleryItemImpl implements MediaGalleryItem, FileContainerMixin, SerializableData {
   private final String url;
   private final String description;
   private final ResolvedMedia media;
   private final boolean spoiler;

   public MediaGalleryItemImpl(DataObject obj) {
      this(obj.getObject("media").getString("url"), obj.getString("description", (String)null), new ResolvedMediaImpl(obj.getObject("media")), obj.getBoolean("spoiler", false));
   }

   public MediaGalleryItemImpl(String url) {
      this(url, (String)null, (ResolvedMedia)null, false);
   }

   public MediaGalleryItemImpl(String url, String description, ResolvedMedia media, boolean spoiler) {
      this.url = url;
      this.media = media;
      this.description = description;
      this.spoiler = spoiler;
   }

   @Nonnull
   public MediaGalleryItem withDescription(@Nullable String description) {
      if (description != null) {
         Checks.notBlank(description, "Description");
         Checks.notLonger(description, 1024, "Description");
      }

      return new MediaGalleryItemImpl(this.url, description, this.media, this.spoiler);
   }

   @Nonnull
   public MediaGalleryItem withSpoiler(boolean spoiler) {
      return new MediaGalleryItemImpl(this.url, this.description, this.media, spoiler);
   }

   @Nonnull
   public String getUrl() {
      return this.url;
   }

   @Nullable
   public ResolvedMedia getResolvedMedia() {
      return this.media;
   }

   public Stream<FileUpload> getFiles() {
      return ComponentsUtil.getFilesFromMedia(this.media);
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   public boolean isSpoiler() {
      return this.spoiler;
   }

   @Nonnull
   public DataObject toData() {
      String outputUrl = ComponentsUtil.getMediaUrl(this.media, this.url);
      return DataObject.empty().put("media", DataObject.empty().put("url", outputUrl)).put("description", this.description).put("spoiler", this.spoiler);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MediaGalleryItemImpl)) {
         return false;
      } else {
         MediaGalleryItemImpl that = (MediaGalleryItemImpl)o;
         return this.spoiler == that.spoiler && Objects.equals(this.url, that.url) && Objects.equals(this.description, that.description);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.url, this.description, this.spoiler});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("url", this.url).addMetadata("media", this.media).addMetadata("spoiler", this.spoiler).addMetadata("description", this.description).toString();
   }
}
