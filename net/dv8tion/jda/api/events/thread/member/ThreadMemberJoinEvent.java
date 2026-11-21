package net.dv8tion.jda.api.events.thread.member;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class ThreadMemberJoinEvent extends GenericThreadMemberEvent {
   public ThreadMemberJoinEvent(@Nonnull JDA api, long responseNumber, ThreadChannel thread, ThreadMember threadMember) {
      super(api, responseNumber, thread, threadMember.getIdLong(), threadMember);
   }

   @Nonnull
   public ThreadMember getThreadMember() {
      return super.getThreadMember();
   }

   @Nonnull
   public Member getMember() {
      return this.getThreadMember().getMember();
   }
}
