package me.xyr0.astralsmpcore.hide;

import java.io.File;
import me.xyr0.astralsmpcore.AstralSMPcore;
import me.xyr0.astralsmpcore.data.PlayerDataStore;
import org.bukkit.configuration.file.FileConfiguration;

public class DataManager {
   private final AstralHide plugin;

   public DataManager(AstralHide plugin) {
      this.plugin = plugin;
   }

   public FileConfiguration getConfig() {
      return PlayerDataStore.get().config();
   }

   public void save() {
      PlayerDataStore.get().save();
   }

   public void reload() {
      PlayerDataStore.init((AstralSMPcore)this.plugin.getCore());
   }

   public File getFile() {
      return PlayerDataStore.get().file();
   }
}
