package net.dv8tion.jda.api.utils.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.utils.MiscUtil;

public interface ChannelCacheView<T extends Channel> extends SnowflakeCacheView<T> {
   @Nonnull
   <C extends T> ChannelCacheView<C> ofType(@Nonnull Class<C> var1);

   @Nullable
   T getElementById(@Nonnull ChannelType var1, long var2);

   @Nullable
   default T getElementById(@Nonnull ChannelType type, @Nonnull String id) {
      return (T)this.getElementById(type, MiscUtil.parseSnowflake(id));
   }
}
