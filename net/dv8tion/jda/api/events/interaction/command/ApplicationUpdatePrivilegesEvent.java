package net.dv8tion.jda.api.events.interaction.command;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.interactions.commands.privileges.PrivilegeTargetType;

public class ApplicationUpdatePrivilegesEvent extends GenericPrivilegeUpdateEvent {
   public ApplicationUpdatePrivilegesEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, long applicationId, @Nonnull List<IntegrationPrivilege> privileges) {
      super(api, responseNumber, guild, applicationId, applicationId, privileges);
   }

   @Nonnull
   public PrivilegeTargetType getTargetType() {
      return PrivilegeTargetType.INTEGRATION;
   }
}
