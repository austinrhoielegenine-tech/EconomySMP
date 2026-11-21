package net.dv8tion.jda.api.events.interaction.component;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

public class StringSelectInteractionEvent extends GenericSelectMenuInteractionEvent<String, StringSelectMenu> implements StringSelectInteraction {
   private final StringSelectInteraction interaction;

   public StringSelectInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull StringSelectInteraction interaction) {
      super(api, responseNumber, interaction);
      this.interaction = interaction;
   }

   @Nonnull
   public StringSelectInteraction getInteraction() {
      return this.interaction;
   }
}
