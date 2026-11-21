package net.dv8tion.jda.api.events.channel;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;
import net.dv8tion.jda.api.events.Event;

public class GenericChannelEvent extends Event {
   protected final Channel channel;

   public GenericChannelEvent(@Nonnull JDA api, long responseNumber, Channel channel) {
      super(api, responseNumber);
      this.channel = channel;
   }

   public boolean isFromGuild() {
      return this.getChannelType().isGuild();
   }

   @Nonnull
   public ChannelType getChannelType() {
      return this.channel.getType();
   }

   public boolean isFromType(@Nonnull ChannelType type) {
      return this.getChannelType() == type;
   }

   @Nonnull
   public ChannelUnion getChannel() {
      return (ChannelUnion)this.channel;
   }

   @Nonnull
   public Guild getGuild() {
      if (!this.isFromGuild()) {
         throw new IllegalStateException("This channel event did not happen in a guild");
      } else {
         return ((GuildChannel)this.channel).getGuild();
      }
   }
}
