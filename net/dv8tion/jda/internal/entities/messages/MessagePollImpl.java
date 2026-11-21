package net.dv8tion.jda.internal.entities.messages;

import java.time.OffsetDateTime;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.messages.MessagePoll;

public class MessagePollImpl implements MessagePoll {
   private final MessagePoll.LayoutType layout;
   private final MessagePoll.Question question;
   private final List<MessagePoll.Answer> answers;
   private final OffsetDateTime expiresAt;
   private final boolean isMultiAnswer;
   private final boolean isFinalizedVotes;

   public MessagePollImpl(MessagePoll.LayoutType layout, MessagePoll.Question question, List<MessagePoll.Answer> answers, OffsetDateTime expiresAt, boolean isMultiAnswer, boolean isFinalizedVotes) {
      this.layout = layout;
      this.question = question;
      this.answers = answers;
      this.expiresAt = expiresAt;
      this.isMultiAnswer = isMultiAnswer;
      this.isFinalizedVotes = isFinalizedVotes;
   }

   @Nonnull
   public MessagePoll.LayoutType getLayout() {
      return this.layout;
   }

   @Nonnull
   public MessagePoll.Question getQuestion() {
      return this.question;
   }

   @Nonnull
   public List<MessagePoll.Answer> getAnswers() {
      return this.answers;
   }

   @Nonnull
   public OffsetDateTime getTimeExpiresAt() {
      return this.expiresAt;
   }

   public boolean isMultiAnswer() {
      return this.isMultiAnswer;
   }

   public boolean isFinalizedVotes() {
      return this.isFinalizedVotes;
   }
}
