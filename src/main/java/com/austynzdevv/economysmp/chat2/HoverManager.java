package com.austynzdevv.economysmp.chat2;

import java.util.List;
import com.austynzdevv.economysmp.chat2.placeholders.PlaceholderUtil;
import com.austynzdevv.economysmp.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class HoverManager {
   public static Component createHoverMessage(Player player, String message) {
      Component base = TextUtil.component(message);
      return applyHover(player, base);
   }

   public static Component applyHover(Player player, Component message) {
      EconomyChat plugin = EconomyChat.getInstance();
      List<String> lines = plugin.getConfig().getStringList("hover.text");
      String hoverText;
      if (lines != null && !lines.isEmpty()) {
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < lines.size(); ++i) {
            if (i > 0) {
               sb.append("\n");
            }

            sb.append((String)lines.get(i));
         }

         hoverText = sb.toString();
      } else {
         hoverText = plugin.getConfig().getString("hover.text", "&7%player%");
      }

      hoverText = PlaceholderUtil.replace(player, hoverText);
      hoverText = hoverText.replace("%player%", player.getName());
      String command = plugin.getConfig().getString("hover.click-command", "/stats %player%");
      command = command.replace("%player%", player.getName());
      if (!command.startsWith("/")) {
         command = "/" + command;
      }

      Component hover = TextUtil.component(hoverText).decoration(TextDecoration.ITALIC, false);
      return message.hoverEvent(HoverEvent.showText(hover)).clickEvent(ClickEvent.runCommand(command));
   }
}
