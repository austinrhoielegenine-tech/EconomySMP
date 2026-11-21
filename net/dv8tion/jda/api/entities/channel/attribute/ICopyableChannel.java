package net.dv8tion.jda.api.entities.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public interface ICopyableChannel extends GuildChannel {
   @Nonnull
   @CheckReturnValue
   ChannelAction<? extends ICopyableChannel> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   ChannelAction<? extends ICopyableChannel> createCopy();
}
