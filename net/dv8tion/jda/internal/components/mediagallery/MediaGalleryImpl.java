package net.dv8tion.jda.internal.components.mediagallery;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.entities.FileContainerMixin;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class MediaGalleryImpl extends AbstractComponentImpl implements MediaGallery, MessageTopLevelComponentUnion, ContainerChildComponentUnion, FileContainerMixin {
   private final int uniqueId;
   private final List<MediaGalleryItem> items;

   private MediaGalleryImpl(Collection<? extends MediaGalleryItem> items) {
      this(-1, items);
   }

   public MediaGalleryImpl(int uniqueId, Collection<? extends MediaGalleryItem> items) {
      this.uniqueId = uniqueId;
      this.items = Helpers.<MediaGalleryItem>copyAsUnmodifiableList(items);
   }

   @Nonnull
   public static MediaGallery validated(@Nonnull Collection<? extends MediaGalleryItem> items) {
      Checks.noneNull(items, "Items");
      Checks.notEmpty(items, "Items");
      Checks.check(items.size() <= 10, "A media gallery can only contain %d items, provided: %d", 10, items.size());
      return new MediaGalleryImpl(items);
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.MEDIA_GALLERY;
   }

   @Nonnull
   public MediaGalleryImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new MediaGalleryImpl(uniqueId, this.items);
   }

   @Nonnull
   public MediaGalleryImpl withItems(@Nonnull Collection<? extends MediaGalleryItem> items) {
      Checks.noneNull(items, "Items");
      return new MediaGalleryImpl(this.uniqueId, items);
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public List<MediaGalleryItem> getItems() {
      return this.items;
   }

   public Stream<FileUpload> getFiles() {
      Stream var10000 = this.items.stream();
      Objects.requireNonNull(FileContainerMixin.class);
      var10000 = var10000.filter(FileContainerMixin.class::isInstance);
      Objects.requireNonNull(FileContainerMixin.class);
      return var10000.map(FileContainerMixin.class::cast).flatMap(FileContainerMixin::getFiles);
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty().put("type", this.getType().getKey()).put("items", DataArray.fromCollection(this.getItems()));
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      return json;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MediaGalleryImpl)) {
         return false;
      } else {
         MediaGalleryImpl that = (MediaGalleryImpl)o;
         return this.uniqueId == that.uniqueId && Objects.equals(this.items, that.items);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.items});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("items", this.items).toString();
   }
}
