package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;

public class GuildUpdateSplashEvent extends GenericGuildUpdateEvent<String> {
   public static final String IDENTIFIER = "splash";

   public GuildUpdateSplashEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String oldSplashId) {
      super(api, responseNumber, guild, oldSplashId, guild.getSplashId(), "splash");
   }

   @Nullable
   public String getOldSplashId() {
      return (String)this.getOldValue();
   }

   @Nullable
   public String getOldSplashUrl() {
      return this.previous == null ? null : this.getOldSplashUrl(ImageFormat.PNG);
   }

   @Nullable
   public String getOldSplashUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getOldSplash(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getOldSplash() {
      String oldSplashUrl = this.getOldSplashUrl();
      return oldSplashUrl == null ? null : new ImageProxy(oldSplashUrl);
   }

   @Nullable
   public ImageProxy getOldSplash(@Nonnull ImageFormat format) {
      return DiscordAssets.guildSplash(format, this.guild.getId(), this.previous);
   }

   @Nullable
   public String getNewSplashId() {
      return (String)this.getNewValue();
   }

   @Nullable
   public String getNewSplashUrl() {
      return this.next == null ? null : this.getNewSplashUrl(ImageFormat.PNG);
   }

   @Nullable
   public String getNewSplashUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getNewSplash(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getNewSplash() {
      String newSplashUrl = this.getNewSplashUrl();
      return newSplashUrl == null ? null : new ImageProxy(newSplashUrl);
   }

   @Nullable
   public ImageProxy getNewSplash(@Nonnull ImageFormat format) {
      return DiscordAssets.guildSplash(format, this.guild.getId(), this.next);
   }
}
