package me.xyr0.economysmpcore.crates;

import java.io.File;
import me.xyr0.economysmpcore.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class MessageManager {
   private final AstralCrates plugin;
   private FileConfiguration messages;

   public MessageManager(AstralCrates plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "messages.yml");
      if (!file.exists()) {
         this.plugin.saveResource("messages.yml", false);
      }

      this.messages = YamlConfiguration.loadConfiguration(file);
   }

   public String raw(String key, String def) {
      return this.messages.getString(key, def);
   }

   public void send(CommandSender sender, String key, String def, String... reps) {
      String text = this.raw(key, def);
      if (reps != null) {
         for(int i = 0; i + 1 < reps.length; i += 2) {
            if (reps[i] != null && reps[i + 1] != null) {
               text = text.replace(reps[i], reps[i + 1]);
            }
         }
      }

      sender.sendMessage(ColorUtil.parse(text));
   }

   public void sendHelp(Player player) {
      for(String line : this.messages.getStringList("help")) {
         player.sendMessage(ColorUtil.parse(line));
      }

   }
}
