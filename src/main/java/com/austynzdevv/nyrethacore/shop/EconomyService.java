package com.austynzdevv.nyrethacore.shop;

import java.util.Locale;
import java.util.Map;
import me.clip.placeholderapi.PlaceholderAPI;
import com.austynzdevv.nyrethacore.crates.EconomyCrates;
import com.austynzdevv.nyrethacore.economy.CoreEconomy;
import com.austynzdevv.nyrethacore.lb.EconomyLeaderboards;
import com.austynzdevv.nyrethacore.shards.EconomyShards;
import com.austynzdevv.nyrethacore.spawners.EconomySpawners;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EconomyService {
   private final EconomyShop plugin;
   private boolean ready;

   public EconomyService(EconomyShop plugin) {
      this.plugin = plugin;
   }

   public boolean setupVault() {
      this.ready = CoreEconomy.isReady();
      return this.ready;
   }

   public boolean hasVault() {
      return this.ready;
   }

   public boolean charge(Player player, String economy, String takeCommand, double total, int amount, String balancePlaceholder) {
      if (economy != null && !economy.equalsIgnoreCase("vault")) {
         if (!economy.equalsIgnoreCase("command")) {
            return false;
         } else if (balancePlaceholder != null && !balancePlaceholder.isBlank() && !this.hasEnoughFromPlaceholder(player, balancePlaceholder, total)) {
            return false;
         } else if (this.tryShardsTake(player, (long)total)) {
            return true;
         } else {
            String cmd = takeCommand;
            if (takeCommand == null || takeCommand.isBlank()) {
               cmd = this.plugin.getConfig().getString("default-take-command", "eco take %player% %total%");
            }

            cmd = this.apply(cmd, player, total, amount, (String)null);
            return this.dispatch(cmd);
         }
      } else if (!CoreEconomy.isReady()) {
         return false;
      } else if (!CoreEconomy.hasFunds(player, total)) {
         return false;
      } else {
         boolean ok = CoreEconomy.take(player, total);
         if (ok) {
            try {
               EconomyLeaderboards _lb = EconomyLeaderboards.get();
               if (_lb != null && _lb.getStats() != null) {
                  _lb.getStats().addShopSpent(player.getUniqueId(), total);
               }
            } catch (Exception var10) {
            }
         }

         return ok;
      }
   }

   private boolean tryShardsTake(Player player, long amount) {
      try {
         EconomyShards shards = EconomyShards.get();
         if (shards != null && shards.getShards() != null) {
            return shards.getShards().take(player, amount);
         }
      } catch (Exception var5) {
      }

      return false;
   }

   private boolean hasEnoughFromPlaceholder(Player player, String placeholder, double needed) {
      try {
         EconomyShards shards = EconomyShards.get();
         if (shards != null && shards.getShards() != null) {
            return (double)shards.getShards().getBalance(player) >= needed;
         }
      } catch (Exception var7) {
      }

      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
         return true;
      } else {
         try {
            String raw = PlaceholderAPI.setPlaceholders(player, placeholder);
            if (raw != null && !raw.isBlank() && !raw.equals(placeholder)) {
               return this.parseAmount(raw) >= needed;
            } else {
               return true;
            }
         } catch (Exception var6) {
            return true;
         }
      }
   }

   private double parseAmount(String raw) {
      raw = raw.replace(",", "").replace(" ", "").trim().toLowerCase();
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
      } else if (raw.endsWith("t")) {
         mult = 1.0E12;
         raw = raw.substring(0, raw.length() - 1);
      }

      return Double.parseDouble(raw) * mult;
   }

   public void giveReward(Player player, String giveCommand, String itemName, int amount, ItemStack stack) {
      if (giveCommand != null && !giveCommand.isBlank()) {
         String cmd = this.apply(giveCommand, player, (double)0.0F, amount, itemName);
         if (!this.tryDirectGive(player, cmd)) {
            this.dispatch(cmd);
         }
      } else {
         if (stack != null) {
            ItemStack give = stack.clone();
            give.setAmount(amount);
            Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{give});
            left.values().forEach((i) -> player.getWorld().dropItemNaturally(player.getLocation(), i));
         }

      }
   }

   private boolean tryDirectGive(Player player, String cmd) {
      try {
         String[] parts = cmd.trim().split("\\s+");
         if (parts.length >= 4 && (parts[0].equalsIgnoreCase("as") || parts[0].equalsIgnoreCase("spawners")) && parts[1].equalsIgnoreCase("give")) {
            EntityType type = EntityType.valueOf(parts[3].toUpperCase(Locale.ROOT));
            int amt = parts.length >= 5 ? Integer.parseInt(parts[4]) : 1;
            EconomySpawners spawners = EconomySpawners.get();
            if (spawners != null && spawners.getSpawnerManager() != null) {
               spawners.getSpawnerManager().giveTo(player, type, amt);
               return true;
            }
         }

         if (parts.length >= 5 && (parts[0].equalsIgnoreCase("crate") || parts[0].equalsIgnoreCase("crates")) && parts[1].equalsIgnoreCase("givekey")) {
            String crate = parts[3];
            boolean virtual = true;
            int amt = 1;
            if (parts.length < 6) {
               if (parts.length >= 5) {
                  try {
                     amt = Integer.parseInt(parts[4]);
                     virtual = true;
                  } catch (Exception var8) {
                     virtual = parts[4].equalsIgnoreCase("virtual") || parts[4].equalsIgnoreCase("virtuell") || !parts[4].equalsIgnoreCase("physical");
                  }
               }
            } else {
               virtual = parts[4].equalsIgnoreCase("virtual") || parts[4].equalsIgnoreCase("virtuell") || !parts[4].equalsIgnoreCase("physical");
               amt = Integer.parseInt(parts[5]);
            }

            EconomyCrates crates = EconomyCrates.get();
            if (crates != null && crates.getData() != null) {
               if (virtual) {
                  crates.getData().addVirtual(player.getUniqueId(), crate, amt);
               } else if (crates.getKeys() != null) {
                  player.getInventory().addItem(new ItemStack[]{crates.getKeys().createPhysical(crate, amt)});
               }

               return true;
            }
         }
      } catch (Exception t) {
         this.plugin.getLogger().warning("tryDirectGive failed for '" + cmd + "': " + t.getMessage());
      }

      return false;
   }

   private boolean dispatch(String cmd) {
      try {
         return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
      } catch (Exception var3) {
         return false;
      }
   }

   private String apply(String cmd, Player player, double total, int amount, String item) {
      return cmd.replace("%player%", player.getName()).replace("%price%", String.valueOf(total)).replace("%total%", String.valueOf((long)total)).replace("%amount%", String.valueOf(amount)).replace("%item%", item == null ? "" : item);
   }
}
