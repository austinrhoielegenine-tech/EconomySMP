package com.austynzdevv.novacore.bounties.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.austynzdevv.novacore.bounties.EconomyBounties;
import com.austynzdevv.novacore.bounties.BountyManager;
import com.austynzdevv.novacore.bounties.gui.BountyMenus;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class BountyCommand implements CommandExecutor, TabCompleter {
   private final EconomyBounties plugin;

   public BountyCommand(EconomyBounties plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            if (!player.hasPermission("novacorecore.system.bounties.use")) {
               this.deny(player);
               return true;
            } else {
               BountyMenus.openMain(player, 0, BountyManager.Sort.HIGHEST);
               return true;
            }
         } else {
            sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-only", "&cYou must be a player to use this command!")));
            return true;
         }
      } else {
         String sub = args[0].toLowerCase(Locale.ROOT);
         if (sub.equals("reload")) {
            if (!sender.hasPermission("novacorecore.system.bounties.admin")) {
               this.deny(sender);
               return true;
            } else {
               this.plugin.reloadAll();
               sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("reload", "&aConfiguration reloaded successfully.")));
               return true;
            }
         } else if (sub.equals("add")) {
            if (sender instanceof Player) {
               Player player = (Player)sender;
               if (!player.hasPermission("novacorecore.system.bounties.add")) {
                  this.deny(player);
                  return true;
               } else if (args.length < 3) {
                  player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("usage-add", "&cUsage: /bounty add <player> <amount>")));
                  return true;
               } else {
                  OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                  if (target.getName() == null && !target.hasPlayedBefore() && !target.isOnline()) {
                     Player online = Bukkit.getPlayerExact(args[1]);
                     if (online == null) {
                        player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-not-found", "&cPlayer not found!")));
                        return true;
                     }

                     target = online;
                  }

                  if (target.getUniqueId().equals(player.getUniqueId())) {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("cannot-add-bounty-to-yourself", "&cYou cannot add a bounty to yourself!")));
                     return true;
                  } else {
                     double amount = this.parseAmount(args[2]);
                     if (amount <= (double)0.0F) {
                        player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("invalid-amount", "&cInvalid bounty amount!")));
                        return true;
                     } else {
                        double min = this.plugin.getConfig().getDouble("limits.min-amount", (double)10.0F);
                        double max = this.plugin.getConfig().getDouble("limits.max-amount", (double)1.0E9F);
                        if (amount < min) {
                           player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("amount-below-min", "&fMinimum amount is &#fb0000$%min_amount_format%.").replace("%min_amount_format%", BountyManager.formatMoney(min))));
                           return true;
                        } else if (amount > max) {
                           player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("amount-above-max", "&fMaximum amount is &#fb0000$%max_amount_format%.").replace("%max_amount_format%", BountyManager.formatMoney(max))));
                           return true;
                        } else {
                           BountyMenus.openConfirm(player, target, amount);
                           return true;
                        }
                     }
                  }
               }
            } else {
               sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-only", "&cYou must be a player to use this command!")));
               return true;
            }
         } else if (sub.equals("admin")) {
            if (!sender.hasPermission("novacorecore.system.bounties.admin")) {
               this.deny(sender);
               return true;
            } else if (sender instanceof Player) {
               Player player = (Player)sender;
               if (args.length < 2) {
                  player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("usage-admin", "&cUsage: /bounty admin <player>")));
                  return true;
               } else {
                  OfflinePlayer target = this.resolve(args[1]);
                  if (target == null) {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-not-found", "&cPlayer not found!")));
                     return true;
                  } else if (!this.plugin.getBounties().hasBounty(target.getUniqueId())) {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-bounty", "&cThat player has no bounty.")));
                     return true;
                  } else {
                     BountyMenus.openAdmin(player, target);
                     return true;
                  }
               }
            } else {
               sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-only", "&cYou must be a player to use this command!")));
               return true;
            }
         } else if (sender instanceof Player) {
            Player player = (Player)sender;
            if (!player.hasPermission("novacorecore.system.bounties.use")) {
               this.deny(player);
               return true;
            } else {
               OfflinePlayer target = this.resolve(args[0]);
               if (target == null) {
                  player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-not-found", "&cPlayer not found!")));
                  return true;
               } else {
                  double amount = this.plugin.getBounties().getAmount(target.getUniqueId());
                  String name = target.getName() != null ? target.getName() : args[0];
                  if (amount <= (double)0.0F) {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-no-bounty", "&f&#00a3fb%target_name% &fhas no bounty.").replace("%target_name%", name)));
                  } else {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-bounty", "&f&#00a3fb%target_name% &fhas a bounty of &#fb0000$%amount_format%&f.").replace("%target_name%", name).replace("%amount_format%", BountyManager.formatMoney(amount))));
                  }

                  return true;
               }
            }
         } else {
            sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-only", "&cYou must be a player to use this command!")));
            return true;
         }
      }
   }

   private OfflinePlayer resolve(String name) {
      Player online = Bukkit.getPlayerExact(name);
      if (online != null) {
         return online;
      } else {
         OfflinePlayer op = Bukkit.getOfflinePlayer(name);
         return op.getName() == null && !op.hasPlayedBefore() ? null : op;
      }
   }

   private double parseAmount(String raw) {
      try {
         raw = raw.trim().toLowerCase(Locale.ROOT).replace(",", "");
         double mult = (double)1.0F;
         if (raw.endsWith("k")) {
            mult = (double)1000.0F;
            raw = raw.substring(0, raw.length() - 1);
         } else if (raw.endsWith("m")) {
            mult = (double)1000000.0F;
            raw = raw.substring(0, raw.length() - 1);
         } else if (raw.endsWith("b")) {
            mult = (double)1.0E9F;
            raw = raw.substring(0, raw.length() - 1);
         }

         return Double.parseDouble(raw) * mult;
      } catch (Exception var4) {
         return (double)-1.0F;
      }
   }

   private void deny(CommandSender sender) {
      sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-permission", "&cYou do not have permission to use this command!")));
      if (sender instanceof Player player) {
         try {
            if (this.plugin.getConfig().getBoolean("sounds.no-perm.enabled", true)) {
               String s = this.plugin.getConfig().getString("sounds.no-perm.sound", "BLOCK_ANVIL_LAND");
               SoundUtil.play(player, player.getLocation(), Sound.valueOf(s.toUpperCase()), 1.0F, 1.0F);
            }
         } catch (Exception var4) {
         }
      }

   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (args.length == 1) {
         List<String> base = new ArrayList(List.of("add"));
         if (sender.hasPermission("novacorecore.system.bounties.admin")) {
            base.add("admin");
            base.add("reload");
         }

         String p = args[0].toLowerCase(Locale.ROOT);

         for(String s : base) {
            if (s.startsWith(p)) {
               out.add(s);
            }
         }

         out.addAll((Collection)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((n) -> n.toLowerCase(Locale.ROOT).startsWith(p)).collect(Collectors.toList()));
         return out;
      } else if (args.length != 2 || !args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("admin")) {
         return args.length == 3 && args[0].equalsIgnoreCase("add") ? List.of("100", "1000", "10k", "100k", "1m") : out;
      } else {
         String p = args[1].toLowerCase(Locale.ROOT);
         return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((n) -> n.toLowerCase(Locale.ROOT).startsWith(p)).collect(Collectors.toList());
      }
   }
}
