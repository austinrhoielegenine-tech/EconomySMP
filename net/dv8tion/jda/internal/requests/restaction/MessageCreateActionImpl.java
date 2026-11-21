package net.dv8tion.jda.internal.requests.restaction;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.message.MessageCreateBuilderMixin;
import okhttp3.RequestBody;

public class MessageCreateActionImpl extends RestActionImpl<Message> implements MessageCreateAction, MessageCreateBuilderMixin<MessageCreateAction> {
   protected static final SecureRandom nonceGenerator = new SecureRandom();
   protected static boolean defaultFailOnInvalidReply = false;
   private final MessageChannel channel;
   private final MessageCreateBuilder builder = new MessageCreateBuilder();
   private final List<String> stickers = new ArrayList();
   private String nonce;
   private MessageReferenceData messageReference;
   private boolean failOnInvalidReply;

   public static void setDefaultFailOnInvalidReply(boolean fail) {
      defaultFailOnInvalidReply = fail;
   }

   public MessageCreateActionImpl(MessageChannel channel) {
      super(channel.getJDA(), Route.Messages.SEND_MESSAGE.compile(channel.getId()));
      this.failOnInvalidReply = defaultFailOnInvalidReply;
      this.channel = channel;
   }

   public MessageCreateBuilder getBuilder() {
      return this.builder;
   }

   protected RequestBody finalizeData() {
      if (this.builder.isUsingComponentsV2()) {
         Checks.check(this.stickers.isEmpty(), "Cannot send stickers when using Components V2!");
      }

      if (!this.builder.isEmpty()) {
         MessageCreateData data = this.builder.build();

         RequestBody var3;
         try {
            DataObject json = data.toData();
            this.populateBody(json);
            var3 = this.getMultipartBody(data.getAllDistinctFiles(), json);
         } catch (Throwable var5) {
            if (data != null) {
               try {
                  data.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (data != null) {
            data.close();
         }

         return var3;
      } else {
         DataObject body = DataObject.empty().put("flags", this.builder.getMessageFlagsRaw());
         this.populateBody(body);
         if (this.stickers.isEmpty() && (this.messageReference == null || this.messageReference.type != MessageReference.MessageReferenceType.FORWARD)) {
            throw new IllegalStateException("Cannot build empty messages! Must provide at least one of: content, embed, file, poll, or stickers");
         } else {
            return this.getRequestBody(body);
         }
      }
   }

   private void populateBody(DataObject json) {
      json.put("enforce_nonce", true);
      if (this.nonce != null && !this.nonce.isEmpty()) {
         json.put("nonce", this.nonce);
      } else {
         json.put("nonce", Long.toUnsignedString(nonceGenerator.nextLong()));
      }

      if (this.stickers != null && !this.stickers.isEmpty()) {
         json.put("sticker_ids", this.stickers);
      }

      if (this.messageReference != null) {
         json.put("message_reference", this.messageReference.toData().put("fail_if_not_exists", this.failOnInvalidReply));
      }

   }

   protected void handleSuccess(Response response, Request<Message> request) {
      request.onSuccess(this.api.getEntityBuilder().createMessageWithChannel(response.getObject(), this.channel, false));
   }

   @Nonnull
   public MessageCreateAction setNonce(@Nullable String nonce) {
      if (nonce != null) {
         Checks.notLonger(nonce, 25, "Nonce");
      }

      this.nonce = nonce;
      return this;
   }

   @Nonnull
   public MessageCreateAction setMessageReference(@Nonnull MessageReference.MessageReferenceType type, @Nullable String guildId, @Nonnull String channelId, @Nonnull String messageId) {
      Checks.notNull(type, "Type");
      if (guildId != null) {
         Checks.isSnowflake(guildId, "Guild ID");
      }

      Checks.isSnowflake(channelId, "Channel ID");
      Checks.isSnowflake(messageId, "Message ID");
      Checks.check(type != MessageReference.MessageReferenceType.UNKNOWN, "Cannot create a message reference of UNKNOWN type");
      this.messageReference = new MessageReferenceData(type, guildId, channelId, messageId);
      return this;
   }

   @Nonnull
   public MessageCreateAction setMessageReference(@Nullable String messageId) {
      if (messageId == null) {
         this.messageReference = null;
         return this;
      } else {
         Checks.isSnowflake(messageId);
         String guildId = null;
         if (this.channel instanceof GuildChannel) {
            guildId = ((GuildChannel)this.channel).getGuild().getId();
         }

         this.messageReference = new MessageReferenceData(MessageReference.MessageReferenceType.DEFAULT, guildId, this.channel.getId(), messageId);
         return this;
      }
   }

   @Nonnull
   public MessageCreateAction failOnInvalidReply(boolean fail) {
      this.failOnInvalidReply = fail;
      return this;
   }

   @Nonnull
   public MessageCreateAction setStickers(@Nullable Collection<? extends StickerSnowflake> stickers) {
      this.stickers.clear();
      if (stickers != null && !stickers.isEmpty()) {
         if (!this.channel.getType().isGuild()) {
            throw new IllegalStateException("Cannot send stickers in direct messages!");
         } else {
            GuildChannel guildChannel = (GuildChannel)this.channel;
            Checks.noneNull(stickers, "Stickers");
            Checks.check(stickers.size() <= 3, "Cannot send more than %d stickers in a message!", (int)3);

            for(StickerSnowflake sticker : stickers) {
               if (sticker instanceof GuildSticker) {
                  GuildSticker guildSticker = (GuildSticker)sticker;
                  Checks.check(guildSticker.isAvailable(), "Cannot use unavailable sticker. The guild may have lost the boost level required to use this sticker!");
                  Checks.check(guildSticker.getGuildIdLong() == guildChannel.getGuild().getIdLong(), "Sticker must be from the same guild. Cross-guild sticker posting is not supported!");
               }
            }

            this.stickers.addAll((Collection)stickers.stream().map(ISnowflake::getId).collect(Collectors.toList()));
            return this;
         }
      } else {
         return this;
      }
   }

   @Nonnull
   public MessageCreateAction setCheck(BooleanSupplier checks) {
      return (MessageCreateAction)super.setCheck(checks);
   }

   @Nonnull
   public MessageCreateAction deadline(long timestamp) {
      return (MessageCreateAction)super.deadline(timestamp);
   }

   private static class MessageReferenceData implements SerializableData {
      private final MessageReference.MessageReferenceType type;
      private final String messageId;
      private final String channelId;
      private final String guildId;

      private MessageReferenceData(MessageReference.MessageReferenceType type, String guildId, String channelId, String messageId) {
         this.type = type;
         this.messageId = messageId;
         this.guildId = guildId;
         this.channelId = channelId;
      }

      @Nonnull
      public DataObject toData() {
         DataObject data = DataObject.empty().put("type", this.type.getId()).put("message_id", this.messageId).put("channel_id", this.channelId);
         if (this.guildId != null) {
            data.put("guild_id", this.guildId);
         }

         return data;
      }
   }
}
