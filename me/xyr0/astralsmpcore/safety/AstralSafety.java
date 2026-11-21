package me.xyr0.astralsmpcore.safety;

import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.safety.listener.JoinListener;

public class AstralSafety extends SystemHost {
   private static AstralSafety instance;

   public AstralSafety() {
      super("safety");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.getServer().getPluginManager().registerEvents(new JoinListener(this), this.getCore());
      this.getLogger().info("AstralSafety enabled!");
   }

   public void stop() {
      this.getLogger().info("AstralSafety disabled.");
   }

   public static AstralSafety getInstance() {
      return instance;
   }
}
