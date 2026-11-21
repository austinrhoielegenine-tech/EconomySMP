package com.austynzdevv.novacore.reports.command;

import java.util.ArrayList;
import java.util.List;
import com.austynzdevv.novacore.reports.EconomyReports;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ReportTabCompleter implements TabCompleter {
   private final EconomyReports plugin;

   public ReportTabCompleter(EconomyReports plugin) {
      this.plugin = plugin;
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
      List<String> list = new ArrayList();
      if (args.length == 1) {
         for(Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getName().equals(sender.getName())) {
               list.add(player.getName());
            }
         }
      }

      if (args.length == 2) {
         list.addAll(this.plugin.getConfig().getStringList("reasons"));
      }

      return list;
   }
}
