package me.xyr0.astralsmpcore.spawners.listener;

import me.xyr0.astralsmpcore.spawners.AstralSpawners;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTask extends BukkitRunnable {
   private final AstralSpawners plugin;

   public SaveTask(AstralSpawners plugin) {
      this.plugin = plugin;
   }

   public void run() {
      this.plugin.getStorage().save();
   }
}
