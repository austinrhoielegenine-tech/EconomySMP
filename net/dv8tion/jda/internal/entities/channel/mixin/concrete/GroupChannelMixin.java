package net.dv8tion.jda.internal.entities.channel.mixin.concrete;

import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.concrete.GroupChannel;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.MessageChannelMixin;

public interface GroupChannelMixin<T extends GroupChannelMixin<T>> extends GroupChannel, MessageChannelMixin<T> {
   T setOwnerId(long var1);

   T setIcon(@Nullable String var1);
}
