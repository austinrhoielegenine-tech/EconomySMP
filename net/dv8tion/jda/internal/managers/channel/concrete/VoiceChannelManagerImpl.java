package net.dv8tion.jda.internal.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import net.dv8tion.jda.internal.managers.channel.ChannelManagerImpl;

public class VoiceChannelManagerImpl extends ChannelManagerImpl<VoiceChannel, VoiceChannelManager> implements VoiceChannelManager {
   public VoiceChannelManagerImpl(VoiceChannel channel) {
      super(channel);
   }
}
