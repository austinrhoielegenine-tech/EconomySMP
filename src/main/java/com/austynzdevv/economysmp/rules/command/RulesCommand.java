package com.austynzdevv.economysmp.rules.command;

import com.austynzdevv.economysmp.rules.EconomyRules;
import com.austynzdevv.economysmp.rules.gui.RulesGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements CommandExecutor {
   private final EconomyRules plugin;

   public RulesCommand(EconomyRules plugin) {
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
