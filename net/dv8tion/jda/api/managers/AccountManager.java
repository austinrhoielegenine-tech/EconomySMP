package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.SelfUser;

public interface AccountManager extends Manager<AccountManager> {
   long NAME = 1L;
   long AVATAR = 2L;
   long BANNER = 4L;

   @Nonnull
   SelfUser getSelfUser();

   @Nonnull
   @CheckReturnValue
   AccountManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   AccountManager reset(@Nonnull long... var1);

   @Nonnull
   @CheckReturnValue
   AccountManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   AccountManager setAvatar(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   AccountManager setBanner(@Nullable Icon var1);
}
