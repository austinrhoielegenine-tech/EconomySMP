package net.dv8tion.jda.internal.entities.channel.mixin.middleman;

import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ICategorizableChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInviteContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPermissionContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPositionableChannelMixin;

public interface StandardGuildChannelMixin<T extends StandardGuildChannelMixin<T>> extends StandardGuildChannel, ICategorizableChannelMixin<T>, IPositionableChannelMixin<T>, IPermissionContainerMixin<T>, IInviteContainerMixin<T> {
}
