package net.dv8tion.jda.api.utils.cache;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.Channel;

public interface SortedChannelCacheView<T extends Channel & Comparable<? super T>> extends ChannelCacheView<T>, SortedSnowflakeCacheView<T> {
   @Nonnull
   <C extends T> SortedChannelCacheView<C> ofType(@Nonnull Class<C> var1);
}
