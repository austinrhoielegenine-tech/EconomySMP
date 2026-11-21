package net.dv8tion.jda.api.events.message;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.internal.utils.Helpers;

public abstract class GenericMessageEvent extends Event {
   protected final long messageId;
   protected final MessageChannel channel;

   public GenericMessageEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel) {
      super(api, responseNumber);
      this.messageId = messageId;
      this.channel = channel;
   }

   @Nonnull
   public MessageChannelUnion getChannel() {
      return (MessageChannelUnion)this.channel;
   }

   @Nonnull
   public GuildMessageChannelUnion getGuildChannel() {
      if (!this.isFromGuild()) {
         throw new IllegalStateException("This message event did not happen in a guild");
      } else {
         return (GuildMessageChannelUnion)this.channel;
      }
   }

   @Nonnull
   public String getMessageId() {
      return Long.toUnsignedString(this.messageId);
   }

   public long getMessageIdLong() {
      return this.messageId;
   }

   public boolean isFromType(@Nonnull ChannelType type) {
      return this.channel.getType() == type;
   }

   public boolean isFromGuild() {
      return this.getChannelType().isGuild();
   }

   @Nonnull
   public ChannelType getChannelType() {
      return this.channel.getType();
   }

   @Nonnull
   public Guild getGuild() {
      if (!this.isFromGuild()) {
         throw new IllegalStateException("This message event did not happen in a guild");
      } else {
         return ((GuildChannel)this.channel).getGuild();
      }
   }

   @Nonnull
   public String getJumpUrl() {
      return Helpers.format("https://discord.com/channels/%s/%s/%s", this.isFromGuild() ? this.getGuild().getId() : "@me", this.getChannel().getId(), this.getMessageId());
   }

   public boolean isFromThread() {
      return this.getChannelType().isThread();
   }
}
