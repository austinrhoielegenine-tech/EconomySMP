package com.austynzdevv.nyrethacore.maintenance.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import java.util.List;
import com.austynzdevv.nyrethacore.maintenance.economyMaintenance;
import com.austynzdevv.nyrethacore.maintenance.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MotdListener implements Listener {
   private final economyMaintenance plugin;
   private final MessageUtil messages;

   public MotdListener(economyMaintenance plugin) {
      this.plugin = plugin;
      this.messages = new MessageUtil(plugin);
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPing(PaperServerListPingEvent event) {
      FileConfiguration motd = this.plugin.getMotd();
      boolean maintenance = this.plugin.isMaintenanceEnabled();
      if (maintenance) {
         this.apply(event, motd, "maintenance", true);
      } else if (motd.getBoolean("normal.enabled", true)) {
         this.apply(event, motd, "normal", false);
      }
   }

   private void apply(PaperServerListPingEvent event, FileConfiguration motd, String path, boolean force) {
      List<String> lines = motd.getStringList(path + ".lines");
      if (lines != null && !lines.isEmpty()) {
         String server = this.plugin.getConfig().getString("server.name", "");
         String discord = this.plugin.getConfig().getString("server.discord", "");
         StringBuilder out = new StringBuilder();

         for(int i = 0; i < lines.size(); ++i) {
            String line = (String)lines.get(i);
            if (line == null) {
               line = "";
            }

            line = line.replace("%server%", server).replace("%discord%", discord);
            out.append(this.messages.color(line));
            if (i < lines.size() - 1) {
               out.append("\n");
            }
         }

         event.setMotd(out.toString());
         if (motd.contains(path + ".max-players")) {
            event.setMaxPlayers(motd.getInt(path + ".max-players"));
         }

      }
   }
}
