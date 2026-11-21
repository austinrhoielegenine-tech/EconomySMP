package net.dv8tion.jda.api.components;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

public interface ModalTopLevelComponentUnion extends ModalTopLevelComponent, IComponentUnion {
   @Nonnull
   Label asLabel();

   @Nonnull
   TextDisplay asTextDisplay();

   @Nonnull
   ModalTopLevelComponentUnion withUniqueId(int var1);
}
