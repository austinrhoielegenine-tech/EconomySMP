package net.dv8tion.jda.api.entities;

import java.time.OffsetDateTime;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.requests.RestAction;

public interface Entitlement extends ISnowflake {
   long getSkuIdLong();

   @Nonnull
   default String getSkuId() {
      return Long.toUnsignedString(this.getSkuIdLong());
   }

   long getApplicationIdLong();

   @Nonnull
   default String getApplicationId() {
      return Long.toUnsignedString(this.getApplicationIdLong());
   }

   long getUserIdLong();

   @Nonnull
   default String getUserId() {
      return Long.toUnsignedString(this.getUserIdLong());
   }

   long getGuildIdLong();

   @Nullable
   default String getGuildId() {
      return this.getGuildIdLong() == 0L ? null : Long.toUnsignedString(this.getGuildIdLong());
   }

   @Nonnull
   EntitlementType getType();

   boolean isDeleted();

   @Nullable
   OffsetDateTime getTimeStarting();

   @Nullable
   OffsetDateTime getTimeEnding();

   boolean isConsumed();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> consume();

   public static enum EntitlementType {
      PURCHASE(1),
      PREMIUM_SUBSCRIPTION(2),
      DEVELOPER_GIFT(3),
      TEST_MODE_PURCHASE(4),
      FREE_PURCHASE(5),
      USER_GIFT(6),
      PREMIUM_PURCHASE(7),
      APPLICATION_SUBSCRIPTION(8),
      UNKNOWN(-1);

      private final int key;

      private EntitlementType(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static EntitlementType fromKey(int key) {
         for(EntitlementType type : values()) {
            if (type.getKey() == key) {
               return type;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static EntitlementType[] $values() {
         return new EntitlementType[]{PURCHASE, PREMIUM_SUBSCRIPTION, DEVELOPER_GIFT, TEST_MODE_PURCHASE, FREE_PURCHASE, USER_GIFT, PREMIUM_PURCHASE, APPLICATION_SUBSCRIPTION, UNKNOWN};
      }
   }
}
