package com.austynzdevv.nyrethacore.chat2.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import com.austynzdevv.nyrethacore.chat2.EconomyChat;
import com.austynzdevv.nyrethacore.chat2.SecurityLogger;
import com.austynzdevv.nyrethacore.chat2.util.MessageUtil;
import com.austynzdevv.nyrethacore.util.HotbarUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatSecurityListener implements Listener {
   private final EconomyChat plugin;
   private final Map<UUID, Long> lastMessage = new ConcurrentHashMap();
   private final List<Pattern> adPatterns = new ArrayList();

   public ChatSecurityListener(EconomyChat plugin) {
      this.plugin = plugin;
      this.reloadPatterns();
   }

   public void reloadPatterns() {
      this.adPatterns.clear();
      List<String> list = this.plugin.getConfig().getStringList("advertising-patterns");
      if (list == null || list.isEmpty()) {
         list = List.of("(?i)\\b\\d{1,3}(\\.\\d{1,3}){3}(:\\d{1,5})?\\b", "(?i)\\b[a-z0-9-]+\\.(com|net|org|gg|xyz|io|me|tk|ml|ga|cf|shop|store|mc)\\b", "(?i)discord\\.gg/[a-z0-9-]+", "(?i)discord\\.com/invite/[a-z0-9-]+", "(?i)\\bjoin\\s+(my|our)\\s+(server|realm)\\b");
      }

      for(String raw : list) {
         try {
            this.adPatterns.add(Pattern.compile(raw));
         } catch (Exception var5) {
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onChat(AsyncChatEvent event) {
      Player player = event.getPlayer();
      if (!player.hasPermission("nyrethacorecore.system.chat.bypass")) {
         String message = PlainTextComponentSerializer.plainText().serialize(event.message());
         if (this.plugin.getConfigManager().isModuleEnabled("spam") && this.isSpamming(player)) {
            long cooldown = this.plugin.getConfig().getLong("spam.cooldown-seconds", 3L);
            long last = (Long)this.lastMessage.getOrDefault(player.getUniqueId(), 0L);
            long left = Math.max(1L, cooldown - (System.currentTimeMillis() - last) / 1000L);
            this.block(event, player, "spam", String.valueOf(left));
         } else if (this.plugin.getConfigManager().isModuleEnabled("blacklist") && this.containsBlockedWord(message)) {
            this.block(event, player, "blacklist", (String)null);
         } else if (this.plugin.getConfigManager().isModuleEnabled("advertising") && this.isAdvertising(message)) {
            this.block(event, player, "advertising", (String)null);
         } else if (this.plugin.getConfigManager().isModuleEnabled("caps") && this.isCaps(message)) {
            this.block(event, player, "caps", (String)null);
         } else {
            this.lastMessage.put(player.getUniqueId(), System.currentTimeMillis());
         }
      }
   }

   private void block(AsyncChatEvent event, Player player, String reason, String seconds) {
      event.setCancelled(true);
      String playerKey = "security." + reason + ".player";
      String barKey = "security." + reason + ".actionbar";
      String staffKey = "security." + reason + ".staff";
      String playerMsg = this.plugin.getConfigManager().getMessage(playerKey);
      String barMsg = this.plugin.getConfigManager().getMessage(barKey);
      if (playerMsg == null || playerMsg.isEmpty() || playerMsg.equals(playerKey)) {
         playerMsg = "&cMessage blocked.";
      }

      if (barMsg == null || barMsg.isEmpty() || barMsg.equals(barKey)) {
         barMsg = playerMsg;
      }

      if (seconds != null) {
         playerMsg = playerMsg.replace("%seconds%", seconds);
         barMsg = barMsg.replace("%seconds%", seconds);
      }

      Component chat = MessageUtil.colorComponent(playerMsg);
      Component bar = MessageUtil.colorComponent(barMsg);
      player.sendMessage(chat);
      HotbarUtil.send(player, bar);
      if (this.plugin.getConfig().getBoolean("effects.enabled", true)) {
         try {
            String soundName = this.plugin.getConfig().getString("effects.sound", "ENTITY_VILLAGER_NO");
            SoundUtil.play(player, player.getLocation(), Sound.valueOf(soundName), 1.0F, 1.0F);
         } catch (Exception var16) {
         }
      }

      String staffMsg = this.plugin.getConfigManager().getMessage(staffKey);
      if (staffMsg != null && !staffMsg.isEmpty() && !staffMsg.equals(staffKey)) {
         staffMsg = staffMsg.replace("%player%", player.getName());
         Component staff = MessageUtil.colorComponent(staffMsg);

         for(Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("nyrethacorecore.system.chat.notify")) {
               online.sendMessage(staff);
            }
         }
      }

      if (this.plugin.getSecurityLogger() != null) {
         SecurityLogger var10000 = this.plugin.getSecurityLogger();
         String var10001 = player.getName();
         var10000.log(var10001 + " blocked for " + reason + ": " + PlainTextComponentSerializer.plainText().serialize(event.message()));
      }

   }

   private boolean isSpamming(Player player) {
      long cooldownMs = this.plugin.getConfig().getLong("spam.cooldown-seconds", 3L) * 1000L;
      if (cooldownMs <= 0L) {
         return false;
      } else {
         long last = (Long)this.lastMessage.getOrDefault(player.getUniqueId(), 0L);
         return System.currentTimeMillis() - last < cooldownMs;
      }
   }

   private boolean containsBlockedWord(String message) {
      List<String> words = this.plugin.getConfig().getStringList("blacklist.words");
      if (words != null && !words.isEmpty()) {
         String lower = message.toLowerCase();

         for(String word : words) {
            if (word != null && !word.isEmpty() && lower.contains(word.toLowerCase())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isAdvertising(String message) {
      if (message != null && !message.isEmpty()) {
         String cleaned = message.replace(" ", "").replace("[dot]", ".").replace("(dot)", ".");

         for(Pattern pattern : this.adPatterns) {
            if (pattern.matcher(message).find() || pattern.matcher(cleaned).find()) {
               return true;
            }
         }

         String lower = message.toLowerCase();
         return lower.contains("discord.gg/") || lower.contains(".minehut") || lower.contains("my server") || lower.contains("join now") && (lower.contains("play.") || lower.contains("mc."));
      } else {
         return false;
      }
   }

   private boolean isCaps(String message) {
      if (message.length() < this.plugin.getConfig().getInt("caps.min-length", 8)) {
         return false;
      } else {
         int letters = 0;
         int upper = 0;

         for(char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
               ++letters;
               if (Character.isUpperCase(c)) {
                  ++upper;
               }
            }
         }

         if (letters == 0) {
            return false;
         } else {
            double percent = (double)upper * (double)100.0F / (double)letters;
            return percent >= this.plugin.getConfig().getDouble("caps.percent", (double)70.0F);
         }
      }
   }
}
