package net.dv8tion.jda.api.requests.restaction.pagination;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

public interface ReactionPaginationAction extends PaginationAction<User, ReactionPaginationAction> {
   @Nonnull
   MessageReaction getReaction();
}
