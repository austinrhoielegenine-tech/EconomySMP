package com.austynzdevv.novacore.rtpq.util;

import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.HotbarUtil;
import com.austynzdevv.novacore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageUtils {
   public static void send(Player player, String message) {
      if (player != null && message != null) {
         player.sendMessage(TextUtil.component(message));
      }
   }

   public static void title(Player player, String title, String subtitle) {
      if (player != null) {
         player.sendTitle(ColorUtil.color(title), ColorUtil.color(subtitle), 10, 40, 10);
      }
   }

   public static void actionBar(Player player, String message) {
      if (player != null && message != null) {
         HotbarUtil.send(player, TextUtil.component(message));
      }
   }

   public static void broadcast(String message) {
      if (message != null) {
         for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(TextUtil.component(message));
         }

      }
   }
}
