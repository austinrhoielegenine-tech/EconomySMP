package net.dv8tion.jda.api.events.role.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleColors;

public class RoleUpdateColorsEvent extends GenericRoleUpdateEvent<RoleColors> {
   public static final String IDENTIFIER = "colors";

   public RoleUpdateColorsEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, @Nullable RoleColors previous) {
      super(api, responseNumber, role, previous, role.getColors(), "colors");
   }

   @Nonnull
   public RoleColors getOldValue() {
      return (RoleColors)super.getOldValue();
   }

   @Nonnull
   public RoleColors getNewValue() {
      return (RoleColors)super.getNewValue();
   }
}
