package net.dv8tion.jda.internal.entities.channel.concrete;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.LongStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadMemberPaginationAction;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractGuildChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.ThreadChannelMixin;
import net.dv8tion.jda.internal.managers.channel.concrete.ThreadChannelManagerImpl;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.ThreadMemberPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class ThreadChannelImpl extends AbstractGuildChannelImpl<ThreadChannelImpl> implements ThreadChannel, ThreadChannelMixin<ThreadChannelImpl> {
   private final ChannelType type;
   private final CacheView.SimpleCacheView<ThreadMember> threadMembers = new CacheView.SimpleCacheView<ThreadMember>(ThreadMember.class, (Function)null);
   private TLongSet appliedTags = new TLongHashSet(5);
   private ThreadChannel.AutoArchiveDuration autoArchiveDuration;
   private IThreadContainerUnion parentChannel;
   private boolean locked;
   private boolean archived;
   private boolean invitable;
   private long archiveTimestamp;
   private long creationTimestamp;
   private long ownerId;
   private long latestMessageId;
   private int messageCount;
   private int totalMessageCount;
   private int memberCount;
   private int slowmode;
   private int flags;

   public ThreadChannelImpl(long id, GuildImpl guild, ChannelType type) {
      super(id, guild);
      this.type = type;
   }

   public boolean isDetached() {
      return false;
   }

   @Nonnull
   public GuildImpl getGuild() {
      return (GuildImpl)super.getGuild();
   }

   @Nonnull
   public EnumSet<ChannelFlag> getFlags() {
      return ChannelFlag.fromRaw(this.flags);
   }

   @Nonnull
   public ChannelType getType() {
      return this.type;
   }

   public long getLatestMessageIdLong() {
      return this.latestMessageId;
   }

   public int getMessageCount() {
      return this.messageCount;
   }

   public int getTotalMessageCount() {
      return this.totalMessageCount;
   }

   public int getMemberCount() {
      return this.memberCount;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public boolean canTalk(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return this.type == ChannelType.GUILD_PRIVATE_THREAD && this.threadMembers.get(member.getIdLong()) == null ? member.hasPermission(this.getParentChannel(), new Permission[]{Permission.MANAGE_THREADS, Permission.MESSAGE_SEND_IN_THREADS}) : member.hasPermission(this.getParentChannel(), new Permission[]{Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND_IN_THREADS});
   }

   @Nonnull
   public List<Member> getMembers() {
      return Collections.emptyList();
   }

   @Nonnull
   public IThreadContainerUnion getParentChannel() {
      IThreadContainer realChannel = (IThreadContainer)this.getGuild().getChannelById(IThreadContainer.class, this.parentChannel.getIdLong());
      if (realChannel != null) {
         this.parentChannel = (IThreadContainerUnion)realChannel;
      }

      return this.parentChannel;
   }

   @Nonnull
   public List<ForumTag> getAppliedTags() {
      IThreadContainerUnion parent = this.getParentChannel();
      return parent.getType() != ChannelType.FORUM ? Collections.emptyList() : (List)parent.asForumChannel().getAvailableTagCache().stream().filter((tag) -> this.appliedTags.contains(tag.getIdLong())).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public RestAction<Message> retrieveParentMessage() {
      return this.getParentMessageChannel().retrieveMessageById(this.getIdLong());
   }

   @Nonnull
   public RestAction<Message> retrieveStartMessage() {
      return this.retrieveMessageById(this.getId());
   }

   @Nonnull
   public IPermissionContainer getPermissionContainer() {
      return this.getParentChannel();
   }

   @Nonnull
   public List<ThreadMember> getThreadMembers() {
      return this.threadMembers.asList();
   }

   @Nullable
   public ThreadMember getThreadMemberById(long id) {
      return (ThreadMember)this.threadMembers.get(id);
   }

   @Nonnull
   public CacheRestAction<ThreadMember> retrieveThreadMemberById(long id) {
      JDAImpl jda = (JDAImpl)this.getJDA();
      return new DeferredRestAction(jda, ThreadMember.class, () -> this.getThreadMemberById(id), () -> {
         Route.CompiledRoute route = Route.Channels.GET_THREAD_MEMBER.compile(this.getId(), Long.toUnsignedString(id)).withQueryParams("with_member", "true");
         return new RestActionImpl(jda, route, (resp, req) -> jda.getEntityBuilder().createThreadMember(this.getGuild(), this, resp.getObject()));
      });
   }

   @Nonnull
   public ThreadMemberPaginationAction retrieveThreadMembers() {
      return new ThreadMemberPaginationActionImpl(this);
   }

   public long getOwnerIdLong() {
      return this.ownerId;
   }

   public boolean isArchived() {
      return this.archived;
   }

   public boolean isInvitable() {
      if (this.type != ChannelType.GUILD_PRIVATE_THREAD) {
         throw new UnsupportedOperationException("Only private threads support the concept of invitable.");
      } else {
         return this.invitable;
      }
   }

   @Nonnull
   public OffsetDateTime getTimeArchiveInfoLastModified() {
      return Helpers.toOffset(this.archiveTimestamp);
   }

   @Nonnull
   public ThreadChannel.AutoArchiveDuration getAutoArchiveDuration() {
      return this.autoArchiveDuration;
   }

   @Nonnull
   public OffsetDateTime getTimeCreated() {
      return this.creationTimestamp == 0L ? TimeUtil.getTimeCreated(this.getIdLong()) : Helpers.toOffset(this.creationTimestamp);
   }

   public int getSlowmode() {
      return this.slowmode;
   }

   @Nonnull
   public RestAction<Void> join() {
      this.checkUnarchived();
      Route.CompiledRoute route = Route.Channels.JOIN_THREAD.compile(this.getId());
      return new RestActionImpl<Void>(this.api, route);
   }

   @Nonnull
   public RestAction<Void> leave() {
      this.checkUnarchived();
      Route.CompiledRoute route = Route.Channels.LEAVE_THREAD.compile(this.getId());
      return new RestActionImpl<Void>(this.api, route);
   }

   @Nonnull
   public RestAction<Void> addThreadMemberById(long id) {
      this.checkUnarchived();
      this.checkInvitable();
      this.checkPermission(Permission.MESSAGE_SEND_IN_THREADS);
      Route.CompiledRoute route = Route.Channels.ADD_THREAD_MEMBER.compile(this.getId(), Long.toUnsignedString(id));
      return new RestActionImpl<Void>(this.api, route);
   }

   @Nonnull
   public RestAction<Void> removeThreadMemberById(long id) {
      this.checkUnarchived();
      boolean privateThreadOwner = this.type == ChannelType.GUILD_PRIVATE_THREAD && this.ownerId == this.api.getSelfUser().getIdLong();
      if (!privateThreadOwner) {
         this.checkPermission(Permission.MANAGE_THREADS);
      }

      Route.CompiledRoute route = Route.Channels.REMOVE_THREAD_MEMBER.compile(this.getId(), Long.toUnsignedString(id));
      return new RestActionImpl<Void>(this.api, route);
   }

   @Nonnull
   public ThreadChannelManager getManager() {
      return new ThreadChannelManagerImpl(this);
   }

   public void checkCanManage() {
      if (!this.isOwner()) {
         this.checkPermission(Permission.MANAGE_THREADS);
      }
   }

   public CacheView.SimpleCacheView<ThreadMember> getThreadMemberView() {
      return this.threadMembers;
   }

   public ThreadChannelImpl setLatestMessageIdLong(long latestMessageId) {
      this.latestMessageId = latestMessageId;
      return this;
   }

   public ThreadChannelImpl setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration autoArchiveDuration) {
      this.autoArchiveDuration = autoArchiveDuration;
      return this;
   }

   public ThreadChannelImpl setParentChannel(IThreadContainer channel) {
      this.parentChannel = (IThreadContainerUnion)channel;
      return this;
   }

   public ThreadChannelImpl setLocked(boolean locked) {
      this.locked = locked;
      return this;
   }

   public ThreadChannelImpl setArchived(boolean archived) {
      this.archived = archived;
      return this;
   }

   public ThreadChannelImpl setInvitable(boolean invitable) {
      this.invitable = invitable;
      return this;
   }

   public ThreadChannelImpl setArchiveTimestamp(long archiveTimestamp) {
      this.archiveTimestamp = archiveTimestamp;
      return this;
   }

   public ThreadChannelImpl setCreationTimestamp(long creationTimestamp) {
      this.creationTimestamp = creationTimestamp;
      return this;
   }

   public ThreadChannelImpl setOwnerId(long ownerId) {
      this.ownerId = ownerId;
      return this;
   }

   public ThreadChannelImpl setMessageCount(int messageCount) {
      this.messageCount = messageCount;
      return this;
   }

   public ThreadChannelImpl setTotalMessageCount(int messageCount) {
      this.totalMessageCount = Math.max(messageCount, this.messageCount);
      return this;
   }

   public ThreadChannelImpl setMemberCount(int memberCount) {
      this.memberCount = memberCount;
      return this;
   }

   public ThreadChannelImpl setSlowmode(int slowmode) {
      this.slowmode = slowmode;
      return this;
   }

   public ThreadChannelImpl setAppliedTags(LongStream tags) {
      TLongSet set = new TLongHashSet(5);
      Objects.requireNonNull(set);
      tags.forEach(set::add);
      this.appliedTags = set;
      return this;
   }

   public ThreadChannelImpl setFlags(int flags) {
      this.flags = flags;
      return this;
   }

   public long getArchiveTimestamp() {
      return this.archiveTimestamp;
   }

   public TLongSet getAppliedTagsSet() {
      return this.appliedTags;
   }

   public int getRawFlags() {
      return this.flags;
   }

   private void checkUnarchived() {
      if (this.archived) {
         throw new IllegalStateException("Cannot modify a ThreadChannel while it is archived!");
      }
   }

   private void checkInvitable() {
      if (this.ownerId != this.api.getSelfUser().getIdLong()) {
         if (!this.isPublic() && !this.isInvitable()) {
            this.checkPermission(Permission.MANAGE_THREADS);
         }

      }
   }
}
