package net.dv8tion.jda.api.events.role.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleIcon;

public class RoleUpdateIconEvent extends GenericRoleUpdateEvent<RoleIcon> {
   public static final String IDENTIFIER = "icon";

   public RoleUpdateIconEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, @Nullable RoleIcon oldIcon) {
      super(api, responseNumber, role, oldIcon, role.getIcon(), "icon");
   }

   @Nullable
   public RoleIcon getOldIcon() {
      return (RoleIcon)this.getOldValue();
   }

   @Nullable
   public RoleIcon getNewIcon() {
      return (RoleIcon)this.getNewValue();
   }
}
