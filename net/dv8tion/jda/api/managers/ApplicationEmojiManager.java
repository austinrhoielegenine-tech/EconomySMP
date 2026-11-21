package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;

public interface ApplicationEmojiManager extends Manager<ApplicationEmojiManager> {
   long NAME = 1L;

   @Nonnull
   @CheckReturnValue
   ApplicationEmojiManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   ApplicationEmojiManager reset(@Nonnull long... var1);

   @Nonnull
   ApplicationEmoji getEmoji();

   @Nonnull
   @CheckReturnValue
   ApplicationEmojiManager setName(@Nonnull String var1);
}
