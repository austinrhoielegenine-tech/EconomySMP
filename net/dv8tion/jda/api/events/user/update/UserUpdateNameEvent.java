package net.dv8tion.jda.api.events.user.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class UserUpdateNameEvent extends GenericUserUpdateEvent<String> {
   public static final String IDENTIFIER = "name";

   public UserUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull String oldName) {
      super(api, responseNumber, user, oldName, user.getName(), "name");
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
