package me.xyr0.economysmpcore.punishment2.listener;

import me.xyr0.economysmpcore.punishment2.AstralPunishment;
import me.xyr0.economysmpcore.punishment2.Punishment;
import me.xyr0.economysmpcore.punishment2.util.BanIdUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
   private final AstralPunishment plugin;

   public QuitListener(AstralPunishment plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      if (this.plugin.getFreezeManager().isFrozen(event.getPlayer().getUniqueId())) {
         String type = this.plugin.getConfig().getString("freeze.punishment.type", "BAN");
         String reason = this.plugin.getConfig().getString("freeze.punishment.reason", "Leaving while Frozen");
         String duration = this.plugin.getConfig().getString("freeze.punishment.duration", "7d");
         Punishment punishment = new Punishment(event.getPlayer().getUniqueId(), event.getPlayer().getName(), type, reason, duration, System.currentTimeMillis(), "Console", 1, BanIdUtil.generate(), System.currentTimeMillis(), true);
         this.plugin.getDatabaseManager().savePunishment(punishment);
         this.plugin.getWebhookManager().sendPunishment(type, punishment);
         this.plugin.getFreezeManager().unfreeze(event.getPlayer().getUniqueId());
      }
   }
}
