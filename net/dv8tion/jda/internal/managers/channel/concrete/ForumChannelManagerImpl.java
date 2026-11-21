package net.dv8tion.jda.internal.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ForumChannelManager;
import net.dv8tion.jda.internal.managers.channel.ChannelManagerImpl;

public class ForumChannelManagerImpl extends ChannelManagerImpl<ForumChannel, ForumChannelManager> implements ForumChannelManager {
   public ForumChannelManagerImpl(ForumChannel channel) {
      super(channel);
   }
}
