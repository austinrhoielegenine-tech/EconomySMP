package com.austynzdevv.novacore.maintenance.listener;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class MaintenanceTab implements TabCompleter {
   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> suggestions = new ArrayList();
      if (args.length == 1) {
         suggestions.add("on");
         suggestions.add("off");
         suggestions.add("safe");
         suggestions.add("add");
         suggestions.add("remove");
         suggestions.add("list");
         suggestions.add("reload");
         suggestions.add("help");
      }

      return suggestions;
   }
}
