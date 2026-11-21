package me.xyr0.economysmpcore.help.command;

import java.util.Collections;
import java.util.List;
import me.xyr0.economysmpcore.help.AstralHelp;
import me.xyr0.economysmpcore.help.gui.HelpGUI;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor, TabCompleter {
   private final AstralHelp plugin;

   public HelpCommand(AstralHelp plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
         if (!sender.hasPermission("economysmpcore.system.help.use")) {
            sender.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages.no-permission")));
            this.playNoPermission(sender);
            return true;
         } else {
            this.plugin.reloadPlugin();
            sender.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages.reload")));
            if (sender instanceof Player) {
               Player player = (Player)sender;
               this.playSound(player, "sounds.reload.sound");
            }

            return true;
         }
      } else if (sender instanceof Player) {
         Player player = (Player)sender;
         (new HelpGUI(this.plugin)).open(player);
         return true;
      } else {
         sender.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages.only-player")));
         return true;
      }
   }

   private void playNoPermission(CommandSender sender) {
      if (sender instanceof Player player) {
         this.playSound(player, "sounds.no-permission.sound");
      }
   }

   private void playSound(Player player, String path) {
      String sound = this.plugin.getConfig().getString(path);
      if (sound != null) {
         try {
            SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.replace("minecraft:", "").toUpperCase()), 1.0F, 1.0F);
         } catch (Exception var5) {
         }

      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      return args.length == 1 && sender.hasPermission("economysmpcore.system.help.use") ? List.of("reload") : Collections.emptyList();
   }
}
