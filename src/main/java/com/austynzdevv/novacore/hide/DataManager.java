package com.austynzdevv.novacore.hide;

import java.io.File;
import com.austynzdevv.novacore.NovaCore;
import com.austynzdevv.novacore.data.PlayerDataStore;
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
      PlayerDataStore.init((NovaCore)this.plugin.getCore());
   }

   public File getFile() {
      return PlayerDataStore.get().file();
   }
}
