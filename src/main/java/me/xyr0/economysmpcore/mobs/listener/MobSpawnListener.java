package me.xyr0.economysmpcore.mobs.listener;

import java.util.Set;
import java.util.UUID;
import me.xyr0.economysmpcore.mobs.AstralMobs;
import me.xyr0.economysmpcore.mobs.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnListener implements Listener {
   private final AstralMobs plugin;

   public MobSpawnListener(AstralMobs plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onMobSpawn(CreatureSpawnEvent event) {
      if (event.getEntityType().isAlive()) {
         PlayerDataManager data = this.plugin.getPlayerDataManager();
         if (data.hasAnyoneDisabled()) {
            Set<UUID> disabled = data.getDisabledPlayers();
            if (!disabled.isEmpty()) {
               Location loc = event.getLocation();
               World world = loc.getWorld();
               if (world != null) {
                  double radiusSq = this.plugin.getRadiusSq();
                  double x = loc.getX();
                  double y = loc.getY();
                  double z = loc.getZ();

                  for(UUID id : disabled) {
                     Player player = Bukkit.getPlayer(id);
                     if (player != null && player.isOnline() && player.getWorld() == world) {
                        Location pl = player.getLocation();
                        double dx = pl.getX() - x;
                        double dy = pl.getY() - y;
                        double dz = pl.getZ() - z;
                        if (dx * dx + dy * dy + dz * dz <= radiusSq) {
                           event.setCancelled(true);
                           return;
                        }
                     }
                  }

               }
            }
         }
      }
   }
}
