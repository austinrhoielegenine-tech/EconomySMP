package net.dv8tion.jda.api.events.role.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

public class RoleUpdateHoistedEvent extends GenericRoleUpdateEvent<Boolean> {
   public static final String IDENTIFIER = "hoist";

   public RoleUpdateHoistedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, boolean wasHoisted) {
      super(api, responseNumber, role, wasHoisted, !wasHoisted, "hoist");
   }

   public boolean wasHoisted() {
      return this.getOldValue();
   }

   @Nonnull
   public Boolean getOldValue() {
      return (Boolean)super.getOldValue();
   }

   @Nonnull
   public Boolean getNewValue() {
      return (Boolean)super.getNewValue();
   }
}
