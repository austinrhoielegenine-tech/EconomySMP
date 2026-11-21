package net.dv8tion.jda.api.entities.sticker;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import org.jetbrains.annotations.Unmodifiable;

public interface StickerPack extends ISnowflake {
   /** @deprecated */
   @Deprecated
   String BANNER_URL = "https://cdn.discordapp.com/app-assets/710982414301790216/store/%s.%s";

   @Nonnull
   @Unmodifiable List<StandardSticker> getStickers();

   @Nonnull
   String getName();

   @Nonnull
   String getDescription();

   long getCoverIdLong();

   @Nullable
   default String getCoverId() {
      long id = this.getCoverIdLong();
      return id == 0L ? null : Long.toUnsignedString(id);
   }

   @Nullable
   default StandardSticker getCoverSticker() {
      long id = this.getCoverIdLong();
      return id == 0L ? null : (StandardSticker)this.getStickers().stream().filter((s) -> s.getIdLong() == id).findFirst().orElse((Object)null);
   }

   long getBannerIdLong();

   @Nullable
   default String getBannerId() {
      long id = this.getBannerIdLong();
      return id == 0L ? null : Long.toUnsignedString(id);
   }

   @Nullable
   default String getBannerUrl() {
      return this.getBannerUrl(ImageFormat.PNG);
   }

   @Nullable
   default String getBannerUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getBanner(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   default ImageProxy getBanner() {
      String url = this.getBannerUrl();
      return url == null ? null : new ImageProxy(url);
   }

   @Nullable
   default ImageProxy getBanner(@Nonnull ImageFormat format) {
      return DiscordAssets.stickerPackBanner(format, this.getBannerId());
   }

   long getSkuIdLong();

   @Nullable
   default String getSkuId() {
      long id = this.getSkuIdLong();
      return id == 0L ? null : Long.toUnsignedString(id);
   }
}
