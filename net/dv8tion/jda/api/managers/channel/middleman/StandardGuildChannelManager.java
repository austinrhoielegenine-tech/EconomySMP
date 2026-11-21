package net.dv8tion.jda.api.managers.channel.middleman;

import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.managers.channel.attribute.ICategorizableChannelManager;
import net.dv8tion.jda.api.managers.channel.attribute.IPermissionContainerManager;
import net.dv8tion.jda.api.managers.channel.attribute.IPositionableChannelManager;

public interface StandardGuildChannelManager<T extends StandardGuildChannel, M extends StandardGuildChannelManager<T, M>> extends IPermissionContainerManager<T, M>, IPositionableChannelManager<T, M>, ICategorizableChannelManager<T, M> {
}
