package net.dv8tion.jda.api.entities.sticker;

import java.util.Set;
import javax.annotation.Nonnull;

public interface RichSticker extends Sticker {
   @Nonnull
   Sticker.Type getType();

   @Nonnull
   Set<String> getTags();

   @Nonnull
   String getDescription();
}
