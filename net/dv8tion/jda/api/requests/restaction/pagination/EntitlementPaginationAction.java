package net.dv8tion.jda.api.requests.restaction.pagination;

import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.internal.utils.Checks;

public interface EntitlementPaginationAction extends PaginationAction<Entitlement, EntitlementPaginationAction> {
   @Nonnull
   @CheckReturnValue
   EntitlementPaginationAction user(@Nullable UserSnowflake var1);

   @Nonnull
   @CheckReturnValue
   EntitlementPaginationAction skuIds(@Nonnull long... var1);

   @Nonnull
   @CheckReturnValue
   EntitlementPaginationAction skuIds(@Nonnull String... var1);

   @Nonnull
   @CheckReturnValue
   EntitlementPaginationAction skuIds(@Nonnull Collection<String> var1);

   @Nonnull
   @CheckReturnValue
   EntitlementPaginationAction guild(long var1);

   @Nonnull
   @CheckReturnValue
   default EntitlementPaginationAction guild(@Nonnull String guildId) {
      Checks.notNull(guildId, "guildId");
      Checks.isSnowflake(guildId, "guildId");
      return this.guild(Long.parseUnsignedLong(guildId));
   }

   @Nonnull
   @CheckReturnValue
   default EntitlementPaginationAction guild(@Nonnull Guild guild) {
      Checks.notNull(guild, "guild");
      return this.guild(guild.getIdLong());
   }

   @Nonnull
   @CheckReturnValue
   EntitlementPaginationAction excludeEnded(boolean var1);
}
