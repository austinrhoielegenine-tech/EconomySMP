package com.austynzdevv.nyrethacore.keyall.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.austynzdevv.nyrethacore.keyall.EconomyKeyall;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class KeyallPlaceholders extends PlaceholderExpansion {
   private final EconomyKeyall plugin;

   public KeyallPlaceholders(EconomyKeyall plugin) {
      this.plugin = plugin;
   }

   public @NotNull String getIdentifier() {
      return "economykeyall";
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
      if (!params.isEmpty() && !params.equalsIgnoreCase("time") && !params.equalsIgnoreCase("timer") && !params.equalsIgnoreCase("remaining")) {
         return params.equalsIgnoreCase("seconds") ? String.valueOf(this.plugin.getTimer().getRemaining(player)) : this.plugin.getTimer().format(player);
      } else {
         return this.plugin.getTimer().format(player);
      }
   }
}
