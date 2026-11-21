package net.dv8tion.jda.api.events.guild.invite;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class GuildInviteCreateEvent extends GenericGuildInviteEvent {
   private final Invite invite;

   public GuildInviteCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Invite invite, @Nonnull GuildChannel channel) {
      super(api, responseNumber, invite.getCode(), channel);
      this.invite = invite;
   }

   @Nonnull
   public Invite getInvite() {
      return this.invite;
   }
}
