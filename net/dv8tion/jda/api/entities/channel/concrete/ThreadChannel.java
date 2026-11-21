package net.dv8tion.jda.api.entities.channel.concrete;

import java.time.OffsetDateTime;
import java.util.Formatter;
import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IMemberContainer;
import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadMemberPaginationAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public interface ThreadChannel extends GuildMessageChannel, IMemberContainer, ISlowmodeChannel {
   default boolean isPublic() {
      ChannelType type = this.getType();
      return type == ChannelType.GUILD_PUBLIC_THREAD || type == ChannelType.GUILD_NEWS_THREAD;
   }

   int getMessageCount();

   int getTotalMessageCount();

   int getMemberCount();

   default boolean isJoined() {
      return this.getSelfThreadMember() != null;
   }

   boolean isLocked();

   boolean isInvitable();

   default boolean isPinned() {
      return this.getFlags().contains(ChannelFlag.PINNED);
   }

   @Nonnull
   IThreadContainerUnion getParentChannel();

   @Nonnull
   default GuildMessageChannelUnion getParentMessageChannel() {
      if (this.getParentChannel() instanceof GuildMessageChannel) {
         return (GuildMessageChannelUnion)this.getParentChannel();
      } else {
         throw new UnsupportedOperationException("Parent of this thread is not a MessageChannel. Parent: " + this.getParentChannel());
      }
   }

   @Nonnull
   @Unmodifiable List<ForumTag> getAppliedTags();

   @Nonnull
   @CheckReturnValue
   RestAction<Message> retrieveParentMessage();

   @Nonnull
   @CheckReturnValue
   RestAction<Message> retrieveStartMessage();

   @Nullable
   default ThreadMember getSelfThreadMember() {
      return this.getThreadMember((User)this.getJDA().getSelfUser());
   }

   @Nonnull
   List<ThreadMember> getThreadMembers();

   @Nullable
   default ThreadMember getThreadMember(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return this.getThreadMemberById(member.getId());
   }

   @Nullable
   default ThreadMember getThreadMember(@Nonnull User user) {
      Checks.notNull(user, "User");
      return this.getThreadMemberById(user.getId());
   }

   @Nullable
   default ThreadMember getThreadMemberById(@Nonnull String id) {
      return this.getThreadMemberById(MiscUtil.parseSnowflake(id));
   }

   @Nullable
   ThreadMember getThreadMemberById(long var1);

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<ThreadMember> retrieveThreadMember(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return this.retrieveThreadMemberById(member.getIdLong());
   }

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<ThreadMember> retrieveThreadMember(@Nonnull User user) {
      Checks.notNull(user, "User");
      return this.retrieveThreadMemberById(user.getIdLong());
   }

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<ThreadMember> retrieveThreadMemberById(@Nonnull String id) {
      return this.retrieveThreadMemberById(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   @CheckReturnValue
   CacheRestAction<ThreadMember> retrieveThreadMemberById(long var1);

   @Nonnull
   @CheckReturnValue
   ThreadMemberPaginationAction retrieveThreadMembers();

   default boolean isOwner() {
      return this.getJDA().getSelfUser().getIdLong() == this.getOwnerIdLong();
   }

   long getOwnerIdLong();

   @Nonnull
   default String getOwnerId() {
      return Long.toUnsignedString(this.getOwnerIdLong());
   }

   @Nullable
   default Member getOwner() {
      return this.getGuild().getMemberById(this.getOwnerIdLong());
   }

   @Nullable
   default ThreadMember getOwnerThreadMember() {
      return this.getThreadMemberById(this.getOwnerIdLong());
   }

   boolean isArchived();

   @Nonnull
   OffsetDateTime getTimeArchiveInfoLastModified();

   @Nonnull
   AutoArchiveDuration getAutoArchiveDuration();

   @Nonnull
   OffsetDateTime getTimeCreated();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> join();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> leave();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> addThreadMemberById(long var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> addThreadMemberById(@Nonnull String id) {
      return this.addThreadMemberById(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> addThreadMember(@Nonnull User user) {
      Checks.notNull(user, "User");
      return this.addThreadMemberById(user.getIdLong());
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> addThreadMember(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return this.addThreadMemberById(member.getIdLong());
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Void> removeThreadMemberById(long var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> removeThreadMemberById(@Nonnull String id) {
      return this.removeThreadMemberById(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> removeThreadMember(@Nonnull User user) {
      Checks.notNull(user, "User");
      return this.removeThreadMemberById(user.getId());
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> removeThreadMember(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return this.removeThreadMemberById(member.getIdLong());
   }

   @Nonnull
   @CheckReturnValue
   ThreadChannelManager getManager();

   default void formatTo(Formatter formatter, int flags, int width, int precision) {
      boolean leftJustified = (flags & 1) == 1;
      boolean upper = (flags & 2) == 2;
      boolean alt = (flags & 4) == 4;
      String out;
      if (alt) {
         out = "#" + (upper ? this.getName().toUpperCase(formatter.locale()) : this.getName());
      } else {
         out = this.getAsMention();
      }

      MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
   }

   public static enum AutoArchiveDuration {
      TIME_1_HOUR(60),
      TIME_24_HOURS(1440),
      TIME_3_DAYS(4320),
      TIME_1_WEEK(10080);

      private final int minutes;

      private AutoArchiveDuration(int minutes) {
         this.minutes = minutes;
      }

      public int getMinutes() {
         return this.minutes;
      }

      @Nonnull
      public static AutoArchiveDuration fromKey(int minutes) {
         for(AutoArchiveDuration duration : values()) {
            if (duration.getMinutes() == minutes) {
               return duration;
            }
         }

         throw new IllegalArgumentException("Provided key was not recognized. Minutes: " + minutes);
      }

      // $FF: synthetic method
      private static AutoArchiveDuration[] $values() {
         return new AutoArchiveDuration[]{TIME_1_HOUR, TIME_24_HOURS, TIME_3_DAYS, TIME_1_WEEK};
      }
   }
}
