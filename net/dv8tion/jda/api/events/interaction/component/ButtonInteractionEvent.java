package net.dv8tion.jda.api.events.interaction.component;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

public class ButtonInteractionEvent extends GenericComponentInteractionCreateEvent implements ButtonInteraction {
   private final ButtonInteraction interaction;

   public ButtonInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull ButtonInteraction interaction) {
      super(api, responseNumber, interaction);
      this.interaction = interaction;
   }

   @Nonnull
   public ButtonInteraction getInteraction() {
      return this.interaction;
   }

   @Nonnull
   public Button getComponent() {
      return this.interaction.getComponent();
   }

   @Nonnull
   public Button getButton() {
      return this.interaction.getButton();
   }
}
