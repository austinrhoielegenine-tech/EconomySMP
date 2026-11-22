package com.austynzdevv.nyrethacore.punishment2.listener;

import com.austynzdevv.nyrethacore.punishment2.EconomyPunishment;
import com.austynzdevv.nyrethacore.punishment2.Punishment;
import com.austynzdevv.nyrethacore.punishment2.util.TimeUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BanListener implements Listener {
   private final EconomyPunishment plugin;

   public BanListener(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      for(Punishment punishment : this.plugin.getHistoryManager().getHistory(event.getPlayer().getUniqueId())) {
         if (punishment.getType().equalsIgnoreCase("BAN") && punishment.isActive()) {
            if (punishment.getExpireTime() == -1L || !TimeUtil.hasExpired(punishment.getExpireTime())) {
               event.getPlayer().kickPlayer(this.plugin.getBanScreenManager().createScreen(punishment));
               return;
            }

            punishment.setActive(false);
         }
      }

   }
}
