package me.xyr0.economysmpcore.tpa.listener;

import me.xyr0.economysmpcore.tpa.AstralTPA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class MoveListener implements Listener {
   private final AstralTPA plugin;

   public MoveListener(AstralTPA plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onMove(PlayerMoveEvent event) {
      if (event.getTo() != null) {
         if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            if (this.plugin.getTpa().isTeleporting(event.getPlayer().getUniqueId())) {
               this.plugin.getTpa().onMove(event.getPlayer());
            }

         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.plugin.getTpa().saveSettings();
   }
}
