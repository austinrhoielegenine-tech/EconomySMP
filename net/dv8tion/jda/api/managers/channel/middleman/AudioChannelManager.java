package net.dv8tion.jda.api.managers.channel.middleman;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public interface AudioChannelManager<T extends AudioChannel, M extends AudioChannelManager<T, M>> extends StandardGuildChannelManager<T, M> {
   @Nonnull
   @CheckReturnValue
   M setBitrate(int var1);

   @Nonnull
   @CheckReturnValue
   M setUserLimit(int var1);

   @Nonnull
   @CheckReturnValue
   M setRegion(@Nonnull Region var1);
}
