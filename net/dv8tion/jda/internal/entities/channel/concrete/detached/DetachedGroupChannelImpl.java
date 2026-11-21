package net.dv8tion.jda.internal.entities.channel.concrete.detached;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.internal.entities.channel.AbstractChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.GroupChannelMixin;
import net.dv8tion.jda.internal.entities.detached.mixin.IDetachableEntityMixin;

public class DetachedGroupChannelImpl extends AbstractChannelImpl<DetachedGroupChannelImpl> implements GroupChannelMixin<DetachedGroupChannelImpl>, IDetachableEntityMixin {
   private long latestMessageId;
   private long ownerId;
   private String icon;

   public DetachedGroupChannelImpl(JDA api, long id) {
      super(id, api);
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.GROUP;
   }

   public boolean isDetached() {
      return true;
   }

   public long getOwnerIdLong() {
      return this.ownerId;
   }

   @Nullable
   public String getIconId() {
      return this.icon;
   }

   public long getLatestMessageIdLong() {
      return this.latestMessageId;
   }

   public boolean canTalk() {
      return false;
   }

   public void checkCanAccess() {
      throw this.detachedException();
   }

   public void checkCanSendMessage() {
      throw this.detachedException();
   }

   public void checkCanSendMessageEmbeds() {
      throw this.detachedException();
   }

   public void checkCanSendFiles() {
      throw this.detachedException();
   }

   public void checkCanViewHistory() {
      throw this.detachedException();
   }

   public void checkCanAddReactions() {
      throw this.detachedException();
   }

   public void checkCanRemoveReactions() {
      throw this.detachedException();
   }

   public void checkCanControlMessagePins() {
      throw this.detachedException();
   }

   public boolean canDeleteOtherUsersMessages() {
      return false;
   }

   public DetachedGroupChannelImpl setLatestMessageIdLong(long latestMessageId) {
      this.latestMessageId = latestMessageId;
      return this;
   }

   public DetachedGroupChannelImpl setOwnerId(long ownerId) {
      this.ownerId = ownerId;
      return this;
   }

   public DetachedGroupChannelImpl setIcon(@Nullable String icon) {
      this.icon = icon;
      return this;
   }
}
