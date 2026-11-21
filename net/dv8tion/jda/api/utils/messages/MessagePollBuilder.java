package net.dv8tion.jda.api.utils.messages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.internal.utils.Checks;

public class MessagePollBuilder {
   private final List<MessagePoll.Answer> answers = new ArrayList(10);
   private MessagePoll.LayoutType layout;
   private String title;
   private Duration duration;
   private boolean isMultiAnswer;

   public MessagePollBuilder(@Nonnull String title) {
      this.layout = MessagePoll.LayoutType.DEFAULT;
      this.duration = Duration.ofHours(24L);
      this.setTitle(title);
   }

   public MessagePollBuilder(@Nonnull MessagePoll poll) {
      this.layout = MessagePoll.LayoutType.DEFAULT;
      this.duration = Duration.ofHours(24L);
      Checks.notNull(poll, "Poll");
      this.title = poll.getQuestion().getText();
      this.isMultiAnswer = poll.isMultiAnswer();
      this.layout = poll.getLayout();

      for(MessagePoll.Answer answer : poll.getAnswers()) {
         this.addAnswer(answer.getText(), answer.getEmoji());
      }

   }

   @Nonnull
   public MessagePollBuilder setLayout(@Nonnull MessagePoll.LayoutType layout) {
      Checks.notNull(layout, "Layout");
      Checks.check(layout != MessagePoll.LayoutType.UNKNOWN, "Layout cannot be UNKNOWN");
      this.layout = layout;
      return this;
   }

   @Nonnull
   public MessagePollBuilder setTitle(@Nonnull String title) {
      Checks.notBlank(title, "Title");
      title = title.trim();
      Checks.notLonger(title, 300, "Title");
      this.title = title;
      return this;
   }

   @Nonnull
   public MessagePollBuilder setDuration(@Nonnull Duration duration) {
      Checks.notNull(duration, "Duration");
      Checks.positive(duration.toHours(), "Duration");
      Checks.notLonger(duration, Duration.ofHours(168L), TimeUnit.HOURS, "Duration");
      this.duration = duration;
      return this;
   }

   @Nonnull
   public MessagePollBuilder setDuration(long duration, @Nonnull TimeUnit unit) {
      Checks.notNull(unit, "TimeUnit");
      return this.setDuration(Duration.ofHours(unit.toHours(duration)));
   }

   @Nonnull
   public MessagePollBuilder setMultiAnswer(boolean multiAnswer) {
      this.isMultiAnswer = multiAnswer;
      return this;
   }

   @Nonnull
   public MessagePollBuilder addAnswer(@Nonnull String title) {
      return this.addAnswer(title, (Emoji)null);
   }

   @Nonnull
   public MessagePollBuilder addAnswer(@Nonnull String title, @Nullable Emoji emoji) {
      Checks.notBlank(title, "Answer title");
      title = title.trim();
      Checks.notLonger(title, 55, "Answer title");
      Checks.check(this.answers.size() < 10, "Poll cannot have more than %d answers", (int)10);
      this.answers.add(new MessagePoll.Answer((long)(this.answers.size() + 1), title, (EmojiUnion)emoji, 0, false));
      return this;
   }

   @Nonnull
   public MessagePollData build() {
      if (this.answers.isEmpty()) {
         throw new IllegalStateException("Cannot build a poll without answers");
      } else {
         return new MessagePollData(this.layout, new MessagePoll.Question(this.title, (Emoji)null), new ArrayList(this.answers), this.duration, this.isMultiAnswer);
      }
   }
}
