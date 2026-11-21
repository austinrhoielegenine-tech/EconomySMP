package com.austynzdevv.economysmp.shards.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.austynzdevv.economysmp.shards.EconomyShards;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ShardsPlaceholders extends PlaceholderExpansion {
   private final EconomyShards plugin;

   public ShardsPlaceholders(EconomyShards plugin) {
      this.plugin = plugin;
   }

   public @NotNull String getIdentifier() {
      return "economyshards";
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
         long bal = this.plugin.getShards().getBalance(player);
         if (!key.isEmpty() && !key.equals("balance") && !key.equals("bal") && !key.equals("value")) {
            if (!key.equals("balance_formatted") && !key.equals("formatted") && !key.equals("short")) {
               if (!key.equals("booster") && !key.equals("booster_active")) {
                  if (!key.equals("booster_remaining") && !key.equals("booster_time")) {
                     if (key.equals("booster_speed")) {
                        double speed = this.plugin.getBoosters().getSpeedMultiplier(player.getUniqueId());
                        return speed == (double)((long)speed) ? String.valueOf((long)speed) : String.valueOf(speed);
                     } else {
                        return null;
                     }
                  } else {
                     long ms = this.plugin.getBoosters().getRemainingMillis(player.getUniqueId());
                     if (ms <= 0L) {
                        return "0h 0m";
                     } else {
                        long hours = ms / 3600000L;
                        long mins = ms / 60000L % 60L;
                        return hours + "h " + mins + "m";
                     }
                  }
               } else {
                  return this.plugin.getBoosters().isActive(player.getUniqueId()) ? "true" : "false";
               }
            } else {
               return this.formatShort(bal);
            }
         } else {
            return String.valueOf(bal);
         }
      }
   }

   private String formatShort(long value) {
      if (value < 1000L) {
         return String.valueOf(value);
      } else if (value < 1000000L) {
         return String.format("%.1fk", (double)value / (double)1000.0F).replace(".0k", "k");
      } else {
         return value < 1000000000L ? String.format("%.1fm", (double)value / (double)1000000.0F).replace(".0m", "m") : String.format("%.1fb", (double)value / (double)1.0E9F).replace(".0b", "b");
      }
   }
}
