package com.austynzdevv.nyrethacore.maintenance;

import java.io.File;
import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.maintenance.listener.JoinListener;
import com.austynzdevv.nyrethacore.maintenance.listener.MotdListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class economyMaintenance extends SystemHost {
   private static economyMaintenance instance;
   private WhitelistManager whitelistManager;
   private FileConfiguration messages;
   private FileConfiguration motd;
   private boolean maintenanceEnabled;

   public economyMaintenance() {
      super("maintenance");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.saveResourceSafe("messages.yml");
      this.saveResourceSafe("motd.yml");
      this.reloadExtra();
      this.maintenanceEnabled = this.getConfig().getBoolean("maintenance.enabled", false);
      this.whitelistManager = new WhitelistManager(this);
      this.whitelistManager.load();
      this.getServer().getPluginManager().registerEvents(new JoinListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new MotdListener(this), this.getCore());
      this.getLogger().info("Maintenance status: " + this.maintenanceEnabled);
      this.getLogger().info("economyMaintenance enabled!");
   }

   public void stop() {
      if (this.whitelistManager != null) {
         this.whitelistManager.save();
      }

      this.getLogger().info("economyMaintenance disabled!");
   }

   public void reloadAll() {
      this.reloadConfig();
      this.reloadExtra();
      if (this.whitelistManager != null) {
         this.whitelistManager.load();
      }

   }

   private void reloadExtra() {
      this.messages = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "messages.yml"));
      this.motd = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "motd.yml"));
   }

   private void saveResourceSafe(String name) {
      File out = new File(this.getDataFolder(), name);
      if (!out.exists()) {
         this.saveResource(name, false);
      }

   }

   public static economyMaintenance getInstance() {
      return instance;
   }

   public boolean isMaintenanceEnabled() {
      return this.maintenanceEnabled;
   }

   public void setMaintenanceEnabled(boolean value) {
      this.maintenanceEnabled = value;
      this.getConfig().set("maintenance.enabled", value);
      this.saveConfig();
   }

   public WhitelistManager getWhitelistManager() {
      return this.whitelistManager;
   }

   public FileConfiguration getMessages() {
      return this.messages;
   }

   public FileConfiguration getMotd() {
      return this.motd;
   }
}
