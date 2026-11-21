package net.dv8tion.jda.api.utils.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;

public interface ShardCacheView extends CacheView<JDA> {
   @Nullable
   JDA getElementById(int var1);

   @Nullable
   default JDA getElementById(@Nonnull String id) {
      return this.getElementById(Integer.parseUnsignedInt(id));
   }
}
