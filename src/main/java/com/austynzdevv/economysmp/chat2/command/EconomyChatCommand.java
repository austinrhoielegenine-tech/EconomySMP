package com.austynzdevv.economysmp.chat2.command;

import com.austynzdevv.economysmp.EconomySMP;
import com.austynzdevv.economysmp.Module;
import com.austynzdevv.economysmp.SettingsModule;
import com.austynzdevv.economysmp.chat2.EconomyChat;
import com.austynzdevv.economysmp.chat2.util.MessageUtil;
import com.austynzdevv.economysmp.util.SoundUtil;
import com.austynzdevv.economysmp.util.TextUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyChatCommand implements CommandExecutor {
   private final EconomyChat plugin;

   public EconomyChatCommand(EconomyChat plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         sender.sendMessage(MessageUtil.color("&bEconomyChat &7v1.0"));
         sender.sendMessage(MessageUtil.color("&7Use &b/economychat reload &7or &b/economychat info"));
         return true;
      } else if (args[0].equalsIgnoreCase("toggle")) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            boolean on = this.plugin.getChatManager().togglePersonal(player.getUniqueId());

            try {
               EconomySMP core = EconomySMP.get();
               if (core != null) {
                  Module mod = core.modules().get("settings");
                  if (mod instanceof SettingsModule) {
                     SettingsModule sm = (SettingsModule)mod;
                     sm.toggleChatOff(player.getUniqueId());
                  }
               }
            } catch (Exception var10) {
            }

            if (on) {
               TextUtil.send(player, "&#00a3fbGeneral chat &#00FF00enabled&7.");
            } else {
               TextUtil.send(player, "&#00a3fbGeneral chat &#fc0404disabled&7.");
            }

            return true;
         } else {
            sender.sendMessage("Players only.");
            return true;
         }
      } else if (args[0].equalsIgnoreCase("reload")) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            if (!player.isOp() && !player.hasPermission("economysmpcore.system.chat.reload")) {
               player.sendMessage(MessageUtil.color("&cYou don't have permission."));
               return true;
            }
         }

         this.plugin.reloadEconomyChat();
         sender.sendMessage(MessageUtil.color("&aEconomyChat reloaded successfully."));
         if (sender instanceof Player) {
            Player player = (Player)sender;
            SoundUtil.play(player, player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
         }

         return true;
      } else if (args[0].equalsIgnoreCase("info")) {
         sender.sendMessage(MessageUtil.color("&b&m----------------"));
         sender.sendMessage(MessageUtil.color("&bEconomyChat &7v1.0"));
         sender.sendMessage(MessageUtil.color("&7DiscordSRV: " + (this.plugin.getDiscordSRVHook().isEnabled() ? "&aEnabled" : "&cDisabled")));
         sender.sendMessage(MessageUtil.color("&b&m----------------"));
         return true;
      } else {
         sender.sendMessage(MessageUtil.color("&cUsage: /economychat <reload|info>"));
         return true;
      }
   }
}
