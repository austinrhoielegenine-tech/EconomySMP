package net.dv8tion.jda.api.events.channel.forum;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.events.Event;

public abstract class GenericForumTagEvent extends Event {
   protected final IPostContainer channel;
   protected final ForumTag tag;

   public GenericForumTagEvent(@Nonnull JDA api, long responseNumber, @Nonnull IPostContainer channel, @Nonnull ForumTag tag) {
      super(api, responseNumber);
      this.channel = channel;
      this.tag = tag;
   }

   @Nonnull
   public IPostContainer getChannel() {
      return this.channel;
   }

   @Nonnull
   public ForumTag getTag() {
      return this.tag;
   }
}
