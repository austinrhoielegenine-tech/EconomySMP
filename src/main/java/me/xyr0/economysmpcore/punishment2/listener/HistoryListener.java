package me.xyr0.economysmpcore.punishment2.listener;

import me.xyr0.economysmpcore.punishment2.gui.HistoryGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HistoryListener implements Listener {
   @EventHandler
   public void onClick(InventoryClickEvent event) {
      if (event.getInventory().getHolder() instanceof HistoryGUI.HistoryHolder) {
         event.setCancelled(true);
      }

   }
}
