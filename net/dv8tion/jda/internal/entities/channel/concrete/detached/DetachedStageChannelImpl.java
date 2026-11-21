package net.dv8tion.jda.internal.entities.channel.concrete.detached;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.managers.channel.concrete.StageChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.StageInstanceAction;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractStandardGuildChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInteractionPermissionMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.StageChannelMixin;
import net.dv8tion.jda.internal.entities.detached.DetachedGuildImpl;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;
import net.dv8tion.jda.internal.utils.Checks;

public class DetachedStageChannelImpl extends AbstractStandardGuildChannelImpl<DetachedStageChannelImpl> implements StageChannel, StageChannelMixin<DetachedStageChannelImpl>, IInteractionPermissionMixin<DetachedStageChannelImpl> {
   private ChannelInteractionPermissions interactionPermissions;
   private String region;
   private int bitrate;
   private int userlimit;
   private int slowmode;
   private boolean ageRestricted;
   private long latestMessageId;

   public DetachedStageChannelImpl(long id, DetachedGuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return true;
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.STAGE;
   }

   public int getBitrate() {
      return this.bitrate;
   }

   public int getUserLimit() {
      return this.userlimit;
   }

   @Nullable
   public String getRegionRaw() {
      return this.region;
   }

   @Nullable
   public StageInstance getStageInstance() {
      throw this.detachedException();
   }

   @Nonnull
   public List<Member> getMembers() {
      throw this.detachedException();
   }

   @Nonnull
   public StageInstanceAction createStageInstance(@Nonnull String topic) {
      throw this.detachedException();
   }

   public int getSlowmode() {
      return this.slowmode;
   }

   public boolean isNSFW() {
      return this.ageRestricted;
   }

   public boolean canTalk(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return member.hasPermission(this, new Permission[]{Permission.MESSAGE_SEND});
   }

   public long getLatestMessageIdLong() {
      return this.latestMessageId;
   }

   @Nonnull
   public StageChannelManager getManager() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Void> requestToSpeak() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Void> cancelRequestToSpeak() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelInteractionPermissions getInteractionPermissions() {
      return this.interactionPermissions;
   }

   public DetachedStageChannelImpl setBitrate(int bitrate) {
      this.bitrate = bitrate;
      return this;
   }

   public DetachedStageChannelImpl setUserLimit(int userlimit) {
      this.userlimit = userlimit;
      return this;
   }

   public DetachedStageChannelImpl setRegion(String region) {
      this.region = region;
      return this;
   }

   public DetachedStageChannelImpl setNSFW(boolean ageRestricted) {
      this.ageRestricted = ageRestricted;
      return this;
   }

   public DetachedStageChannelImpl setSlowmode(int slowmode) {
      this.slowmode = slowmode;
      return this;
   }

   public DetachedStageChannelImpl setLatestMessageIdLong(long latestMessageId) {
      this.latestMessageId = latestMessageId;
      return this;
   }

   @Nonnull
   public DetachedStageChannelImpl setInteractionPermissions(@Nonnull ChannelInteractionPermissions interactionPermissions) {
      this.interactionPermissions = interactionPermissions;
      return this;
   }
}
