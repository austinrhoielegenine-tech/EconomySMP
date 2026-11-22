package com.austynzdevv.nyrethacore.hide.listener;

import com.austynzdevv.nyrethacore.hide.EconomyHide;
import com.austynzdevv.nyrethacore.util.Tasks;
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
