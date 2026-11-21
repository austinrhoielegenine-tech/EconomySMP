package me.xyr0.astralsmpcore.rules;

import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.rules.listener.GUIListener;

public class AstralRules extends SystemHost {
   private static AstralRules instance;
   private ConfigManager configManager;

   public AstralRules() {
      super("rules");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.configManager = new ConfigManager(this);
      this.getServer().getPluginManager().registerEvents(new GUIListener(this), this);
      this.getLogger().info("AstralRules enabled successfully.");
   }

   public void stop() {
      this.getLogger().info("AstralRules disabled.");
   }

   public static AstralRules getInstance() {
      return instance;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }
}
