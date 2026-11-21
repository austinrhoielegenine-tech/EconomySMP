package com.austynzdevv.novacore.chat2.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import com.austynzdevv.novacore.chat2.EconomyChat;
import com.austynzdevv.novacore.chat2.ChatGameManager;
import com.austynzdevv.novacore.util.Tasks;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatGameListener implements Listener {
   private final EconomyChat plugin;

   public ChatGameListener(EconomyChat plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public void onChat(AsyncChatEvent event) {
      ChatGameManager games = this.plugin.getChatGames();
      if (games != null && games.isActiveRound()) {
         Player player = event.getPlayer();
         String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
         if (games.matchesAnswer(msg)) {
            event.setCancelled(true);
            Tasks.run(this.plugin.getCore(), () -> games.tryAnswer(player, msg));
         }
      }
   }
}
