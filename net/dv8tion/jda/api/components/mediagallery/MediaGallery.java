package net.dv8tion.jda.api.components.mediagallery;

import java.util.Collection;
import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.internal.components.mediagallery.MediaGalleryImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface MediaGallery extends Component, MessageTopLevelComponent, ContainerChildComponent {
   int MAX_ITEMS = 10;

   @Nonnull
   static MediaGallery of(@Nonnull Collection<? extends MediaGalleryItem> items) {
      return MediaGalleryImpl.validated(items);
   }

   @Nonnull
   static MediaGallery of(@Nonnull MediaGalleryItem item, @Nonnull MediaGalleryItem... items) {
      Checks.notNull(item, "Item");
      Checks.noneNull(items, "Items");
      return of(Helpers.mergeVararg(item, items));
   }

   @Nonnull
   @CheckReturnValue
   MediaGallery withUniqueId(int var1);

   @Nonnull
   @CheckReturnValue
   MediaGallery withItems(@Nonnull Collection<? extends MediaGalleryItem> var1);

   @Nonnull
   @CheckReturnValue
   default MediaGallery withItems(@Nonnull MediaGalleryItem item, @Nonnull MediaGalleryItem... items) {
      Checks.notNull(item, "Item");
      Checks.notNull(items, "Items");
      return this.withItems(Helpers.mergeVararg(item, items));
   }

   @Nonnull
   @Unmodifiable List<MediaGalleryItem> getItems();
}
