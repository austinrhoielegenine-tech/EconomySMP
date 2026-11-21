package com.austynzdevv.novacore.rtp.util;

import java.io.File;
import com.austynzdevv.novacore.rtp.EconomyRTP;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.HotbarUtil;
import com.austynzdevv.novacore.util.TextUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class MessageUtil {
   private static FileConfiguration messages;

   private MessageUtil() {
   }

   public static void reload(EconomyRTP plugin) {
      File file = new File(plugin.getDataFolder(), "messages.yml");
      if (!file.exists()) {
         plugin.saveResource("messages.yml", false);
      }

      messages = YamlConfiguration.loadConfiguration(file);
   }

   public static String get(String path) {
      if (messages == null && EconomyRTP.getInstance() != null) {
         reload(EconomyRTP.getInstance());
      }

      if (messages == null) {
         return path;
      } else {
         String prefix = messages.getString("prefix", "");
         String msg = messages.getString(path, "&cMissing message: " + path);
         return ColorUtil.color(msg.replace("%prefix%", prefix));
      }
   }

   public static void send(CommandSender sender, String path) {
      sender.sendMessage(get(path));
   }

   public static void send(CommandSender sender, String path, String... replacements) {
      String msg = get(path);
      msg = TextUtil.replace(msg, replacements);
      sender.sendMessage(msg);
   }

   public static void sendActionBar(Player player, String path, String... replacements) {
      String msg = get(path);
      msg = TextUtil.replace(msg, replacements);
      HotbarUtil.send(player, LegacyComponentSerializer.legacySection().deserialize(msg));
   }
}
