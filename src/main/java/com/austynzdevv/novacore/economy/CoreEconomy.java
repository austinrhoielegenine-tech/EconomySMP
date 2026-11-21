package com.austynzdevv.novacore.economy;

import java.io.File;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import com.austynzdevv.novacore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class CoreEconomy {
   private static CoreEconomy INSTANCE;
   private static Object vault;
   private static JavaPlugin plugin;
   private static YamlConfiguration yaml;
   private static File file;
   private static boolean vaultReady;
   private static boolean registeredSelf;
   private static final Object ECONOMY_LOCK = new Object();
   private static final AtomicBoolean SAVE_PENDING = new AtomicBoolean();
   private static final AtomicBoolean SAVE_DIRTY = new AtomicBoolean();

   private CoreEconomy() {
   }

   public static void init(JavaPlugin core) {
      plugin = core;
      vaultReady = false;
      vault = null;
      registeredSelf = false;
      INSTANCE = new CoreEconomy();
      boolean folia = false;

      try {
         folia = Tasks.isFolia();
      } catch (Exception var8) {
      }

      loadYaml();
      if (folia) {
         core.getLogger().info("Economy: Folia detected — using built-in currency (Vault not used).");
      } else {
         try {
            if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
               core.getLogger().info("Economy: Vault not installed — using built-in currency.");
               return;
            }
         } catch (Exception var9) {
            core.getLogger().info("Economy: using built-in currency.");
            return;
         }

         try {
            Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
            RegisteredServiceProvider rsp = Bukkit.getServicesManager().getRegistration(economyClass);
            if (rsp != null && rsp.getProvider() != null && !rsp.getProvider().getClass().getName().contains("CoreEconomy") && !rsp.getProvider().getClass().getName().contains("VaultEconomyBridge")) {
               vault = rsp.getProvider();
               vaultReady = true;

               try {
                  Object name = vault.getClass().getMethod("getName").invoke(vault);
                  core.getLogger().info("Economy: Paper + Vault — using provider " + String.valueOf(name));
               } catch (Exception var6) {
                  core.getLogger().info("Economy: Paper + Vault — external economy hooked.");
               }

               return;
            }
         } catch (Exception t) {
            core.getLogger().warning("Vault economy lookup failed: " + t.getMessage());
         }

         try {
            Class.forName("com.austynzdevv.novacore.economy.VaultEconomyBridge").getMethod("register", JavaPlugin.class, CoreEconomy.class).invoke((Object)null, core, INSTANCE);
            registeredSelf = true;
            vaultReady = true;
            vault = INSTANCE;
            core.getLogger().info("Economy: Paper + Vault — NovaCore registered as currency provider.");
         } catch (Exception t) {
            core.getLogger().warning("Economy: could not register with Vault, using built-in only: " + t.getMessage());
         }

      }
   }

   public static boolean isReady() {
      return vaultReady || yaml != null;
   }

   public static boolean usingVault() {
      return vaultReady;
   }

   public static double balanceOf(OfflinePlayer player) {
      if (player == null) {
         return (double)0.0F;
      } else {
         if (vaultReady && vault != null && !(vault instanceof CoreEconomy)) {
            try {
               Object r = vault.getClass().getMethod("getBalance", OfflinePlayer.class).invoke(vault, player);
               return r instanceof Number ? ((Number)r).doubleValue() : (double)0.0F;
            } catch (Exception var6) {
            }
         }

         synchronized(ECONOMY_LOCK) {
            ensureYaml();
            double start = yaml.getDouble("settings.starting-balance", (double)10000.0F);
            return yaml.getDouble("balances." + String.valueOf(player.getUniqueId()), start);
         }
      }
   }

   public static double balanceOf(Player player) {
      return balanceOf((OfflinePlayer)player);
   }

   public static boolean hasFunds(Player player, double amount) {
      return balanceOf(player) >= amount;
   }

   public static boolean take(OfflinePlayer player, double amount) {
      if (player != null && !(amount < (double)0.0F)) {
         if (vaultReady && vault != null && !(vault instanceof CoreEconomy)) {
            try {
               Object resp = vault.getClass().getMethod("withdrawPlayer", OfflinePlayer.class, Double.TYPE).invoke(vault, player, amount);
               Object ok = resp.getClass().getMethod("transactionSuccess").invoke(resp);
               return Boolean.TRUE.equals(ok);
            } catch (Exception var7) {
               return false;
            }
         } else {
            synchronized(ECONOMY_LOCK) {
               ensureYaml();
               double bal = balanceOf(player);
               if (bal < amount) {
                  return false;
               } else {
                  yaml.set("balances." + String.valueOf(player.getUniqueId()), bal - amount);
                  save();
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public static boolean take(Player player, double amount) {
      return take((OfflinePlayer)player, amount);
   }

   public static boolean give(OfflinePlayer player, double amount) {
      if (player != null && !(amount < (double)0.0F)) {
         if (vaultReady && vault != null && !(vault instanceof CoreEconomy)) {
            try {
               Object resp = vault.getClass().getMethod("depositPlayer", OfflinePlayer.class, Double.TYPE).invoke(vault, player, amount);
               Object ok = resp.getClass().getMethod("transactionSuccess").invoke(resp);
               return Boolean.TRUE.equals(ok);
            } catch (Exception var7) {
               return false;
            }
         } else {
            synchronized(ECONOMY_LOCK) {
               ensureYaml();
               double bal = balanceOf(player);
               yaml.set("balances." + String.valueOf(player.getUniqueId()), bal + amount);
               save();
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean give(Player player, double amount) {
      return give((OfflinePlayer)player, amount);
   }

   public static boolean give(UUID uuid, double amount) {
      return give(Bukkit.getOfflinePlayer(uuid), amount);
   }

   public static void setBalance(OfflinePlayer player, double amount) {
      if (player != null) {
         if (amount < (double)0.0F) {
            amount = (double)0.0F;
         }

         if (vaultReady && vault != null && !(vault instanceof CoreEconomy)) {
            try {
               double cur = balanceOf(player);
               if (cur > amount) {
                  vault.getClass().getMethod("withdrawPlayer", OfflinePlayer.class, Double.TYPE).invoke(vault, player, cur - amount);
               } else if (amount > cur) {
                  vault.getClass().getMethod("depositPlayer", OfflinePlayer.class, Double.TYPE).invoke(vault, player, amount - cur);
               }

               return;
            } catch (Exception var7) {
            }
         }

         synchronized(ECONOMY_LOCK) {
            ensureYaml();
            yaml.set("balances." + String.valueOf(player.getUniqueId()), amount);
            save();
         }
      }
   }

   public static String formatMoney(double amount) {
      return compact(amount);
   }

   public static String compact(double amount) {
      String sign = amount < (double)0.0F ? "-" : "";
      double a = Math.abs(amount);
      if (a >= 1.0E12) {
         return sign + trim(a / 1.0E12) + "T";
      } else if (a >= (double)1.0E9F) {
         return sign + trim(a / (double)1.0E9F) + "B";
      } else if (a >= (double)1000000.0F) {
         return sign + trim(a / (double)1000000.0F) + "M";
      } else if (a >= (double)1000.0F) {
         return sign + trim(a / (double)1000.0F) + "K";
      } else {
         return a == (double)((long)a) ? sign + String.valueOf((long)a) : sign + String.format("%.2f", a);
      }
   }

   private static String trim(double v) {
      if (v >= (double)100.0F) {
         return String.valueOf((long)v);
      } else {
         return v >= (double)10.0F ? String.format("%.1f", v).replace(".0", "") : String.format("%.2f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
      }
   }

   public static double parseAmount(String raw) {
      if (raw == null) {
         return (double)-1.0F;
      } else {
         String s = raw.trim().toLowerCase(Locale.ROOT).replace(",", "");
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
            }

            try {
               double v = Double.parseDouble(s.trim()) * mult;
               return !(v < (double)0.0F) && !Double.isNaN(v) && !Double.isInfinite(v) ? v : (double)-1.0F;
            } catch (Exception var7) {
               return (double)-1.0F;
            }
         }
      }
   }

   private static void loadYaml() {
      try {
         File folder = plugin.getDataFolder();
         if (!folder.exists()) {
            folder.mkdirs();
         }

         file = new File(folder, "economy.yml");
         if (!file.exists()) {
            try {
               file.createNewFile();
            } catch (Exception var2) {
            }
         }

         yaml = YamlConfiguration.loadConfiguration(file);
         if (!yaml.contains("settings.starting-balance")) {
            yaml.set("settings.starting-balance", (double)10000.0F);
            save();
         }
      } catch (Exception t) {
         yaml = new YamlConfiguration();
         if (plugin != null) {
            plugin.getLogger().warning("Economy: could not load economy.yml: " + t.getMessage());
         }
      }

   }

   private static void ensureYaml() {
      if (yaml == null) {
         loadYaml();
      }

   }

   private static void save() {
      SAVE_DIRTY.set(true);
      if (plugin != null && SAVE_PENDING.compareAndSet(false, true)) {
         Tasks.runAsyncLater(plugin, () -> {
            while(true) {
               try {
                  SAVE_DIRTY.set(false);
                  synchronized(ECONOMY_LOCK) {
                     if (file != null && yaml != null) {
                        yaml.save(file);
                     }
                  }

                  if (SAVE_DIRTY.get()) {
                     continue;
                  }
               } catch (Exception e) {
                  SAVE_DIRTY.set(false);
                  if (plugin != null) {
                     plugin.getLogger().warning("economy.yml save failed: " + e.getMessage());
                  }
               } finally {
                  SAVE_PENDING.set(false);
                  if (SAVE_DIRTY.get()) {
                     save();
                  }

               }

               return;
            }
         }, 20L);
      }
   }

   public static void flush() {
      synchronized(ECONOMY_LOCK) {
         try {
            if (file != null && yaml != null) {
               yaml.save(file);
            }
         } catch (Exception e) {
            if (plugin != null) {
               plugin.getLogger().warning("economy.yml save failed: " + e.getMessage());
            }
         } finally {
            SAVE_PENDING.set(false);
            SAVE_DIRTY.set(false);
         }

      }
   }

   public static CoreEconomy getInstance() {
      return INSTANCE;
   }
}
