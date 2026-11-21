package com.austynzdevv.economysmp.tools.util;

import com.austynzdevv.economysmp.tools.EconomyTools;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.HotbarUtil;
import com.austynzdevv.economysmp.util.TextUtil;
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
      String raw = EconomyTools.get().getMessages().getString(path, path);
      String prefix = EconomyTools.get().getMessages().getString("prefix", EconomyTools.get().getConfig().getString("prefix", ""));
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
