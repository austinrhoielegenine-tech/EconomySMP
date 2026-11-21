package me.xyr0.economysmpcore.punishment2.listener;

import me.xyr0.economysmpcore.punishment2.AstralPunishment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncChatListener implements Listener {
   private final AstralPunishment plugin;

   public AsyncChatListener(AstralPunishment plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
   }
}
