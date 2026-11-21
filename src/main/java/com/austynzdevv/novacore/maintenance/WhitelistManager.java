package com.austynzdevv.novacore.maintenance;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

public class WhitelistManager {
   private final economyMaintenance plugin;
   private File file;
   private YamlConfiguration config;

   public WhitelistManager(economyMaintenance plugin) {
      this.plugin = plugin;
   }

   public void load() {
      this.file = new File(this.plugin.getDataFolder(), "whitelist.yml");
      if (!this.file.exists()) {
         this.plugin.saveResource("whitelist.yml", false);
      }

      this.config = YamlConfiguration.loadConfiguration(this.file);
   }

   public void save() {
      try {
         this.config.save(this.file);
      } catch (IOException e) {
         this.plugin.getLogger().severe("Could not save whitelist.yml");
         e.printStackTrace();
      }

   }

   public void addPlayer(String name) {
      List<String> players = this.config.getStringList("players");
      if (!players.contains(name)) {
         players.add(name);
         this.config.set("players", players);
         this.save();
      }

   }

   public void removePlayer(String name) {
      List<String> players = this.config.getStringList("players");
      players.remove(name);
      this.config.set("players", players);
      this.save();
   }

   public boolean contains(String name) {
      return this.config.getStringList("players").contains(name);
   }

   public List<String> getPlayers() {
      return this.config.getStringList("players");
   }
}
