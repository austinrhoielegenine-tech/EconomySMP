package net.dv8tion.jda.api.events.role;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

public class RoleCreateEvent extends GenericRoleEvent {
   public RoleCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role createdRole) {
      super(api, responseNumber, createdRole);
   }
}
