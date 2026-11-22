package com.austynzdevv.nyrethacore.shards.listener;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import com.austynzdevv.nyrethacore.shards.EconomyShards;
import com.austynzdevv.nyrethacore.shards.util.AfkRegionUtil;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.HotbarUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class AfkTrackerTask extends BukkitRunnable {
   private final EconomyShards plugin;
   private final Set<UUID> inside = new HashSet();
   private final Map<UUID, Double> countdown = new HashMap();

   public AfkTrackerTask(EconomyShards plugin) {
      this.plugin = plugin;
   }

   public void run() {
      FileConfiguration cfg = this.plugin.getConfig();
      int interval = Math.max(1, cfg.getInt("reward-interval-seconds", 60));
      Set<UUID> stillInside = new HashSet();

      for(Player player : Bukkit.getOnlinePlayers()) {
         if (player.isOnline()) {
            UUID id = player.getUniqueId();
            boolean everywhere = player.hasPermission("nyrethacorecore.system.shards.everywhere");
            boolean inZone = AfkRegionUtil.isInAfkZone(player, cfg);
            boolean wasInside = this.inside.contains(id);
            double speed = this.plugin.getBoosters().getSpeedMultiplier(id);
            if (inZone) {
               stillInside.add(id);
               if (!wasInside) {
                  this.onEnter(player, cfg);
               }

               if (!wasInside) {
                  this.countdown.put(id, (double)interval);
               }

               double left = (Double)this.countdown.getOrDefault(id, (double)interval);
               left -= speed;
               if (left <= (double)0.0F) {
                  this.giveReward(player, cfg, true);
                  left = (double)interval;
               }

               this.countdown.put(id, left);
               this.showCountdown(player, left, speed);
            } else if (everywhere) {
               double left = (Double)this.countdown.getOrDefault(id, (double)interval);
               left -= speed;
               if (left <= (double)0.0F) {
                  this.giveReward(player, cfg, false);
                  left = (double)interval;
               }

               this.countdown.put(id, left);
            } else if (wasInside) {
               this.onLeave(player);
               this.countdown.remove(id);
            }
         }
      }

      this.inside.clear();
      this.inside.addAll(stillInside);
      this.countdown.keySet().removeIf((idx) -> {
         Player p = Bukkit.getPlayer(idx);
         if (p != null && p.isOnline()) {
            if (p.hasPermission("nyrethacorecore.system.shards.everywhere")) {
               return false;
            } else {
               return !stillInside.contains(idx);
            }
         } else {
            return true;
         }
      });
   }

   private void showCountdown(Player player, double secondsLeft, double speed) {
      String key = speed > 1.01 ? "actionbar-countdown-boosted" : "actionbar-countdown";
      String bar = this.plugin.getMessages().get(key, speed > 1.01 ? "&7Next shard in &#A20AD6%seconds%s &7(&#A20AD6%speed%x&7)" : "&7Next shard in &#A303F9%seconds%s");
      if (bar != null && !bar.isBlank()) {
         int display = (int)Math.ceil(Math.max((double)0.0F, secondsLeft));
         String speedStr = speed == (double)((long)speed) ? String.valueOf((long)speed) : String.valueOf(speed);
         bar = bar.replace("%seconds%", String.valueOf(display)).replace("%time%", String.valueOf(display)).replace("%speed%", speedStr).replace("%player%", player.getName());
         HotbarUtil.send(player, ColorUtil.parse(bar));
      }
   }

   private void onEnter(Player player, FileConfiguration cfg) {
      String chat = this.plugin.getMessages().get("afk-enter", "&7You entered the &#A303F9AFK zone&7. You will earn shards here.");
      if (chat != null && !chat.isBlank()) {
         String var10001 = this.plugin.getMessages().prefix();
         player.sendMessage(ColorUtil.parse(var10001 + chat));
      }

      String title = this.plugin.getMessages().get("afk-enter-title", "&#A303F9AFK Zone");
      String sub = this.plugin.getMessages().get("afk-enter-subtitle", "&7You are now earning shards");
      if (title != null && !title.isBlank() || sub != null && !sub.isBlank()) {
         player.showTitle(Title.title(ColorUtil.parse(title == null ? "" : title), ColorUtil.parse(sub == null ? "" : sub), Times.times(Duration.ofMillis(300L), Duration.ofMillis(2000L), Duration.ofMillis(500L))));
      }

      this.playSound(player, cfg.getString("sounds.afk-enter", "ENTITY_PLAYER_LEVELUP"));
   }

   private void onLeave(Player player) {
      String chat = this.plugin.getMessages().get("afk-leave", "&7You left the &#A303F9AFK zone&7.");
      if (chat != null && !chat.isBlank()) {
         String var10001 = this.plugin.getMessages().prefix();
         player.sendMessage(ColorUtil.parse(var10001 + chat));
      }

   }

   private void giveReward(Player player, FileConfiguration cfg, boolean inZone) {
      int amount = cfg.getInt("shards-per-minute", 1);
      if (inZone && player.hasPermission("nyrethacorecore.system.shards.vip") && cfg.getBoolean("vip.enabled", true)) {
         amount = cfg.getInt("vip.shards-per-minute", 2);
      }

      this.plugin.getShards().add(player, (long)amount);
      String msg = this.plugin.getMessages().get("reward", "&7You earned &#A303F9%amount% &7shard(s)!");
      if (msg != null && !msg.isBlank()) {
         String var10001 = this.plugin.getMessages().prefix();
         player.sendMessage(ColorUtil.parse(var10001 + msg.replace("%amount%", String.valueOf(amount))));
      }

      this.playSound(player, cfg.getString("sounds.reward", "ENTITY_EXPERIENCE_ORB_PICKUP"));
   }

   private void playSound(Player player, String sound) {
      try {
         if (sound == null || sound.isBlank()) {
            return;
         }

         SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.toUpperCase()), 1.0F, 1.0F);
      } catch (Exception var4) {
      }

   }
}
