package net.dv8tion.jda.api.entities.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IAgeRestrictedChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.managers.channel.concrete.MediaChannelManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public interface MediaChannel extends StandardGuildChannel, IPostContainer, IWebhookContainer, IAgeRestrictedChannel, ISlowmodeChannel {
   @Nonnull
   @CheckReturnValue
   MediaChannelManager getManager();

   @Nonnull
   @CheckReturnValue
   ChannelAction<MediaChannel> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<MediaChannel> createCopy() {
      return this.createCopy(this.getGuild());
   }

   @Nonnull
   default ChannelType getType() {
      return ChannelType.MEDIA;
   }

   default boolean isMediaDownloadHidden() {
      return this.getFlags().contains(ChannelFlag.HIDE_MEDIA_DOWNLOAD_OPTIONS);
   }
}
