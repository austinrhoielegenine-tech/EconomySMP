package com.austynzdevv.economysmp.safety.listener;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.economysmp.VoiceChatModule;
import com.austynzdevv.economysmp.safety.EconomySafety;
import com.austynzdevv.economysmp.util.SoundUtil;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {
   private final EconomySafety plugin;
   private final Set<UUID> pendingVoice = ConcurrentHashMap.newKeySet();

   public JoinListener(EconomySafety plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if (!player.hasPlayedBefore()) {
         if (!this.plugin.getConfig().getBoolean("first-join.enabled")) {
            return;
         }

         int delay = this.plugin.getConfig().getInt("first-join.delay");
         Tasks.runLater(this.plugin, () -> {
            this.pendingVoice.add(player.getUniqueId());
            player.performCommand("safety");
            String soundName = this.plugin.getConfig().getString("first-join.sound");

            try {
               Sound sound = Sound.valueOf(soundName);
               SoundUtil.play(player, player.getLocation(), sound, 1.0F, 1.0F);
            } catch (Exception var4) {
               this.plugin.getLogger().warning("Invalid sound: " + soundName);
            }

         }, (long)delay);
      }

   }

   @EventHandler
   public void onMove(PlayerMoveEvent event) {
      if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ()) {
         this.openVoiceIfPending(event.getPlayer());
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.pendingVoice.remove(event.getPlayer().getUniqueId());
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      this.openVoiceIfPending(event.getPlayer());
   }

   @EventHandler
   public void onCommand(PlayerCommandPreprocessEvent event) {
      this.openVoiceIfPending(event.getPlayer());
   }

   private void openVoiceIfPending(Player player) {
      if (this.pendingVoice.remove(player.getUniqueId())) {
         VoiceChatModule vc = VoiceChatModule.getInstance();
         if (vc != null && player.isOnline()) {
            Tasks.run(this.plugin.getCore(), () -> {
               if (player.isOnline()) {
                  vc.openFirstJoin(player);
               }

            });
         }

      }
   }
}
