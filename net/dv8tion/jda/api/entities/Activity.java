package net.dv8tion.jda.api.entities;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Contract;

public interface Activity {
   Pattern STREAMING_URL = Pattern.compile("https?://(www\\.)?(twitch\\.tv/|youtube\\.com/watch\\?v=).+", 2);
   int MAX_ACTIVITY_NAME_LENGTH = 128;
   int MAX_ACTIVITY_STATE_LENGTH = 128;

   boolean isRich();

   @Nullable
   RichPresence asRichPresence();

   @Nonnull
   String getName();

   @Nullable
   String getState();

   @Nullable
   String getUrl();

   @Nonnull
   ActivityType getType();

   @Nullable
   Timestamps getTimestamps();

   @Nullable
   EmojiUnion getEmoji();

   @Nonnull
   @Contract("_->new")
   Activity withState(@Nullable String var1);

   @Nonnull
   static Activity playing(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notLonger(name, 128, "Name");
      return EntityBuilder.createActivity(name, (String)null, Activity.ActivityType.PLAYING);
   }

   @Nonnull
   static Activity streaming(@Nonnull String name, @Nullable String url) {
      Checks.notEmpty(name, "Provided game name");
      name = Helpers.isBlank(name) ? name : name.trim();
      Checks.notLonger(name, 128, "Name");
      ActivityType type;
      if (isValidStreamingUrl(url)) {
         type = Activity.ActivityType.STREAMING;
      } else {
         type = Activity.ActivityType.PLAYING;
      }

      return EntityBuilder.createActivity(name, url, type);
   }

   @Nonnull
   static Activity listening(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notLonger(name, 128, "Name");
      return EntityBuilder.createActivity(name, (String)null, Activity.ActivityType.LISTENING);
   }

   @Nonnull
   static Activity watching(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notLonger(name, 128, "Name");
      return EntityBuilder.createActivity(name, (String)null, Activity.ActivityType.WATCHING);
   }

   @Nonnull
   static Activity competing(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notLonger(name, 128, "Name");
      return EntityBuilder.createActivity(name, (String)null, Activity.ActivityType.COMPETING);
   }

   @Nonnull
   static Activity customStatus(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notLonger(name, 128, "Name");
      return EntityBuilder.createActivity(name, (String)null, Activity.ActivityType.CUSTOM_STATUS);
   }

   @Nonnull
   static Activity of(@Nonnull ActivityType type, @Nonnull String name) {
      return of(type, name, (String)null);
   }

   @Nonnull
   static Activity of(@Nonnull ActivityType type, @Nonnull String name, @Nullable String url) {
      Checks.notNull(type, "Type");
      switch (type.ordinal()) {
         case 0:
            return playing(name);
         case 1:
            return streaming(name, url);
         case 2:
            return listening(name);
         case 3:
            return watching(name);
         case 4:
            return customStatus(name);
         case 5:
            return competing(name);
         default:
            throw new IllegalArgumentException("ActivityType " + type + " is not supported!");
      }
   }

   static boolean isValidStreamingUrl(@Nullable String url) {
      return url != null && STREAMING_URL.matcher(url).matches();
   }

   public static enum ActivityType {
      PLAYING(0),
      STREAMING(1),
      LISTENING(2),
      WATCHING(3),
      CUSTOM_STATUS(4),
      COMPETING(5);

      private final int key;

      private ActivityType(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static ActivityType fromKey(int key) {
         switch (key) {
            case 0:
            default:
               return PLAYING;
            case 1:
               return STREAMING;
            case 2:
               return LISTENING;
            case 3:
               return WATCHING;
            case 4:
               return CUSTOM_STATUS;
            case 5:
               return COMPETING;
         }
      }

      // $FF: synthetic method
      private static ActivityType[] $values() {
         return new ActivityType[]{PLAYING, STREAMING, LISTENING, WATCHING, CUSTOM_STATUS, COMPETING};
      }
   }

   public static class Timestamps {
      protected final long start;
      protected final long end;

      public Timestamps(long start, long end) {
         this.start = start;
         this.end = end;
      }

      public long getStart() {
         return this.start;
      }

      @Nullable
      public Instant getStartTime() {
         return this.start <= 0L ? null : Instant.ofEpochMilli(this.start);
      }

      public long getEnd() {
         return this.end;
      }

      @Nullable
      public Instant getEndTime() {
         return this.end <= 0L ? null : Instant.ofEpochMilli(this.end);
      }

      public long getRemainingTime(@Nonnull TemporalUnit unit) {
         Checks.notNull(unit, "TemporalUnit");
         Instant end = this.getEndTime();
         return end != null ? Instant.now().until(end, unit) : -1L;
      }

      public long getElapsedTime(@Nonnull TemporalUnit unit) {
         Checks.notNull(unit, "TemporalUnit");
         Instant start = this.getStartTime();
         return start != null ? start.until(Instant.now(), unit) : -1L;
      }

      public String toString() {
         return (new EntityString("RichPresenceTimestamp")).addMetadata("start", this.start).addMetadata("end", this.end).toString();
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Timestamps)) {
            return false;
         } else {
            Timestamps t = (Timestamps)obj;
            return this.start == t.start && this.end == t.end;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.start, this.end});
      }
   }
}
