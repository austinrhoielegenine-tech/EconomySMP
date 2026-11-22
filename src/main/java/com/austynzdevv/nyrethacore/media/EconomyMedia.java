package com.austynzdevv.nyrethacore.media;

import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.media.listener.InventoryListener;
import org.bukkit.Bukkit;

public class EconomyMedia extends SystemHost {
   private static EconomyMedia instance;

   public EconomyMedia() {
      super("media");
   }

   public static EconomyMedia getInstance() {
      return instance;
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
      this.getLogger().info("EconomyMedia has been enabled!");
   }

   public void stop() {
      this.getLogger().info("EconomyMedia has been disabled!");
   }
}
