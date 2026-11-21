package net.dv8tion.jda.api.managers;

import java.util.Set;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

public interface CustomEmojiManager extends Manager<CustomEmojiManager> {
   long NAME = 1L;
   long ROLES = 2L;

   @Nonnull
   @CheckReturnValue
   CustomEmojiManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   CustomEmojiManager reset(@Nonnull long... var1);

   @Nonnull
   default Guild getGuild() {
      return this.getEmoji().getGuild();
   }

   @Nonnull
   RichCustomEmoji getEmoji();

   @Nonnull
   @CheckReturnValue
   CustomEmojiManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   CustomEmojiManager setRoles(@Nullable Set<Role> var1);
}
