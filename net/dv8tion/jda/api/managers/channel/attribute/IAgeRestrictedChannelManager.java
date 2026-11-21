package net.dv8tion.jda.api.managers.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.IAgeRestrictedChannel;
import net.dv8tion.jda.api.managers.channel.ChannelManager;

public interface IAgeRestrictedChannelManager<T extends IAgeRestrictedChannel, M extends IAgeRestrictedChannelManager<T, M>> extends ChannelManager<T, M> {
   @Nonnull
   @CheckReturnValue
   M setNSFW(boolean var1);
}
