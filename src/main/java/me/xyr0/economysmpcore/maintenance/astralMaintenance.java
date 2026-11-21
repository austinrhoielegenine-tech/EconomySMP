package me.xyr0.economysmpcore.maintenance;

import java.io.File;
import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.maintenance.listener.JoinListener;
import me.xyr0.economysmpcore.maintenance.listener.MotdListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class astralMaintenance extends SystemHost {
   private static astralMaintenance instance;
   private WhitelistManager whitelistManager;
   private FileConfiguration messages;
   private FileConfiguration motd;
   private boolean maintenanceEnabled;

   public astralMaintenance() {
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
      this.getLogger().info("astralMaintenance enabled!");
   }

   public void stop() {
      if (this.whitelistManager != null) {
         this.whitelistManager.save();
      }

      this.getLogger().info("astralMaintenance disabled!");
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

   public static astralMaintenance getInstance() {
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
