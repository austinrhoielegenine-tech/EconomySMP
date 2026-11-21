package net.dv8tion.jda.api.entities;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class BulkBanResponse {
   private final List<UserSnowflake> bannedUsers;
   private final List<UserSnowflake> failedUsers;

   public BulkBanResponse(@Nonnull List<UserSnowflake> bannedUsers, @Nonnull List<UserSnowflake> failedUsers) {
      this.bannedUsers = Collections.unmodifiableList(bannedUsers);
      this.failedUsers = Collections.unmodifiableList(failedUsers);
   }

   @Nonnull
   public List<UserSnowflake> getBannedUsers() {
      return this.bannedUsers;
   }

   @Nonnull
   public List<UserSnowflake> getFailedUsers() {
      return this.failedUsers;
   }
}
