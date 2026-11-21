package me.xyr0.economysmpcore.combat.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xyr0.economysmpcore.combat.AstralCombat;
import me.xyr0.economysmpcore.combat.CombatManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CombatExpansion extends PlaceholderExpansion {
   private final AstralCombat plugin;

   public CombatExpansion(AstralCombat plugin) {
      this.plugin = plugin;
   }

   public @NotNull String getIdentifier() {
      return "astralcombat";
   }

   public @NotNull String getAuthor() {
      return "AstralStudio";
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
         CombatManager mgr = this.plugin.getCombatManager();
         String var10000;
         switch (params.toLowerCase()) {
            case "time_left":
            case "time":
               var10000 = String.valueOf(mgr.getTimeLeft(player));
               break;
            case "status":
               var10000 = mgr.isInCombat(player) ? "Combat" : "Safe";
               break;
            case "in_combat":
               var10000 = String.valueOf(mgr.isInCombat(player));
               break;
            default:
               var10000 = null;
         }

         return var10000;
      }
   }
}
