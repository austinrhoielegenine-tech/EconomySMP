package com.austynzdevv.novacore.rtpq.listener;

import com.austynzdevv.novacore.rtpq.EconomyRTPQ;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
   private final EconomyRTPQ plugin;

   public MoveListener(EconomyRTPQ plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onMove(PlayerMoveEvent event) {
      if (this.plugin.getConfig().getBoolean("queue.cancel-on-move", false)) {
         if (event.getTo() != null) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
               Player player = event.getPlayer();
               if (this.plugin.getQueueManager() != null && this.plugin.getQueueManager().isQueued(player)) {
                  this.plugin.getQueueManager().removePlayer(player);
                  player.sendMessage("§7You left the RTP queue because you moved.");
               }

            }
         }
      }
   }
}
