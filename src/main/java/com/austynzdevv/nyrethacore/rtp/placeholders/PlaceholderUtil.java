package com.austynzdevv.nyrethacore.rtp.placeholders;

import com.austynzdevv.nyrethacore.rtp.EconomyRTP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class PlaceholderUtil {
   private PlaceholderUtil() {
   }

   public static String replace(Player player, String text) {
      return replace(player, text, (String)null);
   }

   public static String replace(Player player, String text, String worldKey) {
      if (text == null) {
         return "";
      } else {
         int playersInTarget = countPlayers(worldKey);
         text = text.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString()).replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("%players%", String.valueOf(playersInTarget)).replace("%world%", player.getWorld().getName());
         return text;
      }
   }

   private static int countPlayers(String worldKey) {
      if (worldKey != null && !worldKey.isEmpty()) {
         EconomyRTP plugin = EconomyRTP.getInstance();
         World world = null;
         if (plugin != null && plugin.getRTPManager() != null) {
            world = plugin.getRTPManager().resolveWorld(worldKey);
         }

         if (world == null) {
            world = Bukkit.getWorld(worldKey);
         }

         return world == null ? 0 : world.getPlayers().size();
      } else {
         return Bukkit.getOnlinePlayers().size();
      }
   }
}
