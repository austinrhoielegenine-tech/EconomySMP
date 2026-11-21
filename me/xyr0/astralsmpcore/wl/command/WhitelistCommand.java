package me.xyr0.astralsmpcore.wl.command;

import java.util.ArrayList;
import java.util.List;
import me.xyr0.astralsmpcore.wl.AstralWhitelister;
import me.xyr0.astralsmpcore.wl.util.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class WhitelistCommand implements CommandExecutor, TabCompleter {
   private final AstralWhitelister plugin;

   public WhitelistCommand(AstralWhitelister plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         sender.sendMessage(Text.legacy("&#37BFF9AstralWhitelister &7v1.0 &8- &7by Xyr0"));
         sender.sendMessage(Text.legacy("&7/" + label + " reload"));
         sender.sendMessage(Text.legacy("&7/" + label + " status"));
         return true;
      } else if (args[0].equalsIgnoreCase("reload")) {
         if (!sender.hasPermission("astralsmpcore.system.whitelister.reload")) {
            sender.sendMessage(Text.legacy("&cNo permission."));
            return true;
         } else {
            this.plugin.reloadAll();
            sender.sendMessage(Text.legacy("&aConfig reloaded."));
            return true;
         }
      } else if (args[0].equalsIgnoreCase("status")) {
         if (!sender.hasPermission("astralsmpcore.system.whitelister.reload")) {
            sender.sendMessage(Text.legacy("&cNo permission."));
            return true;
         } else {
            boolean on = this.plugin.getSettings().isEnabled();
            if (on) {
               sender.sendMessage(Text.legacy("&7Whitelist: &aON"));
            } else {
               sender.sendMessage(Text.legacy("&7Whitelist: &cOFF"));
            }

            return true;
         }
      } else {
         sender.sendMessage(Text.legacy("&cUnknown. Try /" + label + " reload"));
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> list = new ArrayList();
      if (args.length == 1) {
         String a = args[0].toLowerCase();
         if ("reload".startsWith(a)) {
            list.add("reload");
         }

         if ("status".startsWith(a)) {
            list.add("status");
         }
      }

      return list;
   }
}
