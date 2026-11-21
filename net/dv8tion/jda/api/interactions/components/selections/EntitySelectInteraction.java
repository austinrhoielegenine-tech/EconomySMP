package net.dv8tion.jda.api.interactions.components.selections;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;

public interface EntitySelectInteraction extends SelectMenuInteraction<IMentionable, EntitySelectMenu> {
   @Nonnull
   Mentions getMentions();
}
