package net.dv8tion.jda.api.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.managers.AccountManager;

public interface SelfUser extends User {
   long getApplicationIdLong();

   @Nonnull
   default String getApplicationId() {
      return Long.toUnsignedString(this.getApplicationIdLong());
   }

   boolean isVerified();

   boolean isMfaEnabled();

   long getAllowedFileSize();

   @Nonnull
   @CheckReturnValue
   AccountManager getManager();
}
