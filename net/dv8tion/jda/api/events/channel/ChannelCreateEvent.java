package net.dv8tion.jda.api.events.channel;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;

public class ChannelCreateEvent extends GenericChannelEvent {
   public ChannelCreateEvent(@Nonnull JDA api, long responseNumber, Channel channel) {
      super(api, responseNumber, channel);
   }
}
