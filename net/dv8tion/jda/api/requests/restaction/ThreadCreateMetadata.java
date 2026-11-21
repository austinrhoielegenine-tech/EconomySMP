package net.dv8tion.jda.api.requests.restaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.internal.utils.Checks;

public class ThreadCreateMetadata {
   private final String name;
   private final List<ForumTagSnowflake> appliedTags = new ArrayList(5);

   public ThreadCreateMetadata(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notLonger(name, 100, "Name");
      this.name = name;
   }

   @Nonnull
   public ThreadCreateMetadata addTags(@Nonnull Collection<? extends ForumTagSnowflake> tags) {
      Checks.noneNull(tags, "Tags");
      Checks.check(tags.size() <= 5, "Cannot have more than %d post tags. Provided: %d", 5, tags.size());
      this.appliedTags.addAll(tags);
      return this;
   }

   @Nonnull
   public ThreadCreateMetadata addTags(@Nonnull ForumTagSnowflake... tags) {
      Checks.noneNull(tags, "Tags");
      Checks.check(tags.length <= 5, "Cannot have more than %d post tags. Provided: %d", 5, tags.length);
      Collections.addAll(this.appliedTags, tags);
      return this;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public List<ForumTagSnowflake> getAppliedTags() {
      return this.appliedTags;
   }
}
