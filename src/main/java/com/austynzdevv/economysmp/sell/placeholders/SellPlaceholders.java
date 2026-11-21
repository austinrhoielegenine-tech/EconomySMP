package com.austynzdevv.economysmp.sell.placeholders;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.austynzdevv.economysmp.sell.EconomySell;
import com.austynzdevv.economysmp.util.EconomyUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SellPlaceholders extends PlaceholderExpansion {
   private final EconomySell plugin;
   private final Map<UUID, String> formatCache = new ConcurrentHashMap();

   public SellPlaceholders(EconomySell plugin) {
      this.plugin = plugin;
   }

   public void update(UUID id, double value) {
      this.formatCache.put(id, EconomyUtil.formatSell(value));
   }

   public @NotNull String getIdentifier() {
      return "economysell";
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
      } else if (params.equalsIgnoreCase("money_made")) {
         UUID id = player.getUniqueId();
         String cached = (String)this.formatCache.get(id);
         if (cached != null) {
            return cached;
         } else {
            double value = this.plugin.getProgress().getMoneyMade(id);
            String formatted = EconomyUtil.formatSell(value);
            this.formatCache.put(id, formatted);
            return formatted;
         }
      } else {
         return null;
      }
   }
}
