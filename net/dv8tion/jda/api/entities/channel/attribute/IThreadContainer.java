package net.dv8tion.jda.api.entities.channel.attribute;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadChannelPaginationAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface IThreadContainer extends GuildChannel, IPermissionContainer {
   int getDefaultThreadSlowmode();

   @Nonnull
   default @Unmodifiable List<ThreadChannel> getThreadChannels() {
      return (List)this.getGuild().getThreadChannelCache().applyStream((stream) -> (List)stream.filter((thread) -> thread.getParentChannel() == this).collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   @CheckReturnValue
   default ThreadChannelAction createThreadChannel(@Nonnull String name) {
      return this.createThreadChannel(name, false);
   }

   @Nonnull
   @CheckReturnValue
   ThreadChannelAction createThreadChannel(@Nonnull String var1, boolean var2);

   @Nonnull
   @CheckReturnValue
   ThreadChannelAction createThreadChannel(@Nonnull String var1, long var2);

   @Nonnull
   @CheckReturnValue
   default ThreadChannelAction createThreadChannel(@Nonnull String name, @Nonnull String messageId) {
      return this.createThreadChannel(name, MiscUtil.parseSnowflake(messageId));
   }

   @Nonnull
   @CheckReturnValue
   ThreadChannelPaginationAction retrieveArchivedPublicThreadChannels();

   @Nonnull
   @CheckReturnValue
   ThreadChannelPaginationAction retrieveArchivedPrivateThreadChannels();

   @Nonnull
   @CheckReturnValue
   ThreadChannelPaginationAction retrieveArchivedPrivateJoinedThreadChannels();
}
