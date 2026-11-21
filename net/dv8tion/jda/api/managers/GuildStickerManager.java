package net.dv8tion.jda.api.managers;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.Checks;

public interface GuildStickerManager extends Manager<GuildStickerManager> {
   long NAME = 1L;
   long DESCRIPTION = 2L;
   long TAGS = 4L;

   @Nonnull
   @CheckReturnValue
   GuildStickerManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   GuildStickerManager reset(@Nonnull long... var1);

   @Nullable
   Guild getGuild();

   long getGuildIdLong();

   @Nonnull
   default String getGuildId() {
      return Long.toUnsignedString(this.getGuildIdLong());
   }

   @Nonnull
   @CheckReturnValue
   GuildStickerManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   GuildStickerManager setDescription(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   GuildStickerManager setTags(@Nonnull Collection<String> var1);

   @Nonnull
   @CheckReturnValue
   default GuildStickerManager setTags(@Nonnull String... tags) {
      Checks.noneNull(tags, "Tags");
      return this.setTags(Arrays.asList(tags));
   }
}
