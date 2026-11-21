package com.austynzdevv.economysmp.spawners.listener;

import com.austynzdevv.economysmp.spawners.EconomySpawners;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTask extends BukkitRunnable {
   private final EconomySpawners plugin;

   public SaveTask(EconomySpawners plugin) {
      this.plugin = plugin;
   }

   public void run() {
      this.plugin.getStorage().save();
   }
}
