package com.austynzdevv.novacore.homes.listener;

import com.austynzdevv.novacore.homes.EconomyHomes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
   private final EconomyHomes plugin;

   public PlayerListener(EconomyHomes plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      this.plugin.storage().touch(event.getPlayer());
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.plugin.teleports().cancel(event.getPlayer().getUniqueId());
      this.plugin.storage().unload(event.getPlayer().getUniqueId());
   }
}
