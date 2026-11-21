package net.dv8tion.jda.api.events.guild.member;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildMemberJoinEvent extends GenericGuildMemberEvent {
   public GuildMemberJoinEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member) {
      super(api, responseNumber, member);
   }
}
