package net.dv8tion.jda.internal.entities.sticker;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.sticker.StandardSticker;
import net.dv8tion.jda.api.entities.sticker.StickerPack;
import net.dv8tion.jda.internal.utils.EntityString;

public class StickerPackImpl implements StickerPack {
   private final long id;
   private final List<StandardSticker> stickers;
   private final String name;
   private final String description;
   private final long coverId;
   private final long bannerId;
   private final long skuId;

   public StickerPackImpl(long id, List<StandardSticker> stickers, String name, String description, long coverId, long bannerId, long skuId) {
      this.id = id;
      this.stickers = Collections.unmodifiableList(stickers);
      this.name = name;
      this.description = description;
      this.coverId = coverId;
      this.bannerId = bannerId;
      this.skuId = skuId;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public List<StandardSticker> getStickers() {
      return this.stickers;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public String getDescription() {
      return this.description;
   }

   public long getCoverIdLong() {
      return this.coverId;
   }

   public long getBannerIdLong() {
      return this.bannerId;
   }

   public long getSkuIdLong() {
      return this.skuId;
   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).toString();
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof StickerPackImpl)) {
         return false;
      } else {
         StickerPackImpl other = (StickerPackImpl)obj;
         return this.id == other.id;
      }
   }
}
