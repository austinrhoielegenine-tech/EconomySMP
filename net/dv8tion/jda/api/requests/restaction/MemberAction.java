package net.dv8tion.jda.api.requests.restaction;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

public interface MemberAction extends RestAction<Void> {
   @Nonnull
   @CheckReturnValue
   MemberAction setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   MemberAction timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   MemberAction deadline(long var1);

   @Nonnull
   String getAccessToken();

   @Nonnull
   String getUserId();

   @Nullable
   User getUser();

   @Nonnull
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   MemberAction setNickname(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   MemberAction setRoles(@Nullable Collection<Role> var1);

   @Nonnull
   @CheckReturnValue
   MemberAction setRoles(@Nullable Role... var1);

   @Nonnull
   @CheckReturnValue
   MemberAction setMute(boolean var1);

   @Nonnull
   @CheckReturnValue
   MemberAction setDeafen(boolean var1);
}
