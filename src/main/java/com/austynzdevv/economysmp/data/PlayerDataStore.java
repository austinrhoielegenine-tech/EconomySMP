package com.austynzdevv.economysmp.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import com.austynzdevv.economysmp.EconomySMPcore;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public final class PlayerDataStore {
   private static PlayerDataStore instance;
   private final EconomySMPcore plugin;
   private final File file;
   private final YamlConfiguration data;
   private final Object lock = new Object();
   private final AtomicBoolean savePending = new AtomicBoolean();
   private final AtomicBoolean saveDirty = new AtomicBoolean();

   private PlayerDataStore(EconomySMPcore plugin) {
      this.plugin = plugin;
      File dir = new File(plugin.getDataFolder(), "Data");
      dir.mkdirs();
      this.file = new File(dir, "playerdata.yml");
      this.data = this.file.exists() ? YamlConfiguration.loadConfiguration(this.file) : new YamlConfiguration();
      this.migrateFile("settings-data.yml", "legacy.settings-data");
      this.migrateFile("orders.yml", "");
      this.migrateFile("data.yml", "");
      this.migrateFile("listings.yml", "");
      this.migrateFile("transactions.yml", "");
      this.migrateFile("history.yml", "");
      this.migrateFile("boosters.yml", "");
      this.migrateFile("timer-data.yml", "");
      this.migrateFile("links/links.yml", "");
      this.migrateFile("welcomer/data.yml", "");
      this.migrateFile("voicechat/players.yml", "");
      this.migrateFile("players.yml", "");
      this.migrateFile("shards/data.yml", "");
      this.migrateFile("hidden.yml", "hide");
      this.migrateFile("Data/hide/hidden.yml", "hide");
      this.migrateFile("Data/chainmail/players.yml", "chainmail");
      this.migrateFile("Data/mobs/players.yml", "mobs");
      this.migrateRootKey("current-preset", "billford.current-preset");
      this.migrateRootKey("next-rotate-at", "billford.next-rotate-at");
      this.writeLocked();
   }

   private void migrateFile(String oldPath, String target) {
      File old = new File(this.plugin.getDataFolder(), oldPath);
      if (old.isFile()) {
         try {
            YamlConfiguration legacy = YamlConfiguration.loadConfiguration(old);
            if (!this.data.contains(target)) {
               for(String key : legacy.getKeys(false)) {
                  this.data.set(target.isEmpty() ? key : target + "." + key, legacy.get(key));
               }
            }

            old.delete();
            File parent = old.getParentFile();
            if (parent != null && parent.isDirectory() && parent.list() != null && parent.list().length == 0) {
               parent.delete();
            }
         } catch (Exception var7) {
         }

      }
   }

   private void migrateRootKey(String oldPath, String newPath) {
      if (this.data.contains(oldPath)) {
         if (!this.data.contains(newPath)) {
            this.data.set(newPath, this.data.get(oldPath));
         }

         this.data.set(oldPath, (Object)null);
      }
   }

   public static synchronized PlayerDataStore init(EconomySMPcore plugin) {
      if (instance == null || instance.plugin != plugin) {
         instance = new PlayerDataStore(plugin);
      }

      return instance;
   }

   public static PlayerDataStore get() {
      if (instance == null) {
         throw new IllegalStateException("PlayerDataStore is not initialized");
      } else {
         return instance;
      }
   }

   public YamlConfiguration config() {
      return this.data;
   }

   public ConfigurationSection section(String path) {
      synchronized(this.lock) {
         ConfigurationSection section = this.data.getConfigurationSection(path);
         return section != null ? section : this.data.createSection(path);
      }
   }

   public File file() {
      return this.file;
   }

   public Object lock() {
      return this.lock;
   }

   public void set(String path, Object value) {
      synchronized(this.lock) {
         this.data.set(path, value);
      }
   }

   public void merge(String path, Map<String, Object> values) {
      synchronized(this.lock) {
         for(Map.Entry<String, Object> e : values.entrySet()) {
            this.data.set(path + "." + (String)e.getKey(), e.getValue());
         }

      }
   }

   public void edit(Consumer<YamlConfiguration> action) {
      synchronized(this.lock) {
         action.accept(this.data);
      }
   }

   public <T> T read(Function<YamlConfiguration, T> action) {
      synchronized(this.lock) {
         return (T)action.apply(this.data);
      }
   }

   public void editAndSave(Consumer<YamlConfiguration> action) {
      synchronized(this.lock) {
         action.accept(this.data);
      }

      this.save();
   }

   public void save() {
      this.saveDirty.set(true);
      if (this.savePending.compareAndSet(false, true)) {
         Tasks.runAsyncLater(this.plugin, () -> {
            while(true) {
               try {
                  this.saveDirty.set(false);
                  synchronized(this.lock) {
                     this.writeLocked();
                  }

                  if (this.saveDirty.get()) {
                     continue;
                  }
               } catch (Exception e) {
                  this.saveDirty.set(false);
                  this.plugin.getLogger().warning("Could not save Data/playerdata.yml: " + e.getMessage());
               } finally {
                  this.savePending.set(false);
                  if (this.saveDirty.get()) {
                     this.save();
                  }

               }

               return;
            }
         }, 20L);
      }
   }

   public void flush() {
      this.saveDirty.set(false);
      synchronized(this.lock) {
         this.writeLocked();
      }

      this.savePending.set(false);
   }

   private void writeLocked() {
      int attempt = 0;

      while(attempt < 3) {
         try {
            String yaml = this.data.saveToString();
            File parent = this.file.getParentFile();
            if (parent != null && !parent.exists()) {
               parent.mkdirs();
            }

            Files.writeString(this.file.toPath(), yaml, StandardCharsets.UTF_8);
            return;
         } catch (ConcurrentModificationException var4) {
            if (attempt == 2) {
               this.plugin.getLogger().warning("Could not save Data/playerdata.yml: concurrent modification");
            }

            ++attempt;
         } catch (IOException e) {
            this.plugin.getLogger().warning("Could not save Data/playerdata.yml: " + e.getMessage());
            return;
         } catch (Exception e) {
            Logger var10000 = this.plugin.getLogger();
            String var10001 = e.getClass().getSimpleName();
            var10000.warning("Could not save Data/playerdata.yml: " + var10001 + ": " + e.getMessage());
            return;
         }
      }

   }

   public void remove(String path) {
      synchronized(this.lock) {
         this.data.set(path, (Object)null);
      }
   }
}
