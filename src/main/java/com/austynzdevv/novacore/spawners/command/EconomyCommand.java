package com.austynzdevv.novacore.spawners.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.austynzdevv.novacore.spawners.EconomySpawners;
import com.austynzdevv.novacore.spawners.MessageConfig;
import com.austynzdevv.novacore.spawners.SpawnerData;
import com.austynzdevv.novacore.spawners.gui.SpawnerPanelGui;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EconomyCommand implements CommandExecutor, TabCompleter {
   private final EconomySpawners plugin;

   public EconomyCommand(EconomySpawners plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("novacore.system.spawners.admin")) {
         this.plugin.getMessages().send(sender, "no-permission");
         return true;
      } else if (args.length == 0) {
         this.sendHelp(sender);
         return true;
      } else {
         switch (args[0].toLowerCase()) {
            case "reload":
               this.plugin.reload();
               this.plugin.getMessages().send(sender, "reload-success");
               break;
            case "give":
               this.handleGive(sender, args);
               break;
            case "list":
               this.handleList(sender);
               break;
            case "info":
               this.handleInfo(sender);
               break;
            case "panel":
               this.handlePanel(sender);
               break;
            case "convert":
               this.handleConvert(sender, args);
               break;
            case "help":
               this.sendHelp(sender);
               break;
            default:
               this.plugin.getMessages().send(sender, "unknown-command");
         }

         return true;
      }
   }

   private void handleGive(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage("§cUsage: /as give <player> <entity> [amount]");
      } else {
         Player target = Bukkit.getPlayer(args[1]);
         if (target == null) {
            this.plugin.getMessages().send(sender, "player-not-found");
         } else {
            EntityType type;
            try {
               type = EntityType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException var8) {
               this.plugin.getMessages().send(sender, "invalid-entity", Map.of("entity", args[2]));
               return;
            }

            if (!this.plugin.getEntities().isSupported(type)) {
               this.plugin.getMessages().send(sender, "invalid-entity", Map.of("entity", args[2]));
            } else {
               int amount = 1;
               if (args.length >= 4) {
                  try {
                     amount = Integer.parseInt(args[3]);
                     if (amount < 1) {
                        throw new NumberFormatException();
                     }
                  } catch (NumberFormatException var7) {
                     this.plugin.getMessages().send(sender, "invalid-amount");
                     return;
                  }
               }

               this.plugin.getSpawnerManager().giveTo(target, type, amount);
               this.plugin.getMessages().send(sender, "give-success", Map.of("amount", String.valueOf(amount), "entity", type.name(), "player", target.getName()));
               this.plugin.getMessages().send(target, "receive-spawner", Map.of("amount", String.valueOf(amount), "entity", type.name()));
            }
         }
      }
   }

   private void handleList(CommandSender sender) {
      Collection<SpawnerData> all = this.plugin.getSpawnerManager().getAll();
      if (all.isEmpty()) {
         this.plugin.getMessages().send(sender, "list-empty");
      } else {
         this.plugin.getMessages().send(sender, "list-header", Map.of("count", String.valueOf(all.size())));

         for(SpawnerData data : all) {
            this.plugin.getMessages().send(sender, "list-entry", Map.of("entity", data.getEntityType().name(), "stack", String.valueOf(data.getStackSize()), "x", String.valueOf(data.getX()), "y", String.valueOf(data.getY()), "z", String.valueOf(data.getZ()), "world", data.getWorldName()));
         }

      }
   }

   private void handleInfo(CommandSender sender) {
      if (sender instanceof Player player) {
         SpawnerData data = this.plugin.getSpawnerManager().getAt(player.getTargetBlockExact(5) != null ? player.getTargetBlockExact(5).getLocation() : null);
         if (data == null) {
            sender.sendMessage("§cLook at a spawner.");
         } else {
            this.plugin.getMessages().send(sender, "info-header");
            this.plugin.getMessages().send(sender, "info-line", Map.of("key", "Type", "value", data.getEntityType().name()));
            this.plugin.getMessages().send(sender, "info-line", Map.of("key", "Stack", "value", String.valueOf(data.getStackSize())));
            this.plugin.getMessages().send(sender, "info-line", Map.of("key", "Items", "value", String.valueOf(data.getTotalItems())));
            this.plugin.getMessages().send(sender, "info-line", Map.of("key", "XP", "value", String.valueOf(data.getStoredExp())));
            MessageConfig var10000 = this.plugin.getMessages();
            int var10006 = data.getX();
            var10000.send(sender, "info-line", Map.of("key", "Location", "value", var10006 + " " + data.getY() + " " + data.getZ() + " (" + data.getWorldName() + ")"));
         }
      } else {
         this.plugin.getMessages().send(sender, "player-only");
      }
   }

   private void handleConvert(CommandSender sender, String[] args) {
      if (args.length == 1) {
         boolean on = this.plugin.getSettings().convertNaturalSpawners();
         sender.sendMessage("§7convert-natural-spawners: " + (on ? "§atrue" : "§cfalse"));
         sender.sendMessage("§7Usage: §e/as convert <true|false|toggle>");
      } else {
         String arg = args[1].toLowerCase();
         boolean value;
         if (arg.equals("toggle")) {
            value = !this.plugin.getSettings().convertNaturalSpawners();
         } else if (!arg.equals("true") && !arg.equals("on") && !arg.equals("enable")) {
            if (!arg.equals("false") && !arg.equals("off") && !arg.equals("disable")) {
               sender.sendMessage("§cUsage: /as convert <true|false|toggle>");
               return;
            }

            value = false;
         } else {
            value = true;
         }

         this.plugin.getSettings().setConvertNaturalSpawners(value);
         sender.sendMessage("§7convert-natural-spawners set to " + (value ? "§atrue" : "§cfalse"));
      }
   }

   private void handlePanel(CommandSender sender) {
      if (sender instanceof Player player) {
         SpawnerPanelGui.openMain(player);
      } else {
         this.plugin.getMessages().send(sender, "player-only");
      }
   }

   private void sendHelp(CommandSender sender) {
      sender.sendMessage("§b§lEconomySpawners §7commands:");
      sender.sendMessage("§e/as give <player> <entity> [amount] §7- Give spawners");
      sender.sendMessage("§e/as list §7- List all spawners");
      sender.sendMessage("§e/as info §7- Info about looked-at spawner");
      sender.sendMessage("§e/as panel §7- Open spawner panel");
      sender.sendMessage("§e/as convert <true|false|toggle> §7- Natural dungeon spawners");
      sender.sendMessage("§e/as reload §7- Reload configuration");
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (!sender.hasPermission("novacore.system.spawners.admin")) {
         return List.of();
      } else if (args.length == 1) {
         return this.filter(List.of("give", "list", "info", "panel", "convert", "reload", "help"), args[0]);
      } else if (args.length == 2 && args[0].equalsIgnoreCase("convert")) {
         return this.filter(List.of("true", "false", "toggle"), args[1]);
      } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
         return this.filter((List)Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
      } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
         return this.filter((List)this.plugin.getEntities().all().keySet().stream().map(Enum::name).collect(Collectors.toList()), args[2]);
      } else {
         return args.length == 4 && args[0].equalsIgnoreCase("give") ? this.filter(List.of("1", "8", "16", "32", "64"), args[3]) : List.of();
      }
   }

   private List<String> filter(List<String> options, String input) {
      String lower = input.toLowerCase();
      List<String> result = new ArrayList();

      for(String o : options) {
         if (o.toLowerCase().startsWith(lower)) {
            result.add(o);
         }
      }

      return result;
   }
}
