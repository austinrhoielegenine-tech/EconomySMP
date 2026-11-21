package me.xyr0.economysmpcore.baltop;

import java.util.UUID;
import me.xyr0.economysmpcore.economy.CoreEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class EconomyHook {
   public boolean setup() {
      return CoreEconomy.isReady();
   }

   public boolean isReady() {
      return CoreEconomy.isReady();
   }

   public double getBalance(OfflinePlayer player) {
      return CoreEconomy.balanceOf(player);
   }

   public double getBalance(Player player) {
      return CoreEconomy.balanceOf(player);
   }

   public double getBalance(UUID uuid) {
      return CoreEconomy.balanceOf(Bukkit.getOfflinePlayer(uuid));
   }

   public boolean has(Player player, double amount) {
      return CoreEconomy.hasFunds(player, amount);
   }

   public boolean withdraw(Player player, double amount) {
      return CoreEconomy.take(player, amount);
   }

   public boolean deposit(Player player, double amount) {
      return CoreEconomy.give(player, amount);
   }

   public boolean deposit(UUID uuid, double amount) {
      return CoreEconomy.give(uuid, amount);
   }

   public String format(double amount) {
      return CoreEconomy.formatMoney(amount);
   }
}
