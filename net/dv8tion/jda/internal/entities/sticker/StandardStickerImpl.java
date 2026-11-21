package net.dv8tion.jda.internal.entities.sticker;

import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.sticker.StandardSticker;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.internal.utils.EntityString;

public class StandardStickerImpl extends RichStickerImpl implements StandardSticker {
   private final long packId;
   private final int sortValue;

   public StandardStickerImpl(long id, Sticker.StickerFormat format, String name, Set<String> tags, String description, long packId, int sortValue) {
      super(id, format, name, tags, description);
      this.packId = packId;
      this.sortValue = sortValue;
   }

   @Nonnull
   public StandardSticker asStandardSticker() {
      return this;
   }

   public long getPackIdLong() {
      return this.packId;
   }

   public int getSortValue() {
      return this.sortValue;
   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).addMetadata("pack", this.getPackId()).toString();
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof StandardStickerImpl)) {
         return false;
      } else {
         StandardStickerImpl other = (StandardStickerImpl)obj;
         return this.id == other.id;
      }
   }
}
