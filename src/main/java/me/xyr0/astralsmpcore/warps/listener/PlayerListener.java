package me.xyr0.astralsmpcore.warps.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import me.xyr0.astralsmpcore.warps.AstralWarps;
import me.xyr0.astralsmpcore.warps.NamedLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class PlayerListener implements Listener {
   private final AstralWarps plugin;
   private final Set<UUID> respawning = new HashSet();

   public PlayerListener(AstralWarps plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onMove(PlayerMoveEvent event) {
      if (event.getTo() != null) {
         if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            if (this.plugin.getTeleports().isTeleporting(event.getPlayer().getUniqueId())) {
               this.plugin.getTeleports().onMove(event.getPlayer());
            }

         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.plugin.getTeleports().cancel(event.getPlayer().getUniqueId());
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onRespawn(PlayerRespawnEvent event) {
      if (this.respawning.remove(event.getPlayer().getUniqueId())) {
         if (this.plugin.getConfig().getBoolean("teleport-spawn-on-death", true)) {
            if (!event.isBedSpawn() && !event.isAnchorSpawn()) {
               Location bed = null;

               try {
                  bed = event.getPlayer().getRespawnLocation();
               } catch (Exception var5) {
               }

               if (bed != null && bed.getWorld() != null) {
                  event.setRespawnLocation(bed);
               } else {
                  NamedLocation spawn = this.plugin.getWarps().randomSpawn();
                  if (spawn == null) {
                     spawn = this.plugin.getWarps().defaultSpawn();
                  }

                  if (spawn != null) {
                     Location loc = spawn.toLocation();
                     if (loc != null) {
                        event.setRespawnLocation(loc);
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent event) {
      this.respawning.add(event.getEntity().getUniqueId());
   }
}
