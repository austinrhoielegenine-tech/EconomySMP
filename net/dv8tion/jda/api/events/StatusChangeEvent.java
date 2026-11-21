package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

public class StatusChangeEvent extends Event implements UpdateEvent<JDA, JDA.Status> {
   public static final String IDENTIFIER = "status";
   protected final JDA.Status newStatus;
   protected final JDA.Status oldStatus;

   public StatusChangeEvent(@Nonnull JDA api, @Nonnull JDA.Status newStatus, @Nonnull JDA.Status oldStatus) {
      super(api);
      this.newStatus = newStatus;
      this.oldStatus = oldStatus;
   }

   @Nonnull
   public JDA.Status getNewStatus() {
      return this.newStatus;
   }

   @Nonnull
   public JDA.Status getOldStatus() {
      return this.oldStatus;
   }

   @Nonnull
   public String getPropertyIdentifier() {
      return "status";
   }

   @Nonnull
   public JDA getEntity() {
      return this.getJDA();
   }

   @Nonnull
   public JDA.Status getOldValue() {
      return this.oldStatus;
   }

   @Nonnull
   public JDA.Status getNewValue() {
      return this.newStatus;
   }
}
