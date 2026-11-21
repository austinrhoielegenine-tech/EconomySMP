package me.xyr0.astralsmpcore.billford.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xyr0.astralsmpcore.billford.AstralBillford;
import me.xyr0.astralsmpcore.billford.BillfordManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BillfordPlaceholders extends PlaceholderExpansion {
   private final AstralBillford plugin;

   public BillfordPlaceholders(AstralBillford plugin) {
      this.plugin = plugin;
   }

   public @NotNull String getIdentifier() {
      return "astralbillford";
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
