package com.austynzdevv.nyrethacore.chat2;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class EconomyChatTabCompleter implements TabCompleter {
   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> completions = new ArrayList();
      if (command.getName().equalsIgnoreCase("economychat") && args.length == 1) {
         completions.add("reload");
      }

      if (command.getName().equalsIgnoreCase("chat") && args.length == 1) {
         completions.add("enable");
         completions.add("disable");
      }

      return completions;
   }
}
