package me.xyr0.economysmpcore.wl;

import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.wl.listener.CommandFilterListener;

public class AstralWhitelister extends SystemHost {
   private static AstralWhitelister instance;
   private WhitelistSettings settings;

   public AstralWhitelister() {
      super("wl");
   }

   public void stop() {
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();

      try {
         this.settings = new WhitelistSettings(this);
         this.settings.load();
      } catch (Exception t) {
         this.getLogger().warning("Whitelist settings: " + t.getMessage());
      }

      try {
         this.getServer().getPluginManager().registerEvents(new CommandFilterListener(this), this.getCore());
      } catch (Exception t) {
         this.getLogger().warning("Whitelist listener: " + t.getMessage());
      }

      this.getLogger().info("AstralWhitelister enabled.");
   }

   public void reloadAll() {
      this.reloadConfig();
      if (this.settings != null) {
         this.settings.load();
      }

   }

   public static AstralWhitelister getInstance() {
      return instance;
   }

   public WhitelistSettings getSettings() {
      return this.settings;
   }
}
