package net.dv8tion.jda.api.events.interaction.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.context.ContextInteraction;

public class GenericContextInteractionEvent<T> extends GenericCommandInteractionEvent implements ContextInteraction<T> {
   public GenericContextInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull ContextInteraction<T> interaction) {
      super(api, responseNumber, interaction);
   }

   @Nonnull
   public ContextInteraction<T> getInteraction() {
      return (ContextInteraction)super.getInteraction();
   }

   @Nonnull
   public ContextInteraction.ContextTarget getTargetType() {
      return this.getInteraction().getTargetType();
   }

   @Nonnull
   public T getTarget() {
      return (T)this.getInteraction().getTarget();
   }
}
