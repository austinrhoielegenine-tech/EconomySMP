package me.xyr0.economysmpcore.rules.command;

import me.xyr0.economysmpcore.rules.AstralRules;
import me.xyr0.economysmpcore.rules.gui.RulesGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements CommandExecutor {
   private final AstralRules plugin;

   public RulesCommand(AstralRules plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         RulesGUI.open(player, this.plugin);
         return true;
      } else {
         sender.sendMessage("Only players can use this command.");
         return true;
      }
   }
}
