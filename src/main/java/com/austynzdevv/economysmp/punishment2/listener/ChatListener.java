package com.austynzdevv.economysmp.punishment2.listener;

import com.austynzdevv.economysmp.punishment2.EconomyPunishment;
import com.austynzdevv.economysmp.punishment2.Punishment;
import com.austynzdevv.economysmp.punishment2.util.TimeUtil;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
   private final EconomyPunishment plugin;

   public ChatListener(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (!player.hasPermission("economysmpcore.system.punishment.bypass")) {
         Punishment mute = this.plugin.getDatabaseManager().getActivePunishment(player.getUniqueId(), "MUTE");
         if (mute != null && !TimeUtil.hasExpired(mute.getExpireTime())) {
            this.block(event, player, mute, false);
         } else {
            String ip = null;

            try {
               if (player.getAddress() != null && player.getAddress().getAddress() != null) {
                  ip = player.getAddress().getAddress().getHostAddress();
               }
            } catch (Exception var6) {
            }

            if (ip != null) {
               Punishment ipMute = this.plugin.getIpBanManager().getIPMutePunishment(ip);
               if (ipMute != null) {
                  this.block(event, player, ipMute, true);
               }
            }

         }
      }
   }

   private void block(AsyncPlayerChatEvent event, Player player, Punishment punishment, boolean ip) {
      event.setCancelled(true);
      long left = punishment.getExpireTime() <= 0L ? -1L : punishment.getExpireTime() - System.currentTimeMillis();
      String label = ip ? "IP-muted" : "muted";
      player.sendMessage(ColorUtil.color("&cYou are " + label + " for &f" + punishment.getReason() + " &cTime left: &f" + TimeUtil.formatTime(left)));
   }
}
