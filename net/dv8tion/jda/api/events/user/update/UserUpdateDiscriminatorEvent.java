package net.dv8tion.jda.api.events.user.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class UserUpdateDiscriminatorEvent extends GenericUserUpdateEvent<String> {
   public static final String IDENTIFIER = "discriminator";

   public UserUpdateDiscriminatorEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull String oldDiscriminator) {
      super(api, responseNumber, user, oldDiscriminator, user.getDiscriminator(), "discriminator");
   }

   @Nonnull
   public String getOldDiscriminator() {
      return this.getOldValue();
   }

   @Nonnull
   public String getNewDiscriminator() {
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
