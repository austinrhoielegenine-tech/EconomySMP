package me.xyr0.economysmpcore.rtp.util;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import me.xyr0.economysmpcore.rtp.AstralRTP;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public final class LocationUtil {
   private static final EnumSet<Material> UNSAFE;
   private static Set<Material> extraBlacklist;
   private static Set<String> blockedBiomes;

   private LocationUtil() {
   }

   public static void reload(AstralRTP plugin) {
      extraBlacklist = EnumSet.noneOf(Material.class);

      for(String name : plugin.getConfig().getStringList("blacklisted-blocks")) {
         Material mat = Material.matchMaterial(name);
         if (mat != null) {
            extraBlacklist.add(mat);
         }
      }

      blockedBiomes = new HashSet();

      for(String b : plugin.getConfig().getStringList("blacklisted-biomes")) {
         blockedBiomes.add(b.toUpperCase(Locale.ROOT).replace("MINECRAFT:", ""));
      }

   }

   public static Location findSurface(World world, int x, int z) {
      return world.getEnvironment() == Environment.NETHER ? findNether(world, x, z) : findOverworldOrEnd(world, x, z);
   }

   private static Location findOverworldOrEnd(World world, int x, int z) {
      int y;
      try {
         y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
      } catch (Exception var10) {
         y = world.getHighestBlockYAt(x, z);
      }

      if (y <= world.getMinHeight()) {
         return null;
      } else {
         for(int dy = 0; dy <= 4; ++dy) {
            int groundY = y - dy;
            if (groundY <= world.getMinHeight()) {
               break;
            }

            Block ground = world.getBlockAt(x, groundY, z);
            Material g = ground.getType();
            if (isGoodGround(g)) {
               Block feet = world.getBlockAt(x, groundY + 1, z);
               Block head = world.getBlockAt(x, groundY + 2, z);
               if (isEmptySpace(feet.getType()) && isEmptySpace(head.getType()) && !isUnsafe(feet.getType()) && !isUnsafe(head.getType()) && !isBiomeBlocked(ground.getBiome())) {
                  return new Location(world, (double)x + (double)0.5F, (double)groundY + (double)1.0F, (double)z + (double)0.5F);
               }
            }
         }

         return null;
      }
   }

   private static Location findNether(World world, int x, int z) {
      int top = Math.min(120, world.getMaxHeight() - 3);
      int bottom = Math.max(world.getMinHeight() + 4, 32);

      for(int y = top; y >= bottom; --y) {
         Block ground = world.getBlockAt(x, y, z);
         Material g = ground.getType();
         if (isGoodGround(g) && g != Material.BEDROCK) {
            Block feet = world.getBlockAt(x, y + 1, z);
            Block head = world.getBlockAt(x, y + 2, z);
            if (isEmptySpace(feet.getType()) && isEmptySpace(head.getType()) && !isUnsafe(feet.getType()) && !isUnsafe(head.getType())) {
               return new Location(world, (double)x + (double)0.5F, (double)y + (double)1.0F, (double)z + (double)0.5F);
            }
         }
      }

      return null;
   }

   public static boolean isSafe(Location loc) {
      if (loc != null && loc.getWorld() != null) {
         Block feet = loc.getBlock();
         Block head = loc.clone().add((double)0.0F, (double)1.0F, (double)0.0F).getBlock();
         Block ground = loc.clone().subtract((double)0.0F, (double)1.0F, (double)0.0F).getBlock();
         if (!isGoodGround(ground.getType())) {
            return false;
         } else if (isEmptySpace(feet.getType()) && isEmptySpace(head.getType())) {
            if (!isUnsafe(feet.getType()) && !isUnsafe(head.getType())) {
               return !isBiomeBlocked(ground.getBiome());
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean isGoodGround(Material mat) {
      if (mat != null && !mat.isAir()) {
         if (!mat.isSolid()) {
            return false;
         } else if (isUnsafe(mat)) {
            return false;
         } else {
            String n = mat.name();
            if (n.endsWith("_LEAVES")) {
               return false;
            } else if (!n.endsWith("_FENCE") && !n.endsWith("_WALL") && !n.endsWith("_GATE")) {
               if (!n.contains("SIGN") && !n.contains("BANNER") && !n.contains("BUTTON")) {
                  return !n.contains("PRESSURE_PLATE") && !n.contains("CARPET") && !n.equals("SNOW");
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private static boolean isEmptySpace(Material mat) {
      if (mat != null && !mat.isAir()) {
         return !mat.isSolid();
      } else {
         return true;
      }
   }

   private static boolean isUnsafe(Material mat) {
      if (mat == null) {
         return true;
      } else {
         return UNSAFE.contains(mat) || extraBlacklist.contains(mat);
      }
   }

   private static boolean isBiomeBlocked(Biome biome) {
      if (blockedBiomes.isEmpty()) {
         return false;
      } else {
         String key = biome.getKey().getKey().toUpperCase(Locale.ROOT);

         for(String blocked : blockedBiomes) {
            if (key.equals(blocked) || key.contains(blocked)) {
               return true;
            }
         }

         return false;
      }
   }

   static {
      UNSAFE = EnumSet.of(Material.LAVA, Material.WATER, Material.MAGMA_BLOCK, Material.FIRE, Material.SOUL_FIRE, Material.CACTUS, Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.POWDER_SNOW);
      extraBlacklist = EnumSet.noneOf(Material.class);
      blockedBiomes = new HashSet();
   }
}
