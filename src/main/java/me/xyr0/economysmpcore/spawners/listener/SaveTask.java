package me.xyr0.economysmpcore.spawners.listener;

import me.xyr0.economysmpcore.spawners.AstralSpawners;
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
