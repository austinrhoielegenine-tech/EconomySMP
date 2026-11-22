package com.austynzdevv.nyrethacore.crates.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.austynzdevv.nyrethacore.crates.EconomyCrates;
import com.austynzdevv.nyrethacore.crates.Crate;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class CratePlaceholders extends PlaceholderExpansion {
   private final EconomyCrates plugin;

   public CratePlaceholders(EconomyCrates plugin) {
      this.plugin = plugin;
   }

   public @NotNull String getIdentifier() {
      return "crate";
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
      if (params.startsWith("keys_")) {
         if (player == null) {
            return "0";
         } else {
            String crateId = params.substring(5);
            return String.valueOf(this.plugin.getKeys().totalKeys(player, crateId));
         }
      } else if (params.startsWith("name_")) {
         String crateId = params.substring(5);
         Crate crate = this.plugin.getCrates().get(crateId);
         return crate == null ? crateId : ColorUtil.strip(crate.getDisplayName());
      } else if (params.startsWith("virtual_")) {
         return player == null ? "0" : String.valueOf(this.plugin.getData().getVirtual(player.getUniqueId(), params.substring(8)));
      } else if (params.startsWith("physical_")) {
         return player == null ? "0" : String.valueOf(this.plugin.getKeys().countPhysical(player, params.substring(9)));
      } else {
         return null;
      }
   }
}
