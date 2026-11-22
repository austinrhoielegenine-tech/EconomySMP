package com.austynzdevv.nyrethacore.order;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BlockManager {
   private final EconomyOrder plugin;
   private final Set<UUID> blocked = new HashSet();
   private File file;
   private FileConfiguration data;

   public BlockManager(EconomyOrder plugin) {
      this.plugin = plugin;
      this.load();
   }

   public void load() {
      this.blocked.clear();
      this.file = new File(this.plugin.getDataFolder(), "blocked.yml");
      if (!this.file.exists()) {
         try {
            this.file.getParentFile().mkdirs();
            this.file.createNewFile();
         } catch (Exception var6) {
         }
      }

      this.data = YamlConfiguration.loadConfiguration(this.file);

      for(String s : this.data.getStringList("blocked")) {
         try {
            this.blocked.add(UUID.fromString(s));
         } catch (Exception var5) {
         }
      }

   }

   public void save() {
      if (this.data != null && this.file != null) {
         List<String> list = this.blocked.stream().map(UUID::toString).toList();
         this.data.set("blocked", list);

         try {
            this.data.save(this.file);
         } catch (Exception var3) {
            this.plugin.getLogger().warning("Could not save blocked.yml");
         }

      }
   }

   public boolean isBlocked(UUID id) {
      return this.blocked.contains(id);
   }

   public void block(UUID id) {
      this.blocked.add(id);
      this.save();
   }

   public void unblock(UUID id) {
      this.blocked.remove(id);
      this.save();
   }
}
