package me.xyr0.astralsmpcore.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AstralOrderCommand implements CommandExecutor, TabCompleter {
   private final AstralOrder plugin;

   public AstralOrderCommand(AstralOrder plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("astralsmpcore.system.order.use")) {
         this.plugin.getMessages().send(sender, "no-permission");
         return true;
      } else if (args.length == 0) {
         sender.sendMessage("§e/astralorder reload §7- Reload config");
         sender.sendMessage("§e/orders §7- Open orders menu");
         return true;
      } else if (args[0].equalsIgnoreCase("reload")) {
         this.plugin.reloadAll();
         this.plugin.getMessages().send(sender, "reload");
         return true;
      } else {
         sender.sendMessage("§cUnknown subcommand.");
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      return args.length == 1 ? (List)List.of("reload").stream().filter((s) -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new)) : List.of();
   }
}
