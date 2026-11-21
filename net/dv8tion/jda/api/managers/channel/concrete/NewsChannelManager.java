package net.dv8tion.jda.api.managers.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.managers.channel.middleman.StandardGuildMessageChannelManager;

public interface NewsChannelManager extends StandardGuildMessageChannelManager<NewsChannel, NewsChannelManager> {
   @Nonnull
   @CheckReturnValue
   NewsChannelManager setType(@Nonnull ChannelType var1);
}
