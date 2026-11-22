package com.austynzdevv.nyrethacore.mobs;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.data.PlayerDataStore;
import com.austynzdevv.nyrethacore.util.Tasks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerDataManager {
   private final EconomyMobs plugin;
   private File file;
   private YamlConfiguration data;
   private final Set<UUID> disabled = ConcurrentHashMap.newKeySet();
   private boolean dirty;
   private int saveTask = -1;

   public PlayerDataManager(EconomyMobs plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.file = PlayerDataStore.get().file();
      this.data = PlayerDataStore.get().config();
      this.disabled.clear();
      ConfigurationSection sec = this.data.getConfigurationSection("players");
      if (sec != null) {
         for(String key : sec.getKeys(false)) {
            try {
               UUID id = UUID.fromString(key);
               boolean enabled = sec.getBoolean(key + ".mobs-enabled", true);
               if (!enabled) {
                  this.disabled.add(id);
               }
            } catch (IllegalArgumentException var6) {
            }
         }
      }

   }

   public boolean isMobSpawningEnabled(UUID uuid) {
      return !this.disabled.contains(uuid);
   }

   public void setMobSpawning(UUID uuid, boolean enabled) {
      if (enabled) {
         this.disabled.remove(uuid);
      } else {
         this.disabled.add(uuid);
      }

      this.data.set("players." + String.valueOf(uuid) + ".mobs-enabled", enabled);
      this.scheduleSave();
   }

   public Set<UUID> getDisabledPlayers() {
      return Collections.unmodifiableSet(this.disabled);
   }

   public boolean hasAnyoneDisabled() {
      return !this.disabled.isEmpty();
   }

   private void scheduleSave() {
      this.dirty = true;
      if (this.saveTask == -1) {
         this.saveTask = Tasks.runAsyncLater(this.plugin, () -> {
            this.saveTask = -1;
            if (this.dirty) {
               this.dirty = false;
               this.saveSync();
            }

         }, 20L);
      }
   }

   public void saveSync() {
      if (this.data != null && this.file != null) {
         PlayerDataStore.get().save();
      }

   }
}
