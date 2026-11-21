package com.austynzdevv.economysmp.hide.command;

import com.austynzdevv.economysmp.hide.EconomyHide;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.HotbarUtil;
import com.austynzdevv.economysmp.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideCommand implements CommandExecutor {
   private final EconomyHide plugin;

   public HideCommand(EconomyHide plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("economysmpcore.system.hide.use")) {
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
