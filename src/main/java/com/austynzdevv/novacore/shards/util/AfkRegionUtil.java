package com.austynzdevv.novacore.shards.util;

import java.lang.reflect.Method;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class AfkRegionUtil {
   private AfkRegionUtil() {
   }

   public static boolean isEligible(Player player, FileConfiguration cfg) {
      if (player.hasPermission("novacorecore.system.shards.everywhere")) {
         return true;
      } else {
         return !isWorldAllowed(player.getWorld(), cfg.getStringList("enabled-worlds")) ? false : isInAfkRegion(player, cfg.getStringList("enabled-regions"));
      }
   }

   public static boolean isInAfkZone(Player player, FileConfiguration cfg) {
      return !isWorldAllowed(player.getWorld(), cfg.getStringList("enabled-worlds")) ? false : isInAfkRegion(player, cfg.getStringList("enabled-regions"));
   }

   public static boolean isWorldAllowed(World world, List<String> worlds) {
      if (worlds != null && !worlds.isEmpty()) {
         String name = world.getName();

         for(String w : worlds) {
            if (w != null && w.equalsIgnoreCase(name)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public static boolean isInAfkRegion(Player player, List<String> regions) {
      if (regions != null && !regions.isEmpty()) {
         return Bukkit.getPluginManager().getPlugin("WorldGuard") == null ? false : regionMatches(player.getLocation(), regions);
      } else {
         return false;
      }
   }

   private static boolean regionMatches(Location loc, List<String> names) {
      try {
         Class<?> wgClass = Class.forName("com.sk89q.worldguard.WorldGuard");
         Method getInstance = wgClass.getMethod("getInstance");
         Object wg = getInstance.invoke((Object)null);
         Method getPlatform = wgClass.getMethod("getPlatform");
         Object platform = getPlatform.invoke(wg);
         Method getRegionContainer = platform.getClass().getMethod("getRegionContainer");
         Object container = getRegionContainer.invoke(platform);
         Method createQuery = container.getClass().getMethod("createQuery");
         Object query = createQuery.invoke(container);
         Class<?> adapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
         Method adapt = adapter.getMethod("adapt", Location.class);
         Object weLoc = adapt.invoke((Object)null, loc);
         Class<?> weLocClass = Class.forName("com.sk89q.worldedit.util.Location");
         Method getApplicableRegions = query.getClass().getMethod("getApplicableRegions", weLocClass);
         Object set = getApplicableRegions.invoke(query, weLoc);
         if (!(set instanceof Iterable)) {
            return false;
         } else {
            for(Object region : (Iterable)set) {
               Method getId = region.getClass().getMethod("getId");
               Object idObj = getId.invoke(region);
               if (idObj != null) {
                  String id = idObj.toString();
                  if (names.contains(id) || names.contains(id.toLowerCase())) {
                     return true;
                  }
               }
            }

            return false;
         }
      } catch (ClassNotFoundException var22) {
         return false;
      } catch (NoSuchMethodException var23) {
         return false;
      } catch (ReflectiveOperationException var24) {
         return false;
      } catch (RuntimeException var25) {
         return false;
      }
   }
}
