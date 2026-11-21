package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.SelfMember;

public interface SelfMemberManager extends Manager<SelfMemberManager> {
   long NICKNAME = 1L;
   long AVATAR = 2L;
   long BANNER = 4L;
   long BIO = 8L;

   @Nonnull
   SelfMember getMember();

   @Nonnull
   @CheckReturnValue
   SelfMemberManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   SelfMemberManager reset(@Nonnull long... var1);

   @Nonnull
   @CheckReturnValue
   SelfMemberManager setNickname(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   SelfMemberManager setAvatar(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   SelfMemberManager setBanner(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   SelfMemberManager setBio(@Nullable String var1);
}
