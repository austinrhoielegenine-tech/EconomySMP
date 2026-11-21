package me.xyr0.economysmpcore.tools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import me.xyr0.economysmpcore.tools.AstralTools;
import me.xyr0.economysmpcore.tools.ToolDefinition;
import me.xyr0.economysmpcore.tools.gui.ToolsGui;
import me.xyr0.economysmpcore.tools.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToolsCommand implements CommandExecutor, TabCompleter {
   private final AstralTools plugin;

   public ToolsCommand(AstralTools plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.tools.admin")) {
         MessageUtil.send(sender, "no-permission");
         return true;
      } else if (args.length == 0) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            (new ToolsGui(this.plugin)).open(player);
         } else {
            MessageUtil.send(sender, "usage");
         }

         return true;
      } else {
         switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload":
               this.plugin.reloadAll();
               MessageUtil.send(sender, "reloaded");
               break;
            case "list":
               sender.sendMessage(this.plugin.getToolManager().listIds());
               break;
            case "add":
               if (!(sender instanceof Player)) {
                  MessageUtil.send(sender, "player-only");
                  return true;
               }

               Player player = (Player)sender;
               if (args.length < 2) {
                  sender.sendMessage("§cUsage: /tools add <toolname>");
                  return true;
               }

               ItemStack held = player.getInventory().getItemInMainHand();
               if (held == null || held.getType().isAir()) {
                  sender.sendMessage("§cHold an item first.");
                  return true;
               }

               ToolDefinition def = this.plugin.getToolManager().get(args[1]);
               if (def == null) {
                  MessageUtil.send(sender, "invalid-tool", "%tools%", this.plugin.getToolManager().listIds());
                  return true;
               }

               this.plugin.getToolFactory().applyDefinition(held, def);
               player.getInventory().setItemInMainHand(held);
               sender.sendMessage("§aApplied tool §f" + def.getId() + " §ato your held item.");
               break;
            case "give":
               if (args.length < 2) {
                  MessageUtil.send(sender, "usage");
                  return true;
               }

               int amount = 1;
               String toolId;
               Player target;
               if (args.length >= 3) {
                  Player maybe = Bukkit.getPlayerExact(args[1]);
                  if (maybe != null) {
                     target = maybe;
                     toolId = args[2];
                     if (args.length >= 4) {
                        try {
                           amount = Math.max(1, Math.min(64, Integer.parseInt(args[3])));
                        } catch (Exception var15) {
                        }
                     }
                  } else {
                     if (!(sender instanceof Player)) {
                        MessageUtil.send(sender, "player-only");
                        return true;
                     }

                     Player p = (Player)sender;
                     target = p;
                     toolId = args[1];

                     try {
                        amount = Math.max(1, Math.min(64, Integer.parseInt(args[2])));
                     } catch (Exception var14) {
                     }
                  }
               } else {
                  if (!(sender instanceof Player)) {
                     MessageUtil.send(sender, "player-only");
                     return true;
                  }

                  Player p = (Player)sender;
                  target = p;
                  toolId = args[1];
               }

               ToolDefinition def2 = this.plugin.getToolManager().get(toolId);
               if (def2 == null) {
                  MessageUtil.send(sender, "invalid-tool", "%tools%", this.plugin.getToolManager().listIds());
                  return true;
               }

               for(int i = 0; i < amount; ++i) {
                  ItemStack item = this.plugin.getToolFactory().create(def2);
                  target.getInventory().addItem(new ItemStack[]{item});
               }

               MessageUtil.send(sender, "tool-given", "%tool%", def2.getId(), "%amount%", String.valueOf(amount), "%player%", target.getName());
               if (!target.equals(sender)) {
                  MessageUtil.send(target, "tool-received", "%tool%", def2.getId());
               }
               break;
            case "help":
               MessageUtil.send(sender, "usage");
               break;
            default:
               if (sender instanceof Player) {
                  Player player2 = (Player)sender;
                  (new ToolsGui(this.plugin)).open(player2);
               } else {
                  MessageUtil.send(sender, "usage");
               }
         }

         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.tools.admin")) {
         return List.of();
      } else if (args.length == 1) {
         return this.filter(Arrays.asList("give", "add", "reload", "list", "help"), args[0]);
      } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
         return this.filter((List)this.plugin.getToolManager().all().stream().map(ToolDefinition::getId).collect(Collectors.toList()), args[1]);
      } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
         List<String> list = new ArrayList(this.plugin.getToolManager().all().stream().map(ToolDefinition::getId).toList());
         Bukkit.getOnlinePlayers().forEach((p) -> list.add(p.getName()));
         return this.filter(list, args[1]);
      } else {
         return args.length == 3 && args[0].equalsIgnoreCase("give") ? this.filter((List)this.plugin.getToolManager().all().stream().map(ToolDefinition::getId).collect(Collectors.toList()), args[2]) : List.of();
      }
   }

   private List<String> filter(List<String> options, String input) {
      String i = input.toLowerCase(Locale.ROOT);
      return (List)options.stream().filter((s) -> s.toLowerCase(Locale.ROOT).startsWith(i)).collect(Collectors.toList());
   }
}
