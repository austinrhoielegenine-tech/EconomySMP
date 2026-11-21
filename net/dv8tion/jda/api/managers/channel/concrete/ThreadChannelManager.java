package net.dv8tion.jda.api.managers.channel.concrete;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.managers.channel.ChannelManager;
import net.dv8tion.jda.api.managers.channel.attribute.ISlowmodeChannelManager;
import net.dv8tion.jda.internal.utils.Checks;

public interface ThreadChannelManager extends ChannelManager<ThreadChannel, ThreadChannelManager>, ISlowmodeChannelManager<ThreadChannel, ThreadChannelManager> {
   @Nonnull
   @CheckReturnValue
   ThreadChannelManager setAutoArchiveDuration(@Nonnull ThreadChannel.AutoArchiveDuration var1);

   @Nonnull
   @CheckReturnValue
   ThreadChannelManager setArchived(boolean var1);

   @Nonnull
   @CheckReturnValue
   ThreadChannelManager setLocked(boolean var1);

   @Nonnull
   @CheckReturnValue
   ThreadChannelManager setInvitable(boolean var1);

   @Nonnull
   @CheckReturnValue
   ThreadChannelManager setPinned(boolean var1);

   @Nonnull
   @CheckReturnValue
   ThreadChannelManager setAppliedTags(@Nonnull Collection<? extends ForumTagSnowflake> var1);

   @Nonnull
   @CheckReturnValue
   default ThreadChannelManager setAppliedTags(@Nonnull ForumTagSnowflake... tags) {
      Checks.noneNull(tags, "Tags");
      return this.setAppliedTags(Arrays.asList(tags));
   }
}
