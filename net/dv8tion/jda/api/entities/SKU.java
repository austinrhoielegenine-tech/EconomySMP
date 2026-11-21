package net.dv8tion.jda.api.entities;

import java.util.Set;
import javax.annotation.Nonnull;

public interface SKU extends SkuSnowflake {
   @Nonnull
   SKUType getType();

   @Nonnull
   String getName();

   @Nonnull
   String getSlug();

   @Nonnull
   Set<SKUFlag> getFlags();

   default boolean isGuildSKU() {
      return this.getFlags().contains(SKUFlag.GUILD_SUBSCRIPTION);
   }

   default boolean isUserSKU() {
      return this.getFlags().contains(SKUFlag.USER_SUBSCRIPTION);
   }

   default boolean isAvailable() {
      return this.getFlags().contains(SKUFlag.AVAILABLE);
   }
}
