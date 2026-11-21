package me.xyr0.economysmpcore.chat2.util;

import me.xyr0.economysmpcore.chat2.AstralChat;
import me.xyr0.economysmpcore.chat2.placeholders.PlaceholderUtil;
import org.bukkit.entity.Player;

public class ChatFormatter {
   private final AstralChat plugin;

   public ChatFormatter(AstralChat plugin) {
      this.plugin = plugin;
   }

   public String format(Player player, String message) {
      String format = this.plugin.getConfig().getString("chat-format.format", "%player%: %message%");
      format = format.replace("%player%", player.getName());
      format = format.replace("%message%", message);
      return PlaceholderUtil.replace(player, format);
   }
}
