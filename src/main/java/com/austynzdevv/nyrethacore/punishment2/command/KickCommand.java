package com.austynzdevv.nyrethacore.punishment2.command;

import com.austynzdevv.nyrethacore.punishment2.EconomyPunishment;
import com.austynzdevv.nyrethacore.punishment2.Punishment;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {
   private final EconomyPunishment plugin;

   public KickCommand(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player staff)) {
         sender.sendMessage("Players only.");
         return true;
      } else if (!staff.hasPermission("nyrethacorecore.system.punishment.kick")) {
         staff.sendMessage(ColorUtil.color("&cNo permission."));
         return true;
      } else if (args.length < 2) {
         staff.sendMessage(ColorUtil.color("&cUsage: /kick <player> <reason>"));
         return true;
      } else {
         Player target = Bukkit.getPlayerExact(args[0]);
         if (target == null) {
            staff.sendMessage(ColorUtil.color("&cPlayer not found."));
            return true;
         } else if (target.hasPermission("nyrethacorecore.system.punishment.bypass")) {
            staff.sendMessage(ColorUtil.color("&cThat player cannot be punished."));
            return true;
         } else {
            StringBuilder builder = new StringBuilder();

            for(int i = 1; i < args.length; ++i) {
               builder.append(args[i]);
               if (i != args.length - 1) {
                  builder.append(" ");
               }
            }

            String reason = builder.toString();
            Punishment punishment = new Punishment(target.getUniqueId(), target.getName(), "KICK", reason, "Instant", 0L, staff.getName(), 1, "NONE", System.currentTimeMillis(), false);
            this.plugin.getDatabaseManager().savePunishment(punishment);
            FileConfiguration messages = this.plugin.getMessages();
            String title = messages.getString("kick.screen.title", "&7Connection Lost");
            StringBuilder screen = new StringBuilder();
            screen.append(ColorUtil.color(title));

            for(String line : messages.getStringList("kick.screen.lines")) {
               screen.append("\n").append(ColorUtil.color(line.replace("{reason}", reason)));
            }

            target.kickPlayer(screen.toString());
            String var10000 = target.getName();
            Bukkit.broadcastMessage(ColorUtil.color("&#ff9933" + var10000 + " &7was kicked by &f" + staff.getName() + " &7for &f" + reason));
            this.plugin.getWebhookManager().sendPunishment("KICK", punishment);
            return true;
         }
      }
   }
}
