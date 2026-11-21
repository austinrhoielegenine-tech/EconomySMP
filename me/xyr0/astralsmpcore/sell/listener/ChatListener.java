package me.xyr0.astralsmpcore.sell.listener;

import me.xyr0.astralsmpcore.sell.AstralSell;
import me.xyr0.astralsmpcore.sell.gui.MenuSession;
import me.xyr0.astralsmpcore.sell.gui.SellMenus;
import me.xyr0.astralsmpcore.sell.util.InputUtil;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {
   private final AstralSell plugin;

   public ChatListener(AstralSell plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (Boolean.TRUE.equals(MenuSession.CHAT_SEARCH.get(player.getUniqueId()))) {
         event.setCancelled(true);
         String msg = event.getMessage().trim();
         Tasks.run(this.plugin.getCore(), () -> {
            MenuSession.CHAT_SEARCH.remove(player.getUniqueId());
            if (msg.equalsIgnoreCase("cancel")) {
               this.plugin.getMessages().send(player, "search-cancelled");
               SellMenus.openWorth(player, 0);
            } else {
               MenuSession.SEARCH.put(player.getUniqueId(), msg);
               SellMenus.openWorth(player, 0);
            }
         });
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onSign(SignChangeEvent event) {
      Player player = event.getPlayer();
      if (InputUtil.isSignInput(player.getUniqueId())) {
         event.setCancelled(true);
         String line = event.getLine(0);
         if (line == null) {
            line = "";
         }

         String msg = line.trim();
         Tasks.run(this.plugin.getCore(), () -> {
            InputUtil.cleanup(player);
            if (!msg.isEmpty() && !msg.equalsIgnoreCase("cancel")) {
               MenuSession.SEARCH.put(player.getUniqueId(), msg);
               SellMenus.openWorth(player, 0);
            } else {
               this.plugin.getMessages().send(player, "search-cancelled");
               SellMenus.openWorth(player, 0);
            }
         });
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      InputUtil.cleanup(event.getPlayer());
      MenuSession.clear(event.getPlayer().getUniqueId());
   }
}
