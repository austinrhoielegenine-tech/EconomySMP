package me.xyr0.astralsmpcore.combat.listener;

import java.util.List;
import java.util.Locale;
import me.xyr0.astralsmpcore.combat.AstralCombat;
import me.xyr0.astralsmpcore.combat.CombatManager;
import me.xyr0.astralsmpcore.combat.util.MessageUtil;
import me.xyr0.astralsmpcore.combat.util.RegionUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class CombatListener implements Listener {
   private final AstralCombat plugin;
   private final CombatManager manager;

   public CombatListener(AstralCombat plugin) {
      this.plugin = plugin;
      this.manager = plugin.getCombatManager();
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onDamage(EntityDamageByEntityEvent event) {
      Entity var3 = event.getEntity();
      if (var3 instanceof Player victim) {
         Player var8 = null;
         Entity var7 = event.getDamager();
         if (var7 instanceof Player p) {
            var8 = p;
         } else {
            var7 = event.getDamager();
            if (var7 instanceof Projectile proj) {
               ProjectileSource var10 = proj.getShooter();
               if (var10 instanceof Player p) {
                  var8 = p;
               }
            }
         }

         if (var8 != null && !var8.equals(victim)) {
            if (!RegionUtil.isInSafeZone(victim) && !RegionUtil.isInSafeZone(var8)) {
               this.manager.tagBoth(var8, victim);
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onCommand(PlayerCommandPreprocessEvent event) {
      Player player = event.getPlayer();
      if (this.manager.isInCombat(player)) {
         if (!player.hasPermission("astralsmpcore.system.combat.bypass")) {
            String msg = event.getMessage().toLowerCase(Locale.ROOT);
            if (msg.startsWith("/")) {
               msg = msg.substring(1);
            }

            String base = msg.split(" ")[0];

            for(String cmd : this.plugin.getConfig().getStringList("blocked-commands")) {
               if (base.equalsIgnoreCase(cmd) || msg.startsWith(cmd.toLowerCase(Locale.ROOT) + " ")) {
                  event.setCancelled(true);
                  MessageUtil.send(player, "command-blocked");
                  this.manager.playSound(player, "command-blocked");
                  return;
               }
            }

         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (this.manager.isInCombat(player)) {
         if (!player.hasPermission("astralsmpcore.system.combat.bypass")) {
            ItemStack item = event.getItem();
            if (item != null) {
               List<String> blocked = this.plugin.getConfig().getStringList("blocked-items");
               String type = item.getType().name();

               for(String b : blocked) {
                  if (type.equalsIgnoreCase(b)) {
                     event.setCancelled(true);
                     MessageUtil.send(player, "item-blocked");
                     this.manager.playSound(player, "item-blocked");
                     return;
                  }
               }

            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onMove(PlayerMoveEvent event) {
      if (this.plugin.getConfig().getBoolean("anti-safe-zone.enabled", true)) {
         if (this.plugin.getConfig().getBoolean("anti-safe-zone.block-entry", true)) {
            Player player = event.getPlayer();
            if (this.manager.isInCombat(player)) {
               if (!player.hasPermission("astralsmpcore.system.combat.bypass")) {
                  if (event.getTo() != null) {
                     if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                        if (RegionUtil.isInSafeZone(event.getTo()) && !RegionUtil.isInSafeZone(event.getFrom())) {
                           event.setCancelled(true);
                           String msg = this.plugin.getConfig().getString("anti-safe-zone.message", "&cYou cannot enter a safe zone while in combat!");
                           MessageUtil.sendRaw(player, msg);
                           if (this.plugin.getConfig().getBoolean("anti-safe-zone.show-barrier", true)) {
                              this.showBarrier(player, event.getTo());
                           }
                        }

                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onTeleport(PlayerTeleportEvent event) {
      if (this.plugin.getConfig().getBoolean("anti-safe-zone.enabled", true)) {
         if (this.plugin.getConfig().getBoolean("anti-safe-zone.block-pearl", true)) {
            Player player = event.getPlayer();
            if (this.manager.isInCombat(player)) {
               if (!player.hasPermission("astralsmpcore.system.combat.bypass")) {
                  if (event.getCause() == TeleportCause.ENDER_PEARL || event.getCause() == TeleportCause.CHORUS_FRUIT) {
                     if (event.getTo() != null && RegionUtil.isInSafeZone(event.getTo())) {
                        event.setCancelled(true);
                        String msg = this.plugin.getConfig().getString("anti-safe-zone.message", "&cYou cannot enter a safe zone while in combat!");
                        MessageUtil.sendRaw(player, msg);
                        this.manager.playSound(player, "item-blocked");
                     }

                  }
               }
            }
         }
      }
   }

   private void showBarrier(Player player, Location loc) {
      try {
         String particleName = this.plugin.getConfig().getString("anti-safe-zone.barrier-particle", "REDSTONE");
         Particle particle = Particle.valueOf(particleName);
         int radius = this.plugin.getConfig().getInt("anti-safe-zone.barrier-radius", 3);

         for(int x = -radius; x <= radius; ++x) {
            for(int z = -radius; z <= radius; ++z) {
               if (x * x + z * z <= radius * radius) {
                  Location pLoc = loc.clone().add((double)x, (double)1.0F, (double)z);
                  player.spawnParticle(particle, pLoc, 3, 0.1, 0.3, 0.1, (double)0.0F);
               }
            }
         }
      } catch (Exception var9) {
      }

   }
}
