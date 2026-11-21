package net.dv8tion.jda.api.entities.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.managers.channel.attribute.ISlowmodeChannelManager;

public interface ISlowmodeChannel extends GuildChannel {
   int MAX_SLOWMODE = 21600;

   int getSlowmode();

   @Nonnull
   @CheckReturnValue
   ISlowmodeChannelManager<?, ?> getManager();
}
