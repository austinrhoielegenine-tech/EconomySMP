package net.dv8tion.jda.api.entities.sticker;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.utils.Helpers;

public interface Sticker extends StickerSnowflake {
   String ICON_URL = "https://cdn.discordapp.com/stickers/%s.%s";

   @Nonnull
   static StickerSnowflake fromId(long id) {
      return StickerSnowflake.fromId(id);
   }

   @Nonnull
   static StickerSnowflake fromId(@Nonnull String id) {
      return fromId(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   StickerFormat getFormatType();

   @Nonnull
   String getName();

   @Nonnull
   default String getIconUrl() {
      return Helpers.format("https://cdn.discordapp.com/stickers/%s.%s", this.getId(), this.getFormatType().getExtension());
   }

   @Nonnull
   default ImageProxy getIcon() {
      return new ImageProxy(this.getIconUrl());
   }

   public static enum StickerFormat {
      PNG(1, "png"),
      APNG(2, "png"),
      LOTTIE(3, "json"),
      GIF(4, "gif"),
      UNKNOWN(-1, (String)null);

      private final int id;
      private final String extension;

      private StickerFormat(int id, String extension) {
         this.id = id;
         this.extension = extension;
      }

      @Nonnull
      public String getExtension() {
         if (this == UNKNOWN) {
            throw new IllegalStateException("Cannot get file extension for StickerFormat.UNKNOWN");
         } else {
            return this.extension;
         }
      }

      @Nonnull
      public static StickerFormat fromId(int id) {
         for(StickerFormat stickerFormat : values()) {
            if (stickerFormat.id == id) {
               return stickerFormat;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static StickerFormat[] $values() {
         return new StickerFormat[]{PNG, APNG, LOTTIE, GIF, UNKNOWN};
      }
   }

   public static enum Type {
      STANDARD(1),
      GUILD(2),
      UNKNOWN(-1);

      private final int id;

      private Type(int id) {
         this.id = id;
      }

      @Nonnull
      public static Type fromId(int id) {
         for(Type type : values()) {
            if (type.id == id) {
               return type;
            }
         }

         return UNKNOWN;
      }

      public int getId() {
         return this.id;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{STANDARD, GUILD, UNKNOWN};
      }
   }
}
