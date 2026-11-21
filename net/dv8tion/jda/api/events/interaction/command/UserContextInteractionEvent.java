package net.dv8tion.jda.api.events.interaction.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.context.UserContextInteraction;

public class UserContextInteractionEvent extends GenericContextInteractionEvent<User> implements UserContextInteraction {
   public UserContextInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull UserContextInteraction interaction) {
      super(api, responseNumber, interaction);
   }

   @Nonnull
   public UserContextInteraction getInteraction() {
      return (UserContextInteraction)super.getInteraction();
   }

   @Nullable
   public Member getTargetMember() {
      return this.getInteraction().getTargetMember();
   }
}
