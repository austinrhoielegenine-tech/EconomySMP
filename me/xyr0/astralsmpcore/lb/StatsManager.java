package me.xyr0.astralsmpcore.lb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.astralsmpcore.data.PlayerDataStore;
import me.xyr0.astralsmpcore.sell.AstralSell;
import me.xyr0.astralsmpcore.shards.AstralShards;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class StatsManager {
   private final AstralLeaderboards plugin;
   private File file;
   private FileConfiguration data;
   private final Set<UUID> tracked = ConcurrentHashMap.newKeySet();
   private final Map<Board, List<Entry>> cache = new ConcurrentHashMap();

   public StatsManager(AstralLeaderboards plugin) {
      this.plugin = plugin;
      this.reload();
      this.refreshCache();
   }

   public void reload() {
      this.file = PlayerDataStore.get().file();
      if (!this.file.exists()) {
         try {
            this.plugin.getDataFolder().mkdirs();
            this.file.createNewFile();
         } catch (IOException var8) {
            this.plugin.getLogger().warning("Could not create data.yml");
         }
      }

      this.data = PlayerDataStore.get().config();
      this.tracked.clear();
      synchronized(PlayerDataStore.get().lock()) {
         ConfigurationSection sec = this.data.getConfigurationSection("players");
         if (sec != null) {
            for(String key : sec.getKeys(false)) {
               try {
                  this.tracked.add(UUID.fromString(key));
               } catch (IllegalArgumentException var7) {
               }
            }
         }
      }

      for(Player p : Bukkit.getOnlinePlayers()) {
         this.track(p);
      }

   }

   public void save() {
      if (this.data != null && this.file != null) {
         PlayerDataStore.get().save();
      }
   }

   private Object storeLock() {
      return PlayerDataStore.get().lock();
   }

   public void track(Player player) {
      UUID id = player.getUniqueId();
      this.tracked.add(id);
      synchronized(this.storeLock()) {
         this.data.set("players." + String.valueOf(id) + ".name", player.getName());
      }
   }

   public void addBlocksBroken(Player player, int amount) {
      this.track(player);
      String path = "players." + String.valueOf(player.getUniqueId()) + ".blocks-broken";
      synchronized(this.storeLock()) {
         this.data.set(path, this.data.getLong(path, 0L) + (long)amount);
      }
   }

   public void addBlocksPlaced(Player player, int amount) {
      this.track(player);
      String path = "players." + String.valueOf(player.getUniqueId()) + ".blocks-placed";
      synchronized(this.storeLock()) {
         this.data.set(path, this.data.getLong(path, 0L) + (long)amount);
      }
   }

   public long getBlocksBroken(UUID id) {
      synchronized(this.storeLock()) {
         return this.data.getLong("players." + String.valueOf(id) + ".blocks-broken", 0L);
      }
   }

   public long getBlocksPlaced(UUID id) {
      synchronized(this.storeLock()) {
         return this.data.getLong("players." + String.valueOf(id) + ".blocks-placed", 0L);
      }
   }

   public String getName(UUID id) {
      String stored;
      synchronized(this.storeLock()) {
         stored = this.data.getString("players." + String.valueOf(id) + ".name");
      }

      if (stored != null && !stored.isBlank()) {
         return stored;
      } else {
         OfflinePlayer op = Bukkit.getOfflinePlayer(id);
         return op.getName() != null ? op.getName() : id.toString().substring(0, 8);
      }
   }

   public void refreshCache() {
      for(Board board : StatsManager.Board.values()) {
         this.cache.put(board, this.buildBoard(board));
      }

   }

   public List<Entry> getBoard(Board board) {
      List<Entry> list = (List)this.cache.get(board);
      if (list == null) {
         list = this.buildBoard(board);
         this.cache.put(board, list);
      }

      return list;
   }

   public int getRank(Board board, UUID id) {
      List<Entry> list = this.getBoard(board);

      for(int i = 0; i < list.size(); ++i) {
         if (((Entry)list.get(i)).uuid().equals(id)) {
            return i + 1;
         }
      }

      return -1;
   }

   public double getValue(Board board, UUID id) {
      for(Entry e : this.getBoard(board)) {
         if (e.uuid().equals(id)) {
            return e.value();
         }
      }

      return this.rawValue(board, id);
   }

   public int findPage(Board board, String nameQuery, int pageSize) {
      if (nameQuery != null && !nameQuery.isBlank()) {
         String q = nameQuery.toLowerCase(Locale.ROOT);
         List<Entry> list = this.getBoard(board);

         for(int i = 0; i < list.size(); ++i) {
            if (((Entry)list.get(i)).name().toLowerCase(Locale.ROOT).contains(q)) {
               return i / pageSize;
            }
         }

         return -1;
      } else {
         return 0;
      }
   }

   private List<Entry> buildBoard(Board board) {
      Map<UUID, Entry> map = new HashMap();
      Set<UUID> ids = new HashSet(this.tracked);

      for(Player p : Bukkit.getOnlinePlayers()) {
         ids.add(p.getUniqueId());
         synchronized(this.storeLock()) {
            this.data.set("players." + String.valueOf(p.getUniqueId()) + ".name", p.getName());
         }
      }

      if (board == StatsManager.Board.SHARDS) {
         this.loadShardsInto(map);
      }

      for(UUID id : ids) {
         double value;
         if (board == StatsManager.Board.SHARDS) {
            Entry shardEntry = (Entry)map.get(id);
            value = shardEntry == null ? (double)0.0F : shardEntry.value();
         } else {
            value = this.rawValue(board, id);
         }

         if (!(value <= (double)0.0F) || board == StatsManager.Board.DEATHS) {
            map.put(id, new Entry(id, this.getName(id), value));
         }
      }

      List<Entry> list = new ArrayList(map.values());
      list.sort(Comparator.comparingDouble(Entry::value).reversed());
      return list;
   }

   private double rawValue(Board board, UUID id) {
      OfflinePlayer op = Bukkit.getOfflinePlayer(id);

      try {
         double var10000;
         switch (board.ordinal()) {
            case 0 -> var10000 = this.plugin.getEconomy() == null ? (double)0.0F : this.plugin.getEconomy().getBalance(op);
            case 1 -> var10000 = (double)op.getStatistic(Statistic.DEATHS);
            case 2 -> var10000 = (double)op.getStatistic(Statistic.PLAY_ONE_MINUTE) / (double)20.0F;
            case 3 -> var10000 = (double)this.getBlocksPlaced(id);
            case 4 -> var10000 = (double)this.getBlocksBroken(id);
            case 5 -> var10000 = (double)op.getStatistic(Statistic.MOB_KILLS);
            case 6 -> var10000 = (double)op.getStatistic(Statistic.PLAYER_KILLS);
            case 7 -> var10000 = (double)this.getShards(id);
            case 8 -> var10000 = this.getShopSpent(id);
            case 9 -> var10000 = this.getSellEarned(id);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      } catch (Exception var5) {
         return (double)0.0F;
      }
   }

   private long getShards(UUID id) {
      try {
         AstralShards shardsPlugin = AstralShards.get();
         if (shardsPlugin != null && shardsPlugin.getShards() != null) {
            return shardsPlugin.getShards().getBalance(id);
         }
      } catch (Exception var6) {
      }

      File shardsData = new File(this.plugin.getDataFolder().getParentFile(), "AstralSMPcore/shards/data.yml");
      if (!shardsData.exists()) {
         shardsData = PlayerDataStore.get().file();
      }

      if (!shardsData.exists()) {
         synchronized(this.storeLock()) {
            return this.data.getLong("players." + String.valueOf(id) + ".shards", 0L);
         }
      } else {
         FileConfiguration shards = YamlConfiguration.loadConfiguration(shardsData);
         return shards.getLong("players." + String.valueOf(id), 0L);
      }
   }

   private void loadShardsInto(Map<UUID, Entry> map) {
      File shardsData = new File(this.plugin.getDataFolder().getParentFile(), "AstralSMPcore/shards/data.yml");
      if (!shardsData.exists()) {
         shardsData = PlayerDataStore.get().file();
      }

      if (shardsData.exists()) {
         FileConfiguration shards = YamlConfiguration.loadConfiguration(shardsData);
         ConfigurationSection sec = shards.getConfigurationSection("players");
         if (sec != null) {
            for(String key : sec.getKeys(false)) {
               try {
                  UUID id = UUID.fromString(key);
                  long value = sec.getLong(key, 0L);
                  if (value > 0L) {
                     this.tracked.add(id);
                     map.put(id, new Entry(id, this.getName(id), (double)value));
                  }
               } catch (IllegalArgumentException var10) {
               }
            }

         }
      } else {
         try {
            AstralShards shardsPlugin = AstralShards.get();
            if (shardsPlugin == null || shardsPlugin.getShards() == null) {
               return;
            }

            for(UUID id : this.tracked) {
               long value = shardsPlugin.getShards().getBalance(id);
               if (value > 0L) {
                  map.put(id, new Entry(id, this.getName(id), (double)value));
               }
            }

            for(Player player : Bukkit.getOnlinePlayers()) {
               long value = shardsPlugin.getShards().getBalance(player);
               if (value > 0L) {
                  map.put(player.getUniqueId(), new Entry(player.getUniqueId(), player.getName(), (double)value));
               }
            }
         } catch (Exception var11) {
         }

      }
   }

   public static String formatValue(Board board, double value) {
      String var10000;
      switch (board.ordinal()) {
         case 0:
            var10000 = formatNumber(value);
            break;
         case 1:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
            var10000 = formatNumber(value);
            break;
         case 2:
            long sec = Math.max(0L, (long)value);
            long hours = sec / 3600L;
            long mins = sec % 3600L / 60L;
            var10000 = hours <= 0L ? mins + "m" : hours + "h " + mins + "m";
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static String formatNumber(double value) {
      if (value >= (double)1.0E9F) {
         return String.format(Locale.US, "%.1fB", value / (double)1.0E9F).replace(".0B", "B");
      } else if (value >= (double)1000000.0F) {
         return String.format(Locale.US, "%.1fM", value / (double)1000000.0F).replace(".0M", "M");
      } else if (value >= (double)1000.0F) {
         return String.format(Locale.US, "%.1fK", value / (double)1000.0F).replace(".0K", "K");
      } else {
         return value == (double)((long)value) ? String.valueOf((long)value) : String.format(Locale.US, "%.2f", value);
      }
   }

   public static Board fromId(String id) {
      if (id == null) {
         return null;
      } else {
         Board var10000;
         switch (id.toLowerCase(Locale.ROOT).replace('_', '-')) {
            case "money":
               var10000 = StatsManager.Board.MONEY;
               break;
            case "deaths":
               var10000 = StatsManager.Board.DEATHS;
               break;
            case "playtime":
               var10000 = StatsManager.Board.PLAYTIME;
               break;
            case "blocks-placed":
            case "blocks_placed":
               var10000 = StatsManager.Board.BLOCKS_PLACED;
               break;
            case "blocks-broken":
            case "blocks_broken":
               var10000 = StatsManager.Board.BLOCKS_BROKEN;
               break;
            case "mobs-killed":
            case "mobs_killed":
               var10000 = StatsManager.Board.MOBS_KILLED;
               break;
            case "player-kills":
            case "player_kills":
               var10000 = StatsManager.Board.PLAYER_KILLS;
               break;
            case "shards":
               var10000 = StatsManager.Board.SHARDS;
               break;
            case "shop":
            case "shop_spent":
            case "shop-spent":
            case "spent":
               var10000 = StatsManager.Board.SHOP_SPENT;
               break;
            case "sell":
            case "sell_earned":
            case "sell-earned":
            case "earned":
               var10000 = StatsManager.Board.SELL_EARNED;
               break;
            default:
               var10000 = null;
         }

         return var10000;
      }
   }

   public static String toId(Board board) {
      String var10000;
      switch (board.ordinal()) {
         case 0 -> var10000 = "money";
         case 1 -> var10000 = "deaths";
         case 2 -> var10000 = "playtime";
         case 3 -> var10000 = "blocks-placed";
         case 4 -> var10000 = "blocks-broken";
         case 5 -> var10000 = "mobs-killed";
         case 6 -> var10000 = "player-kills";
         case 7 -> var10000 = "shards";
         case 8 -> var10000 = "shop_spent";
         case 9 -> var10000 = "sell_earned";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private double getShopSpent(UUID id) {
      synchronized(this.storeLock()) {
         return this.data.getDouble("players." + String.valueOf(id) + ".shop-spent", (double)0.0F);
      }
   }

   private double getSellEarned(UUID id) {
      double v;
      synchronized(this.storeLock()) {
         v = this.data.getDouble("players." + String.valueOf(id) + ".sell-earned", (double)0.0F);
      }

      if (v > (double)0.0F) {
         return v;
      } else {
         try {
            AstralSell sell = AstralSell.get();
            if (sell != null && sell.getProgress() != null) {
               return sell.getProgress().getMoneyMade(id);
            }
         } catch (Exception var6) {
         }

         return (double)0.0F;
      }
   }

   public void addShopSpent(UUID id, double amount) {
      if (!(amount <= (double)0.0F) && id != null) {
         synchronized(this.storeLock()) {
            double cur = this.data.getDouble("players." + String.valueOf(id) + ".shop-spent", (double)0.0F);
            this.data.set("players." + String.valueOf(id) + ".shop-spent", cur + amount);
         }

         try {
            this.save();
         } catch (Exception var8) {
         }

      }
   }

   public void addSellEarned(UUID id, double amount) {
      if (!(amount <= (double)0.0F) && id != null) {
         synchronized(this.storeLock()) {
            double cur = this.data.getDouble("players." + String.valueOf(id) + ".sell-earned", (double)0.0F);
            this.data.set("players." + String.valueOf(id) + ".sell-earned", cur + amount);
         }
      }
   }

   public static enum Board {
      MONEY,
      DEATHS,
      PLAYTIME,
      BLOCKS_PLACED,
      BLOCKS_BROKEN,
      MOBS_KILLED,
      PLAYER_KILLS,
      SHARDS,
      SHOP_SPENT,
      SELL_EARNED;

      // $FF: synthetic method
      private static Board[] $values() {
         return new Board[]{MONEY, DEATHS, PLAYTIME, BLOCKS_PLACED, BLOCKS_BROKEN, MOBS_KILLED, PLAYER_KILLS, SHARDS, SHOP_SPENT, SELL_EARNED};
      }
   }

   public static record Entry(UUID uuid, String name, double value) {
   }
}
