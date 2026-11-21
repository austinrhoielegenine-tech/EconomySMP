package net.dv8tion.jda.api.events.role;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

public class RoleDeleteEvent extends GenericRoleEvent {
   public RoleDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role deletedRole) {
      super(api, responseNumber, deletedRole);
   }
}
