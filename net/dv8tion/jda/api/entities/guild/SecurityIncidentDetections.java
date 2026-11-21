package net.dv8tion.jda.api.entities.guild;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class SecurityIncidentDetections {
   public static final SecurityIncidentDetections EMPTY = new SecurityIncidentDetections(0L, 0L);
   private final long dmSpamDetectedAt;
   private final long raidDetectedAt;

   public SecurityIncidentDetections(long dmSpamDetectedAt, long raidDetectedAt) {
      this.dmSpamDetectedAt = dmSpamDetectedAt;
      this.raidDetectedAt = raidDetectedAt;
   }

   @Nullable
   public OffsetDateTime getTimeDetectedDmSpam() {
      return this.dmSpamDetectedAt == 0L ? null : Helpers.toOffset(this.dmSpamDetectedAt);
   }

   @Nullable
   public OffsetDateTime getTimeDetectedRaid() {
      return this.raidDetectedAt == 0L ? null : Helpers.toOffset(this.raidDetectedAt);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.dmSpamDetectedAt, this.raidDetectedAt});
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof SecurityIncidentDetections)) {
         return false;
      } else {
         SecurityIncidentDetections that = (SecurityIncidentDetections)o;
         return this.dmSpamDetectedAt == that.dmSpamDetectedAt && this.raidDetectedAt == that.raidDetectedAt;
      }
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("dmSpamDetectedAt", this.getTimeDetectedDmSpam()).addMetadata("raidDetectedAt", this.getTimeDetectedRaid()).toString();
   }
}
