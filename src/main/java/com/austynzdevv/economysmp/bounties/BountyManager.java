package com.austynzdevv.economysmp.bounties;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import com.austynzdevv.economysmp.data.PlayerDataStore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class BountyManager {
   private final EconomyBounties plugin;
   private File file;
   private FileConfiguration data;

   public BountyManager(EconomyBounties plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.file = PlayerDataStore.get().file();
      this.data = PlayerDataStore.get().config();
   }

   public void save() {
      if (this.data != null && this.file != null) {
         PlayerDataStore.get().save();
      }
   }

   public double getAmount(UUID target) {
      return this.data.getDouble("bounties." + String.valueOf(target) + ".amount", (double)0.0F);
   }

   public boolean hasBounty(UUID target) {
      return this.getAmount(target) > (double)0.0F;
   }

   public void addBounty(UUID target, String name, double amount) {
      String path = "bounties." + String.valueOf(target);
      double current = this.data.getDouble(path + ".amount", (double)0.0F);
      this.data.set(path + ".amount", current + amount);
      this.data.set(path + ".name", name);
      if (!this.data.contains(path + ".created")) {
         this.data.set(path + ".created", System.currentTimeMillis());
      }

      this.save();
   }

   public double clearBounty(UUID target) {
      double amount = this.getAmount(target);
      this.data.set("bounties." + String.valueOf(target), (Object)null);
      this.save();
      return amount;
   }

   public String getName(UUID target) {
      String stored = this.data.getString("bounties." + String.valueOf(target) + ".name");
      if (stored != null && !stored.isBlank()) {
         return stored;
      } else {
         OfflinePlayer op = Bukkit.getOfflinePlayer(target);
         return op.getName() != null ? op.getName() : target.toString().substring(0, 8);
      }
   }

   public List<Bounty> list(Sort sort) {
      List<Bounty> list = new ArrayList();
      ConfigurationSection sec = this.data.getConfigurationSection("bounties");
      if (sec == null) {
         return list;
      } else {
         for(String key : sec.getKeys(false)) {
            try {
               UUID id = UUID.fromString(key);
               double amount = sec.getDouble(key + ".amount", (double)0.0F);
               if (!(amount <= (double)0.0F)) {
                  String name = sec.getString(key + ".name", this.getName(id));
                  long created = sec.getLong(key + ".created", 0L);
                  list.add(new Bounty(id, name, amount, created));
               }
            } catch (IllegalArgumentException var12) {
            }
         }

         Comparator var10000;
         switch (sort.ordinal()) {
            case 0 -> var10000 = Comparator.comparingLong(Bounty::createdAt).reversed();
            case 1 -> var10000 = Comparator.comparingLong(Bounty::createdAt);
            case 2 -> var10000 = Comparator.comparingDouble(Bounty::amount).reversed();
            case 3 -> var10000 = Comparator.comparingDouble(Bounty::amount);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         Comparator<Bounty> cmp = var10000;
         list.sort(cmp);
         return list;
      }
   }

   public int findPage(String query, Sort sort, int pageSize) {
      if (query != null && !query.isBlank()) {
         String q = query.toLowerCase(Locale.ROOT);
         List<Bounty> list = this.list(sort);

         for(int i = 0; i < list.size(); ++i) {
            if (((Bounty)list.get(i)).name().toLowerCase(Locale.ROOT).contains(q)) {
               return i / pageSize;
            }
         }

         return -1;
      } else {
         return 0;
      }
   }

   public static String formatMoney(double amount) {
      if (amount >= (double)1.0E9F) {
         return String.format(Locale.US, "%.1fB", amount / (double)1.0E9F).replace(".0B", "B");
      } else if (amount >= (double)1000000.0F) {
         return String.format(Locale.US, "%.1fM", amount / (double)1000000.0F).replace(".0M", "M");
      } else if (amount >= (double)1000.0F) {
         return String.format(Locale.US, "%.1fK", amount / (double)1000.0F).replace(".0K", "K");
      } else {
         return amount == (double)((long)amount) ? String.valueOf((long)amount) : String.format(Locale.US, "%.2f", amount);
      }
   }

   public static Sort nextSort(Sort current) {
      Sort var10000;
      switch (current.ordinal()) {
         case 0 -> var10000 = BountyManager.Sort.OLDEST;
         case 1 -> var10000 = BountyManager.Sort.HIGHEST;
         case 2 -> var10000 = BountyManager.Sort.LOWEST;
         case 3 -> var10000 = BountyManager.Sort.NEWEST;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static String sortLabel(Sort sort) {
      String var10000;
      switch (sort.ordinal()) {
         case 0 -> var10000 = "Newest";
         case 1 -> var10000 = "Oldest";
         case 2 -> var10000 = "Highest Price";
         case 3 -> var10000 = "Lowest Price";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static enum Sort {
      NEWEST,
      OLDEST,
      HIGHEST,
      LOWEST;

      // $FF: synthetic method
      private static Sort[] $values() {
         return new Sort[]{NEWEST, OLDEST, HIGHEST, LOWEST};
      }
   }

   public static record Bounty(UUID target, String name, double amount, long createdAt) {
   }
}
