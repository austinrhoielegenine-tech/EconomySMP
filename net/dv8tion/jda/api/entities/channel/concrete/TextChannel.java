package net.dv8tion.jda.api.entities.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public interface TextChannel extends StandardGuildMessageChannel, ISlowmodeChannel {
   @Nonnull
   @CheckReturnValue
   ChannelAction<TextChannel> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<TextChannel> createCopy() {
      return this.createCopy(this.getGuild());
   }

   @Nonnull
   @CheckReturnValue
   TextChannelManager getManager();
}
