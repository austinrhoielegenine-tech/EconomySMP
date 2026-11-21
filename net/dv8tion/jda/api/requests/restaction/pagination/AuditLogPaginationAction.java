package net.dv8tion.jda.api.requests.restaction.pagination;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.UserSnowflake;

public interface AuditLogPaginationAction extends PaginationAction<AuditLogEntry, AuditLogPaginationAction> {
   @Nonnull
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   AuditLogPaginationAction type(@Nullable ActionType var1);

   @Nonnull
   @CheckReturnValue
   AuditLogPaginationAction user(@Nullable UserSnowflake var1);
}
