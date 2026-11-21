package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;

public class GuildUpdateIconEvent extends GenericGuildUpdateEvent<String> {
   public static final String IDENTIFIER = "icon";

   public GuildUpdateIconEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String oldIconId) {
      super(api, responseNumber, guild, oldIconId, guild.getIconId(), "icon");
   }

   @Nullable
   public String getOldIconId() {
      return (String)this.getOldValue();
   }

   @Nullable
   public String getOldIconUrl() {
      return this.previous == null ? null : this.getOldIconUrl(((String)this.previous).startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   public String getOldIconUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getOldIcon(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getOldIcon() {
      String oldIconUrl = this.getOldIconUrl();
      return oldIconUrl == null ? null : new ImageProxy(oldIconUrl);
   }

   @Nullable
   public ImageProxy getOldIcon(@Nonnull ImageFormat format) {
      return DiscordAssets.guildIcon(format, this.guild.getId(), this.previous);
   }

   @Nullable
   public String getNewIconId() {
      return (String)this.getNewValue();
   }

   @Nullable
   public String getNewIconUrl() {
      return this.next == null ? null : this.getNewIconUrl(((String)this.next).startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   public String getNewIconUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getNewIcon(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getNewIcon() {
      String newIconUrl = this.getNewIconUrl();
      return newIconUrl == null ? null : new ImageProxy(newIconUrl);
   }

   @Nullable
   public ImageProxy getNewIcon(@Nonnull ImageFormat format) {
      return DiscordAssets.guildIcon(format, this.guild.getId(), this.next);
   }
}
