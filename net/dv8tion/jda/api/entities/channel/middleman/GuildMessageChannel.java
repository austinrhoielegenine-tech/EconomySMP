package net.dv8tion.jda.api.entities.channel.middleman;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.internal.utils.Checks;

public interface GuildMessageChannel extends GuildChannel, MessageChannel {
   default boolean canTalk() {
      return this.canTalk(this.getGuild().getSelfMember());
   }

   boolean canTalk(@Nonnull Member var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Void> removeReactionById(@Nonnull String var1, @Nonnull Emoji var2, @Nonnull User var3);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> removeReactionById(long messageId, @Nonnull Emoji emoji, @Nonnull User user) {
      return this.removeReactionById(Long.toUnsignedString(messageId), emoji, user);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> deleteMessages(@Nonnull Collection<Message> messages) {
      Checks.notEmpty(messages, "Messages collection");
      return this.deleteMessagesByIds((Collection)messages.stream().map(ISnowflake::getId).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Void> deleteMessagesByIds(@Nonnull Collection<String> var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Void> clearReactionsById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> clearReactionsById(long messageId) {
      return this.clearReactionsById(Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Void> clearReactionsById(@Nonnull String var1, @Nonnull Emoji var2);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> clearReactionsById(long messageId, @Nonnull Emoji emoji) {
      return this.clearReactionsById(Long.toUnsignedString(messageId), emoji);
   }

   @Nonnull
   @CheckReturnValue
   MessageCreateAction sendStickers(@Nonnull Collection<? extends StickerSnowflake> var1);

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction sendStickers(@Nonnull StickerSnowflake... stickers) {
      Checks.notEmpty((Object[])stickers, "Stickers");
      return this.sendStickers(Arrays.asList(stickers));
   }
}
