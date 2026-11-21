package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;

public enum SKUType {
   DURABLE(2),
   CONSUMABLE(3),
   SUBSCRIPTION(5),
   SUBSCRIPTION_GROUP(6),
   UNKNOWN(-1);

   private final int id;

   private SKUType(int id) {
      this.id = id;
   }

   @Nonnull
   public static SKUType fromId(int type) {
      for(SKUType value : values()) {
         if (value.id == type) {
            return value;
         }
      }

      return UNKNOWN;
   }

   public int getId() {
      return this.id;
   }

   // $FF: synthetic method
   private static SKUType[] $values() {
      return new SKUType[]{DURABLE, CONSUMABLE, SUBSCRIPTION, SUBSCRIPTION_GROUP, UNKNOWN};
   }
}
