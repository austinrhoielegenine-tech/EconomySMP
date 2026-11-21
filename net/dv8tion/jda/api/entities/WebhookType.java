package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;

public enum WebhookType {
   UNKNOWN(-1),
   INCOMING(1),
   FOLLOWER(2);

   private final int key;

   private WebhookType(int key) {
      this.key = key;
   }

   public int getKey() {
      return this.key;
   }

   @Nonnull
   public static WebhookType fromKey(int key) {
      for(WebhookType type : values()) {
         if (type.key == key) {
            return type;
         }
      }

      return UNKNOWN;
   }

   // $FF: synthetic method
   private static WebhookType[] $values() {
      return new WebhookType[]{UNKNOWN, INCOMING, FOLLOWER};
   }
}
