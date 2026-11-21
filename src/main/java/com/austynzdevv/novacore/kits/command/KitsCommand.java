package com.austynzdevv.novacore.kits.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.austynzdevv.novacore.kits.EconomyKits;
import com.austynzdevv.novacore.kits.Kit;
import com.austynzdevv.novacore.kits.util.MessageUtil;
import com.austynzdevv.novacore.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class KitsCommand implements CommandExecutor, TabCompleter {
   private final EconomyKits host;

   public KitsCommand(EconomyKits host) {
      this.host = host;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!this.host.isEnabled()) {
            MessageUtil.send(player, "disabled");
            return true;
         } else if (args.length == 0) {
            this.host.getGuiManager().openMain(player);
            return true;
         } else {
            String sub = args[0].toLowerCase(Locale.ROOT);
            if (sub.equals("crate")) {
               if (!player.hasPermission("novacorecore.system.kit.admin") && !player.isOp()) {
                  MessageUtil.send(player, "no-permission-kit");
                  return true;
               } else if (args.length < 5) {
                  player.sendMessage(TextUtil.color("&c/kit crate <kitname> <slot> <cooldown> <permission>"));
                  return true;
               } else {
                  try {
                     boolean made = this.host.getManager().create(args[1], Integer.parseInt(args[2]), Long.parseLong(args[3]), args[4]);
                     player.sendMessage(TextUtil.color(made ? "&aCreated kit &f" + args[1] : "&cKit already exists or name is invalid."));
                  } catch (NumberFormatException var8) {
                     player.sendMessage(TextUtil.color("&cSlot and cooldown must be numbers."));
                  }

                  return true;
               }
            } else if (sub.equals("delete")) {
               if (!player.hasPermission("novacorecore.system.kit.admin") && !player.isOp()) {
                  MessageUtil.send(player, "no-permission-kit");
                  return true;
               } else if (args.length < 2) {
                  player.sendMessage(TextUtil.color("&c/kit delete <kitname>"));
                  return true;
               } else {
                  player.sendMessage(TextUtil.color(this.host.getManager().delete(args[1]) ? "&aDeleted kit &f" + args[1] : "&cKit not found."));
                  return true;
               }
            } else if (sub.equals("edit")) {
               if (!player.hasPermission("novacorecore.system.kit.admin") && !player.isOp()) {
                  MessageUtil.send(player, "no-permission-kit");
                  return true;
               } else if (args.length < 2) {
                  player.sendMessage(TextUtil.color("&c/kit edit <kitname>"));
                  return true;
               } else {
                  Kit kit = this.host.getManager().get(args[1]);
                  if (kit == null) {
                     player.sendMessage(TextUtil.color("&cKit not found."));
                     return true;
                  } else {
                     this.host.getGuiManager().openEditor(player, kit);
                     return true;
                  }
               }
            } else {
               return sub.equals("claim") && args.length >= 2 ? this.claim(player, args[1]) : this.claim(player, sub);
            }
         }
      } else {
         MessageUtil.send(sender, "players-only");
         return true;
      }
   }

   private boolean claim(Player player, String id) {
      Kit kit = this.host.getManager().get(id);
      if (kit == null) {
         MessageUtil.send(player, "kit-not-found", "%kit%", id);
         return true;
      } else if (!kit.canClaim(player)) {
         MessageUtil.send(player, "no-permission-kit", "%kit%", kit.getDisplayName());
         return true;
      } else {
         long left = this.host.getManager().getRemaining(player, kit);
         if (left > 0L) {
            long secs = (left + 999L) / 1000L;
            String time = secs >= 3600L ? secs / 3600L + "h " + secs % 3600L / 60L + "m" : (secs >= 60L ? secs / 60L + "m " + secs % 60L + "s" : secs + "s");
            MessageUtil.send(player, "kit-cooldown", "%kit%", kit.getDisplayName(), "%time%", time);
            return true;
         } else {
            if (this.host.getManager().claim(player, id)) {
               MessageUtil.send(player, "kit-received", "%kit%", kit.getDisplayName());
            } else {
               MessageUtil.send(player, "kit-no-space");
            }

            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
         String p = args[0].toLowerCase(Locale.ROOT);
         List<String> out = new ArrayList();
         out.add("claim");
         out.add("crate");
         out.add("delete");
         out.add("edit");

         for(Kit k : this.host.getManager().all()) {
            if (k.getId().startsWith(p)) {
               out.add(k.getId());
            }
         }

         return (List)out.stream().filter((s) -> s.startsWith(p)).collect(Collectors.toList());
      } else if (args.length == 2 && args[0].equalsIgnoreCase("claim")) {
         String p = args[1].toLowerCase(Locale.ROOT);
         return (List)this.host.getManager().all().stream().map(Kit::getId).filter((id) -> id.startsWith(p)).collect(Collectors.toList());
      } else {
         return new ArrayList();
      }
   }
}
