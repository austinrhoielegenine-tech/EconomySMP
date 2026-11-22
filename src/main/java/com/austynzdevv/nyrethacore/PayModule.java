package com.austynzdevv.nyrethacore;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import com.austynzdevv.nyrethacore.economy.CoreEconomy;
import com.austynzdevv.nyrethacore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PayModule implements Module, Listener {
   private final NyrethaCore plugin;
   private FileConfiguration cfg;
   private final Set<UUID> payDisabled = new HashSet();
   private final Map<UUID, PendingPay> pending = new HashMap();

   public PayModule(NyrethaCore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "pay";
   }

   public void onEnable() {
      this.reload();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      CommandRegistry reg = this.plugin.commands();
      reg.registerHandler("pay", this::pay);
      reg.registerHandler("bal", this::bal);
      reg.registerHandler("money", this::bal);
      reg.registerHandler("balance", this::bal);
      reg.registerHandler("paytoggle", this::payToggle);
      reg.registerHandler("paymenttoggle", this::payToggle);
   }

   public void onDisable() {
      this.pending.clear();
   }

   public void reload() {
      File f = new File(this.plugin.getDataFolder(), "pay/config.yml");
      if (!f.exists()) {
         this.plugin.saveResource("pay/config.yml", false);
      }

      this.cfg = YamlConfiguration.loadConfiguration(f);
   }

   private boolean economyReady() {
      return CoreEconomy.isReady();
   }

   private boolean pay(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (!this.economyReady()) {
            p.sendMessage("§cEconomy is not ready.");
            return true;
         } else if (args.length < 2) {
            p.sendMessage(this.c("messages.usage"));
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(args[0]);
            if (t == null) {
               p.sendMessage(this.c("messages.player-not-found"));
               return true;
            } else if (t.getUniqueId().equals(p.getUniqueId())) {
               p.sendMessage(this.c("messages.self-pay"));
               return true;
            } else if (this.payDisabled.contains(t.getUniqueId())) {
               p.sendMessage(this.c("messages.pay-toggle-blocked").replace("%player%", t.getName()));
               return true;
            } else {
               double amount = parseMoney(args[1]);
               if (amount < (double)0.0F) {
                  p.sendMessage(this.c("messages.invalid-number"));
                  return true;
               } else if (!(amount < this.cfg.getDouble("min-pay", (double)1.0F)) && !(amount > this.cfg.getDouble("max-pay", (double)1.0E9F))) {
                  if (!CoreEconomy.hasFunds(p, amount)) {
                     p.sendMessage(this.c("messages.not-enough-money"));
                     return true;
                  } else if (this.cfg.getBoolean("confirm-gui-enabled", false)) {
                     this.pending.put(p.getUniqueId(), new PendingPay(t.getUniqueId(), amount));
                     this.openConfirm(p, t, amount);
                     return true;
                  } else {
                     this.doPay(p, t, amount);
                     return true;
                  }
               } else {
                  p.sendMessage(this.c("messages.invalid-number"));
                  return true;
               }
            }
         }
      } else {
         sender.sendMessage(this.c("messages.player-only"));
         return true;
      }
   }

   private void doPay(Player from, Player to, double amount) {
      if (!CoreEconomy.take(from, amount)) {
         from.sendMessage(this.c("messages.not-enough-money"));
      } else {
         CoreEconomy.give(to, amount);
         from.sendMessage(this.c("messages.send").replace("%amount%", CoreEconomy.formatMoney(amount)).replace("%player%", to.getName()));

         try {
            SettingsModule settings = SettingsModule.get();
            if (settings == null || !settings.isPaymentsOff(to.getUniqueId())) {
               to.sendMessage(this.c("messages.received").replace("%amount%", CoreEconomy.formatMoney(amount)).replace("%player%", from.getName()));
            }
         } catch (Exception var6) {
            to.sendMessage(this.c("messages.received").replace("%amount%", CoreEconomy.formatMoney(amount)).replace("%player%", from.getName()));
         }

      }
   }

   private void openConfirm(Player p, Player t, double amount) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, "Confirm Pay");
      ItemStack yes = new ItemStack(Material.LIME_WOOL);
      ItemMeta ym = yes.getItemMeta();
      ym.setDisplayName("§aConfirm");
      String var10001 = CoreEconomy.formatMoney(amount);
      ym.setLore(List.of("§7Pay §a" + var10001 + " §7to §f" + t.getName()));
      yes.setItemMeta(ym);
      ItemStack no = new ItemStack(Material.RED_WOOL);
      ItemMeta nm = no.getItemMeta();
      nm.setDisplayName("§cCancel");
      no.setItemMeta(nm);
      inv.setItem(11, yes);
      inv.setItem(15, no);
      p.openInventory(inv);
   }

   @EventHandler
   public void onClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player p) {
         if (e.getView().getTitle() != null && e.getView().getTitle().contains("Confirm Pay")) {
            e.setCancelled(true);
            PendingPay pp = (PendingPay)this.pending.remove(p.getUniqueId());
            if (pp == null) {
               p.closeInventory();
            } else {
               if (e.getRawSlot() == 11) {
                  Player t = Bukkit.getPlayer(pp.target);
                  if (t != null) {
                     this.doPay(p, t, pp.amount);
                  }

                  p.closeInventory();
               } else if (e.getRawSlot() == 15) {
                  p.closeInventory();
               }

            }
         }
      }
   }

   private boolean bal(CommandSender sender, String[] args) {
      if (!this.economyReady()) {
         sender.sendMessage("§cEconomy is not ready.");
         return true;
      } else if (args.length >= 1) {
         Player t = Bukkit.getPlayerExact(args[0]);
         if (t == null) {
            this.OfflineBal(sender, args[0]);
            return true;
         } else {
            sender.sendMessage(this.c("messages.balance-other").replace("%player%", t.getName()).replace("%balance%", CoreEconomy.formatMoney(CoreEconomy.balanceOf(t))));
            return true;
         }
      } else if (sender instanceof Player) {
         Player p = (Player)sender;
         p.sendMessage(this.c("messages.balance").replace("%balance%", CoreEconomy.formatMoney(CoreEconomy.balanceOf(p))));
         return true;
      } else {
         sender.sendMessage(this.c("messages.player-only"));
         return true;
      }
   }

   private void OfflineBal(CommandSender sender, String name) {
      OfflinePlayer op = Bukkit.getOfflinePlayer(name);
      sender.sendMessage(this.c("messages.balance-other").replace("%player%", name).replace("%balance%", CoreEconomy.formatMoney(CoreEconomy.balanceOf(op))));
   }

   private boolean payToggle(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.payDisabled.remove(p.getUniqueId())) {
            p.sendMessage(this.c("messages.pay-toggle-off"));
         } else {
            this.payDisabled.add(p.getUniqueId());
            p.sendMessage(this.c("messages.pay-toggle-on"));
         }

         return true;
      } else {
         return true;
      }
   }

   private String c(String path) {
      String s = this.cfg.getString(path);
      return s == null ? path : TextUtil.color(s);
   }

   public static double parseMoney(String raw) {
      if (raw == null) {
         return (double)-1.0F;
      } else {
         String s = raw.trim().toLowerCase(Locale.ROOT).replace(",", "").replace("$", "");
         if (s.isEmpty()) {
            return (double)-1.0F;
         } else {
            double mult = (double)1.0F;
            char last = s.charAt(s.length() - 1);
            if (last == 'k') {
               mult = (double)1000.0F;
               s = s.substring(0, s.length() - 1);
            } else if (last == 'm') {
               mult = (double)1000000.0F;
               s = s.substring(0, s.length() - 1);
            } else if (last == 'b') {
               mult = (double)1.0E9F;
               s = s.substring(0, s.length() - 1);
            } else if (last == 't') {
               mult = 1.0E12;
               s = s.substring(0, s.length() - 1);
            }

            try {
               return Double.parseDouble(s) * mult;
            } catch (Exception var6) {
               return (double)-1.0F;
            }
         }
      }
   }

   private static class PendingPay {
      final UUID target;
      final double amount;

      PendingPay(UUID target, double amount) {
         this.target = target;
         this.amount = amount;
      }
   }
}
