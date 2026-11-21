package me.xyr0.astralsmpcore.teams.listener;

import java.util.UUID;
import me.xyr0.astralsmpcore.teams.AstralTeams;
import me.xyr0.astralsmpcore.teams.Team;
import me.xyr0.astralsmpcore.teams.command.TeamCommand;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public final class ChatListener implements Listener {
   private final AstralTeams plugin;

   public ChatListener(AstralTeams plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (this.plugin.getTeams().isChatToggled(player.getUniqueId())) {
         Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            this.plugin.getTeams().setChatToggled(player.getUniqueId(), false);
         } else {
            event.setCancelled(true);
            String message = event.getMessage();
            Tasks.run(this.plugin.getCore(), () -> {
               Plugin cmdPlugin = this.plugin;
               if (this.plugin.getCommand("team") != null) {
                  CommandExecutor patt0$temp = this.plugin.getCommand("team").getExecutor();
                  if (patt0$temp instanceof TeamCommand) {
                     TeamCommand cmd = (TeamCommand)patt0$temp;
                     cmd.sendTeamChat(player, team, message);
                     return;
                  }
               }

               String format = this.plugin.getMessages().get("team.chat-format", "&#00a3fb[TeamChat] &#00a3fb%player%&7: &#00a3fb%message%");
               String out = format.replace("%player%", player.getName()).replace("%message%", message).replace("%astralsmpcore_teams_name%", team.getName());

               for(UUID id : team.getMembers()) {
                  Player p = Bukkit.getPlayer(id);
                  if (p != null) {
                     p.sendMessage(ColorUtil.parse(out));
                  }
               }

            });
         }
      }
   }
}
