package com.austynzdevv.economysmp.rtp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import com.austynzdevv.economysmp.rtp.util.LocationUtil;
import com.austynzdevv.economysmp.rtp.util.MessageUtil;
import com.austynzdevv.economysmp.util.SoundUtil;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RTPManager {
   private final EconomyRTP plugin;
   private final Map<String, RegionBounds> regions = new HashMap();

   public RTPManager(EconomyRTP plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.regions.clear();
      ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("world-settings");
      if (section == null) {
         this.plugin.getLogger().warning("[RTP] No world-settings section in config.yml");
      } else {
         for(String key : section.getKeys(false)) {
            if (section.getBoolean(key + ".enabled", true)) {
               String worldName = section.getString(key + ".world");
               if (worldName != null && !worldName.isEmpty()) {
                  RegionBounds bounds = new RegionBounds(worldName, section.getInt(key + ".min-x", -5000), section.getInt(key + ".max-x", 5000), section.getInt(key + ".min-z", -5000), section.getInt(key + ".max-z", 5000));
                  this.register(key, bounds);
                  this.register(worldName, bounds);
                  String k = key.toLowerCase(Locale.ROOT);
                  if (k.contains("overworld") || k.contains("over_world") || k.equals("ow")) {
                     this.register("overworld", bounds);
                     this.register("ow", bounds);
                  }

                  if (k.contains("nether")) {
                     this.register("nether", bounds);
                     this.register("the_nether", bounds);
                  }

                  if (k.contains("end")) {
                     this.register("end", bounds);
                     this.register("the_end", bounds);
                     this.register("theend", bounds);
                  }

                  boolean loaded = Bukkit.getWorld(worldName) != null;
                  this.plugin.getLogger().info("[RTP] " + key + " -> world '" + worldName + "' (loaded=" + loaded + ", bounds " + bounds.minX + ".." + bounds.maxX + " / " + bounds.minZ + ".." + bounds.maxZ + ")");
                  if (!loaded) {
                     this.plugin.getLogger().warning("[RTP] World '" + worldName + "' is NOT loaded. Available: " + this.worldList());
                  }
               } else {
                  this.plugin.getLogger().warning("[RTP] world-settings." + key + " has no 'world' set — skipped");
               }
            }
         }

      }
   }

   private void register(String key, RegionBounds bounds) {
      if (key != null && !key.isEmpty()) {
         this.regions.put(key.toLowerCase(Locale.ROOT).trim(), bounds);
      }
   }

   public void teleport(Player player, String worldKey) {
      this.startSearch(player, worldKey);
   }

   private void startSearch(Player player, String worldKey) {
      World world = this.resolveWorld(worldKey);
      if (world == null) {
         MessageUtil.send(player, "rtp.no-location");
         this.plugin.getLogger().warning("[RTP] Could not resolve '" + worldKey + "'. Mappings: " + String.valueOf(this.regions.keySet()) + " | Loaded worlds: " + this.worldList());
      } else {
         int[] bounds = this.clampToBorder(world, this.boundsFor(world.getName()));
         if (bounds[0] < bounds[1] && bounds[2] < bounds[3]) {
            MessageUtil.send(player, "rtp.pregenerating");
            int maxAttempts = Math.max(40, this.plugin.getConfig().getInt("max-location-attempts", 120));
            AtomicInteger left = new AtomicInteger(maxAttempts);
            this.tryCandidate(player, world, bounds, left);
         } else {
            MessageUtil.send(player, "rtp.no-location");
            this.plugin.getLogger().warning("[RTP] Bad bounds for " + world.getName());
         }
      }
   }

   private void tryCandidate(Player player, World world, int[] bounds, AtomicInteger left) {
      if (player.isOnline()) {
         if (left.decrementAndGet() < 0) {
            Tasks.run(this.plugin.getCore(), () -> {
               MessageUtil.send(player, "rtp.no-location");
               this.play(player, "teleport_fail", Sound.ENTITY_VILLAGER_NO);
            });
         } else {
            ThreadLocalRandom rng = ThreadLocalRandom.current();
            int x = rng.nextInt(bounds[0], bounds[1] + 1);
            int z = rng.nextInt(bounds[2], bounds[3] + 1);
            int cx = x >> 4;
            int cz = z >> 4;
            world.getChunkAtAsync(cx, cz, true).thenAccept((chunk) -> Tasks.run(this.plugin.getCore(), () -> {
                  if (player.isOnline()) {
                     Location loc = LocationUtil.findSurface(world, x, z);
                     if (loc == null) {
                        this.tryCandidate(player, world, bounds, left);
                     } else {
                        this.preloadThenTeleport(player, loc);
                     }
                  }
               })).exceptionally((ex) -> {
               Tasks.run(this.plugin.getCore(), () -> this.tryCandidate(player, world, bounds, left));
               return null;
            });
         }
      }
   }

   private void preloadThenTeleport(Player player, Location loc) {
      World world = loc.getWorld();
      if (world != null && player.isOnline()) {
         int radius = this.plugin.getConfig().getInt("preload-chunk-radius", 2);
         int baseCX = loc.getBlockX() >> 4;
         int baseCZ = loc.getBlockZ() >> 4;
         List<CompletableFuture<Chunk>> futures = new ArrayList();

         for(int dx = -radius; dx <= radius; ++dx) {
            for(int dz = -radius; dz <= radius; ++dz) {
               futures.add(world.getChunkAtAsync(baseCX + dx, baseCZ + dz, true));
            }
         }

         CompletableFuture.allOf((CompletableFuture[])futures.toArray(new CompletableFuture[0])).whenComplete((v, err) -> Tasks.run(this.plugin.getCore(), () -> {
               if (player.isOnline()) {
                  player.teleport(loc);
                  this.plugin.getCooldownManager().set(player.getUniqueId());
                  MessageUtil.send(player, "rtp.success");
                  this.play(player, "teleport_success", Sound.ENTITY_PLAYER_LEVELUP);
               }
            }));
      }
   }

   public Location findSafeLocation(String input) {
      World world = this.resolveWorld(input);
      if (world == null) {
         return null;
      } else {
         int[] bounds = this.clampToBorder(world, this.boundsFor(world.getName()));
         if (bounds[0] < bounds[1] && bounds[2] < bounds[3]) {
            int attempts = Math.min(60, this.plugin.getConfig().getInt("max-location-attempts", 120));
            ThreadLocalRandom rng = ThreadLocalRandom.current();

            for(int i = 0; i < attempts; ++i) {
               int x = rng.nextInt(bounds[0], bounds[1] + 1);
               int z = rng.nextInt(bounds[2], bounds[3] + 1);
               Location loc = LocationUtil.findSurface(world, x, z);
               if (loc != null) {
                  return loc;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public Location findLocation(String worldName) {
      return this.findSafeLocation(worldName);
   }

   public World resolveWorld(String name) {
      if (name != null && !name.isEmpty()) {
         World direct = Bukkit.getWorld(name);
         if (direct != null) {
            return direct;
         } else {
            String key = name.toLowerCase(Locale.ROOT).trim();
            RegionBounds bounds = (RegionBounds)this.regions.get(key);
            if (bounds != null) {
               World w = Bukkit.getWorld(bounds.worldName);
               if (w != null) {
                  return w;
               } else {
                  w = this.findLoadedIgnoreCase(bounds.worldName);
                  if (w != null) {
                     return w;
                  } else {
                     this.plugin.getLogger().warning("[RTP] Config maps '" + key + "' -> '" + bounds.worldName + "' but that world is not loaded. Loaded: " + this.worldList());
                     return null;
                  }
               }
            } else {
               String cfgPath = null;
               World.Environment env = null;
               if (!key.equals("overworld") && !key.equals("ow")) {
                  if (!key.equals("nether") && !key.equals("the_nether")) {
                     if (key.equals("end") || key.equals("the_end") || key.equals("theend") || key.equals("the end")) {
                        cfgPath = "world-settings.end-region.world";
                        env = Environment.THE_END;
                     }
                  } else {
                     cfgPath = "world-settings.nether-region.world";
                     env = Environment.NETHER;
                  }
               } else {
                  cfgPath = "world-settings.overworld-region.world";
                  env = Environment.NORMAL;
               }

               if (cfgPath != null) {
                  String cfgName = this.plugin.getConfig().getString(cfgPath);
                  if (cfgName != null && !cfgName.isEmpty()) {
                     World w = Bukkit.getWorld(cfgName);
                     if (w != null) {
                        return w;
                     }

                     w = this.findLoadedIgnoreCase(cfgName);
                     if (w != null) {
                        return w;
                     }

                     this.plugin.getLogger().warning("[RTP] config " + cfgPath + " = '" + cfgName + "' but world not loaded. Loaded: " + this.worldList());
                  } else {
                     this.plugin.getLogger().warning("[RTP] config path " + cfgPath + " is empty/missing");
                  }
               }

               if (env != null) {
                  for(World w : Bukkit.getWorlds()) {
                     if (w.getEnvironment() == env) {
                        return w;
                     }
                  }
               }

               return null;
            }
         }
      } else {
         return null;
      }
   }

   private World findLoadedIgnoreCase(String name) {
      for(World w : Bukkit.getWorlds()) {
         if (w.getName().equalsIgnoreCase(name)) {
            return w;
         }
      }

      return null;
   }

   private String worldList() {
      StringBuilder sb = new StringBuilder();

      for(World w : Bukkit.getWorlds()) {
         if (sb.length() > 0) {
            sb.append(", ");
         }

         sb.append(w.getName()).append("(").append(w.getEnvironment()).append(")");
      }

      return sb.length() == 0 ? "(none)" : sb.toString();
   }

   private int[] clampToBorder(World world, RegionBounds cfg) {
      int minX = cfg != null ? cfg.minX : -5000;
      int maxX = cfg != null ? cfg.maxX : 5000;
      int minZ = cfg != null ? cfg.minZ : -5000;
      int maxZ = cfg != null ? cfg.maxZ : 5000;
      WorldBorder border = world.getWorldBorder();
      if (border != null) {
         Location c = border.getCenter();
         double size = border.getSize() / (double)2.0F - (double)2.0F;
         minX = Math.max(minX, (int)Math.floor(c.getX() - size));
         maxX = Math.min(maxX, (int)Math.floor(c.getX() + size));
         minZ = Math.max(minZ, (int)Math.floor(c.getZ() - size));
         maxZ = Math.min(maxZ, (int)Math.floor(c.getZ() + size));
      }

      return new int[]{minX, maxX, minZ, maxZ};
   }

   private RegionBounds boundsFor(String worldName) {
      RegionBounds b = (RegionBounds)this.regions.get(worldName.toLowerCase(Locale.ROOT));
      if (b != null) {
         return b;
      } else {
         for(RegionBounds rb : this.regions.values()) {
            if (rb.worldName.equalsIgnoreCase(worldName)) {
               return rb;
            }
         }

         return null;
      }
   }

   private void play(Player player, String key, Sound fallback) {
      try {
         String raw = this.plugin.getConfig().getString("sound." + key);
         Sound sound = raw != null ? Sound.valueOf(raw) : fallback;
         SoundUtil.play(player, player.getLocation(), sound, 1.0F, 1.0F);
      } catch (Exception var6) {
         SoundUtil.play(player, player.getLocation(), fallback, 1.0F, 1.0F);
      }

   }

   private static final class RegionBounds {
      final String worldName;
      final int minX;
      final int maxX;
      final int minZ;
      final int maxZ;

      RegionBounds(String worldName, int minX, int maxX, int minZ, int maxZ) {
         this.worldName = worldName;
         this.minX = minX;
         this.maxX = maxX;
         this.minZ = minZ;
         this.maxZ = maxZ;
      }
   }
}
