package me.xyr0.astralsmpcore.tools.util;

import me.xyr0.astralsmpcore.tools.AstralTools;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.HotbarUtil;
import me.xyr0.astralsmpcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageUtil {
   private MessageUtil() {
   }

   public static void send(CommandSender sender, String path) {
      send(sender, path, (String[])null);
   }

   public static void send(CommandSender sender, String path, String... replacements) {
      String raw = AstralTools.get().getMessages().getString(path, path);
      String prefix = AstralTools.get().getMessages().getString("prefix", AstralTools.get().getConfig().getString("prefix", ""));
      if (replacements != null) {
         raw = TextUtil.replace(raw, replacements);
      }

      Component msg = ColorUtil.parse(prefix + raw);
      sender.sendMessage(msg);
      if (sender instanceof Player player) {
         HotbarUtil.send(player, ColorUtil.parse(raw));
      }

   }
}
