package net.dv8tion.jda.api.requests.restaction;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.forums.ForumPost;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.requests.FluentRestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;
import net.dv8tion.jda.internal.utils.Checks;

public interface ForumPostAction extends AbstractThreadCreateAction<ForumPost, ForumPostAction>, MessageCreateRequest<ForumPostAction>, FluentRestAction<ForumPost, ForumPostAction> {
   @Nonnull
   IPostContainer getChannel();

   @Nonnull
   @CheckReturnValue
   ForumPostAction setTags(@Nonnull Collection<? extends ForumTagSnowflake> var1);

   @Nonnull
   @CheckReturnValue
   default ForumPostAction setTags(@Nonnull ForumTagSnowflake... tags) {
      Checks.noneNull(tags, "Tags");
      return this.setTags(Arrays.asList(tags));
   }
}
