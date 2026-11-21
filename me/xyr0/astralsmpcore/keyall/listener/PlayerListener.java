package me.xyr0.astralsmpcore.keyall.listener;

import me.xyr0.astralsmpcore.keyall.AstralKeyall;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerListener implements Listener {
   private final AstralKeyall plugin;

   public PlayerListener(AstralKeyall plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      this.plugin.getTimer().onJoin(event.getPlayer());
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.plugin.getTimer().onQuit(event.getPlayer());
   }
}
