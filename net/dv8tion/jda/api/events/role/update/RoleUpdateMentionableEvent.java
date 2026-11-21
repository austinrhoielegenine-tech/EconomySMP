package net.dv8tion.jda.api.events.role.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

public class RoleUpdateMentionableEvent extends GenericRoleUpdateEvent<Boolean> {
   public static final String IDENTIFIER = "mentionable";

   public RoleUpdateMentionableEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, boolean wasMentionable) {
      super(api, responseNumber, role, wasMentionable, !wasMentionable, "mentionable");
   }

   public boolean wasMentionable() {
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
