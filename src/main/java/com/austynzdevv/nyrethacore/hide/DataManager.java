package com.austynzdevv.nyrethacore.hide;

import java.io.File;
import com.austynzdevv.nyrethacore.NyrethaCore;
import com.austynzdevv.nyrethacore.data.PlayerDataStore;
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
      PlayerDataStore.init((NyrethaCore)this.plugin.getCore());
   }

   public File getFile() {
      return PlayerDataStore.get().file();
   }
}
