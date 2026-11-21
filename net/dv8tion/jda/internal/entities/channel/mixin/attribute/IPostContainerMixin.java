package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.requests.restaction.ForumPostAction;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.requests.restaction.ForumPostActionImpl;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;

public interface IPostContainerMixin<T extends IPostContainerMixin<T>> extends IPostContainer, IThreadContainerMixin<T> {
   @Nonnull
   SortedSnowflakeCacheViewImpl<ForumTag> getAvailableTagCache();

   @Nonnull
   default ForumPostAction createForumPost(@Nonnull String name, @Nonnull MessageCreateData message) {
      this.checkAttached();
      this.checkPermission(Permission.MESSAGE_SEND);
      return new ForumPostActionImpl(this, name, (MessageCreateBuilder)(new MessageCreateBuilder()).applyData(message));
   }

   @Nonnull
   default ThreadChannelAction createThreadChannel(@Nonnull String name) {
      throw new UnsupportedOperationException("You cannot create threads without a message payload in forum/media channels! Use createForumPost(...) instead.");
   }

   @Nonnull
   default ThreadChannelAction createThreadChannel(@Nonnull String name, @Nonnull String messageId) {
      throw new UnsupportedOperationException("You cannot create threads without a message payload in forum/media channels! Use createForumPost(...) instead.");
   }

   T setDefaultReaction(DataObject var1);

   T setDefaultSortOrder(int var1);

   T setFlags(int var1);

   int getRawSortOrder();

   int getRawFlags();
}
