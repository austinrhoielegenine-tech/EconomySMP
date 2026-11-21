package net.dv8tion.jda.internal.entities.channel.concrete.detached;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractStandardGuildChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInteractionPermissionMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.VoiceChannelMixin;
import net.dv8tion.jda.internal.entities.detached.DetachedGuildImpl;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;

public class DetachedVoiceChannelImpl extends AbstractStandardGuildChannelImpl<DetachedVoiceChannelImpl> implements VoiceChannel, VoiceChannelMixin<DetachedVoiceChannelImpl>, IInteractionPermissionMixin<DetachedVoiceChannelImpl> {
   private ChannelInteractionPermissions interactionPermissions;
   private String region;
   private String status = "";
   private long latestMessageId;
   private int bitrate;
   private int userLimit;
   private int slowmode;
   private boolean nsfw;

   public DetachedVoiceChannelImpl(long id, DetachedGuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return true;
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.VOICE;
   }

   public int getBitrate() {
      return this.bitrate;
   }

   @Nullable
   public String getRegionRaw() {
      return this.region;
   }

   public int getUserLimit() {
      return this.userLimit;
   }

   public boolean isNSFW() {
      return this.nsfw;
   }

   public int getSlowmode() {
      return this.slowmode;
   }

   public long getLatestMessageIdLong() {
      return this.latestMessageId;
   }

   @Nonnull
   public List<Member> getMembers() {
      throw this.detachedException();
   }

   @Nonnull
   public VoiceChannelManager getManager() {
      throw this.detachedException();
   }

   @Nonnull
   public String getStatus() {
      return this.status;
   }

   @Nonnull
   public AuditableRestAction<Void> modifyStatus(@Nonnull String status) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelInteractionPermissions getInteractionPermissions() {
      return this.interactionPermissions;
   }

   public DetachedVoiceChannelImpl setBitrate(int bitrate) {
      this.bitrate = bitrate;
      return this;
   }

   public DetachedVoiceChannelImpl setRegion(String region) {
      this.region = region;
      return this;
   }

   public DetachedVoiceChannelImpl setUserLimit(int userLimit) {
      this.userLimit = userLimit;
      return this;
   }

   public DetachedVoiceChannelImpl setNSFW(boolean nsfw) {
      this.nsfw = nsfw;
      return this;
   }

   public DetachedVoiceChannelImpl setSlowmode(int slowmode) {
      this.slowmode = slowmode;
      return this;
   }

   public DetachedVoiceChannelImpl setLatestMessageIdLong(long latestMessageId) {
      this.latestMessageId = latestMessageId;
      return this;
   }

   public DetachedVoiceChannelImpl setStatus(String status) {
      this.status = status;
      return this;
   }

   @Nonnull
   public DetachedVoiceChannelImpl setInteractionPermissions(@Nonnull ChannelInteractionPermissions interactionPermissions) {
      this.interactionPermissions = interactionPermissions;
      return this;
   }
}
