package me.xyr0.astralsmpcore.mobs;

import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.mobs.listener.MobSpawnListener;

public class AstralMobs extends SystemHost {
   private static AstralMobs instance;
   private PlayerDataManager playerDataManager;
   private double radiusSq;

   public AstralMobs() {
      super("mobs");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.playerDataManager = new PlayerDataManager(this);
      this.refreshRadius();
      this.getServer().getPluginManager().registerEvents(new MobSpawnListener(this), this.getCore());
      this.getLogger().info("AstralMobs enabled.");
   }

   public void stop() {
      if (this.playerDataManager != null) {
         this.playerDataManager.saveSync();
      }

      this.getLogger().info("AstralMobs disabled.");
   }

   public void reloadPlugin() {
      this.reloadConfig();
      this.refreshRadius();
      if (this.playerDataManager != null) {
         this.playerDataManager.reload();
      }

   }

   public void refreshRadius() {
      double r = this.getConfig().getDouble("settings.radius", (double)64.0F);
      if (r < (double)1.0F) {
         r = (double)1.0F;
      }

      this.radiusSq = r * r;
   }

   public double getRadius() {
      return Math.sqrt(this.radiusSq);
   }

   public double getRadiusSq() {
      return this.radiusSq;
   }

   public PlayerDataManager getPlayerDataManager() {
      return this.playerDataManager;
   }

   public static AstralMobs getInstance() {
      return instance;
   }
}
