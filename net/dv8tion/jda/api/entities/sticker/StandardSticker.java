package net.dv8tion.jda.api.entities.sticker;

import javax.annotation.Nonnull;

public interface StandardSticker extends RichSticker {
   @Nonnull
   default Sticker.Type getType() {
      return Sticker.Type.STANDARD;
   }

   long getPackIdLong();

   @Nonnull
   default String getPackId() {
      return Long.toUnsignedString(this.getPackIdLong());
   }

   int getSortValue();
}
