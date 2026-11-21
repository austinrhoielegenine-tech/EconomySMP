package net.dv8tion.jda.api.events.guild.invite;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class GuildInviteDeleteEvent extends GenericGuildInviteEvent {
   public GuildInviteDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull String code, @Nonnull GuildChannel channel) {
      super(api, responseNumber, code, channel);
   }
}
