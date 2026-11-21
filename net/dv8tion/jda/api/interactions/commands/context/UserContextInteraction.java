package net.dv8tion.jda.api.interactions.commands.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public interface UserContextInteraction extends ContextInteraction<User> {
   @Nonnull
   default ContextInteraction.ContextTarget getTargetType() {
      return ContextInteraction.ContextTarget.USER;
   }

   @Nullable
   Member getTargetMember();
}
