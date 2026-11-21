package com.austynzdevv.novacore.rtpq;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import com.austynzdevv.novacore.util.SoundUtil;
import com.austynzdevv.novacore.util.Tasks;
import com.austynzdevv.novacore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class QueueManager {
   private final EconomyRTPQ plugin;
   private final Set<UUID> queued = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Integer> music = new ConcurrentHashMap();
   private final Map<UUID, Integer> bars = new ConcurrentHashMap();
   private final Map<UUID, AtomicInteger> ticks = new ConcurrentHashMap();

   public QueueManager(EconomyRTPQ plugin) {
      this.plugin = plugin;
   }

   public boolean isQueued(Player p) {
      return p != null && this.queued.contains(p.getUniqueId());
   }

   public void addPlayer(Player player, String world) {
      this.addPlayer(player);
   }

   public void addPlayer(Player player) {
      if (!this.isQueued(player)) {
         for(UUID id : new HashSet<>(this.queued)) {
            Player other = Bukkit.getPlayer(id);
            if (other != null && other.isOnline() && !other.equals(player)) {
               this.queued.remove(id);
               this.stopEffects(other);
               this.match(player, other);
               return;
            }
         }

         this.queued.add(player.getUniqueId());
         this.startEffects(player);
         TextUtil.send(player, this.plugin.getConfig().getString("messages.queued", "&7Searching for a partner..."));
      }
   }

   public void removePlayer(Player player) {
      if (player != null) {
         this.queued.remove(player.getUniqueId());
         this.stopEffects(player);
         TextUtil.send(player, this.plugin.getConfig().getString("messages.left", "&7Left RTP queue."));
         TextUtil.actionBar(player, " ");
      }
   }

   private void startEffects(Player player) {
      this.stopEffects(player);
      this.ticks.put(player.getUniqueId(), new AtomicInteger(0));
      int m = Tasks.runTimer(this.plugin.getCore(), () -> {
         if (player.isOnline() && this.isQueued(player)) {
            try {
               SoundUtil.play(player, player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.15F, 1.2F);
            } catch (Exception var3) {
            }

         } else {
            this.stopEffects(player);
         }
      }, 20L, 40L);
      this.music.put(player.getUniqueId(), m);
      int b = Tasks.runTimer(this.plugin.getCore(), () -> {
         if (player.isOnline() && this.isQueued(player)) {
            int t = ((AtomicInteger)this.ticks.computeIfAbsent(player.getUniqueId(), (k) -> new AtomicInteger(0))).incrementAndGet();
            String dots = ".".repeat(t % 3 + 1);
            int waiting = this.queued.size();
            String bar = this.plugin.getConfig().getString("messages.actionbar", "&#37BFF9RTP Queue &8| &fSearching" + dots + " &8| &7Queued: &#37BFF9%count%").replace("%count%", String.valueOf(waiting)).replace("%dots%", dots).replace("Searching...", "Searching" + dots);
            if (!bar.contains(dots) && !this.plugin.getConfig().contains("messages.actionbar")) {
               bar = "&#37BFF9RTP Queue &8| &fSearching" + dots + " &8| &7Queued: &#37BFF9" + waiting;
            }

            TextUtil.actionBar(player, bar);
         } else {
            this.stopEffects(player);
         }
      }, 0L, 20L);
      this.bars.put(player.getUniqueId(), b);
   }

   private void stopEffects(Player player) {
      if (player != null) {
         Integer m = (Integer)this.music.remove(player.getUniqueId());
         if (m != null) {
            Tasks.cancel(m);
         }

         Integer b = (Integer)this.bars.remove(player.getUniqueId());
         if (b != null) {
            Tasks.cancel(b);
         }

         this.ticks.remove(player.getUniqueId());

         try {
            player.stopAllSounds();
         } catch (Exception var5) {
         }

      }
   }

   private void match(Player a, Player b) {
      this.stopEffects(a);
      this.stopEffects(b);
      TextUtil.send(a, this.plugin.getConfig().getString("messages.matched", "&aMatch found! Teleporting..."));
      TextUtil.send(b, this.plugin.getConfig().getString("messages.matched", "&aMatch found! Teleporting..."));
      TextUtil.actionBar(a, "&aMatched");
      TextUtil.actionBar(b, "&aMatched");
      Location loc = this.randomOverworld();
      if (loc == null) {
         TextUtil.send(a, "&cCould not find a safe RTP location.");
         TextUtil.send(b, "&cCould not find a safe RTP location.");
      } else {
         a.teleport(loc);
         b.teleport(loc.clone().add((double)2.0F, (double)0.0F, (double)0.0F));

         try {
            SoundUtil.play(a, a.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            SoundUtil.play(b, b.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
         } catch (Exception var5) {
         }

      }
   }

   private Location randomOverworld() {
      World world = Bukkit.getWorld(this.plugin.getConfig().getString("world", "world"));
      if (world == null) {
         world = Bukkit.getWorlds().isEmpty() ? null : (World)Bukkit.getWorlds().get(0);
      }

      if (world == null) {
         return null;
      } else {
         int min = this.plugin.getConfig().getInt("rtp.min-range", 500);
         int max = this.plugin.getConfig().getInt("rtp.max-range", 5000);

         for(int i = 0; i < 25; ++i) {
            int x = ThreadLocalRandom.current().nextInt(min, max + 1) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
            int z = ThreadLocalRandom.current().nextInt(min, max + 1) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
            int y = world.getHighestBlockYAt(x, z) + 1;
            Location loc = new Location(world, (double)x + (double)0.5F, (double)y, (double)z + (double)0.5F);
            if (y > world.getMinHeight() + 5) {
               return loc;
            }
         }

         return world.getSpawnLocation();
      }
   }

   public void shutdown() {
      for(UUID id : new HashSet<>(this.music.keySet())) {
         Player p = Bukkit.getPlayer(id);
         if (p != null) {
            this.stopEffects(p);
         }
      }

      for(UUID id : new HashSet<>(this.bars.keySet())) {
         Player p = Bukkit.getPlayer(id);
         if (p != null) {
            this.stopEffects(p);
         }
      }

      this.queued.clear();
      this.music.clear();
      this.bars.clear();
      this.ticks.clear();
   }
}
