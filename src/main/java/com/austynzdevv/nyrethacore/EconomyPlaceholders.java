package com.austynzdevv.nyrethacore;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/** PlaceholderAPI expansion. Only loaded when PlaceholderAPI is installed. */
public class EconomyPlaceholders extends PlaceholderExpansion {
   private final NyrethaCore plugin;
   private final EconomyPlaceholderResolver resolver;

   public EconomyPlaceholders(NyrethaCore plugin) {
      this.plugin = plugin;
      this.resolver = new EconomyPlaceholderResolver(plugin);
   }

   public String getIdentifier() {
      return "nyrethacore";
   }

   public String getAuthor() {
      return "Xyr0";
   }

   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }

   public boolean persist() {
      return true;
   }

   public String onPlaceholderRequest(Player player, String params) {
      return this.resolver.onPlaceholderRequest(player, params);
   }
}
