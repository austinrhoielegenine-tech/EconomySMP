package me.xyr0.economysmpcore.hide.listener;

import me.xyr0.economysmpcore.hide.AstralHide;
import me.xyr0.economysmpcore.util.Tasks;
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
