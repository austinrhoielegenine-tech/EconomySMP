package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum EmbedType {
   IMAGE("image"),
   VIDEO("video"),
   GIFV("gifv"),
   ARTICLE("article"),
   LINK("link"),
   RICH("rich"),
   AUTO_MODERATION("auto_moderation_message"),
   POLL_RESULT("poll_result"),
   UNKNOWN("");

   private final String key;

   private EmbedType(String key) {
      this.key = key;
   }

   @Nonnull
   public static EmbedType fromKey(@Nullable String key) {
      for(EmbedType type : values()) {
         if (type.key.equals(key)) {
            return type;
         }
      }

      return UNKNOWN;
   }

   // $FF: synthetic method
   private static EmbedType[] $values() {
      return new EmbedType[]{IMAGE, VIDEO, GIFV, ARTICLE, LINK, RICH, AUTO_MODERATION, POLL_RESULT, UNKNOWN};
   }
}
