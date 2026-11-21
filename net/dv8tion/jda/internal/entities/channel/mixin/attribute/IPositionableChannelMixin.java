package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import net.dv8tion.jda.api.entities.channel.attribute.IPositionableChannel;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;

public interface IPositionableChannelMixin<T extends IPositionableChannelMixin<T>> extends IPositionableChannel, GuildChannelMixin<T> {
   T setPosition(int var1);
}
