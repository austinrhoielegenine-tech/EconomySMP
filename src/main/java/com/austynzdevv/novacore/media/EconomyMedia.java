package com.austynzdevv.novacore.media;

import com.austynzdevv.novacore.SystemHost;
import com.austynzdevv.novacore.media.listener.InventoryListener;
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
