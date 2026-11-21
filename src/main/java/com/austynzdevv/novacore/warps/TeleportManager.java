package com.austynzdevv.novacore.warps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.HotbarUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import com.austynzdevv.novacore.util.Tasks;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class TeleportManager {
   private final EconomyWarps plugin;
   private final Map<UUID, Integer> tasks = new HashMap();
   private final Map<UUID, Location> starts = new HashMap();
   private final Set<UUID> teleporting = new HashSet();

   public TeleportManager(EconomyWarps plugin) {
      this.plugin = plugin;
   }

   public boolean isTeleporting(UUID id) {
      return this.teleporting.contains(id);
   }

   public void start(final Player player, final NamedLocation named, final String type) {
      if (this.isTeleporting(player.getUniqueId())) {
         player.sendMessage(ColorUtil.parse(this.msg("already-teleporting", "&cYou are already teleporting!")));
      } else {
         final Location dest = named.toLocation();
         if (dest == null) {
            player.sendMessage(ColorUtil.parse(this.msg("location-invalid", "&cThat location is invalid!")));
         } else if (named.isFull() && !player.hasPermission("novacorecore.system.warps.bypass")) {
            player.sendMessage(ColorUtil.parse(this.msg(type + "-full", "&cThis area is already full!").replace("%name%", named.getName())));
         } else {
            final int delay = this.delayFor(type);
            boolean allowBypass = this.plugin.getConfig().getBoolean("ops-bypass-countdown", false) && player.hasPermission("novacorecore.system.warps.bypass");
            if (delay > 0 && !allowBypass && !this.bypassWorld(player.getWorld().getName())) {
               this.cancel(player.getUniqueId());
               this.teleporting.add(player.getUniqueId());
               this.starts.put(player.getUniqueId(), player.getLocation().clone());
               this.play(player, "teleport-start");
               final int[] __tid = new int[]{-1};
               BukkitRunnable __t = new BukkitRunnable() {
                  int left = delay;

                  public void run() {
                     if (!player.isOnline()) {
                        TeleportManager.this.cleanup(player.getUniqueId());
                        Tasks.cancel(__tid[0]);
                     } else {
                        if (TeleportManager.this.plugin.getConfig().getBoolean(TeleportManager.this.cancelKey(type), true)) {
                           Location start = (Location)TeleportManager.this.starts.get(player.getUniqueId());
                           double max = TeleportManager.this.plugin.getConfig().getDouble("cancel-distance", (double)0.5F);
                           if (start != null && player.getLocation().distanceSquared(start) > max * max) {
                              player.sendMessage(ColorUtil.parse(TeleportManager.this.msg("teleport-cancelled", "&cTeleport cancelled because you moved.")));
                              TeleportManager.this.play(player, "teleport-cancel");
                              TeleportManager.this.cleanup(player.getUniqueId());
                              Tasks.cancel(__tid[0]);
                              return;
                           }
                        }

                        if (this.left <= 0) {
                           TeleportManager.this.finish(player, dest, named.getName(), type);
                           TeleportManager.this.cleanup(player.getUniqueId());
                           Tasks.cancel(__tid[0]);
                        } else {
                           String bar = TeleportManager.this.msg("teleport-countdown", "&7Teleporting in &#00A0FC%seconds% &7seconds...").replace("%seconds%", String.valueOf(this.left)).replace("%count%", String.valueOf(this.left)).replace("%name%", named.getName());
                           if (TeleportManager.this.plugin.getConfig().getBoolean("messages-action-bar", true)) {
                              HotbarUtil.send(player, ColorUtil.parse(bar));
                           }

                           if (TeleportManager.this.plugin.getConfig().getBoolean("messages-chat", false)) {
                              player.sendMessage(ColorUtil.parse(bar));
                           }

                           TeleportManager.this.play(player, "countdown");
                           --this.left;
                        }
                     }
                  }
               };
               __tid[0] = Tasks.runTimer(this.plugin.getCore(), __t, 0L, 20L);
               this.tasks.put(player.getUniqueId(), __tid[0]);
            } else {
               this.finish(player, dest, named.getName(), type);
            }
         }
      }
   }

   public void teleportNow(Player player, NamedLocation named, String type) {
      Location dest = named.toLocation();
      if (dest != null) {
         this.finish(player, dest, named.getName(), type);
      }
   }

   private void finish(Player player, Location dest, String name, String type) {
      player.teleport(dest);
      String var10000;
      switch (type) {
         case "spawn" -> var10000 = "teleported-spawn";
         case "afk" -> var10000 = "teleported-afk";
         default -> var10000 = "teleported-warp";
      }

      String key = var10000;
      switch (type) {
         case "spawn" -> var10000 = "&aYou have been teleported to Spawn %name%!";
         case "afk" -> var10000 = "&aYou have been teleported to AFK area %name%!";
         default -> var10000 = "&7Teleported to warp &#00A0FC%warp%.";
      }

      String def = var10000;
      player.sendMessage(ColorUtil.parse(this.msg(key, def).replace("%name%", name).replace("%warp%", name)));
      this.play(player, "teleport-success");
   }

   public void onMove(Player player) {
      if (this.teleporting.contains(player.getUniqueId())) {
         ;
      }
   }

   public void cancel(UUID id) {
      Integer t = (Integer)this.tasks.remove(id);
      if (t != null) {
         Tasks.cancel(t);
      }

      this.cleanup(id);
   }

   private void cleanup(UUID id) {
      this.teleporting.remove(id);
      this.starts.remove(id);
      this.tasks.remove(id);
   }

   private int delayFor(String type) {
      int var10000;
      switch (type) {
         case "spawn" -> var10000 = this.plugin.getConfig().getInt("spawn-delay", 5);
         case "afk" -> var10000 = this.plugin.getConfig().getInt("afk-delay", 5);
         default -> var10000 = this.plugin.getConfig().getInt("warp-delay", 5);
      }

      return var10000;
   }

   private String cancelKey(String type) {
      String var10000;
      switch (type) {
         case "spawn" -> var10000 = "cancel-on-move";
         case "afk" -> var10000 = "afk-cancel-on-move";
         default -> var10000 = "warp-cancel-on-move";
      }

      return var10000;
   }

   private boolean bypassWorld(String world) {
      return this.plugin.getConfig().getStringList("countdown-bypass-worlds").stream().anyMatch((w) -> w.equalsIgnoreCase(world));
   }

   private String msg(String key, String def) {
      return this.plugin.getMessages().get(key, def);
   }

   private void play(Player player, String key) {
      try {
         if (!this.plugin.getConfig().getBoolean("sounds.enable", true)) {
            return;
         }

         String sound = this.plugin.getConfig().getString("sounds." + key, "");
         if (sound == null || sound.isBlank()) {
            return;
         }

         SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.toUpperCase().replace('.', '_')), 1.0F, 1.0F);
      } catch (Exception var4) {
      }

   }
}
