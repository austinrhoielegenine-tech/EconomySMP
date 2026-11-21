package me.xyr0.economysmpcore.lb.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.xyr0.economysmpcore.lb.AstralLeaderboards;
import me.xyr0.economysmpcore.lb.gui.LeaderboardMenus;
import me.xyr0.economysmpcore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class LeaderboardCommand implements CommandExecutor, TabCompleter {
   private final AstralLeaderboards plugin;

   public LeaderboardCommand(AstralLeaderboards plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
         if (!sender.hasPermission("economysmpcore.system.lb.admin")) {
            sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-permission", "&cYou have no permission for that!")));
            return true;
         } else {
            this.plugin.reloadAll();
            LeaderboardMenus.loadGuis(this.plugin);
            sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("reloaded", "&#00FF00AstralLeaderboards reloaded.")));
            return true;
         }
      } else if (sender instanceof Player) {
         Player player = (Player)sender;
         if (!player.hasPermission("economysmpcore.system.lb.use")) {
            player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-permission", "&cYou have no permission for that!")));
            return true;
         } else {
            LeaderboardMenus.loadGuis(this.plugin);
            LeaderboardMenus.openMain(player);
            return true;
         }
      } else {
         sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-only", "&cOnly players can use this command!")));
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (args.length == 1 && sender.hasPermission("economysmpcore.system.lb.admin") && "reload".startsWith(args[0].toLowerCase(Locale.ROOT))) {
         out.add("reload");
      }

      return out;
   }
}
