package net.dv8tion.jda.api.events.role.update;

import java.awt.Color;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

/** @deprecated */
@Deprecated
@ReplaceWith("RoleUpdateColorsEvent")
public class RoleUpdateColorEvent extends GenericRoleUpdateEvent<Integer> {
   public static final String IDENTIFIER = "color";

   public RoleUpdateColorEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, int oldColor) {
      super(api, responseNumber, role, oldColor, role.getColorRaw(), "color");
   }

   @Nullable
   public Color getOldColor() {
      return (Integer)this.previous != 536870911 ? new Color((Integer)this.previous) : null;
   }

   public int getOldColorRaw() {
      return this.getOldValue();
   }

   @Nullable
   public Color getNewColor() {
      return (Integer)this.next != 536870911 ? new Color((Integer)this.next) : null;
   }

   public int getNewColorRaw() {
      return this.getNewValue();
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
