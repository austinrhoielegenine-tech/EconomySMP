package me.xyr0.economysmpcore.tools.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class FireworkBoostListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public void onFirework(PlayerInteractEvent event) {
      if (event.getHand() != null) {
         Action action = event.getAction();
         if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if (player.isGliding()) {
               ItemStack item = event.getItem();
               if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
                  event.setCancelled(false);
                  event.setUseItemInHand(Result.ALLOW);
                  if (event.getHand() == EquipmentSlot.OFF_HAND) {
                     event.setUseInteractedBlock(Result.DENY);
                  }

               }
            }
         }
      }
   }
}
