package net.dv8tion.jda.internal.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.managers.channel.concrete.NewsChannelManager;
import net.dv8tion.jda.internal.managers.channel.ChannelManagerImpl;

public class NewsChannelManagerImpl extends ChannelManagerImpl<NewsChannel, NewsChannelManager> implements NewsChannelManager {
   public NewsChannelManagerImpl(NewsChannel channel) {
      super(channel);
   }
}
