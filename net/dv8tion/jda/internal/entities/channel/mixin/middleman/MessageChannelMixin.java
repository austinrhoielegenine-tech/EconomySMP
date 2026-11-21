package net.dv8tion.jda.internal.entities.channel.mixin.middleman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PinnedMessagePaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import net.dv8tion.jda.internal.entities.channel.mixin.ChannelMixin;
import net.dv8tion.jda.internal.requests.RestActionImpl;

public interface MessageChannelMixin<T extends MessageChannelMixin<T>> extends MessageChannel, MessageChannelUnion, ChannelMixin<T> {
   @Nonnull
   default List<CompletableFuture<Void>> purgeMessages(@Nonnull List<? extends Message> messages) {
      this.checkCanAccess();
      if (messages != null && !messages.isEmpty()) {
         if (!this.canDeleteOtherUsersMessages()) {
            for(Message m : messages) {
               if (!m.getAuthor().equals(this.getJDA().getSelfUser())) {
                  if (this.getType() == ChannelType.PRIVATE) {
                     throw new IllegalStateException("Cannot delete messages of other users in a private channel");
                  }

                  throw new InsufficientPermissionException((GuildChannel)this, Permission.MESSAGE_MANAGE, "Cannot delete messages of other users");
               }
            }
         }

         return MessageChannelUnion.super.purgeMessages(messages);
      } else {
         return Collections.emptyList();
      }
   }

   @Nonnull
   default List<CompletableFuture<Void>> purgeMessagesById(@Nonnull long... messageIds) {
      this.checkCanAccess();
      if (messageIds != null && messageIds.length != 0) {
         if (!this.canDeleteOtherUsersMessages()) {
            return MessageChannelUnion.super.purgeMessagesById(messageIds);
         } else {
            List<CompletableFuture<Void>> list = new ArrayList();
            TreeSet<Long> bulk = new TreeSet(Comparator.reverseOrder());
            TreeSet<Long> norm = new TreeSet(Comparator.reverseOrder());
            long twoWeeksAgo = TimeUtil.getDiscordTimestamp(System.currentTimeMillis() - 1209600000L + 10000L);

            for(long messageId : messageIds) {
               if (messageId > twoWeeksAgo) {
                  bulk.add(messageId);
               } else {
                  norm.add(messageId);
               }
            }

            if (!bulk.isEmpty()) {
               List<String> toDelete = new ArrayList(100);

               while(!bulk.isEmpty()) {
                  toDelete.clear();

                  for(int i = 0; i < 100 && !bulk.isEmpty(); ++i) {
                     toDelete.add(Long.toUnsignedString((Long)bulk.pollLast()));
                  }

                  if (toDelete.size() == 1) {
                     list.add(this.deleteMessageById((String)toDelete.get(0)).submit());
                  } else if (!toDelete.isEmpty()) {
                     list.add(this.bulkDeleteMessages(toDelete).submit());
                  }
               }
            }

            if (!norm.isEmpty()) {
               for(long message : norm) {
                  list.add(this.deleteMessageById(message).submit());
               }
            }

            return list;
         }
      } else {
         return Collections.emptyList();
      }
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessage(@Nonnull CharSequence text) {
      this.checkCanSendMessage();
      return MessageChannelUnion.super.sendMessage(text);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessageEmbeds(@Nonnull MessageEmbed embed, @Nonnull MessageEmbed... other) {
      this.checkCanSendMessage();
      this.checkCanSendMessageEmbeds();
      return MessageChannelUnion.super.sendMessageEmbeds(embed, other);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      this.checkCanSendMessage();
      this.checkCanSendMessageEmbeds();
      return MessageChannelUnion.super.sendMessageEmbeds(embeds);
   }

   @Nonnull
   default MessageCreateAction sendMessageComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      this.checkCanSendMessage();
      return MessageChannelUnion.super.sendMessageComponents(components);
   }

   @Nonnull
   default MessageCreateAction sendMessagePoll(@Nonnull MessagePollData poll) {
      this.checkCanSendMessage();
      return MessageChannelUnion.super.sendMessagePoll(poll);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessage(@Nonnull MessageCreateData msg) {
      this.checkCanSendMessage();
      return MessageChannelUnion.super.sendMessage(msg);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendFiles(@Nonnull Collection<? extends FileUpload> files) {
      this.checkCanSendMessage();
      this.checkCanSendFiles();
      return MessageChannelUnion.super.sendFiles(files);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Message> retrieveMessageById(@Nonnull String messageId) {
      this.checkCanViewHistory();
      return MessageChannelUnion.super.retrieveMessageById(messageId);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> deleteMessageById(@Nonnull String messageId) {
      this.checkCanAccess();
      return MessageChannelUnion.super.deleteMessageById(messageId);
   }

   @Nonnull
   default MessageHistory getHistory() {
      this.checkCanViewHistory();
      return MessageChannelUnion.super.getHistory();
   }

   @Nonnull
   @CheckReturnValue
   default MessagePaginationAction getIterableHistory() {
      this.checkCanViewHistory();
      return MessageChannelUnion.super.getIterableHistory();
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryAround(@Nonnull String messageId, int limit) {
      this.checkCanViewHistory();
      return MessageChannelUnion.super.getHistoryAround(messageId, limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryAfter(@Nonnull String messageId, int limit) {
      this.checkCanViewHistory();
      return MessageChannelUnion.super.getHistoryAfter(messageId, limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryBefore(@Nonnull String messageId, int limit) {
      this.checkCanViewHistory();
      return MessageChannelUnion.super.getHistoryBefore(messageId, limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryFromBeginning(int limit) {
      this.checkCanViewHistory();
      return MessageHistory.getHistoryFromBeginning(this).limit(limit);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> sendTyping() {
      this.checkCanAccess();
      return MessageChannelUnion.super.sendTyping();
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> addReactionById(@Nonnull String messageId, @Nonnull Emoji emoji) {
      this.checkCanAddReactions();
      return MessageChannelUnion.super.addReactionById(messageId, emoji);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> removeReactionById(@Nonnull String messageId, @Nonnull Emoji emoji) {
      this.checkCanRemoveReactions();
      return MessageChannelUnion.super.removeReactionById(messageId, emoji);
   }

   @Nonnull
   @CheckReturnValue
   default ReactionPaginationAction retrieveReactionUsersById(@Nonnull String messageId, @Nonnull Emoji emoji) {
      this.checkCanRemoveReactions();
      return MessageChannelUnion.super.retrieveReactionUsersById(messageId, emoji);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> pinMessageById(@Nonnull String messageId) {
      this.checkCanControlMessagePins();
      return MessageChannelUnion.super.pinMessageById(messageId);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> unpinMessageById(@Nonnull String messageId) {
      this.checkCanControlMessagePins();
      return MessageChannelUnion.super.unpinMessageById(messageId);
   }

   @Nonnull
   @CheckReturnValue
   default PinnedMessagePaginationAction retrievePinnedMessages() {
      this.checkCanAccess();
      return MessageChannelUnion.super.retrievePinnedMessages();
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageById(@Nonnull String messageId, @Nonnull CharSequence newContent) {
      this.checkCanSendMessage();
      return MessageChannelUnion.super.editMessageById(messageId, newContent);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageById(@Nonnull String messageId, @Nonnull MessageEditData data) {
      this.checkCanSendMessage();
      return MessageChannelUnion.super.editMessageById(messageId, data);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageEmbedsById(@Nonnull String messageId, @Nonnull Collection<? extends MessageEmbed> newEmbeds) {
      this.checkCanSendMessage();
      this.checkCanSendMessageEmbeds();
      return MessageChannelUnion.super.editMessageEmbedsById(messageId, newEmbeds);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageComponentsById(@Nonnull String messageId, @Nonnull Collection<? extends MessageTopLevelComponent> components) {
      this.checkCanSendMessage();
      return MessageChannelUnion.super.editMessageComponentsById(messageId, components);
   }

   @Nonnull
   default MessageEditAction editMessageAttachmentsById(@Nonnull String messageId, @Nonnull Collection<? extends AttachedFile> attachments) {
      this.checkCanSendMessage();
      return MessageChannelUnion.super.editMessageAttachmentsById(messageId, attachments);
   }

   T setLatestMessageIdLong(long var1);

   void checkCanSendMessage();

   void checkCanSendMessageEmbeds();

   void checkCanSendFiles();

   void checkCanViewHistory();

   void checkCanAddReactions();

   void checkCanRemoveReactions();

   void checkCanControlMessagePins();

   boolean canDeleteOtherUsersMessages();

   default RestActionImpl<Void> bulkDeleteMessages(Collection<String> messageIds) {
      DataObject body = DataObject.empty().put("messages", messageIds);
      Route.CompiledRoute route = Route.Messages.DELETE_MESSAGES.compile(this.getId());
      return new RestActionImpl<Void>(this.getJDA(), route, body);
   }
}
