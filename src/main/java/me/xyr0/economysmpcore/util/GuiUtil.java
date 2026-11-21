package me.xyr0.economysmpcore.util;

import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class GuiUtil {
   private GuiUtil() {
   }

   public static void cancelDragForSession(InventoryDragEvent event, Map<UUID, ?> sessions) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (sessions.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
         }
      }

   }
}
