package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import net.dv8tion.jda.api.entities.channel.attribute.IAgeRestrictedChannel;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;

public interface IAgeRestrictedChannelMixin<T extends IAgeRestrictedChannelMixin<T>> extends GuildChannelMixin<T>, IAgeRestrictedChannel {
   T setNSFW(boolean var1);
}
