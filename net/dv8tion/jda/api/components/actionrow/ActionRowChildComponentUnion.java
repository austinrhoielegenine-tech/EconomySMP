package net.dv8tion.jda.api.components.actionrow;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.IComponentUnion;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;

public interface ActionRowChildComponentUnion extends ActionRowChildComponent, IComponentUnion {
   @Nonnull
   Button asButton();

   @Nonnull
   StringSelectMenu asStringSelectMenu();

   @Nonnull
   EntitySelectMenu asEntitySelectMenu();

   @Nonnull
   ActionRowChildComponentUnion withUniqueId(int var1);
}
