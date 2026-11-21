package com.austynzdevv.novacore.spawners.util;

import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class BedrockUtil {
   private static Boolean floodgatePresent;
   private static Object floodgateApi;
   private static Method isFloodgatePlayer;

   private BedrockUtil() {
   }

   public static boolean isBedrock(Player player) {
      if (player == null) {
         return false;
      } else if (!ensureApi()) {
         return false;
      } else {
         try {
            return (Boolean)isFloodgatePlayer.invoke(floodgateApi, player.getUniqueId());
         } catch (Exception var2) {
            return false;
         }
      }
   }

   public static boolean isBedrock(UUID uuid) {
      if (uuid != null && ensureApi()) {
         try {
            return (Boolean)isFloodgatePlayer.invoke(floodgateApi, uuid);
         } catch (Exception var2) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean ensureApi() {
      if (floodgatePresent != null) {
         return floodgatePresent;
      } else if (Bukkit.getPluginManager().getPlugin("floodgate") == null) {
         floodgatePresent = false;
         return false;
      } else {
         try {
            Class<?> apiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            Method getInstance = apiClass.getMethod("getInstance");
            floodgateApi = getInstance.invoke((Object)null);
            isFloodgatePlayer = apiClass.getMethod("isFloodgatePlayer", UUID.class);
            floodgatePresent = true;
            return true;
         } catch (Exception var2) {
            floodgatePresent = false;
            return false;
         }
      }
   }
}
