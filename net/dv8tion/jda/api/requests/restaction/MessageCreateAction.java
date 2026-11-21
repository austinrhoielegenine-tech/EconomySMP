package net.dv8tion.jda.api.requests.restaction;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.requests.FluentRestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface MessageCreateAction extends MessageCreateRequest<MessageCreateAction>, FluentRestAction<Message, MessageCreateAction> {
   static void setDefaultFailOnInvalidReply(boolean fail) {
      MessageCreateActionImpl.setDefaultFailOnInvalidReply(fail);
   }

   @Nonnull
   @CheckReturnValue
   MessageCreateAction setNonce(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   MessageCreateAction setMessageReference(@Nonnull MessageReference.MessageReferenceType var1, @Nullable String var2, @Nonnull String var3, @Nonnull String var4);

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction setMessageReference(@Nonnull MessageReference.MessageReferenceType type, long guildId, long channelId, long messageId) {
      return this.setMessageReference(type, Long.toUnsignedString(guildId), Long.toUnsignedString(channelId), Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction setMessageReference(@Nonnull MessageReference.MessageReferenceType type, @Nonnull Message message) {
      Checks.notNull(message, "Message");
      return this.setMessageReference(type, message.getGuildId(), message.getChannel().getId(), message.getId());
   }

   @Nonnull
   @CheckReturnValue
   MessageCreateAction setMessageReference(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction setMessageReference(long messageId) {
      return this.setMessageReference(Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction setMessageReference(@Nullable Message message) {
      return this.setMessageReference(message == null ? null : message.getId());
   }

   @Nonnull
   @CheckReturnValue
   MessageCreateAction failOnInvalidReply(boolean var1);

   @Nonnull
   @CheckReturnValue
   MessageCreateAction setStickers(@Nullable Collection<? extends StickerSnowflake> var1);

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction setStickers(@Nullable StickerSnowflake... stickers) {
      if (stickers != null) {
         Checks.noneNull(stickers, "Sticker");
      }

      return this.setStickers(stickers == null ? null : Arrays.asList(stickers));
   }
}
