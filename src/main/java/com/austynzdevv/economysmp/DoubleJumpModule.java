package com.austynzdevv.economysmp;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.austynzdevv.economysmp.util.SoundUtil;
import com.austynzdevv.economysmp.util.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DoubleJumpModule implements Module, Listener {
   private final EconomySMP plugin;
   private YamlConfiguration cfg;
   private final Set<UUID> used = new HashSet();
   private final Set<UUID> flyEnabled = new HashSet();

   public DoubleJumpModule(EconomySMP plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "doublejump";
   }

   public void onEnable() {
      File f = new File(this.plugin.getDataFolder(), "doublejump.yml");
      if (!f.exists()) {
         try {
            this.plugin.saveResource("doublejump.yml", false);
         } catch (Exception var4) {
         }
      }

      this.cfg = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.plugin.commands().registerHandler("fly", this::flyCmd);

      try {
         PluginCommand pc = this.plugin.getCommand("fly");
         if (pc != null) {
            pc.setExecutor((sender, command, label, args) -> this.flyCmd(sender, args));
         }
      } catch (Exception var3) {
      }

      this.plugin.getLogger().info("DoubleJump + /fly enabled");
   }

   public void onDisable() {
      this.used.clear();
      this.flyEnabled.clear();
   }

   public void reload() {
      File f = new File(this.plugin.getDataFolder(), "doublejump.yml");
      if (f.exists()) {
         this.cfg = YamlConfiguration.loadConfiguration(f);
      }

   }

   private boolean worldEnabled(Player p) {
      List<String> worlds = this.cfg.getStringList("worlds");
      if (worlds == null || worlds.isEmpty()) {
         worlds = this.cfg.getStringList("world");
      }

      if (worlds != null && !worlds.isEmpty()) {
         String wn = p.getWorld().getName();

         for(String w : worlds) {
            if (w != null && w.equalsIgnoreCase(wn)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private boolean regionAllowed(Player p) {
      List<String> regions = this.cfg.getStringList("WorldGuard-Regions");
      if (regions != null && !regions.isEmpty()) {
         try {
            Class<?> wg = Class.forName("com.sk89q.worldguard.WorldGuard");
            Object inst = wg.getMethod("getInstance").invoke((Object)null);
            Object plat = inst.getClass().getMethod("getPlatform").invoke(inst);
            Object rm = plat.getClass().getMethod("getRegionContainer").invoke(plat);
            Object q = rm.getClass().getMethod("createQuery").invoke(rm);
            return true;
         } catch (Exception var8) {
            return true;
         }
      } else {
         return true;
      }
   }

   private boolean flyCmd(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (!p.hasPermission("economysmp.system.fly.use") && !p.isOp()) {
            TextUtil.send(p, "&cNo permission for /fly.");
            return true;
         } else if (this.worldEnabled(p) && this.regionAllowed(p)) {
            if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
               if (this.flyEnabled.contains(p.getUniqueId())) {
                  this.flyEnabled.remove(p.getUniqueId());
                  p.setAllowFlight(false);
                  p.setFlying(false);
                  TextUtil.send(p, "&cFlight &cdisabled&c.");
               } else {
                  this.flyEnabled.add(p.getUniqueId());
                  p.setAllowFlight(true);
                  p.setFlying(true);
                  TextUtil.send(p, "&aFlight &aenabled&a.");
               }

               return true;
            } else {
               TextUtil.send(p, "&cYou already can fly in this gamemode.");
               return true;
            }
         } else {
            TextUtil.send(p, "&cFlight is only allowed in configured worlds.");
            return true;
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }

   @EventHandler
   public void onToggleFlight(PlayerToggleFlightEvent e) {
      Player p = e.getPlayer();
      if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
         if (!p.isGliding()) {
            if (this.flyEnabled.contains(p.getUniqueId())) {
               if (!this.worldEnabled(p)) {
                  e.setCancelled(true);
                  p.setAllowFlight(false);
                  p.setFlying(false);
                  this.flyEnabled.remove(p.getUniqueId());
                  TextUtil.send(p, "&cFlight is not allowed in this world.");
               }

            } else if (this.worldEnabled(p)) {
               if (this.cfg.getBoolean("enabled", true)) {
                  if (this.used.contains(p.getUniqueId())) {
                     e.setCancelled(true);
                     p.setAllowFlight(false);
                     p.setFlying(false);
                  } else {
                     e.setCancelled(true);
                     p.setAllowFlight(false);
                     p.setFlying(false);
                     this.used.add(p.getUniqueId());
                     double up = this.cfg.getDouble("vertical-boost", this.cfg.getDouble("boost.up", 0.42));
                     double forward = this.cfg.getDouble("jump-power", this.cfg.getDouble("boost.forward", (double)1.5F));
                     Vector dir = p.getLocation().getDirection().normalize();
                     Vector vel = dir.multiply(forward).setY(up);
                     p.setVelocity(vel);

                     try {
                        p.getWorld().spawnParticle(Particle.ITEM_SLIME, p.getLocation(), 15, 0.3, 0.1, 0.3, 0.02);
                     } catch (Exception var14) {
                        try {
                           p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 12, 0.3, 0.1, 0.3, 0.02);
                        } catch (Exception var13) {
                        }
                     }

                     try {
                        SoundUtil.play(p, p.getLocation(), Sound.BLOCK_SLIME_BLOCK_PLACE, 1.0F, 1.2F);
                     } catch (Exception var12) {
                        try {
                           SoundUtil.play(p, p.getLocation(), Sound.ENTITY_SLIME_JUMP, 1.0F, 1.2F);
                        } catch (Exception var11) {
                        }
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onMove(PlayerMoveEvent e) {
      Player p = e.getPlayer();
      if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
         if (this.flyEnabled.contains(p.getUniqueId())) {
            if (!this.worldEnabled(p)) {
               this.flyEnabled.remove(p.getUniqueId());
               p.setAllowFlight(false);
               p.setFlying(false);
            } else if (!p.getAllowFlight()) {
               p.setAllowFlight(true);
            }

         } else if (this.worldEnabled(p)) {
            if (this.cfg.getBoolean("enabled", true)) {
               if (p.isGliding()) {
                  if (p.getAllowFlight()) {
                     p.setAllowFlight(false);
                  }

                  if (p.isFlying()) {
                     p.setFlying(false);
                  }

               } else {
                  if (p.isOnGround()) {
                     this.used.remove(p.getUniqueId());
                     p.setAllowFlight(true);
                  }

               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public void onFireworkWhileGliding(PlayerInteractEvent event) {
      if (event.getHand() != null) {
         Action action = event.getAction();
         if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player p = event.getPlayer();
            if (p.isGliding()) {
               ItemStack item = event.getItem();
               if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
                  event.setCancelled(false);
                  event.setUseItemInHand(Result.ALLOW);
               }
            }
         }
      }
   }
}
