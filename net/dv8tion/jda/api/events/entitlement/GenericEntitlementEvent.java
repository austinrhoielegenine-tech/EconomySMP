package net.dv8tion.jda.api.events.entitlement;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.events.Event;

public abstract class GenericEntitlementEvent extends Event {
   protected final Entitlement entitlement;

   protected GenericEntitlementEvent(@Nonnull JDA api, long responseNumber, @Nonnull Entitlement entitlement) {
      super(api, responseNumber);
      this.entitlement = entitlement;
   }

   @Nonnull
   public Entitlement getEntitlement() {
      return this.entitlement;
   }
}
