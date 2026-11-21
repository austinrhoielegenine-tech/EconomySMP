package net.dv8tion.jda.api.events.guild.member.update;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildMemberUpdateTimeOutEvent extends GenericGuildMemberUpdateEvent<OffsetDateTime> {
   public static final String IDENTIFIER = "timeout_time";

   public GuildMemberUpdateTimeOutEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable OffsetDateTime previous) {
      super(api, responseNumber, member, previous, member.getTimeOutEnd(), "timeout_time");
   }

   @Nullable
   public OffsetDateTime getOldTimeOutEnd() {
      return (OffsetDateTime)this.getOldValue();
   }

   @Nullable
   public OffsetDateTime getNewTimeOutEnd() {
      return (OffsetDateTime)this.getNewValue();
   }
}
