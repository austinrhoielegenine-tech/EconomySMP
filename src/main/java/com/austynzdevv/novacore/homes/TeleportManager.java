package com.austynzdevv.novacore.homes;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.HotbarUtil;
import com.austynzdevv.novacore.util.Tasks;
import com.austynzdevv.novacore.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportManager {
   private final EconomyHomes plugin;
   private final Map<UUID, Session> active = new ConcurrentHashMap();

   public TeleportManager(EconomyHomes plugin) {
      this.plugin = plugin;
   }

   public boolean start(Player player, String homeId, Location dest) {
      UUID uuid = player.getUniqueId();
      if (this.active.containsKey(uuid)) {
         player.sendMessage(ColorUtil.color(this.plugin.configs().msg("busy")));
         this.plugin.homes().sound(player, "denied");
         return false;
      } else {
         int delay = this.plugin.configs().teleportDelay();
         if (!player.hasPermission("novacorecore.system.homes.bypass") && delay > 0) {
            Location start = player.getLocation().clone();
            Session session = new Session(player, homeId, dest, start, delay);
            this.active.put(uuid, session);
            session.task = Tasks.runTimer(this.plugin.getCore(), () -> {
               if (!player.isOnline()) {
                  this.cancel(uuid);
               } else if (player.getLocation().distance(session.start) > this.plugin.configs().teleportDistance()) {
                  player.sendMessage(ColorUtil.color(this.plugin.configs().msg("moved")));
                  HotbarUtil.send(player, TextUtil.component(this.plugin.configs().actionBar("moved")));
                  this.plugin.homes().sound(player, "abort");
                  this.cancel(uuid);
               } else if (session.left <= 0) {
                  player.teleport(session.dest);
                  HotbarUtil.send(player, TextUtil.component(this.plugin.configs().actionBar("arrived").replace("%home%", homeId)));
                  this.plugin.homes().sound(player, "arrive");
                  this.cancel(uuid);
               } else {
                  String count = String.valueOf(session.left);
                  if (this.plugin.configs().cooldownChat()) {
                     for(String line : this.plugin.configs().cooldownChatMsgs()) {
                        player.sendMessage(TextUtil.component(line.replace("%count%", count).replace("%home%", homeId)));
                     }
                  }

                  if (this.plugin.configs().cooldownActionbar()) {
                     for(String line : this.plugin.configs().cooldownActionbarMsgs()) {
                        HotbarUtil.send(player, TextUtil.component(line.replace("%count%", count).replace("%home%", homeId)));
                     }
                  }

                  this.plugin.homes().sound(player, "countdown");
                  --session.left;
               }
            }, 0L, 20L);
            return true;
         } else {
            player.teleport(dest);
            HotbarUtil.send(player, TextUtil.component(this.plugin.configs().actionBar("arrived").replace("%home%", homeId)));
            this.plugin.homes().sound(player, "arrive");
            return true;
         }
      }
   }

   public void cancel(UUID uuid) {
      Session s = (Session)this.active.remove(uuid);
      if (s != null && s.task != -1) {
         Tasks.cancel(s.task);
         s.task = -1;
      }

   }

   public void cancelAll() {
      for(UUID id : this.active.keySet()) {
         this.cancel(id);
      }

   }

   public boolean isTeleporting(UUID uuid) {
      return this.active.containsKey(uuid);
   }

   private static final class Session {
      final Player player;
      final String homeId;
      final Location dest;
      final Location start;
      int left;
      int task = -1;

      Session(Player player, String homeId, Location dest, Location start, int delay) {
         this.player = player;
         this.homeId = homeId;
         this.dest = dest;
         this.start = start;
         this.left = delay;
      }
   }
}
