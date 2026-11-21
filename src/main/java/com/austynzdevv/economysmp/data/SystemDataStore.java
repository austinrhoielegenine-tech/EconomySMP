package com.austynzdevv.economysmp.data;

import java.io.File;
import java.io.IOException;
import com.austynzdevv.economysmp.EconomySMPcore;
import org.bukkit.configuration.file.YamlConfiguration;

public final class SystemDataStore {
   private static SystemDataStore instance;
   private final File folder;

   private SystemDataStore(EconomySMPcore plugin) {
      this.folder = new File(plugin.getDataFolder(), "Data");
      this.folder.mkdirs();
   }

   public static synchronized SystemDataStore init(EconomySMPcore plugin) {
      if (instance == null) {
         instance = new SystemDataStore(plugin);
      }

      return instance;
   }

   public static SystemDataStore get() {
      if (instance == null) {
         throw new IllegalStateException("SystemDataStore is not initialized");
      } else {
         return instance;
      }
   }

   public File file(String name) {
      return new File(this.folder, name.endsWith(".yml") ? name : name + ".yml");
   }

   public YamlConfiguration load(String name) {
      return YamlConfiguration.loadConfiguration(this.file(name));
   }

   public void save(String name, YamlConfiguration config) {
      try {
         config.save(this.file(name));
      } catch (IOException var4) {
      }

   }
}
