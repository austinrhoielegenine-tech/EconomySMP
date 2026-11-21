package net.dv8tion.jda.internal.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.internal.managers.channel.ChannelManagerImpl;

public class TextChannelManagerImpl extends ChannelManagerImpl<TextChannel, TextChannelManager> implements TextChannelManager {
   public TextChannelManagerImpl(TextChannel channel) {
      super(channel);
   }
}
