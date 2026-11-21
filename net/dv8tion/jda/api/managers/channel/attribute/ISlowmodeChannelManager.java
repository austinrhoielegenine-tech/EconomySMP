package net.dv8tion.jda.api.managers.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.api.managers.channel.ChannelManager;

public interface ISlowmodeChannelManager<T extends ISlowmodeChannel, M extends ISlowmodeChannelManager<T, M>> extends ChannelManager<T, M> {
   @Nonnull
   @CheckReturnValue
   M setSlowmode(int var1);
}
