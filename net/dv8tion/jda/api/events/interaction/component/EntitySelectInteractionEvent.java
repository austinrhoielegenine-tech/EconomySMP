package net.dv8tion.jda.api.events.interaction.component;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;

public class EntitySelectInteractionEvent extends GenericSelectMenuInteractionEvent<IMentionable, EntitySelectMenu> implements EntitySelectInteraction {
   private final EntitySelectInteraction interaction;

   public EntitySelectInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull EntitySelectInteraction interaction) {
      super(api, responseNumber, interaction);
      this.interaction = interaction;
   }

   @Nonnull
   public EntitySelectInteraction getInteraction() {
      return this.interaction;
   }

   @Nonnull
   public Mentions getMentions() {
      return this.interaction.getMentions();
   }
}
