package net.dv8tion.jda.api.requests.restaction.pagination;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public interface ScheduledEventMembersPaginationAction extends PaginationAction<Member, ScheduledEventMembersPaginationAction> {
   @Nonnull
   Guild getGuild();
}
