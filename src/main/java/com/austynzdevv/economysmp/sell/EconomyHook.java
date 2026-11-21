package com.austynzdevv.economysmp.sell;

import com.austynzdevv.economysmp.economy.CoreEconomy;
import org.bukkit.entity.Player;

public class EconomyHook {
   public boolean setup() {
      return CoreEconomy.isReady();
   }

   public boolean isReady() {
      return CoreEconomy.isReady();
   }

   public boolean has(Player p, double a) {
      return CoreEconomy.hasFunds(p, a);
   }

   public boolean withdraw(Player p, double a) {
      return CoreEconomy.take(p, a);
   }

   public boolean deposit(Player p, double a) {
      return CoreEconomy.give(p, a);
   }

   public double getBalance(Player p) {
      return CoreEconomy.balanceOf(p);
   }

   public String format(double a) {
      return CoreEconomy.formatMoney(a);
   }
}
