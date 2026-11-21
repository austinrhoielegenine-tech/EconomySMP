package me.xyr0.economysmpcore.reports;

import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.reports.listener.GUIListener;

public final class AstralReports extends SystemHost {
   private static AstralReports instance;
   private DatabaseManager databaseManager;
   private CooldownManager cooldownManager;

   public AstralReports() {
      super("reports");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.databaseManager = new DatabaseManager(this);
      this.databaseManager.initialize();
      this.cooldownManager = new CooldownManager(this);
      this.getServer().getPluginManager().registerEvents(new GUIListener(this), this.getCore());
      this.getLogger().info("AstralReports Enabled");
   }

   public void stop() {
      if (this.databaseManager != null) {
         this.databaseManager.close();
      }

   }

   public static AstralReports getInstance() {
      return instance;
   }

   public DatabaseManager getDatabaseManager() {
      return this.databaseManager;
   }

   public CooldownManager getCooldownManager() {
      return this.cooldownManager;
   }
}
