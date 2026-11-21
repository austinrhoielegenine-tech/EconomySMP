package net.dv8tion.jda.api.events.channel.update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.internal.utils.Helpers;

public class ChannelUpdateAppliedTagsEvent extends GenericChannelUpdateEvent<List<Long>> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateAppliedTagsEvent(@Nonnull JDA api, long responseNumber, @Nonnull ThreadChannel channel, @Nonnull List<Long> oldValue, @Nonnull List<Long> newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   @Nonnull
   public List<ForumTag> getAddedTags() {
      List<ForumTag> newTags = new ArrayList(this.getNewTags());
      newTags.removeAll(this.getOldTags());
      return newTags;
   }

   @Nonnull
   public List<ForumTag> getRemovedTags() {
      List<ForumTag> oldTags = new ArrayList(this.getOldTags());
      oldTags.removeAll(this.getNewTags());
      return oldTags;
   }

   @Nonnull
   public List<ForumTag> getNewTags() {
      SortedSnowflakeCacheView<ForumTag> cache = this.getChannel().asThreadChannel().getParentChannel().asForumChannel().getAvailableTagCache();
      Stream var10000 = this.getNewValue().stream();
      Objects.requireNonNull(cache);
      return (List)var10000.map(cache::getElementById).filter(Objects::nonNull).sorted().collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public List<ForumTag> getOldTags() {
      SortedSnowflakeCacheView<ForumTag> cache = this.getChannel().asThreadChannel().getParentChannel().asForumChannel().getAvailableTagCache();
      Stream var10000 = this.getOldValue().stream();
      Objects.requireNonNull(cache);
      return (List)var10000.map(cache::getElementById).filter(Objects::nonNull).sorted().collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public List<Long> getOldValue() {
      return (List)super.getOldValue();
   }

   @Nonnull
   public List<Long> getNewValue() {
      return (List)super.getNewValue();
   }

   static {
      FIELD = ChannelField.APPLIED_TAGS;
      IDENTIFIER = FIELD.getFieldName();
   }
}
