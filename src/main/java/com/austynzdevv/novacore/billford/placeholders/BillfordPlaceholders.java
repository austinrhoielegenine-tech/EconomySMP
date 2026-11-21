package com.austynzdevv.novacore.billford.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.austynzdevv.novacore.billford.EconomyBillford;
import com.austynzdevv.novacore.billford.BillfordManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BillfordPlaceholders extends PlaceholderExpansion {
   private final EconomyBillford plugin;

   public BillfordPlaceholders(EconomyBillford plugin) {
      this.plugin = plugin;
   }

   public @NotNull String getIdentifier() {
      return "economybillford";
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
      String key = params.toLowerCase().replace("-", "_");
      if (!key.equals("next_preset") && !key.equals("nextpreset") && !key.equals("next")) {
         if (!key.equals("current_preset") && !key.equals("preset")) {
            return null;
         } else {
            BillfordManager.Preset preset = this.plugin.getBillford().getCurrentPreset();
            return preset == null ? "" : preset.id();
         }
      } else {
         return this.plugin.getBillford().formatTimeUntilNextPreset();
      }
   }
}
