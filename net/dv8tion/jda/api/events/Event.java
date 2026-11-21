package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.handle.SocketHandler;
import net.dv8tion.jda.internal.utils.EntityString;

public abstract class Event implements GenericEvent {
   protected final JDA api;
   protected final long responseNumber;
   protected final DataObject rawData;

   public Event(@Nonnull JDA api, long responseNumber) {
      this.api = api;
      this.responseNumber = responseNumber;
      this.rawData = api instanceof JDAImpl && ((JDAImpl)api).isEventPassthrough() ? (DataObject)SocketHandler.CURRENT_EVENT.get() : null;
   }

   public Event(@Nonnull JDA api) {
      this(api, api.getResponseTotal());
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   public long getResponseNumber() {
      return this.responseNumber;
   }

   @Nullable
   public DataObject getRawData() {
      if (this.api instanceof JDAImpl && !((JDAImpl)this.api).isEventPassthrough()) {
         throw new IllegalStateException("Event passthrough is not enabled, see JDABuilder#setEventPassthrough(boolean)");
      } else {
         return this.rawData;
      }
   }

   public String toString() {
      if (this instanceof UpdateEvent) {
         UpdateEvent<?, ?> event = (UpdateEvent)this;
         return (new EntityString(this)).setType(event.getPropertyIdentifier()).addMetadata((String)null, event.getOldValue() + " -> " + event.getNewValue()).toString();
      } else {
         return (new EntityString(this)).toString();
      }
   }
}
