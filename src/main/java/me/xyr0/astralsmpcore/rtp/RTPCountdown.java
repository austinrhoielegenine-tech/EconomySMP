package me.xyr0.astralsmpcore.rtp;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.astralsmpcore.rtp.util.MessageUtil;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.HotbarUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import me.xyr0.astralsmpcore.util.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RTPCountdown {
   private static final Map<UUID, RTPCountdown> active = new ConcurrentHashMap();
   private final AstralRTP plugin;
   private final Player player;
   private final String world;
   private final Location start;
   private final double maxDistSq;
   private int remaining;
   private int taskId = -1;

   public RTPCountdown(AstralRTP plugin, Player player, String world) {
      this.plugin = plugin;
      this.player = player;
      this.world = world;
      this.start = player.getLocation().clone();
      this.remaining = plugin.getConfig().getInt("wait-time-seconds", 5);
      double range = plugin.getConfig().getDouble("allowed-walk-range", (double)0.0F);
      if (range < (double)0.0F) {
         range = (double)0.0F;
      }

      this.maxDistSq = range * range;
   }

   public static boolean isRunning(Player player) {
      return active.containsKey(player.getUniqueId());
   }

   public static void cancel(Player player) {
      RTPCountdown c = (RTPCountdown)active.remove(player.getUniqueId());
      if (c != null && c.taskId != -1) {
         Tasks.cancel(c.taskId);
         c.taskId = -1;
      }

   }

   public void start() {
      if (!isRunning(this.player)) {
         active.put(this.player.getUniqueId(), this);
         int[] __tid = new int[]{-1};
         BukkitRunnable __t = new BukkitRunnable() {
            public void run() {
               if (!RTPCountdown.this.player.isOnline()) {
                  RTPCountdown.this.stop();
               } else {
                  Location now = RTPCountdown.this.player.getLocation();
                  if (!now.getWorld().equals(RTPCountdown.this.start.getWorld())) {
                     RTPCountdown.this.cancelMove();
                  } else {
                     if (RTPCountdown.this.maxDistSq <= (double)0.0F) {
                        if (now.getBlockX() != RTPCountdown.this.start.getBlockX() || now.getBlockY() != RTPCountdown.this.start.getBlockY() || now.getBlockZ() != RTPCountdown.this.start.getBlockZ()) {
                           RTPCountdown.this.cancelMove();
                           return;
                        }
                     } else if (now.distanceSquared(RTPCountdown.this.start) > RTPCountdown.this.maxDistSq) {
                        RTPCountdown.this.cancelMove();
                        return;
                     }

                     if (RTPCountdown.this.remaining <= 0) {
                        RTPCountdown.this.stop();
                        HotbarUtil.send(RTPCountdown.this.player, Component.empty());
                        RTPCountdown.this.plugin.getRTPManager().teleport(RTPCountdown.this.player, RTPCountdown.this.world);
                     } else {
                        MessageUtil.send(RTPCountdown.this.player, "rtp.starting", "%time%", String.valueOf(RTPCountdown.this.remaining));
                        String bar = ColorUtil.color("&7Teleporting in &b" + RTPCountdown.this.remaining + "s&7...");
                        HotbarUtil.send(RTPCountdown.this.player, LegacyComponentSerializer.legacySection().deserialize(bar));

                        try {
                           SoundUtil.play(RTPCountdown.this.player, RTPCountdown.this.player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.5F, 1.3F);
                        } catch (Exception var4) {
                        }

                        --RTPCountdown.this.remaining;
                     }
                  }
               }
            }
         };
         __tid[0] = Tasks.runTimer(this.plugin.getCore(), __t, 0L, 20L);
         this.taskId = __tid[0];
      }
   }

   private void cancelMove() {
      MessageUtil.send(this.player, "rtp.cancelled");
      HotbarUtil.send(this.player, Component.empty());
      this.stop();
   }

   private void stop() {
      active.remove(this.player.getUniqueId());
      if (this.taskId != -1) {
         Tasks.cancel(this.taskId);
         this.taskId = -1;
      }

   }
}
