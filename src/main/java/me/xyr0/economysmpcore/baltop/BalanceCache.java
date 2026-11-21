package me.xyr0.economysmpcore.baltop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import me.xyr0.economysmpcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class BalanceCache {
   private final AstralBaltop plugin;
   private final Map<UUID, Double> balances = new ConcurrentHashMap();
   private final Map<UUID, String> names = new ConcurrentHashMap();
   private volatile List<Entry> ranked = List.of();
   private int onlineTask = -1;
   private int offlineTask = -1;
   private final AtomicInteger offlineIndex = new AtomicInteger(0);
   private OfflinePlayer[] offlineSnapshot = new OfflinePlayer[0];

   public BalanceCache(AstralBaltop plugin) {
      this.plugin = plugin;
   }

   public void start() {
      this.stop();
      int onlineTicks = Math.max(5, this.plugin.getConfig().getInt("cache.online-refresh-ticks", 20));
      int offlineTicks = Math.max(20, this.plugin.getConfig().getInt("cache.offline-refresh-ticks", 100));
      this.onlineTask = Tasks.runAsyncTimer(this.plugin.getCore(), this::refreshOnline, 1L, (long)onlineTicks);
      this.offlineTask = Tasks.runAsyncTimer(this.plugin.getCore(), this::refreshOfflineBatch, 40L, (long)offlineTicks);
      Tasks.runAsync(this.plugin.getCore(), this::fullOfflinePass);
   }

   public void stop() {
      if (this.onlineTask != -1) {
         Tasks.cancel(this.onlineTask);
         this.onlineTask = -1;
      }

      if (this.offlineTask != -1) {
         Tasks.cancel(this.offlineTask);
         this.offlineTask = -1;
      }

   }

   public void refreshOnline() {
      for(Player player : Bukkit.getOnlinePlayers()) {
         double bal = this.plugin.getEconomy().getBalance(player);
         this.balances.put(player.getUniqueId(), bal);
         this.names.put(player.getUniqueId(), player.getName());
      }

      this.rebuildRanked();
   }

   private void fullOfflinePass() {
      this.offlineSnapshot = Bukkit.getOfflinePlayers();
      this.offlineIndex.set(0);
   }

   private void refreshOfflineBatch() {
      OfflinePlayer[] all = this.offlineSnapshot;
      if (all == null || all.length == 0) {
         this.fullOfflinePass();
         all = this.offlineSnapshot;
      }

      if (all != null && all.length != 0) {
         int batch = Math.max(10, this.plugin.getConfig().getInt("cache.offline-batch-size", 40));
         int start = this.offlineIndex.getAndAdd(batch);
         if (start >= all.length) {
            this.offlineIndex.set(0);
            this.fullOfflinePass();
         } else {
            int end = Math.min(all.length, start + batch);

            for(int i = start; i < end; ++i) {
               OfflinePlayer offline = all[i];
               if (offline != null && offline.getUniqueId() != null && (offline.getName() != null || offline.hasPlayedBefore())) {
                  UUID id = offline.getUniqueId();
                  if (Bukkit.getPlayer(id) == null) {
                     double bal = this.plugin.getEconomy().getBalance(offline);
                     if (!(bal <= (double)0.0F) || offline.getName() != null) {
                        this.balances.put(id, bal);
                        if (offline.getName() != null) {
                           this.names.put(id, offline.getName());
                        }
                     }
                  }
               }
            }

            this.rebuildRanked();
         }
      }
   }

   public void forceRefresh(Runnable after) {
      Tasks.runAsync(this.plugin.getCore(), () -> {
         this.refreshOnline();
         OfflinePlayer[] all = Bukkit.getOfflinePlayers();
         int max = Math.min(all.length, this.plugin.getConfig().getInt("max-pages", 10) * this.plugin.getConfig().getInt("players-per-page", 45) * 3);
         int counted = 0;

         for(OfflinePlayer offline : all) {
            if (offline != null && offline.getUniqueId() != null && Bukkit.getPlayer(offline.getUniqueId()) == null) {
               double bal = this.plugin.getEconomy().getBalance(offline);
               this.balances.put(offline.getUniqueId(), bal);
               if (offline.getName() != null) {
                  this.names.put(offline.getUniqueId(), offline.getName());
               }

               ++counted;
               if (counted >= max) {
                  break;
               }
            }
         }

         this.rebuildRanked();
         if (after != null) {
            Tasks.run(this.plugin.getCore(), after);
         }

      });
   }

   private void rebuildRanked() {
      List<Entry> list = new ArrayList(this.balances.size());

      for(Map.Entry<UUID, Double> e : this.balances.entrySet()) {
         String name = (String)this.names.getOrDefault(e.getKey(), "Unknown");
         list.add(new Entry((UUID)e.getKey(), name, (Double)e.getValue()));
      }

      list.sort(Comparator.comparingDouble((Entry a) -> a.balance).reversed());
      int max = this.plugin.getConfig().getInt("max-pages", 10) * this.plugin.getConfig().getInt("players-per-page", 45);
      if (list.size() > max) {
         list = new ArrayList(list.subList(0, max));
      }

      this.ranked = List.copyOf(list);
   }

   public List<Entry> getRanked() {
      return this.ranked;
   }

   public List<Entry> search(String query) {
      if (query != null && !query.isBlank()) {
         String q = query.toLowerCase();
         List<Entry> out = new ArrayList();

         for(Entry e : this.ranked) {
            if (e.name.toLowerCase().contains(q)) {
               out.add(e);
            }
         }

         return out;
      } else {
         return this.getRanked();
      }
   }

   public int getRank(UUID uuid) {
      List<Entry> list = this.ranked;

      for(int i = 0; i < list.size(); ++i) {
         if (((Entry)list.get(i)).uuid.equals(uuid)) {
            return i + 1;
         }
      }

      return -1;
   }

   public double getBalance(UUID uuid) {
      return (Double)this.balances.getOrDefault(uuid, (double)0.0F);
   }

   public void updatePlayer(Player player) {
      if (player != null) {
         this.balances.put(player.getUniqueId(), this.plugin.getEconomy().getBalance(player));
         this.names.put(player.getUniqueId(), player.getName());
         this.rebuildRanked();
      }
   }

   public static final class Entry {
      public final UUID uuid;
      public final String name;
      public final double balance;

      public Entry(UUID uuid, String name, double balance) {
         this.uuid = uuid;
         this.name = name == null ? "Unknown" : name;
         this.balance = balance;
      }
   }
}
