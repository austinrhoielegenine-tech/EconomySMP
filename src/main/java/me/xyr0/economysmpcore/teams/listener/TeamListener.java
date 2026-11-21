package me.xyr0.economysmpcore.teams.listener;

import me.xyr0.economysmpcore.teams.AstralTeams;
import me.xyr0.economysmpcore.teams.Team;
import me.xyr0.economysmpcore.util.ColorUtil;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.projectiles.ProjectileSource;

public final class TeamListener implements Listener {
   private final AstralTeams plugin;

   public TeamListener(AstralTeams plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      Team team = this.plugin.getTeams().getByPlayer(event.getPlayer().getUniqueId());
      if (team != null) {
         team.updateName(event.getPlayer().getUniqueId(), event.getPlayer().getName());
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onDamage(EntityDamageByEntityEvent event) {
      Entity var3 = event.getEntity();
      if (var3 instanceof Player victim) {
         Player attacker = this.resolveAttacker(event);
         if (attacker != null) {
            if (!attacker.getUniqueId().equals(victim.getUniqueId())) {
               Entity damager = event.getDamager();
               if (!(damager instanceof EnderPearl)) {
                  if (damager instanceof Projectile) {
                     Projectile proj = (Projectile)damager;
                     if (proj instanceof EnderPearl) {
                        return;
                     }
                  }

                  if (damager instanceof Projectile) {
                     Projectile proj = (Projectile)damager;
                     if (proj.getShooter() instanceof Player && damager.getType().name().contains("ENDER_PEARL")) {
                        return;
                     }
                  }

                  Team team = this.plugin.getTeams().getByPlayer(victim.getUniqueId());
                  if (team != null) {
                     if (team.isMember(attacker.getUniqueId())) {
                        if (!team.isPvpEnabled()) {
                           event.setCancelled(true);
                           attacker.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("team.pvp-disabled-damage", "&cYou cannot damage team members!")));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private Player resolveAttacker(EntityDamageByEntityEvent event) {
      Entity var3 = event.getDamager();
      if (var3 instanceof Player p) {
         return p;
      } else if (event.getDamager() instanceof EnderPearl) {
         return null;
      } else {
         var3 = event.getDamager();
         if (var3 instanceof Projectile) {
            Projectile proj = (Projectile)var3;
            if (proj instanceof EnderPearl) {
               return null;
            }

            ProjectileSource var4 = proj.getShooter();
            if (var4 instanceof Player) {
               Player p = (Player)var4;
               return p;
            }
         }

         Entity var10 = event.getDamager();
         if (var10 instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed)var10;
            var10 = tnt.getSource();
            if (var10 instanceof Player) {
               Player p = (Player)var10;
               return p;
            }
         }

         return null;
      }
   }
}
