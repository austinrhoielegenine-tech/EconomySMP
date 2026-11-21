package com.austynzdevv.novacore.chat2.command;

import com.austynzdevv.novacore.chat2.EconomyChat;
import com.austynzdevv.novacore.chat2.util.MessageUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {
   private final EconomyChat plugin;

   public ClearChatCommand(EconomyChat plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.isOp() && !player.hasPermission("novacorecore.system.chat.clear")) {
            player.sendMessage(MessageUtil.color(this.plugin.getConfigManager().getMessage("chat.no-permission")));
            return true;
         }
      }

      Component blank = Component.empty();

      for(Player online : Bukkit.getOnlinePlayers()) {
         for(int i = 0; i < 100; ++i) {
            online.sendMessage(blank);
         }
      }

      String var10000;
      if (sender instanceof Player p) {
         var10000 = p.getName();
      } else {
         var10000 = "Console";
      }

      String clearerName = var10000;
      String clearMessage = this.plugin.getConfigManager().getMessage("chat.cleared");
      clearMessage = clearMessage.replace("%player%", clearerName);
      Component broadcast = MessageUtil.colorComponent(clearMessage);

      for(Player online : Bukkit.getOnlinePlayers()) {
         online.sendMessage(broadcast);
         SoundUtil.play(online, online.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
      }

      if (!(sender instanceof Player)) {
         sender.sendMessage(MessageUtil.color(clearMessage));
      }

      return true;
   }
}
