package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentDetections;

public class GuildUpdateSecurityIncidentDetectionsEvent extends GenericGuildUpdateEvent<SecurityIncidentDetections> {
   public static final String IDENTIFIER = "security_incident_detections";

   public GuildUpdateSecurityIncidentDetectionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull SecurityIncidentDetections previous) {
      super(api, responseNumber, guild, previous, guild.getSecurityIncidentDetections(), "security_incident_detections");
   }

   @Nonnull
   public SecurityIncidentDetections getOldSecurityIncidentDetections() {
      return (SecurityIncidentDetections)this.getOldValue();
   }

   @Nonnull
   public SecurityIncidentDetections getNewSecurityIncidentDetections() {
      return (SecurityIncidentDetections)this.getNewValue();
   }
}
