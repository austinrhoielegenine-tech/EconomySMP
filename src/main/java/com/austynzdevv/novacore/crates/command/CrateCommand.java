package com.austynzdevv.novacore.crates.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.austynzdevv.novacore.crates.EconomyCrates;
import com.austynzdevv.novacore.crates.Crate;
import com.austynzdevv.novacore.crates.DataManager;
import com.austynzdevv.novacore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CrateCommand implements CommandExecutor, TabCompleter {
   private final EconomyCrates plugin;

   public CrateCommand(EconomyCrates plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length != 0 && !args[0].equalsIgnoreCase("help")) {
         String sub = args[0].toLowerCase(Locale.ROOT);
         boolean admin = sender.hasPermission("novacorecore.system.crates.admin");
         switch (sub) {
            case "reload":
               if (!admin) {
                  this.plugin.getMessages().send(sender, "no-permission", "&cYou don't have permission!");
                  return true;
               }

               this.plugin.reloadAll();
               this.plugin.getMessages().send(sender, "reloaded", "&aEconomyCrates reloaded!");
               return true;
            case "list":
               String list = (String)this.plugin.getCrates().map().keySet().stream().sorted().collect(Collectors.joining(", "));
               sender.sendMessage(ColorUtil.parse("&7Crates: &#7afc00" + (list.isEmpty() ? "none" : list)));
               return true;
            case "givekey":
               if (!admin) {
                  this.plugin.getMessages().send(sender, "no-permission", "&cYou don't have permission!");
                  return true;
               } else if (args.length < 5) {
                  this.plugin.getMessages().send(sender, "invalid-usage", "&cUsage: %usage%", "%usage%", "/" + label + " givekey <player> <crate> <physical|virtual> <amount>");
                  return true;
               } else {
                  Player target = Bukkit.getPlayerExact(args[1]);
                  if (target == null) {
                     this.plugin.getMessages().send(sender, "player-not-found", "&cPlayer not found!");
                     return true;
                  } else {
                     Crate crate = this.plugin.getCrates().get(args[2]);
                     if (crate == null) {
                        this.plugin.getMessages().send(sender, "crate-not-found", "&cCrate %crate% does not exist!", "%crate%", args[2]);
                        return true;
                     } else {
                        int amount;
                        try {
                           amount = Integer.parseInt(args[4]);
                        } catch (Exception var15) {
                           this.plugin.getMessages().send(sender, "invalid-amount", "&cInvalid amount!");
                           return true;
                        }

                        if (!args[3].equalsIgnoreCase("virtual") && !args[3].equalsIgnoreCase("virtuell")) {
                           target.getInventory().addItem(new ItemStack[]{this.plugin.getKeys().createPhysical(crate.getId(), amount)});
                        } else {
                           this.plugin.getData().addVirtual(target.getUniqueId(), crate.getId(), amount);
                        }

                        this.plugin.getMessages().send(target, "received-keys", "&fYou received &#7afc00%amount%x %crate% &fkeys!", "%amount%", String.valueOf(amount), "%crate%", crate.getId());
                        this.plugin.getMessages().send(sender, "given-keys", "&#7afc00%amount%x %crate% &fkeys given to %player%.", "%amount%", String.valueOf(amount), "%crate%", crate.getId(), "%player%", target.getName());
                        return true;
                     }
                  }
               }
            case "takekey":
               if (!admin) {
                  this.plugin.getMessages().send(sender, "no-permission", "&cYou don't have permission!");
                  return true;
               } else if (args.length < 5) {
                  this.plugin.getMessages().send(sender, "invalid-usage", "&cUsage: %usage%", "%usage%", "/" + label + " takekey <player> <crate> <physical|virtual> <amount>");
                  return true;
               } else {
                  Player target = Bukkit.getPlayerExact(args[1]);
                  if (target == null) {
                     this.plugin.getMessages().send(sender, "player-not-found", "&cPlayer not found!");
                     return true;
                  } else {
                     Crate crate = this.plugin.getCrates().get(args[2]);
                     if (crate == null) {
                        this.plugin.getMessages().send(sender, "crate-not-found", "&cCrate %crate% does not exist!", "%crate%", args[2]);
                        return true;
                     } else {
                        int amount;
                        try {
                           amount = Integer.parseInt(args[4]);
                        } catch (Exception var14) {
                           this.plugin.getMessages().send(sender, "invalid-amount", "&cInvalid amount!");
                           return true;
                        }

                        if (!args[3].equalsIgnoreCase("virtual") && !args[3].equalsIgnoreCase("virtuell")) {
                           this.plugin.getKeys().takePhysical(target, crate.getId(), amount);
                        } else {
                           int cur = this.plugin.getData().getVirtual(target.getUniqueId(), crate.getId());
                           this.plugin.getData().setVirtual(target.getUniqueId(), crate.getId(), Math.max(0, cur - amount));
                        }

                        sender.sendMessage(ColorUtil.parse("&aTook " + amount + " " + crate.getId() + " keys from " + target.getName()));
                        return true;
                     }
                  }
               }
            case "keyall":
               if (!admin) {
                  this.plugin.getMessages().send(sender, "no-permission", "&cYou don't have permission!");
                  return true;
               } else if (args.length < 4) {
                  this.plugin.getMessages().send(sender, "invalid-usage", "&cUsage: %usage%", "%usage%", "/" + label + " keyall <crate> <physical|virtual> <amount>");
                  return true;
               } else {
                  Crate crate = this.plugin.getCrates().get(args[1]);
                  if (crate == null) {
                     this.plugin.getMessages().send(sender, "crate-not-found", "&cCrate %crate% does not exist!", "%crate%", args[1]);
                     return true;
                  } else {
                     int amount;
                     try {
                        amount = Integer.parseInt(args[3]);
                     } catch (Exception var13) {
                        this.plugin.getMessages().send(sender, "invalid-amount", "&cInvalid amount!");
                        return true;
                     }

                     for(Player p : Bukkit.getOnlinePlayers()) {
                        if (!args[2].equalsIgnoreCase("virtual") && !args[2].equalsIgnoreCase("virtuell")) {
                           p.getInventory().addItem(new ItemStack[]{this.plugin.getKeys().createPhysical(crate.getId(), amount)});
                        } else {
                           this.plugin.getData().addVirtual(p.getUniqueId(), crate.getId(), amount);
                        }
                     }

                     sender.sendMessage(ColorUtil.parse("&aGave " + amount + "x " + crate.getId() + " keys to all online players."));
                     return true;
                  }
               }
            case "bind":
               if (sender instanceof Player) {
                  Player player = (Player)sender;
                  if (!admin) {
                     this.plugin.getMessages().send(sender, "no-permission", "&cYou don't have permission!");
                     return true;
                  }

                  if (args.length < 2) {
                     this.plugin.getMessages().send(sender, "invalid-usage", "&cUsage: %usage%", "%usage%", "/" + label + " bind <crate>");
                     return true;
                  }

                  Crate crate = this.plugin.getCrates().get(args[1]);
                  if (crate == null) {
                     this.plugin.getMessages().send(sender, "crate-not-found", "&cCrate %crate% does not exist!", "%crate%", args[1]);
                     return true;
                  }

                  Block block = player.getTargetBlockExact(6);
                  if (block != null && !block.getType().isAir()) {
                     this.plugin.getData().bind(block.getLocation(), crate.getId());
                     String locKey = DataManager.locKey(block.getLocation());
                     if (locKey != null && this.plugin.getHolograms() != null) {
                        this.plugin.getHolograms().spawnAt(locKey, crate.getId());
                     }

                     this.plugin.getMessages().send(player, "bind", "&aCrate bound to the block you are looking at!", "%crate%", crate.getId());
                     return true;
                  }

                  this.plugin.getMessages().send(player, "no-block", "&cYou must be looking at a block!");
                  return true;
               }

               this.plugin.getMessages().send(sender, "player-only", "&cOnly players can use this!");
               return true;
            case "unbind":
               if (sender instanceof Player) {
                  Player player = (Player)sender;
                  if (!admin) {
                     this.plugin.getMessages().send(sender, "no-permission", "&cYou don't have permission!");
                     return true;
                  }

                  Block block = player.getTargetBlockExact(6);
                  if (block != null && !block.getType().isAir()) {
                     String locKey = DataManager.locKey(block.getLocation());
                     if (!this.plugin.getData().unbind(block.getLocation())) {
                        this.plugin.getMessages().send(player, "no-crate-bind", "&cNo crate is bound to this block!");
                        return true;
                     }

                     if (locKey != null && this.plugin.getHolograms() != null) {
                        this.plugin.getHolograms().removeAt(locKey);
                     }

                     this.plugin.getMessages().send(player, "unbind", "&aCrate unbound from that block!");
                     return true;
                  }

                  this.plugin.getMessages().send(player, "no-block", "&cYou must be looking at a block!");
                  return true;
               }

               this.plugin.getMessages().send(sender, "player-only", "&cOnly players can use this!");
               return true;
            case "seteffect":
               if (!admin) {
                  this.plugin.getMessages().send(sender, "no-permission", "&cYou don't have permission!");
                  return true;
               } else if (args.length < 3) {
                  this.plugin.getMessages().send(sender, "invalid-usage", "&cUsage: %usage%", "%usage%", "/" + label + " seteffect <crate> <CHOOSE|NOWAIT|ROLLING>");
                  return true;
               } else {
                  Crate crate = this.plugin.getCrates().get(args[1]);
                  if (crate == null) {
                     this.plugin.getMessages().send(sender, "crate-not-found", "&cCrate %crate% does not exist!", "%crate%", args[1]);
                     return true;
                  } else {
                     String effect = args[2].toUpperCase(Locale.ROOT);
                     if (!effect.equals("CHOOSE") && !effect.equals("NOWAIT") && !effect.equals("ROLLING")) {
                        this.plugin.getMessages().send(sender, "invalid-effect", "&cInvalid effect!");
                        return true;
                     }

                     crate.setEffect(effect);
                     this.plugin.getMessages().send(sender, "effect-set", "&fEffect &#7afc00%effect% &fset for crate &#7afc00%crate%&f!", "%effect%", effect, "%crate%", crate.getId());
                     return true;
                  }
               }
            default:
               if (sender instanceof Player) {
                  Player p = (Player)sender;
                  this.plugin.getMessages().sendHelp(p);
               }

               return true;
         }
      } else {
         if (sender instanceof Player) {
            Player p = (Player)sender;
            this.plugin.getMessages().sendHelp(p);
         } else {
            sender.sendMessage("/crates help");
         }

         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (args.length == 1) {
         for(String s : List.of("help", "reload", "list", "givekey", "takekey", "keyall", "bind", "unbind", "seteffect")) {
            if (s.startsWith(args[0].toLowerCase(Locale.ROOT))) {
               out.add(s);
            }
         }
      } else if (args.length == 2) {
         if (List.of("bind", "seteffect", "keyall", "givekey", "takekey").contains(args[0].toLowerCase(Locale.ROOT))) {
            if (!args[0].equalsIgnoreCase("givekey") && !args[0].equalsIgnoreCase("takekey")) {
               out.addAll(this.plugin.getCrates().map().keySet());
            } else {
               out.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            }
         }
      } else if (args.length == 3) {
         if (!args[0].equalsIgnoreCase("givekey") && !args[0].equalsIgnoreCase("takekey")) {
            if (args[0].equalsIgnoreCase("keyall")) {
               out.addAll(List.of("physical", "virtual"));
            } else if (args[0].equalsIgnoreCase("seteffect")) {
               out.addAll(List.of("CHOOSE", "NOWAIT", "ROLLING"));
            }
         } else {
            out.addAll(this.plugin.getCrates().map().keySet());
         }
      } else if (args.length == 4 && (args[0].equalsIgnoreCase("givekey") || args[0].equalsIgnoreCase("takekey"))) {
         out.addAll(List.of("physical", "virtual"));
      }

      return (List)out.stream().filter((sx) -> sx.toLowerCase(Locale.ROOT).startsWith(args[args.length - 1].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
   }
}
