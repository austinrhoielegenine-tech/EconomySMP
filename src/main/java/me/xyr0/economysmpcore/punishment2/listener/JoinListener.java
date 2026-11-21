package me.xyr0.economysmpcore.punishment2.listener;

import me.xyr0.economysmpcore.punishment2.AstralPunishment;
import me.xyr0.economysmpcore.punishment2.Punishment;
import me.xyr0.economysmpcore.punishment2.util.TimeUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class JoinListener implements Listener {
   private final AstralPunishment plugin;

   public JoinListener(AstralPunishment plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onLogin(PlayerLoginEvent event) {
      if (!event.getPlayer().hasPermission("economysmpcore.system.punishment.bypass")) {
         Punishment ban = this.plugin.getDatabaseManager().getActivePunishment(event.getPlayer().getUniqueId(), "BAN");
         if (ban != null) {
            if (ban.getExpireTime() <= 0L || !TimeUtil.hasExpired(ban.getExpireTime())) {
               event.disallow(Result.KICK_OTHER, this.plugin.getBanScreenManager().createScreen(ban));
            }
         }
      }
   }
}
