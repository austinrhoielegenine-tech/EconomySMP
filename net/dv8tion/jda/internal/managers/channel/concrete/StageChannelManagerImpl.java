package net.dv8tion.jda.internal.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.managers.channel.concrete.StageChannelManager;
import net.dv8tion.jda.internal.managers.channel.ChannelManagerImpl;

public class StageChannelManagerImpl extends ChannelManagerImpl<StageChannel, StageChannelManager> implements StageChannelManager {
   public StageChannelManagerImpl(StageChannel channel) {
      super(channel);
   }
}
