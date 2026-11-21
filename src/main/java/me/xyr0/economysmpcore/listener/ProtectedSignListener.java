package me.xyr0.economysmpcore.listener;

import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public final class ProtectedSignListener implements Listener {
   private final Predicate<Location> isProtected;

   public ProtectedSignListener(Predicate<Location> isProtected) {
      this.isProtected = isProtected;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onBreak(BlockBreakEvent event) {
      if (this.isProtected.test(event.getBlock().getLocation())) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onExplode(BlockExplodeEvent event) {
      event.blockList().removeIf((block) -> this.isProtected.test(block.getLocation()));
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onEntityExplode(EntityExplodeEvent event) {
      event.blockList().removeIf((block) -> this.isProtected.test(block.getLocation()));
   }
}
