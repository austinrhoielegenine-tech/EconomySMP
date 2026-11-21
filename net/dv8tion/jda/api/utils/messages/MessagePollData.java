package net.dv8tion.jda.api.utils.messages;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Helpers;

public class MessagePollData implements SerializableData {
   private final MessagePoll.LayoutType layout;
   private final MessagePoll.Question question;
   private final List<MessagePoll.Answer> answers;
   private final Duration duration;
   private final boolean isMultiAnswer;

   public MessagePollData(MessagePoll.LayoutType layout, MessagePoll.Question question, List<MessagePoll.Answer> answers, Duration duration, boolean isMultiAnswer) {
      this.layout = layout;
      this.question = question;
      this.answers = answers;
      this.duration = duration;
      this.isMultiAnswer = isMultiAnswer;
   }

   @Nonnull
   public static MessagePollBuilder builder(@Nonnull String title) {
      return new MessagePollBuilder(title);
   }

   @Nonnull
   public static MessagePollData from(@Nonnull MessagePoll poll) {
      return (new MessagePollBuilder(poll)).build();
   }

   @Nonnull
   public DataObject toData() {
      DataObject data = DataObject.empty();
      data.put("duration", TimeUnit.SECONDS.toHours(this.duration.getSeconds()));
      data.put("allow_multiselect", this.isMultiAnswer);
      data.put("layout_type", this.layout.getKey());
      data.put("question", DataObject.empty().put("text", this.question.getText()));
      data.put("answers", this.answers.stream().map((answer) -> DataObject.empty().put("answer_id", answer.getId()).put("poll_media", DataObject.empty().put("text", answer.getText()).put("emoji", answer.getEmoji()))).collect(Helpers.toDataArray()));
      return data;
   }
}
