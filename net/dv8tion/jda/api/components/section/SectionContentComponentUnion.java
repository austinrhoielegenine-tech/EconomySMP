package net.dv8tion.jda.api.components.section;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.IComponentUnion;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

public interface SectionContentComponentUnion extends SectionContentComponent, IComponentUnion {
   @Nonnull
   TextDisplay asTextDisplay();

   @Nonnull
   SectionContentComponentUnion withUniqueId(int var1);
}
