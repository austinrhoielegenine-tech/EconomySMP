package net.dv8tion.jda.api.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;

public class Timestamp {
   private final TimeFormat format;
   private final long timestamp;

   protected Timestamp(TimeFormat format, long timestamp) {
      Checks.notNull(format, "TimeFormat");
      this.format = format;
      this.timestamp = timestamp;
   }

   @Nonnull
   public TimeFormat getFormat() {
      return this.format;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   @Nonnull
   public Instant toInstant() {
      return Instant.ofEpochMilli(this.timestamp);
   }

   @Nonnull
   public Timestamp plus(long millis) {
      return new Timestamp(this.format, this.timestamp + millis);
   }

   @Nonnull
   public Timestamp plus(@Nonnull Duration duration) {
      Checks.notNull(duration, "Duration");
      return this.plus(duration.toMillis());
   }

   @Nonnull
   public Timestamp minus(long millis) {
      return new Timestamp(this.format, this.timestamp - millis);
   }

   @Nonnull
   public Timestamp minus(@Nonnull Duration duration) {
      Checks.notNull(duration, "Duration");
      return this.minus(duration.toMillis());
   }

   public String toString() {
      return "<t:" + this.timestamp / 1000L + ":" + this.format.getStyle() + ">";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Timestamp)) {
         return false;
      } else {
         Timestamp other = (Timestamp)o;
         return this.timestamp == other.timestamp && this.format == other.format;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.format, this.timestamp});
   }
}
