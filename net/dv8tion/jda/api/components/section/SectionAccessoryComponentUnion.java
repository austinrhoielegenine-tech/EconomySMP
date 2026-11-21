package net.dv8tion.jda.api.components.section;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.IComponentUnion;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;

public interface SectionAccessoryComponentUnion extends SectionAccessoryComponent, IComponentUnion {
   @Nonnull
   Button asButton();

   @Nonnull
   Thumbnail asThumbnail();

   @Nonnull
   SectionAccessoryComponentUnion withUniqueId(int var1);
}
