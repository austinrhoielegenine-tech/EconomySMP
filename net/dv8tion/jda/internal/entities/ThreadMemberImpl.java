package net.dv8tion.jda.internal.entities;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.internal.entities.channel.concrete.ThreadChannelImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class ThreadMemberImpl implements ThreadMember {
   private final JDA api;
   private final ThreadChannelImpl thread;
   private Member member;
   private long joinedTimestamp;

   public ThreadMemberImpl(Member member, ThreadChannelImpl thread) {
      this.api = member.getJDA();
      this.member = member;
      this.thread = thread;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public Guild getGuild() {
      return this.thread.getGuild();
   }

   @Nonnull
   public ThreadChannel getThread() {
      return this.thread;
   }

   @Nonnull
   public User getUser() {
      return this.member.getUser();
   }

   @Nonnull
   public Member getMember() {
      return this.member;
   }

   @Nonnull
   public OffsetDateTime getTimeJoined() {
      return Helpers.toOffset(this.joinedTimestamp);
   }

   @Nonnull
   public String getAsMention() {
      return this.member.getAsMention();
   }

   public long getIdLong() {
      return this.member.getIdLong();
   }

   public ThreadMemberImpl setJoinedTimestamp(long joinedTimestamp) {
      this.joinedTimestamp = joinedTimestamp;
      return this;
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("member", this.getMember()).toString();
   }
}
