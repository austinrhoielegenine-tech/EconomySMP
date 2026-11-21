package com.austynzdevv.novacore.hide.listener;

import com.austynzdevv.novacore.hide.EconomyHide;
import com.austynzdevv.novacore.util.Tasks;
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
