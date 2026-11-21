package net.dv8tion.jda.api.events.user.update;

import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class UserUpdatePrimaryGuildEvent extends GenericUserUpdateEvent<User.PrimaryGuild> {
   public static final String IDENTIFIER = "primary_guild";

   public UserUpdatePrimaryGuildEvent(JDA api, long responseNumber, User user, User.PrimaryGuild oldPrimaryGuild) {
      super(api, responseNumber, user, oldPrimaryGuild, user.getPrimaryGuild(), "primary_guild");
   }

   @Nullable
   public User.PrimaryGuild getOldPrimaryGuild() {
      return (User.PrimaryGuild)this.getOldValue();
   }

   @Nullable
   public User.PrimaryGuild getNewPrimaryGuild() {
      return (User.PrimaryGuild)this.getNewValue();
   }
}
