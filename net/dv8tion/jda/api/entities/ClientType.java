package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;

public enum ClientType {
   DESKTOP("desktop"),
   MOBILE("mobile"),
   WEB("web"),
   UNKNOWN("unknown");

   private final String key;

   private ClientType(String key) {
      this.key = key;
   }

   @Nonnull
   public String getKey() {
      return this.key;
   }

   @Nonnull
   public static ClientType fromKey(@Nonnull String key) {
      for(ClientType type : values()) {
         if (type.key.equals(key)) {
            return type;
         }
      }

      return UNKNOWN;
   }

   // $FF: synthetic method
   private static ClientType[] $values() {
      return new ClientType[]{DESKTOP, MOBILE, WEB, UNKNOWN};
   }
}
