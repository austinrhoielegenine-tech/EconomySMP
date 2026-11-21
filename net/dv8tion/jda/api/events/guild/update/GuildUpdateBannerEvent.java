package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;

public class GuildUpdateBannerEvent extends GenericGuildUpdateEvent<String> {
   public static final String IDENTIFIER = "banner";

   public GuildUpdateBannerEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous) {
      super(api, responseNumber, guild, previous, guild.getBannerId(), "banner");
   }

   @Nullable
   public String getNewBannerId() {
      return (String)this.getNewValue();
   }

   @Nullable
   public String getNewBannerUrl() {
      return this.next == null ? null : this.getNewBannerUrl(((String)this.next).startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   public String getNewBannerUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getNewBanner(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getNewBanner() {
      String newBannerUrl = this.getNewBannerUrl();
      return newBannerUrl == null ? null : new ImageProxy(newBannerUrl);
   }

   @Nullable
   public ImageProxy getNewBanner(@Nonnull ImageFormat format) {
      return DiscordAssets.guildBanner(format, this.guild.getId(), this.next);
   }

   @Nullable
   public String getOldBannerId() {
      return (String)this.getOldValue();
   }

   @Nullable
   public String getOldBannerUrl() {
      return this.previous == null ? null : this.getOldBannerUrl(((String)this.previous).startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   public String getOldBannerUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getOldBanner(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getOldBanner() {
      String oldBannerUrl = this.getOldBannerUrl();
      return oldBannerUrl == null ? null : new ImageProxy(oldBannerUrl);
   }

   @Nullable
   public ImageProxy getOldBanner(@Nonnull ImageFormat format) {
      return DiscordAssets.guildBanner(format, this.guild.getId(), this.previous);
   }
}
