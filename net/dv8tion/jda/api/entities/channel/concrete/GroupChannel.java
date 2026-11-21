package net.dv8tion.jda.api.entities.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.detached.IDetachableEntity;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.internal.utils.Helpers;

public interface GroupChannel extends MessageChannel, IDetachableEntity {
   /** @deprecated */
   @Deprecated
   String ICON_URL = "https://cdn.discordapp.com/channel-icons/%s/%s.png";

   @Nullable
   String getIconId();

   @Nullable
   default String getIconUrl() {
      String iconId = this.getIconId();
      return iconId == null ? null : this.getIconUrl(ImageFormat.PNG);
   }

   @Nullable
   default String getIconUrl(@Nonnull ImageFormat format) {
      ImageProxy icon = this.getIcon(format);
      return icon == null ? null : icon.getUrl();
   }

   @Nullable
   default ImageProxy getIcon() {
      String iconUrl = this.getIconUrl();
      return iconUrl == null ? null : new ImageProxy(iconUrl);
   }

   @Nullable
   default ImageProxy getIcon(@Nonnull ImageFormat format) {
      return DiscordAssets.channelIcon(format, this.getId(), this.getIconId());
   }

   long getOwnerIdLong();

   @Nonnull
   default String getOwnerId() {
      return Long.toUnsignedString(this.getOwnerIdLong());
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<User> retrieveOwner() {
      return this.getJDA().retrieveUserById(this.getOwnerIdLong());
   }

   @Nonnull
   default String getJumpUrl() {
      return Helpers.format("https://discord.com/channels/%s/%s", "@me", this.getId());
   }
}
