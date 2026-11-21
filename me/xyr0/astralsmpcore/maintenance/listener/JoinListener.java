package me.xyr0.astralsmpcore.maintenance.listener;

import java.util.List;
import me.xyr0.astralsmpcore.maintenance.astralMaintenance;
import me.xyr0.astralsmpcore.maintenance.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class JoinListener implements Listener {
   private final astralMaintenance plugin;
   private final MessageUtil messages;

   public JoinListener(astralMaintenance plugin) {
      this.plugin = plugin;
      this.messages = new MessageUtil(plugin);
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onLogin(PlayerLoginEvent event) {
      if (this.plugin.isMaintenanceEnabled()) {
         Player player = event.getPlayer();
         if (!this.plugin.getConfig().getBoolean("settings.allow-ops", true) || !player.isOp()) {
            String bypass = this.plugin.getConfig().getString("permissions.bypass", "maintenance.bypass");
            if (bypass == null || bypass.isEmpty() || !player.hasPermission(bypass)) {
               if (!this.plugin.getConfig().getBoolean("settings.use-whitelist", true) || !this.plugin.getWhitelistManager().contains(player.getName())) {
                  event.disallow(Result.KICK_OTHER, this.buildKickMessage());
               }
            }
         }
      }
   }

   private String buildKickMessage() {
      List<String> lines = this.plugin.getConfig().getStringList("kick.message");
      if (lines == null || lines.isEmpty()) {
         lines = List.of("&cServer is under maintenance.");
      }

      String discord = this.plugin.getConfig().getString("server.discord", "");
      String name = this.plugin.getConfig().getString("server.name", "Server");
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < lines.size(); ++i) {
         String line = (String)lines.get(i);
         if (line == null) {
            line = "";
         }

         line = line.replace("%discord%", discord == null ? "" : discord).replace("%server%", name == null ? "" : name).replace("%name%", name == null ? "" : name);
         sb.append(this.messages.color(line));
         if (i < lines.size() - 1) {
            sb.append("\n");
         }
      }

      return sb.toString();
   }
}
