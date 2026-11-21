package me.xyr0.economysmpcore.economy;

import java.util.Collections;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class VaultEconomyBridge implements Economy {
   private final CoreEconomy core;

   private VaultEconomyBridge(CoreEconomy core) {
      this.core = core;
   }

   public static void register(JavaPlugin plugin, CoreEconomy core) {
      VaultEconomyBridge bridge = new VaultEconomyBridge(core);
      Bukkit.getServicesManager().register(Economy.class, bridge, plugin, ServicePriority.Normal);
   }

   public boolean isEnabled() {
      return true;
   }

   public String getName() {
      return "EconomySMPcore";
   }

   public boolean hasBankSupport() {
      return false;
   }

   public int fractionalDigits() {
      return 2;
   }

   public String format(double amount) {
      return CoreEconomy.formatMoney(amount);
   }

   public String currencyNamePlural() {
      return "dollars";
   }

   public String currencyNameSingular() {
      return "dollar";
   }

   public boolean hasAccount(String playerName) {
      return true;
   }

   public boolean hasAccount(OfflinePlayer player) {
      return true;
   }

   public boolean hasAccount(String playerName, String worldName) {
      return true;
   }

   public boolean hasAccount(OfflinePlayer player, String worldName) {
      return true;
   }

   public double getBalance(String playerName) {
      OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
      return CoreEconomy.balanceOf(op);
   }

   public double getBalance(OfflinePlayer player) {
      return CoreEconomy.balanceOf(player);
   }

   public double getBalance(String playerName, String world) {
      return this.getBalance(playerName);
   }

   public double getBalance(OfflinePlayer player, String world) {
      return this.getBalance(player);
   }

   public boolean has(String playerName, double amount) {
      return this.getBalance(playerName) >= amount;
   }

   public boolean has(OfflinePlayer player, double amount) {
      return this.getBalance(player) >= amount;
   }

   public boolean has(String playerName, String worldName, double amount) {
      return this.has(playerName, amount);
   }

   public boolean has(OfflinePlayer player, String worldName, double amount) {
      return this.has(player, amount);
   }

   public EconomyResponse withdrawPlayer(String playerName, double amount) {
      return this.withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount);
   }

   public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
      if (amount < (double)0.0F) {
         return new EconomyResponse((double)0.0F, CoreEconomy.balanceOf(player), ResponseType.FAILURE, "Negative amounts not allowed");
      } else {
         double bal = CoreEconomy.balanceOf(player);
         if (bal < amount) {
            return new EconomyResponse((double)0.0F, bal, ResponseType.FAILURE, "Insufficient funds");
         } else {
            CoreEconomy.take(player, amount);
            return new EconomyResponse(amount, CoreEconomy.balanceOf(player), ResponseType.SUCCESS, (String)null);
         }
      }
   }

   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
      return this.withdrawPlayer(playerName, amount);
   }

   public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
      return this.withdrawPlayer(player, amount);
   }

   public EconomyResponse depositPlayer(String playerName, double amount) {
      return this.depositPlayer(Bukkit.getOfflinePlayer(playerName), amount);
   }

   public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
      if (amount < (double)0.0F) {
         return new EconomyResponse((double)0.0F, CoreEconomy.balanceOf(player), ResponseType.FAILURE, "Negative amounts not allowed");
      } else {
         CoreEconomy.give(player, amount);
         return new EconomyResponse(amount, CoreEconomy.balanceOf(player), ResponseType.SUCCESS, (String)null);
      }
   }

   public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
      return this.depositPlayer(playerName, amount);
   }

   public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
      return this.depositPlayer(player, amount);
   }

   public EconomyResponse createBank(String name, String player) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse createBank(String name, OfflinePlayer player) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse deleteBank(String name) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse bankBalance(String name) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse bankHas(String name, double amount) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse bankWithdraw(String name, double amount) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse bankDeposit(String name, double amount) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse isBankOwner(String name, String playerName) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse isBankMember(String name, String playerName) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse isBankMember(String name, OfflinePlayer player) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public List<String> getBanks() {
      return Collections.emptyList();
   }

   public boolean createPlayerAccount(String playerName) {
      return true;
   }

   public boolean createPlayerAccount(OfflinePlayer player) {
      return true;
   }

   public boolean createPlayerAccount(String playerName, String worldName) {
      return true;
   }

   public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
      return true;
   }
}
