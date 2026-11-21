package com.austynzdevv.economysmp.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class ClickGuard {
   private static final Map<UUID, Long> LAST = new ConcurrentHashMap();
   private static final long COOLDOWN_MS = 180L;

   private ClickGuard() {
   }

   public static boolean tooFast(Player player) {
      if (player == null) {
         return true;
      } else {
         long now = System.currentTimeMillis();
         Long prev = (Long)LAST.get(player.getUniqueId());
         if (prev != null && now - prev < 180L) {
            return true;
         } else {
            LAST.put(player.getUniqueId(), now);
            return false;
         }
      }
   }

   public static boolean guard(InventoryClickEvent e) {
      HumanEntity var2 = e.getWhoClicked();
      if (var2 instanceof Player p) {
         e.setCancelled(true);
         return tooFast(p);
      } else {
         e.setCancelled(true);
         return true;
      }
   }
}
