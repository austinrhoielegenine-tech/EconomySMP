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
import net.dv8tion.jda.internal.components.ResolvedMediaImpl;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.entities.FileContainerMixin;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class ThumbnailImpl extends AbstractComponentImpl implements Thumbnail, SectionAccessoryComponentUnion, FileContainerMixin {
   private final int uniqueId;
   private final String url;
   private final ResolvedMedia media;
   private final String description;
   private final boolean spoiler;

   public ThumbnailImpl(DataObject data) {
      this(data.getInt("id", -1), data.getObject("media").getString("url"), new ResolvedMediaImpl(data.getObject("media")), data.getString("description", (String)null), data.getBoolean("spoiler", false));
   }

   public ThumbnailImpl(String url) {
      this(-1, url, (ResolvedMedia)null, (String)null, false);
   }

   private ThumbnailImpl(int uniqueId, String url, ResolvedMedia media, String description, boolean spoiler) {
      this.uniqueId = uniqueId;
      this.url = url;
      this.media = media;
      this.description = description;
      this.spoiler = spoiler;
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.THUMBNAIL;
   }

   @Nonnull
   public ThumbnailImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new ThumbnailImpl(uniqueId, this.url, this.media, this.description, this.spoiler);
   }

   @Nonnull
   public Thumbnail withDescription(@Nullable String description) {
      if (description != null) {
         Checks.notBlank(description, "Description");
         Checks.notLonger(description, 1024, "Description");
      }

      return new ThumbnailImpl(this.uniqueId, this.url, this.media, description, this.spoiler);
   }

   @Nonnull
   public Thumbnail withSpoiler(boolean spoiler) {
      return new ThumbnailImpl(this.uniqueId, this.url, this.media, this.description, spoiler);
   }

   public int getUniqueId() {
      return this.uniqueId;
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
      DataObject json = DataObject.empty().put("type", this.getType().getKey()).put("media", DataObject.empty().put("url", outputUrl)).put("spoiler", this.spoiler);
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      if (this.description != null) {
         json.put("description", this.description);
      }

      return json;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ThumbnailImpl)) {
         return false;
      } else {
         ThumbnailImpl thumbnail = (ThumbnailImpl)o;
         return this.uniqueId == thumbnail.uniqueId && this.spoiler == thumbnail.spoiler && Objects.equals(this.url, thumbnail.url) && Objects.equals(this.description, thumbnail.description);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.url, this.description, this.spoiler});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("url", this.url).addMetadata("media", this.media).addMetadata("spoiler", this.spoiler).addMetadata("description", this.description).toString();
   }
}
