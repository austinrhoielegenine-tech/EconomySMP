package net.dv8tion.jda.api.events.interaction;

import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;

public class GenericAutoCompleteInteractionEvent extends GenericInteractionCreateEvent implements IAutoCompleteCallback {
   public GenericAutoCompleteInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Interaction interaction) {
      super(api, responseNumber, interaction);
   }

   @Nonnull
   public IAutoCompleteCallback getInteraction() {
      return (IAutoCompleteCallback)super.getInteraction();
   }

   @Nonnull
   @CheckReturnValue
   public AutoCompleteCallbackAction replyChoices(@Nonnull Collection<Command.Choice> choices) {
      return this.getInteraction().replyChoices(choices);
   }
}
