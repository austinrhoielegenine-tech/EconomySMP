package com.austynzdevv.nyrethacore.spawners;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHook {
   private Economy economy;

   public boolean setup() {
      if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
         return false;
      } else {
         RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
         if (rsp == null) {
            return false;
         } else {
            this.economy = (Economy)rsp.getProvider();
            return this.economy != null;
         }
      }
   }

   public boolean isReady() {
      return this.economy != null;
   }

   public boolean deposit(Player player, double amount) {
      return this.economy != null && !(amount <= (double)0.0F) ? this.economy.depositPlayer(player, amount).transactionSuccess() : false;
   }

   public double getBalance(Player player) {
      return this.economy == null ? (double)0.0F : this.economy.getBalance(player);
   }
}
