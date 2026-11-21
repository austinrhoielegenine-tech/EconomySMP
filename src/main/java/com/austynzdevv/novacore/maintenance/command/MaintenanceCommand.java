package com.austynzdevv.novacore.maintenance.command;

import java.util.List;
import com.austynzdevv.novacore.maintenance.economyMaintenance;
import com.austynzdevv.novacore.maintenance.util.MessageUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import com.austynzdevv.novacore.util.Tasks;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MaintenanceCommand implements CommandExecutor {
   private final economyMaintenance plugin;
   private final MessageUtil messages;

   public MaintenanceCommand(economyMaintenance plugin) {
      this.plugin = plugin;
      this.messages = new MessageUtil(plugin);
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission(this.plugin.getConfig().getString("permissions.use"))) {
         this.messages.send(sender, "no-permission");
         return true;
      } else if (args.length == 0) {
         this.help(sender);
         return true;
      } else {
         switch (args[0].toLowerCase()) {
            case "safe":
               this.startCountdown(sender);
               break;
            case "on":
               this.enableMaintenance(sender);
               break;
            case "off":
               if (!this.plugin.isMaintenanceEnabled()) {
                  this.messages.send(sender, "already-disabled");
                  return true;
               }

               this.plugin.setMaintenanceEnabled(false);
               this.messages.send(sender, "disabled");
               Bukkit.broadcastMessage(this.messages.get("disabled"));
               break;
            case "add":
               if (args.length < 2) {
                  sender.sendMessage("/maintenance add <player>");
                  return true;
               }

               this.plugin.getWhitelistManager().addPlayer(args[1]);
               this.messages.send(sender, "whitelist-added", args[1]);
               break;
            case "remove":
               if (args.length < 2) {
                  sender.sendMessage("/maintenance remove <player>");
                  return true;
               }

               this.plugin.getWhitelistManager().removePlayer(args[1]);
               this.messages.send(sender, "whitelist-removed", args[1]);
               break;
            case "list":
               sender.sendMessage(this.messages.color("<#55CFFF>&lMaintenance Whitelist"));

               for(String player : this.plugin.getWhitelistManager().getPlayers()) {
                  sender.sendMessage(this.messages.color("&7• &f" + player));
               }
               break;
            case "reload":
               this.plugin.reloadAll();
               this.messages.send(sender, "reload");
               break;
            case "help":
               this.help(sender);
               break;
            default:
               this.help(sender);
         }

         return true;
      }
   }

   private void startCountdown(final CommandSender sender) {
      if (this.plugin.isMaintenanceEnabled()) {
         this.messages.send(sender, "already-enabled");
      } else if (!this.plugin.getConfig().getBoolean("countdown.enabled", true)) {
         this.enableMaintenance(sender);
      } else {
         final int seconds = this.plugin.getConfig().getInt("countdown.seconds", 30);
         final int[] __tid = new int[]{-1};
         BukkitRunnable __t = new BukkitRunnable() {
            int time = seconds;

            public void run() {
               if (this.time <= 0) {
                  MaintenanceCommand.this.enableMaintenance(sender);
                  Tasks.cancel(__tid[0]);
               } else {
                  List<String> countdownMessages = MaintenanceCommand.this.plugin.getMessages().getStringList("countdown");
                  if (countdownMessages == null || countdownMessages.isEmpty()) {
                     countdownMessages = MaintenanceCommand.this.plugin.getConfig().getStringList("countdown.messages.countdown");
                  }

                  for(String msg : countdownMessages) {
                     Bukkit.broadcastMessage(MaintenanceCommand.this.messages.color(msg.replace("%time%", String.valueOf(this.time))));
                  }

                  for(Player player : Bukkit.getOnlinePlayers()) {
                     if (MaintenanceCommand.this.plugin.getConfig().getBoolean("countdown.sounds.enabled")) {
                        try {
                           Sound sound = Sound.valueOf(MaintenanceCommand.this.plugin.getConfig().getString("countdown.sounds.sound"));
                           float volume = (float)MaintenanceCommand.this.plugin.getConfig().getDouble("countdown.sounds.volume", (double)1.0F);
                           float pitch = (float)MaintenanceCommand.this.plugin.getConfig().getDouble("countdown.sounds.pitch", (double)1.0F);
                           SoundUtil.play(player, player.getLocation(), sound, volume, pitch);
                        } catch (Exception var10) {
                           MaintenanceCommand.this.plugin.getLogger().warning("Invalid countdown sound in config!");
                        }
                     }

                     int titleLast = MaintenanceCommand.this.plugin.getConfig().getInt("countdown.title.only-last-seconds", 10);
                     if (this.time <= titleLast && MaintenanceCommand.this.plugin.getConfig().getBoolean("countdown.title.enabled")) {
                        int fadeIn = MaintenanceCommand.this.plugin.getConfig().getInt("countdown.title.fade-in", 10);
                        int stay = MaintenanceCommand.this.plugin.getConfig().getInt("countdown.title.stay", 30);
                        int fadeOut = MaintenanceCommand.this.plugin.getConfig().getInt("countdown.title.fade-out", 10);
                        String titleText = MaintenanceCommand.this.plugin.getConfig().getString("countdown.title.title", MaintenanceCommand.this.plugin.getConfig().getString("countdown.title.title.text", ""));
                        String subText = MaintenanceCommand.this.plugin.getConfig().getString("countdown.title.subtitle", MaintenanceCommand.this.plugin.getConfig().getString("countdown.title.subtitle.text", ""));
                        player.sendTitle(MaintenanceCommand.this.messages.color(titleText), MaintenanceCommand.this.messages.color(subText.replace("%time%", String.valueOf(this.time))), fadeIn, stay, fadeOut);
                     }
                  }

                  --this.time;
               }
            }
         };
         __tid[0] = Tasks.runTimer(this.plugin, __t, 0L, 20L);
      }
   }

   private void enableMaintenance(CommandSender sender) {
      if (this.plugin.isMaintenanceEnabled()) {
         this.messages.send(sender, "already-enabled");
      } else {
         this.plugin.setMaintenanceEnabled(true);
         this.messages.send(sender, "enabled");
         List<String> finished = this.plugin.getMessages().getStringList("finished");
         if (finished == null || finished.isEmpty()) {
            finished = this.plugin.getConfig().getStringList("countdown.messages.finished");
         }

         if (finished != null && !finished.isEmpty()) {
            for(String line : finished) {
               Bukkit.broadcastMessage(this.messages.color(line.replace("%server%", this.plugin.getConfig().getString("server.name", "")).replace("%discord%", this.plugin.getConfig().getString("server.discord", ""))));
            }
         } else {
            Bukkit.broadcastMessage(this.messages.get("enabled"));
         }

         this.kickPlayers();
      }
   }

   private void kickPlayers() {
      for(Player player : Bukkit.getOnlinePlayers()) {
         if (!this.plugin.getConfig().getBoolean("settings.allow-ops", true) || !player.isOp()) {
            String bypass = this.plugin.getConfig().getString("permissions.bypass", "maintenance.bypass");
            if ((bypass == null || bypass.isEmpty() || !player.hasPermission(bypass)) && (!this.plugin.getConfig().getBoolean("settings.use-whitelist", true) || !this.plugin.getWhitelistManager().contains(player.getName()))) {
               String kickMsg = this.messages.color(String.join("\n", this.plugin.getConfig().getStringList("kick.message")).replace("%discord%", this.plugin.getConfig().getString("server.discord", "")));

               try {
                  player.kick(LegacyComponentSerializer.legacySection().deserialize(kickMsg));
               } catch (Exception var6) {
                  player.kickPlayer(kickMsg);
               }
            }
         }
      }

   }

   private void help(CommandSender sender) {
      sender.sendMessage(this.messages.color("<#55CFFF>&lMaintenance Help"));
      sender.sendMessage("/maintenance safe");
      sender.sendMessage("/maintenance on");
      sender.sendMessage("/maintenance off");
      sender.sendMessage("/maintenance add <player>");
      sender.sendMessage("/maintenance remove <player>");
      sender.sendMessage("/maintenance list");
      sender.sendMessage("/maintenance reload");
   }
}
