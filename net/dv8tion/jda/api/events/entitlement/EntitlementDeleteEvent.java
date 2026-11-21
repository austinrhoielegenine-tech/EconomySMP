package net.dv8tion.jda.api.events.entitlement;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;

public class EntitlementDeleteEvent extends GenericEntitlementEvent {
   public EntitlementDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Entitlement entitlement) {
      super(api, responseNumber, entitlement);
   }
}
