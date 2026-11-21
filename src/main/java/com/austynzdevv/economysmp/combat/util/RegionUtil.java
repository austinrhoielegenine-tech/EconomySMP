package com.austynzdevv.economysmp.combat.util;

import java.util.List;
import com.austynzdevv.economysmp.combat.EconomyCombat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class RegionUtil {
   private static final boolean WORLD_GUARD = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;

   private RegionUtil() {
   }

   public static boolean isInSafeZone(Player player) {
      return isInSafeZone(player.getLocation());
   }

   public static boolean isInSafeZone(Location loc) {
      List<String> zones = EconomyCombat.getInstance().getConfig().getStringList("regions.safe-zones");
      return !zones.isEmpty() && WORLD_GUARD ? regionMatches(loc, zones) : false;
   }

   public static boolean isCombatAllowed(Location loc) {
      List<String> only = EconomyCombat.getInstance().getConfig().getStringList("regions.combat-only");
      return !only.isEmpty() && WORLD_GUARD ? regionMatches(loc, only) : true;
   }

   private static boolean regionMatches(Location loc, List<String> names) {
      try {
         Class<?> wgClass = Class.forName("com.sk89q.worldguard.WorldGuard");
         Object wg = wgClass.getMethod("getInstance").invoke((Object)null);
         Object platform = wgClass.getMethod("getPlatform").invoke(wg);
         Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);
         Object query = container.getClass().getMethod("createQuery").invoke(container);
         Class<?> adapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
         Object weLoc = adapter.getMethod("adapt", Location.class).invoke((Object)null, loc);
         Object set = query.getClass().getMethod("getApplicableRegions", Class.forName("com.sk89q.worldedit.util.Location")).invoke(query, weLoc);

         for(Object region : (Iterable)set) {
            String id = (String)region.getClass().getMethod("getId").invoke(region);
            if (names.contains(id) || names.contains(id.toLowerCase())) {
               return true;
            }
         }
      } catch (Exception var14) {
      }

      return false;
   }
}
