package net.dv8tion.jda.api.managers.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.managers.channel.ChannelManager;

public interface IThreadContainerManager<T extends IThreadContainer, M extends IThreadContainerManager<T, M>> extends ChannelManager<T, M> {
   @Nonnull
   @CheckReturnValue
   M setDefaultThreadSlowmode(int var1);
}
