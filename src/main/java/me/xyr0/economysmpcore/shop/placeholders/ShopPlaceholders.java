package me.xyr0.economysmpcore.shop.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xyr0.economysmpcore.shop.AstralShop;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ShopPlaceholders extends PlaceholderExpansion {
   private final AstralShop plugin;

   public ShopPlaceholders(AstralShop plugin) {
      this.plugin = plugin;
   }

   public @NotNull String getIdentifier() {
      return "astralshop";
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
