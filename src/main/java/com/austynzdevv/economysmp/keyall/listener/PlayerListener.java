package com.austynzdevv.economysmp.keyall.listener;

import com.austynzdevv.economysmp.keyall.EconomyKeyall;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerListener implements Listener {
   private final EconomyKeyall plugin;

   public PlayerListener(EconomyKeyall plugin) {
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
