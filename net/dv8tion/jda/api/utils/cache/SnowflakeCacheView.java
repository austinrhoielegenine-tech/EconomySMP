package net.dv8tion.jda.api.utils.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.MiscUtil;

public interface SnowflakeCacheView<T extends ISnowflake> extends CacheView<T> {
   @Nullable
   T getElementById(long var1);

   @Nullable
   default T getElementById(@Nonnull String id) {
      return (T)this.getElementById(MiscUtil.parseSnowflake(id));
   }
}
