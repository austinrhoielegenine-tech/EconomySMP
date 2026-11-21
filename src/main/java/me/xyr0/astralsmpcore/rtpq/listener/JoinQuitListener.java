package me.xyr0.astralsmpcore.rtpq.listener;

import me.xyr0.astralsmpcore.rtpq.AstralRTPQ;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {
   private final AstralRTPQ plugin;

   public JoinQuitListener(AstralRTPQ plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      if (this.plugin.getQueueManager() != null && this.plugin.getQueueManager().isQueued(player)) {
         this.plugin.getQueueManager().removePlayer(player);
      }

   }
}
