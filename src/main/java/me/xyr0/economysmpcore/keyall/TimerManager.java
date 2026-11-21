package me.xyr0.economysmpcore.keyall;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.economysmpcore.data.PlayerDataStore;
import me.xyr0.economysmpcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class TimerManager {
   private final AstralKeyall plugin;
   private final Map<UUID, Integer> playerRemaining = new ConcurrentHashMap();
   private int globalRemaining;
   private int duration;
   private boolean perPlayer;
   private int task = -1;

   public TimerManager(AstralKeyall plugin) {
      this.plugin = plugin;
      this.reloadSettings();
      this.loadData();
      this.start();
   }

   public void reloadSettings() {
      this.duration = Math.max(1, this.plugin.getConfig().getInt("keyall-timer", 3600));
      this.perPlayer = this.plugin.getConfig().getBoolean("keyall-timer-type.per-player", false);
      if (this.globalRemaining <= 0 || this.globalRemaining > this.duration) {
         this.globalRemaining = this.duration;
      }

   }

   public void start() {
      this.stop();
      this.task = Tasks.runTimer(this.plugin.getCore(), this::tick, 20L, 20L);
   }

   public void stop() {
      if (this.task != -1) {
         Tasks.cancel(this.task);
         this.task = -1;
      }

   }

   private void tick() {
      if (this.perPlayer) {
         for(Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();
            int left = (Integer)this.playerRemaining.getOrDefault(id, this.duration) - 1;
            if (left <= 0) {
               this.playerRemaining.put(id, this.duration);
               this.plugin.getKeys().runKeyall(player);
            } else {
               this.playerRemaining.put(id, left);
            }
         }
      } else {
         --this.globalRemaining;
         if (this.globalRemaining <= 0) {
            this.globalRemaining = this.duration;
            this.plugin.getKeys().runKeyall();
         }
      }

   }

   public int getRemaining(Player player) {
      if (this.perPlayer) {
         return player == null ? this.duration : (Integer)this.playerRemaining.getOrDefault(player.getUniqueId(), this.duration);
      } else {
         return Math.max(0, this.globalRemaining);
      }
   }

   public void reset() {
      this.globalRemaining = this.duration;
      this.playerRemaining.clear();

      for(Player player : Bukkit.getOnlinePlayers()) {
         this.playerRemaining.put(player.getUniqueId(), this.duration);
      }

      this.saveData();
   }

   public void onJoin(Player player) {
      if (this.perPlayer) {
         this.playerRemaining.putIfAbsent(player.getUniqueId(), this.duration);
      }

   }

   public void onQuit(Player player) {
      if (this.perPlayer) {
         this.saveData();
      }

   }

   public String format(Player player) {
      int seconds = this.getRemaining(player);
      if (seconds <= 0) {
         return this.plugin.getConfig().getString("placeholder.ready", "&aReady!");
      } else {
         int m = seconds / 60;
         int s = seconds % 60;
         String format = this.plugin.getConfig().getString("placeholder.format", "%minutes%m %seconds%s");
         return format.replace("%minutes%", String.valueOf(m)).replace("%seconds%", String.valueOf(s));
      }
   }

   public boolean isPerPlayer() {
      return this.perPlayer;
   }

   public void loadData() {
      YamlConfiguration cfg = PlayerDataStore.get().config();
      this.globalRemaining = cfg.getInt("keyall.global", this.duration);
      this.playerRemaining.clear();
      if (cfg.isConfigurationSection("keyall.players")) {
         for(String key : cfg.getConfigurationSection("keyall.players").getKeys(false)) {
            try {
               UUID id = UUID.fromString(key);
               this.playerRemaining.put(id, cfg.getInt("keyall.players." + key, this.duration));
            } catch (Exception var5) {
            }
         }
      }

   }

   public void saveData() {
      YamlConfiguration cfg = PlayerDataStore.get().config();
      cfg.set("keyall.global", this.globalRemaining);
      cfg.set("keyall.players", (Object)null);

      for(Map.Entry<UUID, Integer> e : this.playerRemaining.entrySet()) {
         cfg.set("keyall.players." + ((UUID)e.getKey()).toString(), e.getValue());
      }

      PlayerDataStore.get().save();
   }
}
