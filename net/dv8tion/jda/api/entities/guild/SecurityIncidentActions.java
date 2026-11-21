package net.dv8tion.jda.api.entities.guild;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class SecurityIncidentActions {
   private static final SecurityIncidentActions disabled = new SecurityIncidentActions(0L, 0L);
   private final long invitesDisabledUntil;
   private final long directMessagesDisabledUntil;

   private SecurityIncidentActions(long invitesDisabledUntil, long directMessagesDisabledUntil) {
      this.invitesDisabledUntil = invitesDisabledUntil;
      this.directMessagesDisabledUntil = directMessagesDisabledUntil;
   }

   @Nullable
   public OffsetDateTime getInvitesDisabledUntil() {
      return this.invitesDisabledUntil == 0L ? null : Helpers.toOffset(this.invitesDisabledUntil);
   }

   @Nullable
   public OffsetDateTime getDirectMessagesDisabledUntil() {
      return this.directMessagesDisabledUntil == 0L ? null : Helpers.toOffset(this.directMessagesDisabledUntil);
   }

   @Nonnull
   public static SecurityIncidentActions disabled() {
      return disabled;
   }

   @Nonnull
   public static SecurityIncidentActions enabled(@Nullable OffsetDateTime invitesDisabledUntil, @Nullable OffsetDateTime directMessagesDisabledUntil) {
      return new SecurityIncidentActions(invitesDisabledUntil == null ? 0L : invitesDisabledUntil.toInstant().toEpochMilli(), directMessagesDisabledUntil == null ? 0L : directMessagesDisabledUntil.toInstant().toEpochMilli());
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.invitesDisabledUntil, this.directMessagesDisabledUntil});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof SecurityIncidentActions)) {
         return false;
      } else {
         SecurityIncidentActions other = (SecurityIncidentActions)obj;
         return this.invitesDisabledUntil == other.invitesDisabledUntil && this.directMessagesDisabledUntil == other.directMessagesDisabledUntil;
      }
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("invitesDisabledUntil", this.getInvitesDisabledUntil()).addMetadata("directMessagesDisabledUntil", this.getDirectMessagesDisabledUntil()).toString();
   }
}
