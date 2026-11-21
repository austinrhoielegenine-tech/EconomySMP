package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.utils.Checks;

public interface InviteAction extends AuditableRestAction<Invite> {
   @Nonnull
   @CheckReturnValue
   InviteAction setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   InviteAction timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   InviteAction deadline(long var1);

   @Nonnull
   @CheckReturnValue
   InviteAction setMaxAge(@Nullable Integer var1);

   @Nonnull
   @CheckReturnValue
   InviteAction setMaxAge(@Nullable Long var1, @Nonnull TimeUnit var2);

   @Nonnull
   @CheckReturnValue
   InviteAction setMaxUses(@Nullable Integer var1);

   @Nonnull
   @CheckReturnValue
   InviteAction setTemporary(@Nullable Boolean var1);

   @Nonnull
   @CheckReturnValue
   InviteAction setUnique(@Nullable Boolean var1);

   @Nonnull
   @CheckReturnValue
   InviteAction setTargetApplication(long var1);

   @Nonnull
   @CheckReturnValue
   default InviteAction setTargetApplication(@Nonnull String applicationId) {
      return this.setTargetApplication(MiscUtil.parseSnowflake(applicationId));
   }

   @Nonnull
   @CheckReturnValue
   InviteAction setTargetStream(long var1);

   @Nonnull
   @CheckReturnValue
   default InviteAction setTargetStream(@Nonnull String userId) {
      return this.setTargetStream(MiscUtil.parseSnowflake(userId));
   }

   @Nonnull
   @CheckReturnValue
   default InviteAction setTargetStream(@Nonnull User user) {
      Checks.notNull(user, "User");
      return this.setTargetStream(user.getIdLong());
   }

   @Nonnull
   @CheckReturnValue
   default InviteAction setTargetStream(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return this.setTargetStream(member.getIdLong());
   }
}
