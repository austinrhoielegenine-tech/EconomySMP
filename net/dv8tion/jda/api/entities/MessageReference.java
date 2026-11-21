package net.dv8tion.jda.api.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public class MessageReference {
   private final int type;
   private final long messageId;
   private final long channelId;
   private final long guildId;
   private final JDA api;
   private final MessageChannel channel;
   private final Guild guild;
   private Message referencedMessage;

   public MessageReference(int type, long messageId, long channelId, long guildId, @Nullable Message referencedMessage, JDA api) {
      this.type = type;
      this.messageId = messageId;
      this.channelId = channelId;
      this.guildId = guildId;
      this.referencedMessage = referencedMessage;
      if (guildId == 0L) {
         this.channel = api.getPrivateChannelById(channelId);
      } else {
         this.channel = (MessageChannel)api.getChannelById(MessageChannel.class, channelId);
      }

      this.guild = api.getGuildById(guildId);
      this.api = api;
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<Message> resolve() {
      return this.resolve(true);
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<Message> resolve(boolean update) {
      this.checkPermission(Permission.VIEW_CHANNEL);
      this.checkPermission(Permission.MESSAGE_HISTORY);
      if (this.channel == null) {
         throw new IllegalStateException("Cannot resolve a message without a channel present.");
      } else {
         JDAImpl jda = (JDAImpl)this.getJDA();
         Message referenced = this.getMessage();
         if (referenced != null && !update) {
            return new CompletedRestAction<Message>(jda, referenced);
         } else {
            Route.CompiledRoute route = Route.Messages.GET_MESSAGE.compile(this.getChannelId(), this.getMessageId());
            return new RestActionImpl<Message>(jda, route, (response, request) -> {
               Message created = jda.getEntityBuilder().createMessageWithChannel(response.getObject(), this.channel, false);
               this.referencedMessage = created;
               return created;
            });
         }
      }
   }

   @Nullable
   public Message getMessage() {
      return this.referencedMessage;
   }

   @Nullable
   public MessageChannelUnion getChannel() {
      return (MessageChannelUnion)this.channel;
   }

   @Nullable
   public Guild getGuild() {
      return this.guild;
   }

   public int getTypeRaw() {
      return this.type;
   }

   @Nonnull
   public MessageReferenceType getType() {
      return MessageReference.MessageReferenceType.fromId(this.type);
   }

   public long getMessageIdLong() {
      return this.messageId;
   }

   public long getChannelIdLong() {
      return this.channelId;
   }

   public long getGuildIdLong() {
      return this.guildId;
   }

   @Nonnull
   public String getMessageId() {
      return Long.toUnsignedString(this.getMessageIdLong());
   }

   @Nonnull
   public String getChannelId() {
      return Long.toUnsignedString(this.getChannelIdLong());
   }

   @Nonnull
   public String getGuildId() {
      return Long.toUnsignedString(this.getGuildIdLong());
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   private void checkPermission(Permission permission) {
      if (this.guild != null && this.channel instanceof GuildChannel) {
         Member selfMember = this.guild.getSelfMember();
         GuildChannel guildChannel = (GuildChannel)this.channel;
         Checks.checkAccess(selfMember, guildChannel);
         if (!selfMember.hasPermission(guildChannel, new Permission[]{permission})) {
            throw new InsufficientPermissionException(guildChannel, permission);
         }
      }
   }

   public static enum MessageReferenceType {
      DEFAULT(0),
      FORWARD(1),
      UNKNOWN(-1);

      private final int id;

      private MessageReferenceType(int id) {
         this.id = id;
      }

      @Nonnull
      public static MessageReferenceType fromId(int id) {
         for(MessageReferenceType type : values()) {
            if (type.id == id) {
               return type;
            }
         }

         return UNKNOWN;
      }

      public int getId() {
         return this.id;
      }

      // $FF: synthetic method
      private static MessageReferenceType[] $values() {
         return new MessageReferenceType[]{DEFAULT, FORWARD, UNKNOWN};
      }
   }
}
