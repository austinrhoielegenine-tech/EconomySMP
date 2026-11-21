package net.dv8tion.jda.api.entities.channel.middleman;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.managers.channel.middleman.AudioChannelManager;

public interface AudioChannel extends StandardGuildChannel {
   @Nonnull
   @CheckReturnValue
   AudioChannelManager<?, ?> getManager();

   int getBitrate();

   int getUserLimit();

   @Nonnull
   default Region getRegion() {
      return this.getRegionRaw() == null ? Region.AUTOMATIC : Region.fromKey(this.getRegionRaw());
   }

   @Nullable
   String getRegionRaw();
}
