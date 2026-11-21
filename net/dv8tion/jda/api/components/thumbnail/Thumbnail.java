package net.dv8tion.jda.api.components.thumbnail;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.ResolvedMedia;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.components.thumbnail.ThumbnailFileUpload;
import net.dv8tion.jda.internal.components.thumbnail.ThumbnailImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface Thumbnail extends SectionAccessoryComponent {
   int MAX_DESCRIPTION_LENGTH = 1024;

   @Nonnull
   static Thumbnail fromUrl(@Nonnull String url) {
      Checks.notBlank(url, "URL");
      return new ThumbnailImpl(url);
   }

   @Nonnull
   static Thumbnail fromFile(@Nonnull FileUpload file) {
      Checks.notNull(file, "FileUpload");
      return new ThumbnailFileUpload(file);
   }

   @Nonnull
   @CheckReturnValue
   Thumbnail withUniqueId(int var1);

   @Nonnull
   @CheckReturnValue
   Thumbnail withDescription(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   Thumbnail withSpoiler(boolean var1);

   @Nonnull
   String getUrl();

   @Nullable
   ResolvedMedia getResolvedMedia();

   @Nullable
   String getDescription();

   boolean isSpoiler();
}
