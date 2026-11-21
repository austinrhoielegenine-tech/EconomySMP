package net.dv8tion.jda.api.entities.emoji;

import java.util.Formatter;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;

public interface CustomEmoji extends Emoji, IMentionable {
   int EMOJI_NAME_MAX_LENGTH = 32;
   /** @deprecated */
   @Deprecated
   String ICON_URL = "https://cdn.discordapp.com/emojis/%s.%s";

   @Nonnull
   default Emoji.Type getType() {
      return Emoji.Type.CUSTOM;
   }

   boolean isAnimated();

   @Nonnull
   default String getImageUrl() {
      return this.getImageUrl(this.isAnimated() ? ImageFormat.ANIMATED_WEBP : ImageFormat.STATIC_WEBP);
   }

   @Nonnull
   default String getImageUrl(@Nonnull ImageFormat format) {
      return this.getImage(format).getUrl();
   }

   @Nonnull
   default ImageProxy getImage() {
      return new ImageProxy(this.getImageUrl());
   }

   @Nonnull
   default ImageProxy getImage(@Nonnull ImageFormat format) {
      return DiscordAssets.customEmoji(format, this.getId());
   }

   @Nonnull
   default String getAsMention() {
      return (this.isAnimated() ? "<a:" : "<:") + this.getName() + ":" + this.getId() + ">";
   }

   @Nonnull
   default String getFormatted() {
      return this.getAsMention();
   }

   default void formatTo(Formatter formatter, int flags, int width, int precision) {
      Emoji.super.formatTo(formatter, flags, width, precision);
   }
}
