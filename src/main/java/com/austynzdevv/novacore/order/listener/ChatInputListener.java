package com.austynzdevv.novacore.order.listener;

import java.util.Map;
import java.util.UUID;
import com.austynzdevv.novacore.order.EconomyOrder;
import com.austynzdevv.novacore.order.gui.MenuSession;
import com.austynzdevv.novacore.order.gui.OrderMenus;
import com.austynzdevv.novacore.util.EconomyUtil;
import com.austynzdevv.novacore.util.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputListener implements Listener {
   private final EconomyOrder plugin;

   public ChatInputListener(EconomyOrder plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      UUID id = player.getUniqueId();
      MenuSession.ChatMode mode = (MenuSession.ChatMode)MenuSession.CHAT.getOrDefault(id, MenuSession.ChatMode.NONE);
      if (mode != MenuSession.ChatMode.NONE) {
         event.setCancelled(true);
         String msg = event.getMessage().trim();
         Tasks.run(this.plugin.getCore(), () -> this.handle(player, mode, msg));
      }
   }

   private void handle(Player player, MenuSession.ChatMode mode, String msg) {
      UUID id = player.getUniqueId();
      MenuSession.CHAT.put(id, MenuSession.ChatMode.NONE);
      if (msg.equalsIgnoreCase("cancel")) {
         this.plugin.getMessages().send(player, "chat-input-cancel");
         if (mode == MenuSession.ChatMode.SEARCH) {
            OrderMenus.openBrowse(player, 0);
         } else {
            OrderMenus.openNewOrder(player);
         }

      } else if (mode == MenuSession.ChatMode.SEARCH) {
         MenuSession.SEARCH.put(id, msg);
         OrderMenus.openBrowse(player, 0);
      } else if (mode == MenuSession.ChatMode.AMOUNT) {
         try {
            int amount = Integer.parseInt(msg.replace(",", ""));
            int min = Math.max(1, this.plugin.getConfig().getInt("minimum-item-per-order", 1));
            int max = this.plugin.getConfig().getInt("maximum-item-per-order", -1);
            if (max < 0) {
               max = Integer.MAX_VALUE;
            }

            if (amount < min) {
               this.plugin.getMessages().send(player, "amount-too-low", Map.of("min", String.valueOf(min)));
               OrderMenus.openNewOrder(player);
               return;
            }

            if (amount > max) {
               this.plugin.getMessages().send(player, "amount-too-high", Map.of("max", String.valueOf(max)));
               OrderMenus.openNewOrder(player);
               return;
            }

            MenuSession.DRAFT_AMOUNT.put(id, amount);
            this.plugin.getMessages().send(player, "amount-set", Map.of("amount", String.valueOf(amount)));
         } catch (NumberFormatException var11) {
            this.plugin.getMessages().send(player, "error");
         }

         OrderMenus.openNewOrder(player);
      } else {
         if (mode == MenuSession.ChatMode.PRICE) {
            try {
               double price = EconomyUtil.parseCompact(msg);
               double min = this.plugin.getConfig().getDouble("minimum-price-per-item", (double)1.0F);
               if (min < (double)0.0F) {
                  min = 0.01;
               }

               double max = this.plugin.getConfig().getDouble("maximum-price-per-item", (double)-1.0F);
               if (price < min) {
                  this.plugin.getMessages().send(player, "price-too-low", Map.of("min", String.format("%.2f", min)));
                  OrderMenus.openNewOrder(player);
                  return;
               }

               if (max > (double)0.0F && price > max) {
                  this.plugin.getMessages().send(player, "price-too-high", Map.of("max", String.format("%.2f", max)));
                  OrderMenus.openNewOrder(player);
                  return;
               }

               MenuSession.DRAFT_PRICE.put(id, (double)Math.round(price * (double)100.0F) / (double)100.0F);
               this.plugin.getMessages().send(player, "price-set", Map.of("price", String.format("%.2f", price)));
            } catch (NumberFormatException var12) {
               this.plugin.getMessages().send(player, "error");
            }

            OrderMenus.openNewOrder(player);
         }

      }
   }
}
