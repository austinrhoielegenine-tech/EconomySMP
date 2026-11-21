package net.dv8tion.jda.api.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildWelcomeScreen;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public interface GuildWelcomeScreenManager extends Manager<GuildWelcomeScreenManager> {
   long ENABLED = 1L;
   long DESCRIPTION = 2L;
   long CHANNELS = 4L;

   @Nonnull
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   GuildWelcomeScreenManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   GuildWelcomeScreenManager reset(@Nonnull long... var1);

   @Nonnull
   @CheckReturnValue
   GuildWelcomeScreenManager setEnabled(boolean var1);

   @Nonnull
   @CheckReturnValue
   GuildWelcomeScreenManager setDescription(@Nullable String var1);

   @Nonnull
   @Unmodifiable List<GuildWelcomeScreen.Channel> getWelcomeChannels();

   @Nonnull
   @CheckReturnValue
   GuildWelcomeScreenManager clearWelcomeChannels();

   @Nonnull
   @CheckReturnValue
   GuildWelcomeScreenManager setWelcomeChannels(@Nonnull Collection<? extends GuildWelcomeScreen.Channel> var1);

   @Nonnull
   @CheckReturnValue
   default GuildWelcomeScreenManager setWelcomeChannels(@Nonnull GuildWelcomeScreen.Channel... channels) {
      Checks.notNull(channels, "Welcome channels");
      return this.setWelcomeChannels(Arrays.asList(channels));
   }
}
