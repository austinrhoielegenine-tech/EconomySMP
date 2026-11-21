package me.xyr0.astralsmpcore.hide.command;

import me.xyr0.astralsmpcore.hide.AstralHide;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.HotbarUtil;
import me.xyr0.astralsmpcore.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideCommand implements CommandExecutor {
   private final AstralHide plugin;

   public HideCommand(AstralHide plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("astralsmpcore.system.hide.use")) {
            player.sendMessage(ColorUtil.color("&cYou don't have permission."));
            return true;
         } else {
            if (this.plugin.getHideManager().isHidden(player)) {
               this.plugin.getHideManager().unhide(player);
               player.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages.unhide.chat")));
               HotbarUtil.send(player, TextUtil.component(this.plugin.getConfig().getString("messages.unhide.actionbar")));
            } else {
               this.plugin.getHideManager().hide(player);
               player.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages.hide.chat")));
               HotbarUtil.send(player, TextUtil.component(this.plugin.getConfig().getString("messages.hide.actionbar")));
            }

            return true;
         }
      } else {
         sender.sendMessage("Only players can use this command.");
         return true;
      }
   }
}
