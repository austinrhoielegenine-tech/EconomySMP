package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

public class GatewayPingEvent extends Event implements UpdateEvent<JDA, Long> {
   public static final String IDENTIFIER = "gateway-ping";
   private final long next;
   private final long prev;

   public GatewayPingEvent(@Nonnull JDA api, long old) {
      super(api);
      this.next = api.getGatewayPing();
      this.prev = old;
   }

   public long getNewPing() {
      return this.next;
   }

   public long getOldPing() {
      return this.prev;
   }

   @Nonnull
   public String getPropertyIdentifier() {
      return "gateway-ping";
   }

   @Nonnull
   public JDA getEntity() {
      return this.getJDA();
   }

   @Nonnull
   public Long getOldValue() {
      return this.prev;
   }

   @Nonnull
   public Long getNewValue() {
      return this.next;
   }
}
