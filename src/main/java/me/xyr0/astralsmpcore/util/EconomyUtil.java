package me.xyr0.astralsmpcore.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import me.xyr0.astralsmpcore.auction.AstralAuction;
import me.xyr0.astralsmpcore.baltop.AstralBaltop;
import me.xyr0.astralsmpcore.order.AstralOrder;
import me.xyr0.astralsmpcore.sell.AstralSell;
import org.bukkit.configuration.file.FileConfiguration;

public final class EconomyUtil {
   private EconomyUtil() {
   }

   public static double parseCompact(String input) {
      if (input == null) {
         throw new NumberFormatException("empty");
      } else {
         String raw = input.trim().replace(",", "").replace("$", "").replace(" ", "");
         if (raw.isEmpty()) {
            throw new NumberFormatException("empty");
         } else {
            char suffix = Character.toUpperCase(raw.charAt(raw.length() - 1));
            double var10000;
            switch (suffix) {
               case 'B' -> var10000 = (double)1.0E9F;
               case 'K' -> var10000 = (double)1000.0F;
               case 'M' -> var10000 = (double)1000000.0F;
               case 'T' -> var10000 = 1.0E12;
               default -> var10000 = (double)1.0F;
            }

            double multiplier = var10000;
            String number = multiplier == (double)1.0F ? raw : raw.substring(0, raw.length() - 1);
            return Double.parseDouble(number) * multiplier;
         }
      }
   }

   public static String formatAuction(double amount) {
      FileConfiguration config = AstralAuction.get().getConfig();
      String symbol = config.getString("currency-symbol", config.getString("economy.currency-symbol", "$"));
      if (!config.getBoolean("economy.abbreviations.enabled", true)) {
         return symbol + String.format(Locale.US, "%.2f", amount);
      } else {
         double abs = Math.abs(amount);
         if (abs >= 1.0E12) {
            return symbol + trim(amount / 1.0E12) + "T";
         } else if (abs >= (double)1.0E9F) {
            return symbol + trim(amount / (double)1.0E9F) + "B";
         } else if (abs >= (double)1000000.0F) {
            return symbol + trim(amount / (double)1000000.0F) + "M";
         } else {
            return abs >= (double)1000.0F ? symbol + trim(amount / (double)1000.0F) + "K" : symbol + String.format(Locale.US, "%.2f", amount);
         }
      }
   }

   public static String formatBaltop(double amount) {
      FileConfiguration config = AstralBaltop.get().getConfig();
      if (config.getBoolean("economy.abbreviations.enabled", true)) {
         List<String> suffixes = config.getStringList("economy.abbreviations.formats");
         if (suffixes == null || suffixes.isEmpty()) {
            suffixes = List.of("K", "M", "B", "T");
         }

         double abs = Math.abs(amount);
         if (abs >= 1.0E12 && suffixes.size() > 3) {
            String var9 = trimOne(amount / 1.0E12);
            return var9 + (String)suffixes.get(3);
         }

         if (abs >= (double)1.0E9F && suffixes.size() > 2) {
            String var8 = trimOne(amount / (double)1.0E9F);
            return var8 + (String)suffixes.get(2);
         }

         if (abs >= (double)1000000.0F && suffixes.size() > 1) {
            String var7 = trimOne(amount / (double)1000000.0F);
            return var7 + (String)suffixes.get(1);
         }

         if (abs >= (double)1000.0F && !suffixes.isEmpty()) {
            String var10000 = trimOne(amount / (double)1000.0F);
            return var10000 + (String)suffixes.get(0);
         }
      }

      try {
         return (new DecimalFormat(config.getString("economy.currency-format", "#,##0.##"))).format(amount);
      } catch (IllegalArgumentException var6) {
         return String.format(Locale.US, "%.2f", amount);
      }
   }

   public static String formatOrder(double amount) {
      FileConfiguration config = AstralOrder.get().getConfig();
      String symbol = config.getString("currency-symbol", "$");
      if (!config.getBoolean("economy.abbreviations.enabled", true)) {
         return symbol + String.format(Locale.US, "%.2f", amount);
      } else {
         double abs = Math.abs(amount);
         if (abs >= 1.0E12) {
            return symbol + trim(amount / 1.0E12) + "T";
         } else if (abs >= (double)1.0E9F) {
            return symbol + trim(amount / (double)1.0E9F) + "B";
         } else if (abs >= (double)1000000.0F) {
            return symbol + trim(amount / (double)1000000.0F) + "M";
         } else {
            return abs >= (double)1000.0F ? symbol + trim(amount / (double)1000.0F) + "K" : symbol + String.format(Locale.US, "%.2f", amount);
         }
      }
   }

   public static String formatSell(double amount) {
      FileConfiguration config = AstralSell.get().getConfig();
      String symbol = config.getString("currency-symbol", "$");
      if (!config.getBoolean("economy.abbreviations.enabled", true)) {
         return symbol + (Math.abs(amount - Math.rint(amount)) < 0.001 ? String.format(Locale.US, "%.0f", amount) : String.format(Locale.US, "%.2f", amount));
      } else {
         double abs = Math.abs(amount);
         if (abs >= 1.0E12) {
            return symbol + trimOne(amount / 1.0E12) + "T";
         } else if (abs >= (double)1.0E9F) {
            return symbol + trimOne(amount / (double)1.0E9F) + "B";
         } else if (abs >= (double)1000000.0F) {
            return symbol + trimOne(amount / (double)1000000.0F) + "M";
         } else {
            return abs >= (double)1000.0F ? symbol + trimOne(amount / (double)1000.0F) + "k" : symbol + (Math.abs(amount - Math.rint(amount)) < 0.001 ? String.format(Locale.US, "%.0f", amount) : String.format(Locale.US, "%.2f", amount));
         }
      }
   }

   private static String trim(double value) {
      return Math.abs(value - Math.rint(value)) < 0.001 ? String.format(Locale.US, "%.0f", value) : String.format(Locale.US, "%.2f", value);
   }

   private static String trimOne(double value) {
      return Math.abs(value - Math.rint(value)) < 0.001 ? String.format(Locale.US, "%.0f", value) : String.format(Locale.US, "%.1f", value);
   }
}
