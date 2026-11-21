package com.austynzdevv.novacore.punishment2.listener;

import com.austynzdevv.novacore.punishment2.EconomyPunishment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncChatListener implements Listener {
   private final EconomyPunishment plugin;

   public AsyncChatListener(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
   }
}
