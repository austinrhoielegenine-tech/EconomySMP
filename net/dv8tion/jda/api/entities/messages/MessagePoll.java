package net.dv8tion.jda.api.entities.messages;

import java.time.OffsetDateTime;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import org.jetbrains.annotations.Unmodifiable;

public interface MessagePoll {
   int MAX_QUESTION_TEXT_LENGTH = 300;
   int MAX_ANSWER_TEXT_LENGTH = 55;
   int MAX_ANSWERS = 10;
   long MAX_DURATION_HOURS = 168L;

   @Nonnull
   LayoutType getLayout();

   @Nonnull
   Question getQuestion();

   @Nonnull
   @Unmodifiable List<Answer> getAnswers();

   @Nullable
   OffsetDateTime getTimeExpiresAt();

   boolean isMultiAnswer();

   boolean isFinalizedVotes();

   default boolean isExpired() {
      return this.getTimeExpiresAt().isBefore(OffsetDateTime.now());
   }

   public static class Question {
      private final String text;
      private final EmojiUnion emoji;

      public Question(String text, Emoji emoji) {
         this.text = text;
         this.emoji = (EmojiUnion)emoji;
      }

      @Nonnull
      public String getText() {
         return this.text;
      }

      @Nullable
      public EmojiUnion getEmoji() {
         return this.emoji;
      }
   }

   public static class Answer {
      private final long id;
      private final String text;
      private final EmojiUnion emoji;
      private final int votes;
      private final boolean selfVoted;

      public Answer(long id, String text, EmojiUnion emoji, int votes, boolean selfVoted) {
         this.id = id;
         this.text = text;
         this.emoji = emoji;
         this.votes = votes;
         this.selfVoted = selfVoted;
      }

      public long getId() {
         return this.id;
      }

      @Nonnull
      public String getText() {
         return this.text;
      }

      @Nullable
      public EmojiUnion getEmoji() {
         return this.emoji;
      }

      public int getVotes() {
         return this.votes;
      }

      public boolean isSelfVoted() {
         return this.selfVoted;
      }
   }

   public static enum LayoutType {
      DEFAULT(1),
      UNKNOWN(-1);

      private final int key;

      private LayoutType(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static LayoutType fromKey(int key) {
         for(LayoutType type : values()) {
            if (type.key == key) {
               return type;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static LayoutType[] $values() {
         return new LayoutType[]{DEFAULT, UNKNOWN};
      }
   }
}
