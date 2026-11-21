package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.events.entitlement.EntitlementCreateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public class EntitlementCreateHandler extends SocketHandler {
   public EntitlementCreateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      this.getJDA().handleEvent(new EntitlementCreateEvent(this.getJDA(), this.responseNumber, this.getJDA().getEntityBuilder().createEntitlement(content)));
      return null;
   }
}
