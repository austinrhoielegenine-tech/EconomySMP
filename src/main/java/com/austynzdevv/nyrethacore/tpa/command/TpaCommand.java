package com.austynzdevv.nyrethacore.tpa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.austynzdevv.nyrethacore.tpa.EconomyTPA;
import com.austynzdevv.nyrethacore.tpa.TpaManager;
import com.austynzdevv.nyrethacore.tpa.TpaRequest;
import com.austynzdevv.nyrethacore.tpa.gui.TpaMenus;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.HotbarUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class TpaCommand implements CommandExecutor, TabCompleter {
   private final EconomyTPA plugin;

   public TpaCommand(EconomyTPA plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      String name = command.getName().toLowerCase(Locale.ROOT);
      if (name.equals("tpareload")) {
         if (!sender.hasPermission("nyrethacorecore.system.tpa.admin")) {
            this.msg(sender, "no-permission", "&cYou do not have permission!");
            return true;
         } else {
            this.plugin.reloadAll();
            this.msg(sender, "reloaded", "&aEconomyTPA reloaded.");
            return true;
         }
      } else if (sender instanceof Player) {
         Player player = (Player)sender;
         TpaManager mgr = this.plugin.getTpa();
         boolean var10000;
         switch (name) {
            case "tpa":
               var10000 = this.sendTpa(player, args, TpaRequest.Type.TPA);
               break;
            case "tpahere":
               var10000 = this.sendTpa(player, args, TpaRequest.Type.TPAHERE);
               break;
            case "tpaccept":
               mgr.openAcceptGui(player, args.length > 0 ? args[0] : null);
               var10000 = true;
               break;
            case "tpadeny":
            case "tpdeny":
               mgr.deny(player, args.length > 0 ? args[0] : null);
               var10000 = true;
               break;
            case "tpacancel":
               mgr.cancel(player);
               var10000 = true;
               break;
            case "tpauto":
               boolean on = mgr.toggleTpAuto(player.getUniqueId());
               this.msg(player, on ? "tpauto-chat-on" : "tpauto-chat-off", on ? "&7You turned on tpauto." : "&7You turned off tpauto.");
               if (on) {
                  HotbarUtil.send(player, ColorUtil.parse(this.plugin.getMessages().get("tpauto-actionbar-on", "&aYou have tpauto on.")));
                  mgr.playSound(player, "tpauto");
               } else {
                  HotbarUtil.send(player, ColorUtil.parse(this.plugin.getMessages().get("tpauto-actionbar-off", "&7You turned off tpauto.")));
                  mgr.playSound(player, "tpauto-off");
               }

               var10000 = true;
               break;
            case "tpatoggle":
               boolean accepting = mgr.toggleTpa(player.getUniqueId());
               this.msg(player, accepting ? "tpa-settings-enabled" : "tpa-settings-disabled", accepting ? "&aYou are now accepting all TPA requests." : "&cYou are now blocking all TPA requests.");
               var10000 = true;
               break;
            case "tpaheretoggle":
               boolean accepting2 = mgr.toggleTpahere(player.getUniqueId());
               this.msg(player, accepting2 ? "tpa-here-enabled" : "tpa-here-disabled", accepting2 ? "&aYou are now accepting /tpahere requests." : "&cYou are now blocking /tpahere requests.");
               var10000 = true;
               break;
            case "tpaconfirmtoggle":
               boolean on2 = mgr.toggleConfirmGui(player.getUniqueId());
               this.msg(player, on2 ? "confirm-gui-enabled" : "confirm-gui-disabled", on2 ? "&aConfirm GUI enabled for send and accept." : "&cConfirm GUI disabled. /tpa and /tpaccept are instant.");
               var10000 = true;
               break;
            case "tpaignore":
               if (args.length < 1) {
                  this.msg(player, "tpaignore-usage", "&cUsage: /tpaignore <player>");
                  var10000 = true;
               } else {
                  Player other = Bukkit.getPlayerExact(args[0]);
                  if (other == null) {
                     this.msg(player, "player-not-found", "&cPlayer not found!");
                     var10000 = true;
                  } else if (other.getUniqueId().equals(player.getUniqueId())) {
                     this.msg(player, "tpa-self", "&cYou cannot send a teleport request to yourself!");
                     var10000 = true;
                  } else {
                     boolean ignored = mgr.toggleIgnore(player.getUniqueId(), other.getUniqueId());
                     this.msg(player, ignored ? "tpaignore-on" : "tpaignore-off", ignored ? "&cYou are now ignoring &#37BFF8{player}&c." : "&aYou are no longer ignoring &#37BFF8{player}&a.", "{player}", other.getName());
                     var10000 = true;
                  }
               }
               break;
            default:
               var10000 = true;
         }

         return var10000;
      } else {
         this.msg(sender, "player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean sendTpa(Player player, String[] args, TpaRequest.Type type) {
      if (args.length < 1) {
         this.msg(player, type == TpaRequest.Type.TPA ? "tpa-usage" : "tpahere-usage", type == TpaRequest.Type.TPA ? "&cUsage: /tpa <player>" : "&cUsage: /tpahere <player>");
         return true;
      } else {
         Player target = Bukkit.getPlayerExact(args[0]);
         if (target == null) {
            this.msg(player, "player-not-found", "&cPlayer not found!");
            return true;
         } else if (target.getUniqueId().equals(player.getUniqueId())) {
            this.msg(player, "tpa-self", "&cYou cannot send a teleport request to yourself!");
            return true;
         } else {
            TpaManager mgr = this.plugin.getTpa();
            if (mgr.isIgnoring(target.getUniqueId(), player.getUniqueId())) {
               this.msg(player, "tpa-ignored", "&c{player} is ignoring your teleport requests.", "{player}", target.getName());
               return true;
            } else if (type == TpaRequest.Type.TPA && mgr.isTpaDisabled(target.getUniqueId())) {
               this.msg(player, "tpa-receiver-disabled", "&c{player} has tpa requests disabled.", "{player}", target.getName());
               return true;
            } else if (type == TpaRequest.Type.TPAHERE && mgr.isTpahereDisabled(target.getUniqueId())) {
               this.msg(player, "tpa-here-receiver-disabled", "&c{player} has tpahere requests disabled.", "{player}", target.getName());
               return true;
            } else if (mgr.hasOutgoingTo(player.getUniqueId(), target.getUniqueId())) {
               this.msg(player, "tpa-already-sent", "&cYou have already sent a teleport request to &#37BFF8{target}&c. Please wait.", "{target}", args[0]);
               return true;
            } else if (mgr.getIncomingFrom(target.getUniqueId(), player.getUniqueId()) != null) {
               this.msg(player, "tpa-already-has-request", "&#37BFF8{target}&c already has an incoming teleport request from you. Please wait.", "{target}", target.getName());
               return true;
            } else {
               if (mgr.isConfirmGuiEnabled(player.getUniqueId())) {
                  TpaMenus.openSend(player, target, type);
               } else {
                  mgr.sendRequest(player, target, type);
               }

               return true;
            }
         }
      }
   }

   private void msg(CommandSender sender, String key, String def, String... replacements) {
      String text = this.plugin.getMessages().get(key, def);
      if (text == null) {
         text = def;
      }

      if (replacements != null) {
         for(int i = 0; i + 1 < replacements.length; i += 2) {
            if (replacements[i] != null && replacements[i + 1] != null) {
               text = text.replace(replacements[i], replacements[i + 1]);
            }
         }
      }

      sender.sendMessage(ColorUtil.parse(text));
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      String name = command.getName().toLowerCase(Locale.ROOT);
      if (args.length != 1 || !name.equals("tpa") && !name.equals("tpahere") && !name.equals("tpaccept") && !name.equals("tpadeny") && !name.equals("tpdeny") && !name.equals("tpaignore")) {
         return new ArrayList();
      } else {
         String p = args[0].toLowerCase(Locale.ROOT);
         return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((n) -> n.toLowerCase(Locale.ROOT).startsWith(p)).collect(Collectors.toList());
      }
   }
}
