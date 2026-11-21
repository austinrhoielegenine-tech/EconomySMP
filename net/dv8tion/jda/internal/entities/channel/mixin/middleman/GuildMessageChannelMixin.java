package net.dv8tion.jda.internal.entities.channel.mixin.middleman;

import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface GuildMessageChannelMixin<T extends GuildMessageChannelMixin<T>> extends GuildMessageChannel, GuildMessageChannelUnion, GuildChannelMixin<T>, MessageChannelMixin<T> {
   @Nonnull
   @CheckReturnValue
   default RestAction<Void> deleteMessagesByIds(@Nonnull Collection<String> messageIds) {
      this.checkCanAccess();
      this.checkPermission(Permission.MESSAGE_MANAGE, "Must have MESSAGE_MANAGE in order to bulk delete messages in this channel regardless of author.");
      if (messageIds.size() >= 2 && messageIds.size() <= 100) {
         long twoWeeksAgo = TimeUtil.getDiscordTimestamp(System.currentTimeMillis() - 1209600000L);

         for(String id : messageIds) {
            Checks.check(MiscUtil.parseSnowflake(id) > twoWeeksAgo, "Message Id provided was older than 2 weeks. Id: " + id);
         }

         return this.bulkDeleteMessages(messageIds);
      } else {
         throw new IllegalArgumentException("Must provide at least 2 or at most 100 messages to be deleted.");
      }
   }

   @Nonnull
   default RestAction<Void> removeReactionById(@Nonnull String messageId, @Nonnull Emoji emoji, @Nonnull User user) {
      Checks.isSnowflake(messageId, "Message ID");
      Checks.notNull(emoji, "Emoji");
      Checks.notNull(user, "User");
      this.checkCanAccess();
      if (!this.getJDA().getSelfUser().equals(user)) {
         this.checkPermission(Permission.MESSAGE_MANAGE);
      }

      String targetUser;
      if (user.equals(this.getJDA().getSelfUser())) {
         targetUser = "@me";
      } else {
         targetUser = user.getId();
      }

      Route.CompiledRoute route = Route.Messages.REMOVE_REACTION.compile(this.getId(), messageId, emoji.getAsReactionCode(), targetUser);
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   default RestAction<Void> clearReactionsById(@Nonnull String messageId) {
      Checks.isSnowflake(messageId, "Message ID");
      this.checkCanAccess();
      this.checkPermission(Permission.MESSAGE_MANAGE);
      Route.CompiledRoute route = Route.Messages.REMOVE_ALL_REACTIONS.compile(this.getId(), messageId);
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   default RestAction<Void> clearReactionsById(@Nonnull String messageId, @Nonnull Emoji emoji) {
      Checks.notNull(messageId, "Message ID");
      Checks.notNull(emoji, "Emoji");
      this.checkCanAccess();
      this.checkPermission(Permission.MESSAGE_MANAGE);
      Route.CompiledRoute route = Route.Messages.CLEAR_EMOJI_REACTIONS.compile(this.getId(), messageId, emoji.getAsReactionCode());
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   default MessageCreateAction sendStickers(@Nonnull Collection<? extends StickerSnowflake> stickers) {
      this.checkCanSendMessage();
      Checks.notEmpty(stickers, "Stickers");
      Checks.noneNull(stickers, "Stickers");
      return (new MessageCreateActionImpl(this)).setStickers(stickers);
   }

   default void checkCanSendMessage() {
      this.checkCanAccess();
      if (this.getType().isThread()) {
         this.checkPermission(Permission.MESSAGE_SEND_IN_THREADS);
      } else {
         this.checkPermission(Permission.MESSAGE_SEND);
      }

   }

   default void checkCanSendMessageEmbeds() {
      this.checkCanAccess();
      this.checkPermission(Permission.MESSAGE_EMBED_LINKS);
   }

   default void checkCanSendFiles() {
      this.checkCanAccess();
      this.checkPermission(Permission.MESSAGE_ATTACH_FILES);
   }

   default void checkCanViewHistory() {
      this.checkCanAccess();
      this.checkPermission(Permission.MESSAGE_HISTORY);
   }

   default void checkCanAddReactions() {
      this.checkCanAccess();
      this.checkPermission(Permission.MESSAGE_ADD_REACTION);
      this.checkPermission(Permission.MESSAGE_HISTORY, "You need MESSAGE_HISTORY to add reactions to a message");
   }

   default void checkCanRemoveReactions() {
      this.checkCanAccess();
      this.checkPermission(Permission.MESSAGE_HISTORY, "You need MESSAGE_HISTORY to remove reactions from a message");
   }

   default void checkCanControlMessagePins() {
      this.checkCanAccess();
      this.checkPermission(Permission.PIN_MESSAGES);
   }

   default boolean canDeleteOtherUsersMessages() {
      return this.hasPermission(Permission.MESSAGE_MANAGE);
   }
}
