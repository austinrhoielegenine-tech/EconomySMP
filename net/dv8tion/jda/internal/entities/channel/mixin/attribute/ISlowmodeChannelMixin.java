package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;

public interface ISlowmodeChannelMixin<T extends ISlowmodeChannelMixin<T>> extends GuildChannelMixin<T>, ISlowmodeChannel {
   T setSlowmode(int var1);
}
