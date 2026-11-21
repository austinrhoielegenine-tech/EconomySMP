package net.dv8tion.jda.internal.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import net.dv8tion.jda.internal.managers.channel.ChannelManagerImpl;

public class ThreadChannelManagerImpl extends ChannelManagerImpl<ThreadChannel, ThreadChannelManager> implements ThreadChannelManager {
   public ThreadChannelManagerImpl(ThreadChannel channel) {
      super(channel);
   }
}
