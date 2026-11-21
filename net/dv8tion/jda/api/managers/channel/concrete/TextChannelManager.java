package net.dv8tion.jda.api.managers.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.channel.attribute.ISlowmodeChannelManager;
import net.dv8tion.jda.api.managers.channel.middleman.StandardGuildMessageChannelManager;

public interface TextChannelManager extends StandardGuildMessageChannelManager<TextChannel, TextChannelManager>, ISlowmodeChannelManager<TextChannel, TextChannelManager> {
   @Nonnull
   @CheckReturnValue
   TextChannelManager setType(@Nonnull ChannelType var1);
}
