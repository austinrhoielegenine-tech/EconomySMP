package net.dv8tion.jda.api.managers.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.managers.channel.attribute.IAgeRestrictedChannelManager;
import net.dv8tion.jda.api.managers.channel.attribute.IPostContainerManager;
import net.dv8tion.jda.api.managers.channel.attribute.ISlowmodeChannelManager;
import net.dv8tion.jda.api.managers.channel.middleman.StandardGuildChannelManager;

public interface ForumChannelManager extends StandardGuildChannelManager<ForumChannel, ForumChannelManager>, IPostContainerManager<ForumChannel, ForumChannelManager>, IAgeRestrictedChannelManager<ForumChannel, ForumChannelManager>, ISlowmodeChannelManager<ForumChannel, ForumChannelManager> {
   @Nonnull
   @CheckReturnValue
   ForumChannelManager setDefaultLayout(@Nonnull ForumChannel.Layout var1);
}
