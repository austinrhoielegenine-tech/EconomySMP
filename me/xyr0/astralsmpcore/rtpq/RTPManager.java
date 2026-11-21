package me.xyr0.astralsmpcore.rtpq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import me.xyr0.astralsmpcore.rtpq.util.MessageUtils;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

public class RTPManager {
   private final AstralRTPQ plugin;
   private final Random random = new Random();

   public RTPManager(AstralRTPQ plugin) {
      this.plugin = plugin;
   }

   public void teleport(Player player) {
      this.teleportTogether(player, (Player)null, (String)null);
   }

   public void teleportTogether(Player one, Player two, String worldId) {
      Location location = this.findSafeLocation(one, worldId);
      if (location == null) {
         MessageUtils.send(one, "&cCould not find a safe location. Check worlds.yml world names.");
         if (two != null) {
            MessageUtils.send(two, "&cCould not find a safe location. Check worlds.yml world names.");
         }

      } else {
         World world = location.getWorld();
         if (world != null) {
            int cx = location.getBlockX() >> 4;
            int cz = location.getBlockZ() >> 4;
            int range = 1;
            List<CompletableFuture<Chunk>> futures = new ArrayList();

            for(int x = cx - range; x <= cx + range; ++x) {
               for(int z = cz - range; z <= cz + range; ++z) {
                  futures.add(world.getChunkAtAsync(x, z, true));
               }
            }

            CompletableFuture.allOf((CompletableFuture[])futures.toArray(new CompletableFuture[0])).thenRun(() -> Tasks.run(this.plugin.getCore(), () -> {
                  if (one.isOnline()) {
                     if (two == null || two.isOnline()) {
                        one.teleportAsync(location).thenAccept((success) -> {
                           if (success) {
                              MessageUtils.send(one, this.plugin.getConfig().getString("messages.teleported", "&aGood luck!"));
                           }

                        });
                        if (two != null) {
                           Location twoLoc = location.clone().add((double)2.0F, (double)0.0F, (double)0.0F);
                           two.teleportAsync(twoLoc).thenAccept((success) -> {
                              if (success) {
                                 MessageUtils.send(two, this.plugin.getConfig().getString("messages.teleported", "&aGood luck!"));
                              }

                           });
                        }

                     }
                  }
               }));
         }
      }
   }

   private Location findSafeLocation(Player player, String worldId) {
      WorldConfig worldConfig = worldId != null ? this.plugin.getWorldManager().getWorld(worldId) : null;
      String worldName;
      int minRadius;
      int radius;
      if (worldConfig != null) {
         worldName = worldConfig.getWorldName();
         minRadius = worldConfig.getMinRadius();
         radius = worldConfig.getRadius();
      } else {
         worldName = this.plugin.getConfig().getString("rtp.world", "world");
         minRadius = this.plugin.getConfig().getInt("rtp.min-radius", 500);
         radius = this.plugin.getConfig().getInt("rtp.radius", 5000);
      }

      World world = Bukkit.getWorld(worldName);
      if (world == null) {
         this.plugin.getLogger().warning("World '" + worldName + "' is not loaded! Check worlds.yml.");
         return null;
      } else {
         for(int i = 0; i < 100; ++i) {
            int x = this.randomCoordinate(minRadius, radius);
            int z = this.randomCoordinate(minRadius, radius);
            int y = world.getHighestBlockYAt(x, z);
            if (world.getEnvironment() == Environment.NETHER) {
               y = this.findNetherY(world, x, z);
               if (y == -1) {
                  continue;
               }
            }

            Location location = new Location(world, (double)x + (double)0.5F, (double)(y + 1), (double)z + (double)0.5F);
            if (this.isSafe(location)) {
               return location;
            }
         }

         return null;
      }
   }

   private int findNetherY(World world, int x, int z) {
      for(int y = 32; y < 120; ++y) {
         Material ground = world.getBlockAt(x, y, z).getType();
         Material feet = world.getBlockAt(x, y + 1, z).getType();
         Material head = world.getBlockAt(x, y + 2, z).getType();
         if (ground.isSolid() && feet == Material.AIR && head == Material.AIR && !this.isDangerous(ground) && !this.isDangerous(feet) && !this.isDangerous(head)) {
            return y;
         }
      }

      return -1;
   }

   private boolean isSafe(Location location) {
      Material ground = location.clone().subtract((double)0.0F, (double)1.0F, (double)0.0F).getBlock().getType();
      Material feet = location.getBlock().getType();
      Material head = location.clone().add((double)0.0F, (double)1.0F, (double)0.0F).getBlock().getType();
      if (!ground.isSolid()) {
         return false;
      } else if (!this.isDangerous(ground) && !this.isDangerous(feet) && !this.isDangerous(head)) {
         return feet == Material.AIR && head == Material.AIR;
      } else {
         return false;
      }
   }

   private boolean isDangerous(Material material) {
      return material == Material.WATER || material == Material.LAVA || material == Material.KELP || material == Material.SEAGRASS || material == Material.FIRE || material == Material.SOUL_FIRE;
   }

   private int randomCoordinate(int min, int max) {
      int value = this.random.nextInt(max - min + 1) + min;
      if (this.random.nextBoolean()) {
         value = -value;
      }

      return value;
   }
}
