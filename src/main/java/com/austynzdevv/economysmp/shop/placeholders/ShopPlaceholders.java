package com.austynzdevv.economysmp.shop.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.austynzdevv.economysmp.shop.EconomyShop;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ShopPlaceholders extends PlaceholderExpansion {
   private final EconomyShop plugin;

   public ShopPlaceholders(EconomyShop plugin) {
      this.plugin = plugin;
   }

   public @NotNull String getIdentifier() {
      return "economyshop";
   }

   public @NotNull String getAuthor() {
      return "xyr0";
   }

   public @NotNull String getVersion() {
      return this.plugin.getDescription().getVersion();
   }

   public boolean persist() {
      return true;
   }

   public String onPlaceholderRequest(Player player, @NotNull String params) {
      if (player == null) {
         return "";
      } else {
         String key = params.toLowerCase().replace("-", "_");
         return !key.equals("vault_money_spend") && !key.equals("vault_money_spent") && !key.equals("money_spend") && !key.equals("money_spent") ? null : this.plugin.getSpend().formatVaultSpent(player.getUniqueId());
      }
   }
}
