package net.dv8tion.jda.api.entities.channel.middleman;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formattable;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PinnedMessagePaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PollVotersPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.MessageEditActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.MessagePaginationActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.PinnedMessagePaginationActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.PollVotersPaginationActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.ReactionPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public interface MessageChannel extends Channel, Formattable {
   @Nonnull
   default String getLatestMessageId() {
      return Long.toUnsignedString(this.getLatestMessageIdLong());
   }

   long getLatestMessageIdLong();

   boolean canTalk();

   @Nonnull
   default List<CompletableFuture<Void>> purgeMessagesById(@Nonnull List<String> messageIds) {
      if (messageIds != null && !messageIds.isEmpty()) {
         long[] ids = new long[messageIds.size()];

         for(int i = 0; i < ids.length; ++i) {
            ids[i] = MiscUtil.parseSnowflake((String)messageIds.get(i));
         }

         return this.purgeMessagesById(ids);
      } else {
         return Collections.emptyList();
      }
   }

   @Nonnull
   default List<CompletableFuture<Void>> purgeMessagesById(@Nonnull String... messageIds) {
      return messageIds != null && messageIds.length != 0 ? this.purgeMessagesById(Arrays.asList(messageIds)) : Collections.emptyList();
   }

   @Nonnull
   default List<CompletableFuture<Void>> purgeMessages(@Nonnull Message... messages) {
      return messages != null && messages.length != 0 ? this.purgeMessages(Arrays.asList(messages)) : Collections.emptyList();
   }

   @Nonnull
   default List<CompletableFuture<Void>> purgeMessages(@Nonnull List<? extends Message> messages) {
      return messages != null && !messages.isEmpty() ? this.purgeMessagesById(messages.stream().filter((m) -> m.getType().canDelete()).mapToLong(ISnowflake::getIdLong).toArray()) : Collections.emptyList();
   }

   @Nonnull
   default List<CompletableFuture<Void>> purgeMessagesById(@Nonnull long... messageIds) {
      if (messageIds != null && messageIds.length != 0) {
         List<CompletableFuture<Void>> list = new ArrayList(messageIds.length);
         TreeSet<Long> sortedIds = new TreeSet(Comparator.reverseOrder());

         for(long messageId : messageIds) {
            sortedIds.add(messageId);
         }

         for(long messageId : sortedIds) {
            list.add(this.deleteMessageById(messageId).submit());
         }

         return list;
      } else {
         return Collections.emptyList();
      }
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessage(@Nonnull CharSequence text) {
      Checks.notNull(text, "Content");
      return (MessageCreateAction)(new MessageCreateActionImpl(this)).setContent(text.toString());
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessage(@Nonnull MessageCreateData msg) {
      Checks.notNull(msg, "Message");
      return (MessageCreateAction)(new MessageCreateActionImpl(this)).applyData(msg);
   }

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default MessageCreateAction sendMessageFormat(@Nonnull @FormatString String format, @Nonnull Object... args) {
      Checks.notEmpty(format, "Format");
      return this.sendMessage((CharSequence)String.format(format, args));
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessageEmbeds(@Nonnull MessageEmbed embed, @Nonnull MessageEmbed... other) {
      Checks.notNull(embed, "MessageEmbeds");
      Checks.noneNull(other, "MessageEmbeds");
      List<MessageEmbed> embeds = new ArrayList(1 + other.length);
      embeds.add(embed);
      Collections.addAll(embeds, other);
      return (MessageCreateAction)(new MessageCreateActionImpl(this)).setEmbeds(embeds);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      return (MessageCreateAction)(new MessageCreateActionImpl(this)).setEmbeds(embeds);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessageComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      Checks.noneNull(components, "MessageTopLevelComponents");
      return (MessageCreateAction)(new MessageCreateActionImpl(this)).setComponents(components);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessageComponents(@Nonnull MessageTopLevelComponent component, @Nonnull MessageTopLevelComponent... other) {
      Checks.notNull(component, "MessageTopLevelComponent");
      Checks.noneNull(other, "MessageTopLevelComponents");
      return this.sendMessageComponents(Helpers.mergeVararg(component, other));
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessageComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.sendMessageComponents(tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendMessagePoll(@Nonnull MessagePollData poll) {
      Checks.notNull(poll, "Poll");
      return (MessageCreateAction)(new MessageCreateActionImpl(this)).setPoll(poll);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendFiles(@Nonnull Collection<? extends FileUpload> files) {
      Checks.notEmpty(files, "File Collection");
      Checks.noneNull(files, "Files");
      return (MessageCreateAction)(new MessageCreateActionImpl(this)).addFiles(files);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendFiles(@Nonnull FileUpload... files) {
      Checks.notEmpty((Object[])files, "File Collection");
      Checks.noneNull(files, "Files");
      return this.sendFiles(Arrays.asList(files));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Message> retrieveMessageById(@Nonnull String messageId) {
      Checks.isSnowflake(messageId, "Message ID");
      JDAImpl jda = (JDAImpl)this.getJDA();
      Route.CompiledRoute route = Route.Messages.GET_MESSAGE.compile(this.getId(), messageId);
      return new RestActionImpl<Message>(jda, route, (response, request) -> jda.getEntityBuilder().createMessageWithChannel(response.getObject(), this, false));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Message> retrieveMessageById(long messageId) {
      return this.retrieveMessageById(Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> deleteMessageById(@Nonnull String messageId) {
      Checks.isSnowflake(messageId, "Message ID");
      Route.CompiledRoute route = Route.Messages.DELETE_MESSAGE.compile(this.getId(), messageId);
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> deleteMessageById(long messageId) {
      return this.deleteMessageById(Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Message> endPollById(@Nonnull String messageId) {
      Checks.isSnowflake(messageId, "Message ID");
      return new AuditableRestActionImpl<Message>(this.getJDA(), Route.Messages.END_POLL.compile(this.getId(), messageId), (response, request) -> {
         JDAImpl jda = (JDAImpl)this.getJDA();
         return jda.getEntityBuilder().createMessageWithChannel(response.getObject(), this, false);
      });
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Message> endPollById(long messageId) {
      return this.endPollById(Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   default PollVotersPaginationAction retrievePollVotersById(@Nonnull String messageId, long answerId) {
      return new PollVotersPaginationActionImpl(this.getJDA(), this.getId(), messageId, answerId);
   }

   @Nonnull
   @CheckReturnValue
   default PollVotersPaginationAction retrievePollVotersById(long messageId, long answerId) {
      return new PollVotersPaginationActionImpl(this.getJDA(), this.getId(), Long.toUnsignedString(messageId), answerId);
   }

   @Nonnull
   default MessageHistory getHistory() {
      return new MessageHistory(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessagePaginationAction getIterableHistory() {
      return new MessagePaginationActionImpl(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryAround(@Nonnull String messageId, int limit) {
      return MessageHistory.getHistoryAround(this, messageId).limit(limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryAround(long messageId, int limit) {
      return this.getHistoryAround(Long.toUnsignedString(messageId), limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryAround(@Nonnull Message message, int limit) {
      Checks.notNull(message, "Provided target message");
      return this.getHistoryAround(message.getId(), limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryAfter(@Nonnull String messageId, int limit) {
      return MessageHistory.getHistoryAfter(this, messageId).limit(limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryAfter(long messageId, int limit) {
      return this.getHistoryAfter(Long.toUnsignedString(messageId), limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryAfter(@Nonnull Message message, int limit) {
      Checks.notNull(message, "Message");
      return this.getHistoryAfter(message.getId(), limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryBefore(@Nonnull String messageId, int limit) {
      return MessageHistory.getHistoryBefore(this, messageId).limit(limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryBefore(long messageId, int limit) {
      return this.getHistoryBefore(Long.toUnsignedString(messageId), limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryBefore(@Nonnull Message message, int limit) {
      Checks.notNull(message, "Message");
      return this.getHistoryBefore(message.getId(), limit);
   }

   @Nonnull
   @CheckReturnValue
   default MessageHistory.MessageRetrieveAction getHistoryFromBeginning(int limit) {
      return MessageHistory.getHistoryFromBeginning(this).limit(limit);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> sendTyping() {
      Route.CompiledRoute route = Route.Channels.SEND_TYPING.compile(this.getId());
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> addReactionById(@Nonnull String messageId, @Nonnull Emoji emoji) {
      Checks.isSnowflake(messageId, "Message ID");
      Checks.notNull(emoji, "Emoji");
      Route.CompiledRoute route = Route.Messages.ADD_REACTION.compile(this.getId(), messageId, emoji.getAsReactionCode(), "@me");
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> addReactionById(long messageId, @Nonnull Emoji emoji) {
      return this.addReactionById(Long.toUnsignedString(messageId), emoji);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> removeReactionById(@Nonnull String messageId, @Nonnull Emoji emoji) {
      Checks.isSnowflake(messageId, "Message ID");
      Checks.notNull(emoji, "Emoji");
      Route.CompiledRoute route = Route.Messages.REMOVE_REACTION.compile(this.getId(), messageId, emoji.getAsReactionCode(), "@me");
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> removeReactionById(long messageId, @Nonnull Emoji emoji) {
      return this.removeReactionById(Long.toUnsignedString(messageId), emoji);
   }

   @Nonnull
   @CheckReturnValue
   default ReactionPaginationAction retrieveReactionUsersById(@Nonnull String messageId, @Nonnull Emoji emoji) {
      return this.retrieveReactionUsersById(messageId, emoji, MessageReaction.ReactionType.NORMAL);
   }

   @Nonnull
   @CheckReturnValue
   default ReactionPaginationAction retrieveReactionUsersById(long messageId, @Nonnull Emoji emoji) {
      return this.retrieveReactionUsersById(Long.toUnsignedString(messageId), emoji);
   }

   @Nonnull
   @CheckReturnValue
   default ReactionPaginationAction retrieveReactionUsersById(@Nonnull String messageId, @Nonnull Emoji emoji, @Nonnull MessageReaction.ReactionType type) {
      Checks.isSnowflake(messageId, "Message ID");
      Checks.notNull(emoji, "Emoji");
      Checks.notNull(type, "ReactionType");
      return new ReactionPaginationActionImpl(this, messageId, emoji.getAsReactionCode(), type);
   }

   @Nonnull
   @CheckReturnValue
   default ReactionPaginationAction retrieveReactionUsersById(long messageId, @Nonnull Emoji emoji, @Nonnull MessageReaction.ReactionType type) {
      return this.retrieveReactionUsersById(Long.toUnsignedString(messageId), emoji, type);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> pinMessageById(@Nonnull String messageId) {
      Checks.isSnowflake(messageId, "Message ID");
      Route.CompiledRoute route = Route.Messages.PIN_MESSAGE.compile(this.getId(), messageId);
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> pinMessageById(long messageId) {
      return this.pinMessageById(Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> unpinMessageById(@Nonnull String messageId) {
      Checks.isSnowflake(messageId, "Message ID");
      Route.CompiledRoute route = Route.Messages.UNPIN_MESSAGE.compile(this.getId(), messageId);
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> unpinMessageById(long messageId) {
      return this.unpinMessageById(Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   default PinnedMessagePaginationAction retrievePinnedMessages() {
      return new PinnedMessagePaginationActionImpl(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageById(@Nonnull String messageId, @Nonnull CharSequence newContent) {
      Checks.isSnowflake(messageId, "Message ID");
      Checks.notEmpty(newContent, "Provided message content");
      Checks.check(newContent.length() <= 2000, "Provided newContent length must be %d or less characters.", (int)2000);
      return (MessageEditAction)(new MessageEditActionImpl(this, messageId)).setContent(newContent.toString());
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageById(long messageId, @Nonnull CharSequence newContent) {
      return this.editMessageById(Long.toUnsignedString(messageId), newContent);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageById(@Nonnull String messageId, @Nonnull MessageEditData data) {
      Checks.isSnowflake(messageId, "Message ID");
      Checks.notNull(data, "message");
      return (MessageEditAction)(new MessageEditActionImpl(this, messageId)).applyData(data);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageById(long messageId, @Nonnull MessageEditData data) {
      return this.editMessageById(Long.toUnsignedString(messageId), data);
   }

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default MessageEditAction editMessageFormatById(@Nonnull String messageId, @Nonnull @FormatString String format, @Nonnull Object... args) {
      Checks.notBlank(format, "Format String");
      return this.editMessageById(messageId, (CharSequence)String.format(format, args));
   }

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default MessageEditAction editMessageFormatById(long messageId, @Nonnull @FormatString String format, @Nonnull Object... args) {
      Checks.notBlank(format, "Format String");
      return this.editMessageById(messageId, (CharSequence)String.format(format, args));
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageEmbedsById(@Nonnull String messageId, @Nonnull MessageEmbed... newEmbeds) {
      Checks.noneNull(newEmbeds, "MessageEmbeds");
      return this.editMessageEmbedsById(messageId, (Collection)Arrays.asList(newEmbeds));
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageEmbedsById(long messageId, @Nonnull MessageEmbed... newEmbeds) {
      return this.editMessageEmbedsById(Long.toUnsignedString(messageId), newEmbeds);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageEmbedsById(@Nonnull String messageId, @Nonnull Collection<? extends MessageEmbed> newEmbeds) {
      Checks.isSnowflake(messageId, "Message ID");
      return (MessageEditAction)(new MessageEditActionImpl(this, messageId)).setEmbeds(newEmbeds);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageEmbedsById(long messageId, @Nonnull Collection<? extends MessageEmbed> newEmbeds) {
      return this.editMessageEmbedsById(Long.toUnsignedString(messageId), newEmbeds);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageComponentsById(@Nonnull String messageId, @Nonnull Collection<? extends MessageTopLevelComponent> components) {
      Checks.isSnowflake(messageId, "Message ID");
      Checks.noneNull(components, "Components");
      return (MessageEditAction)(new MessageEditActionImpl(this, messageId)).setComponents(components);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageComponentsById(long messageId, @Nonnull Collection<? extends MessageTopLevelComponent> components) {
      return this.editMessageComponentsById(Long.toUnsignedString(messageId), components);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageComponentsById(@Nonnull String messageId, @Nonnull MessageTopLevelComponent... components) {
      Checks.noneNull(components, "Components");
      return this.editMessageComponentsById(messageId, Arrays.asList(components));
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageComponentsById(long messageId, @Nonnull MessageTopLevelComponent... components) {
      Checks.noneNull(components, "Components");
      return this.editMessageComponentsById(messageId, Arrays.asList(components));
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageComponentsById(@Nonnull String messageId, @Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.editMessageComponentsById(messageId, tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageComponentsById(long messageId, @Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.editMessageComponentsById(messageId, tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageAttachmentsById(@Nonnull String messageId, @Nonnull Collection<? extends AttachedFile> attachments) {
      Checks.isSnowflake(messageId, "Message ID");
      return (MessageEditAction)(new MessageEditActionImpl(this, messageId)).setAttachments(attachments);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageAttachmentsById(@Nonnull String messageId, @Nonnull AttachedFile... attachments) {
      Checks.noneNull(attachments, "Attachments");
      return this.editMessageAttachmentsById(messageId, Arrays.asList(attachments));
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageAttachmentsById(long messageId, @Nonnull Collection<? extends AttachedFile> attachments) {
      return this.editMessageAttachmentsById(Long.toUnsignedString(messageId), attachments);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageAttachmentsById(long messageId, @Nonnull AttachedFile... attachments) {
      return this.editMessageAttachmentsById(Long.toUnsignedString(messageId), attachments);
   }
}
