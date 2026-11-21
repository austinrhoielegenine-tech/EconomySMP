package net.dv8tion.jda.api.managers.channel.middleman;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.managers.channel.attribute.IAgeRestrictedChannelManager;
import net.dv8tion.jda.api.managers.channel.attribute.IThreadContainerManager;

public interface StandardGuildMessageChannelManager<T extends StandardGuildMessageChannel, M extends StandardGuildMessageChannelManager<T, M>> extends StandardGuildChannelManager<T, M>, IAgeRestrictedChannelManager<T, M>, IThreadContainerManager<T, M> {
   @Nonnull
   @CheckReturnValue
   M setTopic(@Nullable String var1);
}
