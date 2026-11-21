package me.xyr0.astralsmpcore.chat2.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlaceholderUtil {
   public static String replace(Player player, String text) {
      if (text == null) {
         return "";
      } else {
         text = text.replace("%player%", player.getName());
         text = text.replace("%kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS)));
         text = text.replace("%deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS)));
         long ticks = (long)player.getStatistic(Statistic.PLAY_ONE_MINUTE);
         long hours = ticks / 20L / 3600L;
         long minutes = ticks / 20L % 3600L / 60L;
         text = text.replace("%playtime%", hours + "h " + minutes + "m");
         text = text.replace("%money%", "0");
         if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
         }

         return text;
      }
   }
}
