package com.austynzdevv.economysmp.hide;

import java.io.File;
import com.austynzdevv.economysmp.EconomySMPcore;
import com.austynzdevv.economysmp.data.PlayerDataStore;
import org.bukkit.configuration.file.FileConfiguration;

public class DataManager {
   private final EconomyHide plugin;

   public DataManager(EconomyHide plugin) {
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
