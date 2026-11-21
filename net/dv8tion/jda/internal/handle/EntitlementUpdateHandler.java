package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.events.entitlement.EntitlementUpdateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public class EntitlementUpdateHandler extends SocketHandler {
   public EntitlementUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      this.getJDA().handleEvent(new EntitlementUpdateEvent(this.getJDA(), this.responseNumber, this.getJDA().getEntityBuilder().createEntitlement(content)));
      return null;
   }
}
