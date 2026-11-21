package net.dv8tion.jda.api.managers.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.IPositionableChannel;
import net.dv8tion.jda.api.managers.channel.ChannelManager;

public interface IPositionableChannelManager<T extends IPositionableChannel, M extends IPositionableChannelManager<T, M>> extends ChannelManager<T, M> {
   @Nonnull
   @CheckReturnValue
   M setPosition(int var1);
}
