package com.austynzdevv.novacore.rtpq;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.austynzdevv.novacore.rtpq.util.MessageUtils;
import com.austynzdevv.novacore.rtpq.util.SoundUtils;
import com.austynzdevv.novacore.util.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchManager {
   private final EconomyRTPQ plugin;
   private final RTPManager rtpManager;
   private final Map<UUID, UUID> matches = new HashMap();
   private final Map<UUID, String> matchWorlds = new HashMap();

   public MatchManager(EconomyRTPQ plugin) {
      this.plugin = plugin;
      this.rtpManager = new RTPManager(plugin);
   }

   public void startMatch(Player one, Player two, String worldId) {
      this.matches.put(one.getUniqueId(), two.getUniqueId());
      this.matches.put(two.getUniqueId(), one.getUniqueId());
      this.matchWorlds.put(one.getUniqueId(), worldId);
      this.matchWorlds.put(two.getUniqueId(), worldId);
      if (this.plugin.getConfig().getBoolean("sounds.matched.enabled", true)) {
         this.playSound(one, "sounds.matched.sound");
         this.playSound(two, "sounds.matched.sound");
      }

      if (this.plugin.getConfig().getBoolean("titles.opponent.enabled", true)) {
         String title = this.plugin.getConfig().getString("titles.opponent.title", "&bOpponent Found!");
         String subtitle = this.plugin.getConfig().getString("titles.opponent.subtitle", "&7Teleporting...");
         MessageUtils.title(one, title, subtitle);
         MessageUtils.title(two, title, subtitle);
      }

      String found = this.plugin.getConfig().getString("messages.opponent-found", "&bYou have found an opponent!");
      MessageUtils.send(one, found);
      MessageUtils.send(two, found);
      this.countdown(one, two, worldId);
   }

   private void countdown(final Player one, final Player two, final String worldId) {
      final int[] __tid = new int[]{-1};
      BukkitRunnable __t = new BukkitRunnable() {
         int time = 5;

         public void run() {
            if (one.isOnline() && two.isOnline()) {
               if (this.time <= 0) {
                  if (MatchManager.this.plugin.getConfig().getBoolean("sounds.teleport.enabled", true)) {
                     MatchManager.this.playSound(one, "sounds.teleport.sound");
                     MatchManager.this.playSound(two, "sounds.teleport.sound");
                  }

                  MatchManager.this.rtpManager.teleportTogether(one, two, worldId);
                  MatchManager.this.removeMatch(one);
                  Tasks.cancel(__tid[0]);
               } else {
                  String message = MatchManager.this.plugin.getConfig().getString("messages.teleporting", "&#008CFFTeleporting in %time%").replace("%time%", String.valueOf(this.time));
                  MessageUtils.send(one, message);
                  MessageUtils.send(two, message);
                  MessageUtils.actionBar(one, message);
                  MessageUtils.actionBar(two, message);
                  if (MatchManager.this.plugin.getConfig().getBoolean("sounds.countdown.enabled", true)) {
                     MatchManager.this.playSound(one, "sounds.countdown.sound");
                     MatchManager.this.playSound(two, "sounds.countdown.sound");
                  }

                  --this.time;
               }
            } else {
               Tasks.cancel(__tid[0]);
            }
         }
      };
      __tid[0] = Tasks.runTimer(this.plugin.getCore(), __t, 0L, 20L);
   }

   private void playSound(Player player, String path) {
      SoundUtils.play(player, this.plugin.getConfig().getString(path, ""), (float)this.plugin.getConfig().getDouble(path.replace(".sound", ".volume"), (double)1.0F), (float)this.plugin.getConfig().getDouble(path.replace(".sound", ".pitch"), (double)1.0F));
   }

   public boolean isMatched(Player player) {
      return this.matches.containsKey(player.getUniqueId());
   }

   public UUID getOpponent(Player player) {
      return (UUID)this.matches.get(player.getUniqueId());
   }

   public void cancelMatch(Player one, Player two) {
      this.matches.remove(one.getUniqueId());
      this.matches.remove(two.getUniqueId());
      this.matchWorlds.remove(one.getUniqueId());
      this.matchWorlds.remove(two.getUniqueId());
      String message = this.plugin.getConfig().getString("messages.teleport-cancelled", "&cTeleport cancelled because a player moved.");
      MessageUtils.send(one, message);
      MessageUtils.send(two, message);
   }

   public void removeMatch(Player player) {
      UUID opponent = (UUID)this.matches.remove(player.getUniqueId());
      this.matchWorlds.remove(player.getUniqueId());
      if (opponent != null) {
         this.matches.remove(opponent);
         this.matchWorlds.remove(opponent);
      }

   }
}
