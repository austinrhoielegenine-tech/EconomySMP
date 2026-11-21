package net.dv8tion.jda.api.utils;

import javax.annotation.Nonnull;

public enum Compression {
   NONE(""),
   ZLIB("zlib-stream");

   private final String key;

   private Compression(String key) {
      this.key = key;
   }

   @Nonnull
   public String getKey() {
      return this.key;
   }

   // $FF: synthetic method
   private static Compression[] $values() {
      return new Compression[]{NONE, ZLIB};
   }
}
