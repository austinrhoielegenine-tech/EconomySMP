package net.dv8tion.jda.api.interactions;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.internal.utils.ChannelUtil;

public interface Interaction extends ISnowflake {
   int getTypeRaw();

   @Nonnull
   default InteractionType getType() {
      return InteractionType.fromKey(this.getTypeRaw());
   }

   @Nonnull
   String getToken();

   @Nullable
   Guild getGuild();

   default boolean isFromAttachedGuild() {
      Guild guild = this.getGuild();
      if (guild == null) {
         return false;
      } else {
         return !guild.isDetached();
      }
   }

   default boolean isFromGuild() {
      return this.getGuild() != null;
   }

   @Nonnull
   default ChannelType getChannelType() {
      Channel channel = this.getChannel();
      return channel != null ? channel.getType() : ChannelType.UNKNOWN;
   }

   @Nonnull
   User getUser();

   @Nullable
   Member getMember();

   boolean isAcknowledged();

   @Nullable
   Channel getChannel();

   long getChannelIdLong();

   @Nullable
   default String getChannelId() {
      long id = this.getChannelIdLong();
      return id != 0L ? Long.toUnsignedString(this.getChannelIdLong()) : null;
   }

   @Nonnull
   default GuildChannel getGuildChannel() {
      return (GuildChannel)ChannelUtil.safeChannelCast(this.getChannel(), GuildChannel.class);
   }

   @Nonnull
   default MessageChannel getMessageChannel() {
      return (MessageChannel)ChannelUtil.safeChannelCast(this.getChannel(), MessageChannel.class);
   }

   @Nonnull
   DiscordLocale getUserLocale();

   @Nonnull
   default DiscordLocale getGuildLocale() {
      if (!this.isFromGuild()) {
         throw new IllegalStateException("This interaction did not happen in a guild");
      } else {
         return this.getGuild().getLocale();
      }
   }

   @Nonnull
   List<Entitlement> getEntitlements();

   @Nonnull
   InteractionContextType getContext();

   @Nonnull
   IntegrationOwners getIntegrationOwners();

   @Nonnull
   JDA getJDA();
}
