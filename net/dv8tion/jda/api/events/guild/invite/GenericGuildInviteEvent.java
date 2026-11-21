package net.dv8tion.jda.api.events.guild.invite;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

public class GenericGuildInviteEvent extends GenericGuildEvent {
   private final String code;
   private final GuildChannel channel;

   public GenericGuildInviteEvent(@Nonnull JDA api, long responseNumber, @Nonnull String code, @Nonnull GuildChannel channel) {
      super(api, responseNumber, channel.getGuild());
      this.code = code;
      this.channel = channel;
   }

   @Nonnull
   public String getCode() {
      return this.code;
   }

   @Nonnull
   public String getUrl() {
      return "https://discord.gg/" + this.code;
   }

   @Nonnull
   public GuildChannelUnion getChannel() {
      return (GuildChannelUnion)this.channel;
   }

   @Nonnull
   public ChannelType getChannelType() {
      return this.channel.getType();
   }
}
