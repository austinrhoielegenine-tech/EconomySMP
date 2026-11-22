package com.austynzdevv.nyrethacore.spawners.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.austynzdevv.nyrethacore.spawners.EconomySpawners;
import com.austynzdevv.nyrethacore.spawners.SpawnerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GenerationTask extends BukkitRunnable {
   private final EconomySpawners plugin;

   public GenerationTask(EconomySpawners plugin) {
      this.plugin = plugin;
   }

   public void run() {
      Collection<SpawnerData> all = this.plugin.getSpawnerManager().getAll();
      if (!all.isEmpty()) {
         boolean requireNearby = this.plugin.getSettings().requirePlayerNearby();
         int range = this.plugin.getSettings().getActivationRange();
         int rangeSq = range * range;
         List<SpawnerData> snapshot = new ArrayList(all);
         Iterator var6 = snapshot.iterator();

         while(true) {
            SpawnerData data;
            while(true) {
               if (!var6.hasNext()) {
                  return;
               }

               data = (SpawnerData)var6.next();
               Location loc = data.getLocation();
               if (loc != null && loc.getWorld() != null && loc.isWorldLoaded() && loc.getChunk().isLoaded()) {
                  if (!requireNearby) {
                     break;
                  }

                  boolean near = false;

                  for(Player p : loc.getWorld().getPlayers()) {
                     if (p.getWorld() == loc.getWorld() && p.getLocation().distanceSquared(loc) <= (double)rangeSq) {
                        near = true;
                        break;
                     }
                  }

                  if (near) {
                     break;
                  }
               }
            }

            this.plugin.getSpawnerManager().generate(data);
         }
      }
   }
}
