package me.xyr0.economysmpcore.media;

import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.media.listener.InventoryListener;
import org.bukkit.Bukkit;

public class AstralMedia extends SystemHost {
   private static AstralMedia instance;

   public AstralMedia() {
      super("media");
   }

   public static AstralMedia getInstance() {
      return instance;
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
      this.getLogger().info("AstralMedia has been enabled!");
   }

   public void stop() {
      this.getLogger().info("AstralMedia has been disabled!");
   }
}
