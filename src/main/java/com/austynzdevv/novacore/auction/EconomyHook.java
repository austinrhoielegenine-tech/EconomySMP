package com.austynzdevv.novacore.auction;

import java.util.UUID;
import com.austynzdevv.novacore.economy.CoreEconomy;
import org.bukkit.entity.Player;

public class EconomyHook {
   public boolean setup() {
      return CoreEconomy.isReady();
   }

   public boolean isReady() {
      return CoreEconomy.isReady();
   }

   public boolean has(Player player, double amount) {
      return CoreEconomy.hasFunds(player, amount);
   }

   public boolean withdraw(Player player, double amount) {
      return CoreEconomy.take(player, amount);
   }

   public boolean deposit(UUID uuid, double amount) {
      return CoreEconomy.give(uuid, amount);
   }

   public boolean deposit(Player player, double amount) {
      return CoreEconomy.give(player, amount);
   }
}
