package com.austynzdevv.novacore.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public final class SearchInputListener implements Listener {
   private final Plugin plugin;
   private final Predicate<Player> chatActive;
   private final Predicate<Player> signActive;
   private final Consumer<Player> clearInput;
   private final BiConsumer<Player, String> search;
   private final Consumer<Player> cancel;
   private final Consumer<Player> quit;

   public SearchInputListener(Plugin plugin, Predicate<Player> chatActive, Predicate<Player> signActive, Consumer<Player> clearInput, BiConsumer<Player, String> search, Consumer<Player> cancel, Consumer<Player> quit) {
      this.plugin = plugin;
      this.chatActive = chatActive;
      this.signActive = signActive;
      this.clearInput = clearInput;
      this.search = search;
      this.cancel = cancel;
      this.quit = quit;
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (this.chatActive.test(player)) {
         event.setCancelled(true);
         String message = event.getMessage().trim();
         Tasks.run(this.plugin, () -> this.finish(player, message));
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onSign(SignChangeEvent event) {
      Player player = event.getPlayer();
      if (this.signActive.test(player)) {
         event.setCancelled(true);

         String message;
         try {
            Component line = event.line(0);
            message = line == null ? "" : PlainTextComponentSerializer.plainText().serialize(line);
         } catch (RuntimeException var6) {
            String line = event.getLine(0);
            message = line == null ? "" : line;
         }

         String value = message.trim();
         Tasks.run(this.plugin, () -> this.finish(player, value));
      }
   }

   private void finish(Player player, String message) {
      this.clearInput.accept(player);
      if (!message.isEmpty() && !message.equalsIgnoreCase("cancel")) {
         this.search.accept(player, message);
      } else {
         this.cancel.accept(player);
      }

   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      this.clearInput.accept(player);
      this.quit.accept(player);
   }
}
