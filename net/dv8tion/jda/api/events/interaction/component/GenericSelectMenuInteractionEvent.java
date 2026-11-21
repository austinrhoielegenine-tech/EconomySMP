package net.dv8tion.jda.api.events.interaction.component;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;

public class GenericSelectMenuInteractionEvent<T, S extends SelectMenu> extends GenericComponentInteractionCreateEvent implements SelectMenuInteraction<T, S> {
   private final SelectMenuInteraction<T, S> menuInteraction;

   public GenericSelectMenuInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull SelectMenuInteraction<T, S> interaction) {
      super(api, responseNumber, interaction);
      this.menuInteraction = interaction;
   }

   @Nonnull
   public SelectMenuInteraction<T, S> getInteraction() {
      return this.menuInteraction;
   }

   @Nonnull
   public S getComponent() {
      return (S)this.menuInteraction.getComponent();
   }

   @Nonnull
   public List<T> getValues() {
      return this.menuInteraction.getValues();
   }
}
