package com.austynzdevv.novacore.reports;

import com.austynzdevv.novacore.SystemHost;
import com.austynzdevv.novacore.reports.listener.GUIListener;

public final class EconomyReports extends SystemHost {
   private static EconomyReports instance;
   private DatabaseManager databaseManager;
   private CooldownManager cooldownManager;

   public EconomyReports() {
      super("reports");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.databaseManager = new DatabaseManager(this);
      this.databaseManager.initialize();
      this.cooldownManager = new CooldownManager(this);
      this.getServer().getPluginManager().registerEvents(new GUIListener(this), this.getCore());
      this.getLogger().info("EconomyReports Enabled");
   }

   public void stop() {
      if (this.databaseManager != null) {
         this.databaseManager.close();
      }

   }

   public static EconomyReports getInstance() {
      return instance;
   }

   public DatabaseManager getDatabaseManager() {
      return this.databaseManager;
   }

   public CooldownManager getCooldownManager() {
      return this.cooldownManager;
   }
}
