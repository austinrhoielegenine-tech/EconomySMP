package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;

public interface ITopicChannelMixin<T extends ITopicChannelMixin<T>> extends GuildChannelMixin<T> {
   T setTopic(String var1);

   String getTopic();
}
