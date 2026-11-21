package me.xyr0.economysmpcore.hide;

import java.io.File;
import me.xyr0.economysmpcore.EconomySMPcore;
import me.xyr0.economysmpcore.data.PlayerDataStore;
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
      PlayerDataStore.init((EconomySMPcore)this.plugin.getCore());
   }

   public File getFile() {
      return PlayerDataStore.get().file();
   }
}
