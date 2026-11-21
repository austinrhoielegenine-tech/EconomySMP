package me.xyr0.astralsmpcore.chat2.command;

import me.xyr0.astralsmpcore.chat2.AstralChat;
import me.xyr0.astralsmpcore.chat2.ChatManager;
import me.xyr0.astralsmpcore.chat2.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand implements CommandExecutor {
   private final AstralChat plugin;
   private final ChatManager chatManager;

   public ChatCommand(AstralChat plugin, ChatManager chatManager) {
      this.plugin = plugin;
      this.chatManager = chatManager;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.isOp() && !player.hasPermission("astralsmpcore.system.chat.manage")) {
            player.sendMessage(MessageUtil.color(this.plugin.getConfigManager().getMessage("chat.no-permission")));
            return true;
         } else if (args.length == 0) {
            player.sendMessage(MessageUtil.color("&cUsage: /chat <enable|disable>"));
            return true;
         } else if (args[0].equalsIgnoreCase("disable")) {
            this.chatManager.disableChat();
            this.chatManager.saveState();
            player.sendMessage(MessageUtil.color("&aGlobal chat disabled."));
            return true;
         } else if (args[0].equalsIgnoreCase("enable")) {
            this.chatManager.enableChat();
            this.chatManager.saveState();
            player.sendMessage(MessageUtil.color("&aGlobal chat enabled."));
            return true;
         } else {
            player.sendMessage(MessageUtil.color("&cUsage: /chat <enable|disable>"));
            return true;
         }
      } else {
         sender.sendMessage("Only players can use this command.");
         return true;
      }
   }
}
