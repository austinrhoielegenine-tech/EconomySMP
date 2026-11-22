package com.austynzdevv.nyrethacore.chat2.util;

import com.austynzdevv.nyrethacore.chat2.EconomyChat;
import com.austynzdevv.nyrethacore.chat2.placeholders.PlaceholderUtil;
import org.bukkit.entity.Player;

public class ChatFormatter {
   private final EconomyChat plugin;

   public ChatFormatter(EconomyChat plugin) {
      this.plugin = plugin;
   }

   public String format(Player player, String message) {
      String format = this.plugin.getConfig().getString("chat-format.format", "%player%: %message%");
      format = format.replace("%player%", player.getName());
      format = format.replace("%message%", message);
      return PlaceholderUtil.replace(player, format);
   }
}
