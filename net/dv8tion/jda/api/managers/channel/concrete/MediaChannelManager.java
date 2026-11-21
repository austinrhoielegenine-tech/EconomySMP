package net.dv8tion.jda.api.managers.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.managers.channel.attribute.IAgeRestrictedChannelManager;
import net.dv8tion.jda.api.managers.channel.attribute.IPostContainerManager;
import net.dv8tion.jda.api.managers.channel.attribute.ISlowmodeChannelManager;
import net.dv8tion.jda.api.managers.channel.middleman.StandardGuildChannelManager;

public interface MediaChannelManager extends StandardGuildChannelManager<MediaChannel, MediaChannelManager>, IPostContainerManager<MediaChannel, MediaChannelManager>, IAgeRestrictedChannelManager<MediaChannel, MediaChannelManager>, ISlowmodeChannelManager<MediaChannel, MediaChannelManager> {
   @Nonnull
   @CheckReturnValue
   MediaChannelManager setHideMediaDownloadOption(boolean var1);
}
