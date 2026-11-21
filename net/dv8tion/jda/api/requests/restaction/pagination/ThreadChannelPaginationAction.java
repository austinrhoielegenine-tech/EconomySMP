package net.dv8tion.jda.api.requests.restaction.pagination;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;

public interface ThreadChannelPaginationAction extends PaginationAction<ThreadChannel, ThreadChannelPaginationAction> {
   @Nonnull
   IThreadContainerUnion getChannel();

   @Nonnull
   default Guild getGuild() {
      return this.getChannel().getGuild();
   }
}
