package me.xyr0.economysmpcore.punishment2.command;

import me.xyr0.economysmpcore.punishment2.AstralPunishment;
import me.xyr0.economysmpcore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
   private final AstralPunishment plugin;

   public ReloadCommand(AstralPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.punishment.admin")) {
         sender.sendMessage(ColorUtil.color(this.plugin.getMessages().getString("no-permission", "&cYou do not have permission.")));
         return true;
      } else {
         this.plugin.reloadConfig();
         this.plugin.reloadMessages();
         if (this.plugin.getReasonManager() != null) {
            this.plugin.getReasonManager().load();
         }

         this.plugin.getLogger().info("AstralPunishment configurations reloaded.");
         sender.sendMessage(ColorUtil.color(this.plugin.getMessages().getString("reload.success", "&#00ff66Configuration files reloaded successfully.")));
         return true;
      }
   }
}
