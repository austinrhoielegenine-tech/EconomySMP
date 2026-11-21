package com.austynzdevv.economysmp.hide.listener;

import com.austynzdevv.economysmp.hide.EconomyHide;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
   private final EconomyHide plugin;

   public PlayerJoinListener(EconomyHide plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      Tasks.runLater(this.plugin, () -> {
         if (this.plugin.getHideManager().isHidden(event.getPlayer())) {
            this.plugin.getHideManager().applyHide(event.getPlayer());
         }

      }, 20L);
   }
}
