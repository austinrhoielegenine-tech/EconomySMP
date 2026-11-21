package net.dv8tion.jda.api.events.channel.forum;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;

public class ForumTagRemoveEvent extends GenericForumTagEvent {
   public ForumTagRemoveEvent(@Nonnull JDA api, long responseNumber, @Nonnull IPostContainer channel, @Nonnull ForumTag tag) {
      super(api, responseNumber, channel, tag);
   }
}
