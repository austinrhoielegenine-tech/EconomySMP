package com.austynzdevv.novacore.wl;

import com.austynzdevv.novacore.SystemHost;
import com.austynzdevv.novacore.wl.listener.CommandFilterListener;

public class EconomyWhitelister extends SystemHost {
   private static EconomyWhitelister instance;
   private WhitelistSettings settings;

   public EconomyWhitelister() {
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

      this.getLogger().info("EconomyWhitelister enabled.");
   }

   public void reloadAll() {
      this.reloadConfig();
      if (this.settings != null) {
         this.settings.load();
      }

   }

   public static EconomyWhitelister getInstance() {
      return instance;
   }

   public WhitelistSettings getSettings() {
      return this.settings;
   }
}
