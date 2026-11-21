package net.dv8tion.jda.api.components;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

public interface MessageTopLevelComponentUnion extends MessageTopLevelComponent, IComponentUnion {
   @Nonnull
   ActionRow asActionRow();

   @Nonnull
   Section asSection();

   @Nonnull
   TextDisplay asTextDisplay();

   @Nonnull
   MediaGallery asMediaGallery();

   @Nonnull
   Separator asSeparator();

   @Nonnull
   FileDisplay asFileDisplay();

   @Nonnull
   Container asContainer();

   @Nonnull
   MessageTopLevelComponentUnion withUniqueId(int var1);
}
