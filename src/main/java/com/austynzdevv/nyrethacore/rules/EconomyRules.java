package com.austynzdevv.nyrethacore.rules;

import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.rules.listener.GUIListener;

public class EconomyRules extends SystemHost {
   private static EconomyRules instance;
   private ConfigManager configManager;

   public EconomyRules() {
      super("rules");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.configManager = new ConfigManager(this);
      this.getServer().getPluginManager().registerEvents(new GUIListener(this), this);
      this.getLogger().info("EconomyRules enabled successfully.");
   }

   public void stop() {
      this.getLogger().info("EconomyRules disabled.");
   }

   public static EconomyRules getInstance() {
      return instance;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }
}
