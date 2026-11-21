package me.xyr0.astralsmpcore.hide.listener;

import me.xyr0.astralsmpcore.hide.AstralHide;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
   private final AstralHide plugin;

   public PlayerJoinListener(AstralHide plugin) {
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
