package com.austynzdevv.novacore.safety;

import com.austynzdevv.novacore.SystemHost;
import com.austynzdevv.novacore.safety.listener.JoinListener;

public class EconomySafety extends SystemHost {
   private static EconomySafety instance;

   public EconomySafety() {
      super("safety");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.getServer().getPluginManager().registerEvents(new JoinListener(this), this.getCore());
      this.getLogger().info("EconomySafety enabled!");
   }

   public void stop() {
      this.getLogger().info("EconomySafety disabled.");
   }

   public static EconomySafety getInstance() {
      return instance;
   }
}
