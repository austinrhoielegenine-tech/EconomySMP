package net.dv8tion.jda.api.events.self;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

public class SelfUpdateNameEvent extends GenericSelfUpdateEvent<String> {
   public static final String IDENTIFIER = "name";

   public SelfUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull String oldName) {
      super(api, responseNumber, oldName, api.getSelfUser().getName(), "name");
   }

   @Nonnull
   public String getOldName() {
      return this.getOldValue();
   }

   @Nonnull
   public String getNewName() {
      return this.getNewValue();
   }

   @Nonnull
   public String getOldValue() {
      return (String)super.getOldValue();
   }

   @Nonnull
   public String getNewValue() {
      return (String)super.getNewValue();
   }
}
