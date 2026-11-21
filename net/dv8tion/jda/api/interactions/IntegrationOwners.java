package net.dv8tion.jda.api.interactions;

import javax.annotation.Nullable;

public interface IntegrationOwners {
   default boolean isUserIntegration() {
      return this.getAuthorizingUserIdLong() != 0L;
   }

   long getAuthorizingUserIdLong();

   @Nullable
   default String getAuthorizingUserId() {
      return this.getAuthorizingUserIdLong() == 0L ? null : Long.toUnsignedString(this.getAuthorizingUserIdLong());
   }

   default boolean isGuildIntegration() {
      return this.getAuthorizingGuildIdLong() != null;
   }

   @Nullable
   Long getAuthorizingGuildIdLong();

   @Nullable
   default String getAuthorizingGuildId() {
      return this.getAuthorizingGuildIdLong() == null ? null : Long.toUnsignedString(this.getAuthorizingGuildIdLong());
   }
}
