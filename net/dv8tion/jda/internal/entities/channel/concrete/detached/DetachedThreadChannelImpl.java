package net.dv8tion.jda.internal.entities.channel.concrete.detached;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadMemberPaginationAction;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractGuildChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInteractionPermissionMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.ThreadChannelMixin;
import net.dv8tion.jda.internal.entities.detached.DetachedGuildImpl;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;
import net.dv8tion.jda.internal.utils.Helpers;

public class DetachedThreadChannelImpl extends AbstractGuildChannelImpl<DetachedThreadChannelImpl> implements ThreadChannel, ThreadChannelMixin<DetachedThreadChannelImpl>, IInteractionPermissionMixin<DetachedThreadChannelImpl> {
   private final ChannelType type;
   private ChannelInteractionPermissions interactionPermissions;
   private ThreadChannel.AutoArchiveDuration autoArchiveDuration;
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

   public DetachedThreadChannelImpl(long id, DetachedGuildImpl guild, ChannelType type) {
      super(id, guild);
      this.type = type;
   }

   public boolean isDetached() {
      return true;
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
      throw this.detachedException();
   }

   @Nonnull
   public List<Member> getMembers() {
      throw this.detachedException();
   }

   @Nonnull
   public IThreadContainerUnion getParentChannel() {
      throw this.detachedException();
   }

   @Nonnull
   public List<ForumTag> getAppliedTags() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Message> retrieveParentMessage() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Message> retrieveStartMessage() {
      throw this.detachedException();
   }

   @Nonnull
   public IPermissionContainer getPermissionContainer() {
      throw this.detachedException();
   }

   @Nonnull
   public List<ThreadMember> getThreadMembers() {
      throw this.detachedException();
   }

   @Nullable
   public ThreadMember getThreadMemberById(long id) {
      throw this.detachedException();
   }

   @Nonnull
   public CacheRestAction<ThreadMember> retrieveThreadMemberById(long id) {
      throw this.detachedException();
   }

   @Nonnull
   public ThreadMemberPaginationAction retrieveThreadMembers() {
      throw this.detachedException();
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
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Void> leave() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Void> addThreadMemberById(long id) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Void> removeThreadMemberById(long id) {
      throw this.detachedException();
   }

   @Nonnull
   public ThreadChannelManager getManager() {
      throw this.detachedException();
   }

   public void checkCanManage() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelInteractionPermissions getInteractionPermissions() {
      return this.interactionPermissions;
   }

   public DetachedThreadChannelImpl setLatestMessageIdLong(long latestMessageId) {
      this.latestMessageId = latestMessageId;
      return this;
   }

   public DetachedThreadChannelImpl setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration autoArchiveDuration) {
      this.autoArchiveDuration = autoArchiveDuration;
      return this;
   }

   public DetachedThreadChannelImpl setLocked(boolean locked) {
      this.locked = locked;
      return this;
   }

   public DetachedThreadChannelImpl setArchived(boolean archived) {
      this.archived = archived;
      return this;
   }

   public DetachedThreadChannelImpl setInvitable(boolean invitable) {
      this.invitable = invitable;
      return this;
   }

   public DetachedThreadChannelImpl setArchiveTimestamp(long archiveTimestamp) {
      this.archiveTimestamp = archiveTimestamp;
      return this;
   }

   public DetachedThreadChannelImpl setCreationTimestamp(long creationTimestamp) {
      this.creationTimestamp = creationTimestamp;
      return this;
   }

   public DetachedThreadChannelImpl setOwnerId(long ownerId) {
      this.ownerId = ownerId;
      return this;
   }

   public DetachedThreadChannelImpl setMessageCount(int messageCount) {
      this.messageCount = messageCount;
      return this;
   }

   public DetachedThreadChannelImpl setTotalMessageCount(int messageCount) {
      this.totalMessageCount = Math.max(messageCount, this.messageCount);
      return this;
   }

   public DetachedThreadChannelImpl setMemberCount(int memberCount) {
      this.memberCount = memberCount;
      return this;
   }

   public DetachedThreadChannelImpl setSlowmode(int slowmode) {
      this.slowmode = slowmode;
      return this;
   }

   public DetachedThreadChannelImpl setFlags(int flags) {
      this.flags = flags;
      return this;
   }

   @Nonnull
   public DetachedThreadChannelImpl setInteractionPermissions(@Nonnull ChannelInteractionPermissions interactionPermissions) {
      this.interactionPermissions = interactionPermissions;
      return this;
   }
}
