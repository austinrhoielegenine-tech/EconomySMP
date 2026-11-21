package net.dv8tion.jda.api.components.mediagallery;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.ResolvedMedia;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.components.mediagallery.MediaGalleryItemFileUpload;
import net.dv8tion.jda.internal.components.mediagallery.MediaGalleryItemImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface MediaGalleryItem {
   int MAX_DESCRIPTION_LENGTH = 1024;

   @Nonnull
   static MediaGalleryItem fromUrl(@Nonnull String url) {
      Checks.notBlank(url, "URL");
      return new MediaGalleryItemImpl(url);
   }

   @Nonnull
   static MediaGalleryItem fromFile(@Nonnull FileUpload file) {
      Checks.notNull(file, "FileUpload");
      return new MediaGalleryItemFileUpload(file);
   }

   @Nonnull
   @CheckReturnValue
   MediaGalleryItem withDescription(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   MediaGalleryItem withSpoiler(boolean var1);

   @Nonnull
   String getUrl();

   @Nullable
   ResolvedMedia getResolvedMedia();

   @Nullable
   String getDescription();

   boolean isSpoiler();
}
