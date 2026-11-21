package net.dv8tion.jda.api.events.guild.member.update;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildMemberUpdateFlagsEvent extends GenericGuildMemberUpdateEvent<EnumSet<Member.MemberFlag>> {
   public static final String IDENTIFIER = "flags";

   public GuildMemberUpdateFlagsEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull EnumSet<Member.MemberFlag> previous) {
      super(api, responseNumber, member, previous, member.getFlags(), "flags");
   }
}
