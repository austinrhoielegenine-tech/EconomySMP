package net.dv8tion.jda.api.events.user.update;

import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class UserUpdateGlobalNameEvent extends GenericUserUpdateEvent<String> {
   public static final String IDENTIFIER = "global_name";

   public UserUpdateGlobalNameEvent(JDA api, long responseNumber, User user, String oldName) {
      super(api, responseNumber, user, oldName, user.getGlobalName(), "global_name");
   }

   @Nullable
   public String getOldGlobalName() {
      return (String)this.getOldValue();
   }

   @Nullable
   public String getNewGlobalName() {
      return (String)this.getNewValue();
   }

   public String toString() {
      return "UserUpdateGlobalName(" + (String)this.getOldValue() + "->" + (String)this.getNewValue() + ')';
   }
}
