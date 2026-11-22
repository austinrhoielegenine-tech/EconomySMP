package com.austynzdevv.nyrethacore.shards.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.austynzdevv.nyrethacore.shards.EconomyShards;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ShardsCommand implements CommandExecutor, TabCompleter {
   private final EconomyShards plugin;

   public ShardsCommand(EconomyShards plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            if (!sender.hasPermission("nyrethacorecore.system.shards.use")) {
               this.send(sender, "no-permission", "&cYou have no permission for that!");
               return true;
            } else {
               long bal = this.plugin.getShards().getBalance(player);
               String msg = this.plugin.getMessages().withPrefix("shards-balance", "&fBalance: &#A303F9%shards% shards").replace("%shards%", String.valueOf(bal)).replace("%player%", player.getName());
               sender.sendMessage(ColorUtil.parse(msg));
               return true;
            }
         } else {
            this.send(sender, "player-only", "Only players can use this command!");
            return true;
         }
      } else {
         String sub = args[0].toLowerCase(Locale.ROOT);
         if (!sub.equals("givebooster") && !sub.equals("booster")) {
            if (sub.equals("reload")) {
               if (!sender.hasPermission("nyrethacorecore.system.shards.admin")) {
                  this.send(sender, "no-permission", "&cYou have no permission for that!");
                  return true;
               } else {
                  this.plugin.reloadAll();
                  this.send(sender, "reload-success", "&#00FF00Shards reloaded success!");
                  return true;
               }
            } else if (sub.equals("pay")) {
               if (!this.plugin.getConfig().getBoolean("pay-enabled", true)) {
                  this.send(sender, "pay-disabled", "&cPay is disabled!");
                  return true;
               } else if (sender instanceof Player) {
                  Player player = (Player)sender;
                  if (!sender.hasPermission("nyrethacorecore.system.shards.use")) {
                     this.send(sender, "no-permission", "&cYou have no permission for that!");
                     return true;
                  } else if (args.length < 3) {
                     sender.sendMessage(ColorUtil.parse("&cUsage: /shards pay <player> <amount>"));
                     return true;
                  } else {
                     Player target = Bukkit.getPlayerExact(args[1]);
                     if (target == null) {
                        this.send(sender, "player-not-found", "&cPlayer not found");
                        return true;
                     } else if (target.getUniqueId().equals(player.getUniqueId())) {
                        this.send(sender, "self-pay", "&cYou can´t give ur self shards!");
                        return true;
                     } else {
                        long amount = this.parseAmount(args[2]);
                        if (amount <= 0L) {
                           this.send(sender, "invalid-number", "&cInvaild number!");
                           return true;
                        } else if (!this.plugin.getShards().take(player, amount)) {
                           this.send(sender, "not-enough-shards", "&cYou have not enough shards!");
                           return true;
                        } else {
                           this.plugin.getShards().add(target, amount);
                           String sent = this.plugin.getMessages().withPrefix("shards-send", "&fYou sent &#A303F9%shards_send% shards &fto &#00FF00%player%").replace("%shards_send%", String.valueOf(amount)).replace("%player%", target.getName());
                           String received = this.plugin.getMessages().withPrefix("shards-received", "&fYou received &#A303F9%shards_received% shards &ffrom &#00FF00%target_player%").replace("%shards_received%", String.valueOf(amount)).replace("%target_player%", player.getName());
                           player.sendMessage(ColorUtil.parse(sent));
                           target.sendMessage(ColorUtil.parse(received));
                           return true;
                        }
                     }
                  }
               } else {
                  this.send(sender, "player-only", "Only players can use this command!");
                  return true;
               }
            } else if (sub.equals("give")) {
               if (!sender.hasPermission("nyrethacorecore.system.shards.admin")) {
                  this.send(sender, "no-permission", "&cYou have no permission for that!");
                  return true;
               } else if (args.length < 3) {
                  this.sendUsage(sender, true);
                  return true;
               } else {
                  Player target = Bukkit.getPlayerExact(args[1]);
                  if (target == null) {
                     this.send(sender, "player-not-found", "&cPlayer not found");
                     return true;
                  } else {
                     long amount = this.parseAmount(args[2]);
                     if (amount <= 0L) {
                        this.send(sender, "invalid-number", "&cInvaild number!");
                        return true;
                     } else {
                        this.plugin.getShards().add(target, amount);
                        String msg = this.plugin.getMessages().withPrefix("shards-given", "&fYou gave &#A303F9%shards_given% shards &fto &#00FF00%player%!").replace("%shards_given%", String.valueOf(amount)).replace("%player%", target.getName());
                        sender.sendMessage(ColorUtil.parse(msg));
                        return true;
                     }
                  }
               }
            } else if (sub.equals("take")) {
               if (!sender.hasPermission("nyrethacorecore.system.shards.admin")) {
                  this.send(sender, "no-permission", "&cYou have no permission for that!");
                  return true;
               } else if (args.length < 3) {
                  this.sendUsage(sender, true);
                  return true;
               } else {
                  Player target = Bukkit.getPlayerExact(args[1]);
                  if (target == null) {
                     this.send(sender, "player-not-found", "&cPlayer not found");
                     return true;
                  } else {
                     long amount = this.parseAmount(args[2]);
                     if (amount <= 0L) {
                        this.send(sender, "invalid-number", "&cInvaild number!");
                        return true;
                     } else if (!this.plugin.getShards().take(target, amount)) {
                        this.send(sender, "not-enough-shards", "&cYou have not enough shards!");
                        return true;
                     } else {
                        String msg = this.plugin.getMessages().withPrefix("shards-taken", "&fYou took &#A303F9%shards_taken% shards &ffrom &#00FF00%player%!").replace("%shards_taken%", String.valueOf(amount)).replace("%player%", target.getName());
                        sender.sendMessage(ColorUtil.parse(msg));
                        return true;
                     }
                  }
               }
            } else if (sub.equals("set")) {
               if (!sender.hasPermission("nyrethacorecore.system.shards.admin")) {
                  this.send(sender, "no-permission", "&cYou have no permission for that!");
                  return true;
               } else if (args.length < 3) {
                  this.sendUsage(sender, true);
                  return true;
               } else {
                  Player target = Bukkit.getPlayerExact(args[1]);
                  if (target == null) {
                     this.send(sender, "player-not-found", "&cPlayer not found");
                     return true;
                  } else {
                     long amount = this.parseAmount(args[2]);
                     if (amount < 0L) {
                        this.send(sender, "invalid-number", "&cInvaild number!");
                        return true;
                     } else {
                        this.plugin.getShards().setBalance(target, amount);
                        String msg = this.plugin.getMessages().withPrefix("shards-set", "&fYou set the shards of &#00FF00%player% &fto &#A303F9%shards_set%&f!").replace("%shards_set%", String.valueOf(amount)).replace("%player%", target.getName());
                        sender.sendMessage(ColorUtil.parse(msg));
                        return true;
                     }
                  }
               }
            } else if (!sub.equals("bal") && !sub.equals("balance")) {
               if (args.length == 1) {
                  if (!sender.hasPermission("nyrethacorecore.system.shards.use") && !sender.hasPermission("nyrethacorecore.system.shards.admin")) {
                     if (sender instanceof Player) {
                        Player player = (Player)sender;
                        if (sender.hasPermission("nyrethacorecore.system.shards.use")) {
                           long bal = this.plugin.getShards().getBalance(player);
                           String msg = this.plugin.getMessages().withPrefix("shards-balance", "&fBalance: &#A303F9%shards% shards").replace("%shards%", String.valueOf(bal));
                           sender.sendMessage(ColorUtil.parse(msg));
                           return true;
                        }
                     }

                     this.send(sender, "no-permission", "&cYou have no permission for that!");
                     return true;
                  } else {
                     Player target = Bukkit.getPlayerExact(args[0]);
                     if (target == null) {
                        this.send(sender, "player-not-found", "&cPlayer not found");
                        return true;
                     } else {
                        long bal = this.plugin.getShards().getBalance(target);
                        String msg = this.plugin.getMessages().withPrefix("shards-balance-other", "&#00FF00%target% &fhas &#A303F9%shards% shards&f!").replace("%target%", target.getName()).replace("%shards%", String.valueOf(bal));
                        sender.sendMessage(ColorUtil.parse(msg));
                        return true;
                     }
                  }
               } else {
                  this.sendUsage(sender, sender.hasPermission("nyrethacorecore.system.shards.admin"));
                  return true;
               }
            } else if (!sender.hasPermission("nyrethacorecore.system.shards.use")) {
               this.send(sender, "no-permission", "&cYou have no permission for that!");
               return true;
            } else if (args.length >= 2) {
               if (!sender.hasPermission("nyrethacorecore.system.shards.use") && !sender.hasPermission("nyrethacorecore.system.shards.admin")) {
                  this.send(sender, "no-permission", "&cYou have no permission for that!");
                  return true;
               } else {
                  Player target = Bukkit.getPlayerExact(args[1]);
                  if (target == null) {
                     this.send(sender, "player-not-found", "&cPlayer not found");
                     return true;
                  } else {
                     long bal = this.plugin.getShards().getBalance(target);
                     String msg = this.plugin.getMessages().withPrefix("shards-balance-other", "&#00FF00%target% &fhas &#A303F9%shards% shards&f!").replace("%target%", target.getName()).replace("%shards%", String.valueOf(bal));
                     sender.sendMessage(ColorUtil.parse(msg));
                     return true;
                  }
               }
            } else if (sender instanceof Player) {
               Player player = (Player)sender;
               long amount = this.plugin.getShards().getBalance(player);
               String msg = this.plugin.getMessages().withPrefix("shards-balance", "&fBalance: &#A303F9%shards% shards").replace("%shards%", String.valueOf(amount)).replace("%player%", player.getName());
               sender.sendMessage(ColorUtil.parse(msg));
               return true;
            } else {
               this.send(sender, "player-only", "Only players can use this command!");
               return true;
            }
         } else if (!sender.hasPermission("nyrethacorecore.system.shards.admin")) {
            this.send(sender, "no-permission", "&cYou have no permission for that!");
            return true;
         } else {
            Player target;
            if (args.length < 2) {
               if (!(sender instanceof Player)) {
                  this.send(sender, "player-only", "Only players can use this command!");
                  return true;
               }

               Player player = (Player)sender;
               target = player;
            } else {
               target = Bukkit.getPlayerExact(args[1]);
               if (target == null) {
                  this.send(sender, "player-not-found", "&cPlayer not found");
                  return true;
               }
            }

            int amount = 1;
            if (args.length >= 3) {
               try {
                  amount = Math.max(1, Integer.parseInt(args[2]));
               } catch (Exception var12) {
                  amount = 1;
               }
            }

            for(int i = 0; i < amount; ++i) {
               target.getInventory().addItem(new ItemStack[]{this.plugin.getBoosterFactory().create()});
            }

            String msg = this.plugin.getMessages().withPrefix("booster-given", "&fGave &#A20AD6%amount%x Shard Booster &fto &#00FF00%player%!").replace("%amount%", String.valueOf(amount)).replace("%player%", target.getName());
            sender.sendMessage(ColorUtil.parse(msg));
            if (!target.equals(sender)) {
               String got = this.plugin.getMessages().withPrefix("booster-received", "&fYou received &#A20AD6%amount%x Shard Booster&f!").replace("%amount%", String.valueOf(amount));
               target.sendMessage(ColorUtil.parse(got));
            }

            return true;
         }
      }
   }

   private void sendUsage(CommandSender sender, boolean admin) {
      String key = admin ? "command-usage-admin" : "command-usage";
      String def = admin ? "&#FF0000&lSHARDS ADMIN\n&#FF0000➤ &f/shards reload\n&#FF0000➤ &f/shards give <player> <amount>\n&#FF0000➤ &f/shards take <player> <amount>\n&#FF0000➤ &f/shards set <player> <amount>" : "&#A303F9&lSHARDS\n&#A303F9➤ &f/shards\n&#A303F9➤ &f/shards pay <player> <amount>\n&#A303F9➤ &f/shards bal <player>";
      String msg = this.plugin.getMessages().get(key, def);

      for(String line : msg.split("\n")) {
         sender.sendMessage(ColorUtil.parse(line));
      }

   }

   private void send(CommandSender sender, String key, String def) {
      sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().withPrefix(key, def)));
   }

   private long parseAmount(String raw) {
      try {
         return Long.parseLong(raw.replace(",", "").trim());
      } catch (NumberFormatException var3) {
         return -1L;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (args.length == 1) {
         List<String> base = new ArrayList();
         if (sender.hasPermission("nyrethacorecore.system.shards.use")) {
            base.add("bal");
            base.add("balance");
         }

         if (this.plugin.getConfig().getBoolean("pay-enabled", true) && sender.hasPermission("nyrethacorecore.system.shards.use")) {
            base.add("pay");
         }

         if (sender.hasPermission("nyrethacorecore.system.shards.admin")) {
            base.addAll(Arrays.asList("give", "givebooster", "booster", "take", "set", "reload"));
         }

         String partial = args[0].toLowerCase(Locale.ROOT);

         for(String s : base) {
            if (s.startsWith(partial)) {
               out.add(s);
            }
         }

         if (sender.hasPermission("nyrethacorecore.system.shards.use") || sender.hasPermission("nyrethacorecore.system.shards.admin")) {
            for(Player p : Bukkit.getOnlinePlayers()) {
               if (p.getName().toLowerCase(Locale.ROOT).startsWith(partial)) {
                  out.add(p.getName());
               }
            }
         }

         return out;
      } else {
         if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            if (sub.equals("pay") || sub.equals("give") || sub.equals("take") || sub.equals("set") || sub.equals("bal") || sub.equals("balance")) {
               return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((n) -> n.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
            }
         }

         if (args.length == 3) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            if (sub.equals("pay") || sub.equals("give") || sub.equals("take") || sub.equals("set")) {
               return Arrays.asList("1", "10", "100", "1000");
            }
         }

         return out;
      }
   }
}
