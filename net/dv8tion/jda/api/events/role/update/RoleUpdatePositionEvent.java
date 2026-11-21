package net.dv8tion.jda.api.events.role.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

public class RoleUpdatePositionEvent extends GenericRoleUpdateEvent<Integer> {
   public static final String IDENTIFIER = "position";
   private final int oldPositionRaw;
   private final int newPositionRaw;

   public RoleUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, int oldPosition, int oldPositionRaw) {
      super(api, responseNumber, role, oldPosition, role.getPosition(), "position");
      this.oldPositionRaw = oldPositionRaw;
      this.newPositionRaw = role.getPositionRaw();
   }

   public int getOldPosition() {
      return this.getOldValue();
   }

   public int getOldPositionRaw() {
      return this.oldPositionRaw;
   }

   public int getNewPosition() {
      return this.getNewValue();
   }

   public int getNewPositionRaw() {
      return this.newPositionRaw;
   }

   @Nonnull
   public Integer getOldValue() {
      return (Integer)super.getOldValue();
   }

   @Nonnull
   public Integer getNewValue() {
      return (Integer)super.getNewValue();
   }
}
