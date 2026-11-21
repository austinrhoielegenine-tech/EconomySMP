package me.xyr0.astralsmpcore.kits.util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import me.xyr0.astralsmpcore.AstralSMPcore;
import me.xyr0.astralsmpcore.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public final class MessageUtil {
   private static YamlConfiguration messages;

   private MessageUtil() {
   }

   public static void reload(File dataFolder) {
      File f = new File(dataFolder, "messages.yml");
      if (!f.exists()) {
         try {
            InputStream in = AstralSMPcore.class.getResourceAsStream("/kits/messages.yml");
            if (in != null) {
               Files.copy(in, f.toPath(), new CopyOption[0]);
               in.close();
            }
         } catch (Exception var3) {
         }
      }

      messages = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
   }

   public static void send(CommandSender sender, String key, String... replacements) {
      String prefix = messages != null ? messages.getString("prefix", "&#A20AD6Astral &8» ") : "&#A20AD6Astral &8» ";
      String msg = messages != null ? messages.getString(key, prefix + key) : prefix + key;
      if (msg.contains("%prefix%")) {
         msg = msg.replace("%prefix%", prefix);
      }

      msg = TextUtil.replace(msg, replacements);
      TextUtil.send(sender, msg);
   }
}
