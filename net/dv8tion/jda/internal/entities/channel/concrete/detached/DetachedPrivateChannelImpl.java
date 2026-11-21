package net.dv8tion.jda.internal.entities.channel.concrete.detached;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.exceptions.DetachedEntityException;
import net.dv8tion.jda.internal.entities.channel.AbstractChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.PrivateChannelMixin;

public class DetachedPrivateChannelImpl extends AbstractChannelImpl<DetachedPrivateChannelImpl> implements PrivateChannel, PrivateChannelMixin<DetachedPrivateChannelImpl> {
   private final User user;
   private long latestMessageId;

   public DetachedPrivateChannelImpl(JDA api, long id, @Nullable User user) {
      super(id, api);
      this.user = user;
   }

   @Nonnull
   public DetachedEntityException detachedException() {
      return new DetachedEntityException("Cannot perform action in friend DMs");
   }

   public boolean isDetached() {
      return true;
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.PRIVATE;
   }

   @Nullable
   public User getUser() {
      return this.user;
   }

   @Nonnull
   public String getName() {
      return PrivateChannelMixin.super.getName();
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

   public DetachedPrivateChannelImpl setLatestMessageIdLong(long latestMessageId) {
      this.latestMessageId = latestMessageId;
      return this;
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof DetachedPrivateChannelImpl)) {
         return false;
      } else {
         DetachedPrivateChannelImpl impl = (DetachedPrivateChannelImpl)obj;
         return impl.id == this.id;
      }
   }
}
