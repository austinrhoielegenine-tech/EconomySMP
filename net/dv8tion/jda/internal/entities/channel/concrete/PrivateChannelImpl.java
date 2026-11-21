package net.dv8tion.jda.internal.entities.channel.concrete;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.internal.entities.channel.AbstractChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.PrivateChannelMixin;

public class PrivateChannelImpl extends AbstractChannelImpl<PrivateChannelImpl> implements PrivateChannel, PrivateChannelMixin<PrivateChannelImpl> {
   private User user;
   private long latestMessageId;

   public PrivateChannelImpl(JDA api, long id, @Nullable User user) {
      super(id, api);
      this.user = user;
   }

   public boolean isDetached() {
      return false;
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.PRIVATE;
   }

   @Nullable
   public User getUser() {
      this.updateUser();
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
      return this.user == null || !this.user.isBot();
   }

   public void checkCanAccess() {
   }

   public void checkCanSendMessage() {
      this.checkBot();
   }

   public void checkCanSendMessageEmbeds() {
   }

   public void checkCanSendFiles() {
   }

   public void checkCanViewHistory() {
   }

   public void checkCanAddReactions() {
   }

   public void checkCanRemoveReactions() {
   }

   public void checkCanControlMessagePins() {
   }

   public boolean canDeleteOtherUsersMessages() {
      return false;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public PrivateChannelImpl setLatestMessageIdLong(long latestMessageId) {
      this.latestMessageId = latestMessageId;
      return this;
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof PrivateChannelImpl)) {
         return false;
      } else {
         PrivateChannelImpl impl = (PrivateChannelImpl)obj;
         return impl.id == this.id;
      }
   }

   private void updateUser() {
      if (this.user != null) {
         User realUser = this.getJDA().getUserById(this.user.getIdLong());
         if (realUser != null) {
            this.user = realUser;
         }

      }
   }

   private void checkBot() {
      if (this.getUser() != null && this.getUser().isBot()) {
         throw new UnsupportedOperationException("Cannot send a private message between bots.");
      }
   }
}
