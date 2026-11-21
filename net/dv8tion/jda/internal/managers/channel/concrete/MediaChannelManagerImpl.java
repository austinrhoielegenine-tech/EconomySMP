package net.dv8tion.jda.internal.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.managers.channel.concrete.MediaChannelManager;
import net.dv8tion.jda.internal.managers.channel.ChannelManagerImpl;

public class MediaChannelManagerImpl extends ChannelManagerImpl<MediaChannel, MediaChannelManager> implements MediaChannelManager {
   public MediaChannelManagerImpl(MediaChannel channel) {
      super(channel);
   }
}
