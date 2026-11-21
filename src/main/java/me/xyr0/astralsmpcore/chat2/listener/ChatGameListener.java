package me.xyr0.astralsmpcore.chat2.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.xyr0.astralsmpcore.chat2.AstralChat;
import me.xyr0.astralsmpcore.chat2.ChatGameManager;
import me.xyr0.astralsmpcore.util.Tasks;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatGameListener implements Listener {
   private final AstralChat plugin;

   public ChatGameListener(AstralChat plugin) {
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
