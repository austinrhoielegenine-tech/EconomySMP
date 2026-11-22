package com.austynzdevv.nyrethacore.mobs;

import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.mobs.listener.MobSpawnListener;

public class EconomyMobs extends SystemHost {
   private static EconomyMobs instance;
   private PlayerDataManager playerDataManager;
   private double radiusSq;

   public EconomyMobs() {
      super("mobs");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.playerDataManager = new PlayerDataManager(this);
      this.refreshRadius();
      this.getServer().getPluginManager().registerEvents(new MobSpawnListener(this), this.getCore());
      this.getLogger().info("EconomyMobs enabled.");
   }

   public void stop() {
      if (this.playerDataManager != null) {
         this.playerDataManager.saveSync();
      }

      this.getLogger().info("EconomyMobs disabled.");
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

   public static EconomyMobs getInstance() {
      return instance;
   }
}
