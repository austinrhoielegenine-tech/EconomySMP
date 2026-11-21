package net.dv8tion.jda.api.requests.restaction.pagination;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;

public interface BanPaginationAction extends PaginationAction<Guild.Ban, BanPaginationAction> {
   @Nonnull
   Guild getGuild();
}
