package me.xyr0.economysmpcore.combat.util;

import me.xyr0.economysmpcore.combat.AstralCombat;
import me.xyr0.economysmpcore.util.ColorUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageUtil {
   private static AstralCombat plugin;

   private MessageUtil() {
   }

   public static void init(AstralCombat pl) {
      plugin = pl;
   }

   public static String color(String input) {
      return ColorUtil.color(input);
   }

   public static String get(String path) {
      String prefix = plugin.getConfig().getString("messages.prefix", "");
      String msg = plugin.getConfig().getString("messages." + path, path);
      return color(prefix + msg);
   }

   public static void send(CommandSender sender, String path) {
      sender.sendMessage(get(path));
   }

   public static void sendRaw(CommandSender sender, String message) {
      sender.sendMessage(color(message));
   }

   public static void sendAction(Player player, String path) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(get(path)));
   }
}
