package com.austynzdevv.novacore.tpa;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import com.austynzdevv.novacore.tpa.gui.TpaMenus;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.HotbarUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import com.austynzdevv.novacore.util.Tasks;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class TpaManager {
   private final EconomyTPA plugin;
   private final Map<UUID, Map<UUID, TpaRequest>> incoming = new HashMap();
   private final Map<UUID, Set<UUID>> outgoing = new HashMap();
   private final Map<String, Integer> timeoutTasks = new HashMap();
   private final Map<UUID, Integer> teleportTasks = new HashMap();
   private final Set<UUID> teleporting = new HashSet();
   private final Map<UUID, Location> teleportStart = new HashMap();
   private final Set<UUID> tpAuto = new HashSet();
   private final Set<UUID> tpaDisabled = new HashSet();
   private final Set<UUID> tpahereDisabled = new HashSet();
   private final Set<UUID> confirmGuiDisabled = new HashSet();
   private final Map<UUID, Set<UUID>> ignoreList = new HashMap();
   private File settingsFile;
   private FileConfiguration settings;
   private int tpAutoBarTask = -1;

   public TpaManager(EconomyTPA plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.settingsFile = new File(this.plugin.getDataFolder(), "settings.yml");
      if (!this.settingsFile.exists()) {
         try {
            this.plugin.getDataFolder().mkdirs();
            this.settingsFile.createNewFile();
         } catch (IOException var13) {
         }
      }

      this.settings = YamlConfiguration.loadConfiguration(this.settingsFile);
      this.tpAuto.clear();
      this.tpaDisabled.clear();
      this.tpahereDisabled.clear();
      this.confirmGuiDisabled.clear();
      this.ignoreList.clear();

      for(String raw : this.settings.getStringList("tpauto")) {
         try {
            this.tpAuto.add(UUID.fromString(raw));
         } catch (Exception var12) {
         }
      }

      for(String raw : this.settings.getStringList("tpa-disabled")) {
         try {
            this.tpaDisabled.add(UUID.fromString(raw));
         } catch (Exception var11) {
         }
      }

      for(String raw : this.settings.getStringList("tpahere-disabled")) {
         try {
            this.tpahereDisabled.add(UUID.fromString(raw));
         } catch (Exception var10) {
         }
      }

      for(String raw : this.settings.getStringList("confirm-gui-disabled")) {
         try {
            this.confirmGuiDisabled.add(UUID.fromString(raw));
         } catch (Exception var9) {
         }
      }

      if (this.settings.getConfigurationSection("ignore") != null) {
         for(String key : this.settings.getConfigurationSection("ignore").getKeys(false)) {
            try {
               UUID id = UUID.fromString(key);
               Set<UUID> set = new HashSet();

               for(String o : this.settings.getStringList("ignore." + key)) {
                  try {
                     set.add(UUID.fromString(o));
                  } catch (Exception var8) {
                  }
               }

               this.ignoreList.put(id, set);
            } catch (Exception var14) {
            }
         }
      }

   }

   public void saveSettings() {
      if (this.settings != null && this.settingsFile != null) {
         this.settings.set("tpauto", this.tpAuto.stream().map(UUID::toString).toList());
         this.settings.set("tpa-disabled", this.tpaDisabled.stream().map(UUID::toString).toList());
         this.settings.set("tpahere-disabled", this.tpahereDisabled.stream().map(UUID::toString).toList());
         this.settings.set("confirm-gui-disabled", this.confirmGuiDisabled.stream().map(UUID::toString).toList());
         this.settings.set("ignore", (Object)null);

         for(Map.Entry<UUID, Set<UUID>> e : this.ignoreList.entrySet()) {
            this.settings.set("ignore." + String.valueOf(e.getKey()), e.getValue().stream().map(UUID::toString).toList());
         }

         try {
            this.settings.save(this.settingsFile);
         } catch (IOException var3) {
         }

      }
   }

   public boolean isTpAuto(UUID id) {
      return this.tpAuto.contains(id);
   }

   public boolean toggleTpAuto(UUID id) {
      if (this.tpAuto.contains(id)) {
         this.tpAuto.remove(id);
         this.saveSettings();
         return false;
      } else {
         this.tpAuto.add(id);
         this.saveSettings();
         return true;
      }
   }

   public boolean isTpaDisabled(UUID id) {
      return this.tpaDisabled.contains(id);
   }

   public boolean toggleTpa(UUID id) {
      if (this.tpaDisabled.contains(id)) {
         this.tpaDisabled.remove(id);
         this.saveSettings();
         return true;
      } else {
         this.tpaDisabled.add(id);
         this.saveSettings();
         return false;
      }
   }

   public boolean isTpahereDisabled(UUID id) {
      return this.tpahereDisabled.contains(id);
   }

   public boolean toggleTpahere(UUID id) {
      if (this.tpahereDisabled.contains(id)) {
         this.tpahereDisabled.remove(id);
         this.saveSettings();
         return true;
      } else {
         this.tpahereDisabled.add(id);
         this.saveSettings();
         return false;
      }
   }

   public boolean isConfirmGuiEnabled(UUID id) {
      return !this.confirmGuiDisabled.contains(id);
   }

   public boolean toggleConfirmGui(UUID id) {
      if (this.confirmGuiDisabled.contains(id)) {
         this.confirmGuiDisabled.remove(id);
         this.saveSettings();
         return true;
      } else {
         this.confirmGuiDisabled.add(id);
         this.saveSettings();
         return false;
      }
   }

   public boolean isIgnoring(UUID player, UUID other) {
      Set<UUID> set = (Set)this.ignoreList.get(player);
      return set != null && set.contains(other);
   }

   public boolean toggleIgnore(UUID player, UUID other) {
      Set<UUID> set = (Set)this.ignoreList.computeIfAbsent(player, (k) -> new HashSet());
      if (set.contains(other)) {
         set.remove(other);
         this.saveSettings();
         return false;
      } else {
         set.add(other);
         this.saveSettings();
         return true;
      }
   }

   public TpaRequest getIncoming(UUID target) {
      Map<UUID, TpaRequest> map = (Map)this.incoming.get(target);
      if (map != null && !map.isEmpty()) {
         TpaRequest oldest = null;

         for(TpaRequest r : map.values()) {
            if (oldest == null || r.getCreatedAt() < oldest.getCreatedAt()) {
               oldest = r;
            }
         }

         return oldest;
      } else {
         return null;
      }
   }

   public TpaRequest getIncomingFrom(UUID target, UUID sender) {
      Map<UUID, TpaRequest> map = (Map)this.incoming.get(target);
      return map == null ? null : (TpaRequest)map.get(sender);
   }

   public boolean hasOutgoing(UUID sender) {
      Set<UUID> set = (Set)this.outgoing.get(sender);
      return set != null && !set.isEmpty();
   }

   public boolean hasOutgoingTo(UUID sender, UUID target) {
      Set<UUID> set = (Set)this.outgoing.get(sender);
      return set != null && set.contains(target);
   }

   public boolean isTeleporting(UUID id) {
      return this.teleporting.contains(id);
   }

   private static String pairKey(UUID target, UUID sender) {
      String var10000 = String.valueOf(target);
      return var10000 + ":" + String.valueOf(sender);
   }

   public void sendRequest(Player sender, Player target, TpaRequest.Type type) {
      TpaRequest request = new TpaRequest(sender.getUniqueId(), target.getUniqueId(), type);
      ((Map)this.incoming.computeIfAbsent(target.getUniqueId(), (k) -> new HashMap())).put(sender.getUniqueId(), request);
      ((Set)this.outgoing.computeIfAbsent(sender.getUniqueId(), (k) -> new HashSet())).add(target.getUniqueId());
      int timeout = Math.max(5, this.plugin.getConfig().getInt("teleport.tpa-timeout-seconds", 60));
      this.cancelTimeout(target.getUniqueId(), sender.getUniqueId());
      int task = Tasks.runLater(this.plugin.getCore(), () -> {
         TpaRequest current = this.getIncomingFrom(target.getUniqueId(), sender.getUniqueId());
         if (current != null && current.getSender().equals(sender.getUniqueId())) {
            this.clearRequest(current);
            if (sender.isOnline()) {
               sender.sendMessage(ColorUtil.parse(this.msg("tpa-request-timeout", "&cYour teleport request to &#37BFF8{target}&c has expired.").replace("{target}", target.getName())));
            }

            if (target.isOnline()) {
               target.sendMessage(ColorUtil.parse(this.msg("tpa-request-expired", "&cThe teleport request from &#37BFF8{sender}&c has expired.").replace("{sender}", sender.getName())));
            }
         }

      }, (long)timeout * 20L);
      this.timeoutTasks.put(pairKey(target.getUniqueId(), sender.getUniqueId()), task);
      if (type == TpaRequest.Type.TPA) {
         sender.sendMessage(ColorUtil.parse(this.msg("tpa-request-sent", "&7Tpa request sent to &#37BFF8{target}&7.").replace("{target}", target.getName())));
         target.sendMessage(ColorUtil.parse(this.msg("tpa-request-received", "&7&#37BFF8{sender} &7wants to teleport to you.").replace("{sender}", sender.getName())));
         HotbarUtil.send(sender, ColorUtil.parse(this.msg("actionbar-tpa-sent", "&7Tpa sent to &#37BFF8{target}").replace("{target}", target.getName())));
         HotbarUtil.send(target, ColorUtil.parse(this.msg("actionbar-tpa-received", "&#37BFF8{sender} &7wants to teleport to you").replace("{sender}", sender.getName())));
      } else {
         sender.sendMessage(ColorUtil.parse(this.msg("tpahere-request-sent", "&7Tpahere request sent to &#37BFF8{target}&7.").replace("{target}", target.getName())));
         target.sendMessage(ColorUtil.parse(this.msg("tpahere-request-received", "&7&#37BFF8{sender} &7wants you to teleport to them.").replace("{sender}", sender.getName())));
         HotbarUtil.send(sender, ColorUtil.parse(this.msg("actionbar-tpahere-sent", "&7Tpahere sent to &#37BFF8{target}").replace("{target}", target.getName())));
         HotbarUtil.send(target, ColorUtil.parse(this.msg("actionbar-tpahere-received", "&#37BFF8{sender} &7requested tpahere").replace("{sender}", sender.getName())));
      }

      String click = this.msg("click-accept-text", "&#37BFF8[CLICK] ");
      String hover = this.msg("click-accept-hover", "Click to accept request");
      String orType = this.msg("or-type-text", "&7or type &#37BFF8/tpaccept {sender}").replace("{sender}", sender.getName());
      target.sendMessage(ColorUtil.parse(click).clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getName())).hoverEvent(HoverEvent.showText(ColorUtil.parse(hover))).append(ColorUtil.parse(orType)));
      if (this.isTpAuto(target.getUniqueId())) {
         Tasks.runLater(this.plugin.getCore(), () -> {
            if (this.getIncomingFrom(target.getUniqueId(), sender.getUniqueId()) != null) {
               this.accept(target, sender.getName());
            }

         }, 5L);
      }

      this.play(sender, "success");
      this.play(target, "request");
   }

   public void openAcceptGui(Player target, String senderName) {
      TpaRequest request = this.resolveIncoming(target.getUniqueId(), senderName);
      if (request != null) {
         Player sender = Bukkit.getPlayer(request.getSender());
         if (sender != null && sender.isOnline()) {
            if (!this.isConfirmGuiEnabled(target.getUniqueId())) {
               this.accept(target, sender.getName());
            } else {
               TpaMenus.openAccept(target, sender, request.getType());
            }
         } else {
            this.clearRequest(request);
            target.sendMessage(ColorUtil.parse(this.msg("player-not-found", "&cPlayer not found!")));
         }
      } else {
         if (senderName != null && !senderName.isBlank()) {
            target.sendMessage(ColorUtil.parse(this.msg("tpa-no-active-request", "&cYou do not have an active teleport request from &#37BFF8{sender}&c.").replace("{sender}", senderName)));
         } else {
            target.sendMessage(ColorUtil.parse(this.msg("tpa-no-pending-request", "&cThis tpa does not exist.")));
         }

      }
   }

   public void accept(Player target, String senderName) {
      TpaRequest request = this.resolveIncoming(target.getUniqueId(), senderName);
      if (request != null) {
         Player sender = Bukkit.getPlayer(request.getSender());
         if (sender != null && sender.isOnline()) {
            this.clearRequest(request);
            if (request.getType() == TpaRequest.Type.TPA) {
               target.sendMessage(ColorUtil.parse(this.msg("tpa-accepted-target", "&7You accepted the tpa from &#37BFF8{sender}&7.").replace("{sender}", sender.getName())));
               sender.sendMessage(ColorUtil.parse(this.msg("tpa-accepted-sender", "&7Your tpa to &#37BFF8{target} &7was accepted.").replace("{target}", target.getName())));
               this.startTeleport(sender, target);
            } else {
               target.sendMessage(ColorUtil.parse(this.msg("tpahere-accepted-target", "&7You accepted the tpahere.")));
               sender.sendMessage(ColorUtil.parse(this.msg("tpahere-accepted-sender", "&7Your tpahere to &#37BFF8{target} &7was accepted.").replace("{target}", target.getName())));
               this.startTeleport(target, sender);
            }

            this.play(target, "success");
            this.play(sender, "success");
         } else {
            this.clearRequest(request);
            target.sendMessage(ColorUtil.parse(this.msg("player-not-found", "&cPlayer not found!")));
         }
      } else {
         if (senderName != null && !senderName.isBlank()) {
            target.sendMessage(ColorUtil.parse(this.msg("tpa-no-active-request", "&cYou do not have an active teleport request from &#37BFF8{sender}&c.").replace("{sender}", senderName)));
         } else {
            target.sendMessage(ColorUtil.parse(this.msg("tpa-no-pending-request", "&cThis tpa does not exist.")));
         }

      }
   }

   public void deny(Player target, String senderName) {
      TpaRequest request = this.resolveIncoming(target.getUniqueId(), senderName);
      if (request != null) {
         Player sender = Bukkit.getPlayer(request.getSender());
         this.clearRequest(request);
         if (request.getType() == TpaRequest.Type.TPA) {
            target.sendMessage(ColorUtil.parse(this.msg("tpa-denied-target", "&cYou denied &#37BFF8{sender}'s&c teleport request.").replace("{sender}", sender != null ? sender.getName() : "player")));
            if (sender != null) {
               sender.sendMessage(ColorUtil.parse(this.msg("tpa-denied-sender", "&cYour teleport request to &#37BFF8{target}&c has been denied.").replace("{target}", target.getName())));
            }
         } else {
            target.sendMessage(ColorUtil.parse(this.msg("tpahere-denied-target", "&cYou denied &#37BFF8{sender}'s&c tpahere request.").replace("{sender}", sender != null ? sender.getName() : "player")));
            if (sender != null) {
               sender.sendMessage(ColorUtil.parse(this.msg("tpahere-denied-sender", "&cYour tpahere request to &#37BFF8{target}&c has been denied.").replace("{target}", target.getName())));
            }
         }

         this.play(target, "error");
         if (sender != null) {
            this.play(sender, "error");
         }

      } else {
         if (senderName != null && !senderName.isBlank()) {
            target.sendMessage(ColorUtil.parse(this.msg("tpa-no-active-request", "&cYou do not have an active teleport request from &#37BFF8{sender}&c.").replace("{sender}", senderName)));
         } else {
            target.sendMessage(ColorUtil.parse(this.msg("tpa-no-pending-request", "&cThis tpa does not exist.")));
         }

      }
   }

   public void cancel(Player sender) {
      Set<UUID> targets = (Set)this.outgoing.get(sender.getUniqueId());
      if (targets != null && !targets.isEmpty()) {
         for(UUID targetId : new HashSet<>(targets)) {
            TpaRequest request = this.getIncomingFrom(targetId, sender.getUniqueId());
            if (request != null) {
               this.clearRequest(request);
               Player target = Bukkit.getPlayer(targetId);
               if (target != null) {
                  target.sendMessage(ColorUtil.parse(this.msg("tpa-cancelled-target", "&c&#37BFF8{sender} &ccancelled their teleport request.").replace("{sender}", sender.getName())));
               }
            }
         }

         sender.sendMessage(ColorUtil.parse(this.msg("tpa-cancelled-sender", "&cYou cancelled your teleport request.")));
      } else {
         sender.sendMessage(ColorUtil.parse(this.msg("tpa-no-pending-request", "&cThis tpa does not exist.")));
      }
   }

   private TpaRequest resolveIncoming(UUID targetId, String senderName) {
      if (senderName != null && !senderName.isBlank()) {
         Player named = Bukkit.getPlayerExact(senderName);
         if (named != null) {
            return this.getIncomingFrom(targetId, named.getUniqueId());
         } else {
            Map<UUID, TpaRequest> map = (Map)this.incoming.get(targetId);
            if (map != null) {
               for(Map.Entry<UUID, TpaRequest> e : map.entrySet()) {
                  Player p = Bukkit.getPlayer((UUID)e.getKey());
                  if (p != null && p.getName().equalsIgnoreCase(senderName)) {
                     return (TpaRequest)e.getValue();
                  }
               }
            }

            return null;
         }
      } else {
         return this.getIncoming(targetId);
      }
   }

   private void startTeleport(final Player traveler, final Player destination) {
      final int delay = Math.max(0, this.plugin.getConfig().getInt("teleport.tpa-teleport-delay-seconds", 5));
      if (delay <= 0) {
         if (destination.isOnline()) {
            traveler.teleport(destination.getLocation().clone());
            traveler.sendMessage(ColorUtil.parse(this.msg("teleport-done", "&aTeleported!")));
            this.play(traveler, "teleport");
         }
      } else {
         this.cancelTeleport(traveler.getUniqueId());
         this.teleporting.add(traveler.getUniqueId());
         this.teleportStart.put(traveler.getUniqueId(), traveler.getLocation().clone());
         final int[] __tid = new int[]{-1};
         BukkitRunnable __t = new BukkitRunnable() {
            int left = delay;

            public void run() {
               if (!traveler.isOnline()) {
                  TpaManager.this.cleanupTeleport(traveler.getUniqueId());
                  Tasks.cancel(__tid[0]);
               } else {
                  Location start = (Location)TpaManager.this.teleportStart.get(traveler.getUniqueId());
                  double maxDist = TpaManager.this.plugin.getConfig().getDouble("teleport.cancel-distance", (double)0.5F);
                  if (start != null && traveler.getLocation().distanceSquared(start) > maxDist * maxDist) {
                     traveler.sendMessage(ColorUtil.parse(TpaManager.this.msg("tpa-moved-cancel", "&cTeleport cancelled: movement detected!")));
                     TpaManager.this.play(traveler, "error");
                     TpaManager.this.cleanupTeleport(traveler.getUniqueId());
                     Tasks.cancel(__tid[0]);
                  } else if (this.left <= 0) {
                     if (!destination.isOnline()) {
                        TpaManager.this.cleanupTeleport(traveler.getUniqueId());
                        Tasks.cancel(__tid[0]);
                     } else {
                        traveler.teleport(destination.getLocation().clone());
                        traveler.sendMessage(ColorUtil.parse(TpaManager.this.msg("teleport-done", "&aTeleported!")));
                        TpaManager.this.play(traveler, "teleport");
                        TpaManager.this.cleanupTeleport(traveler.getUniqueId());
                        Tasks.cancel(__tid[0]);
                     }
                  } else {
                     HotbarUtil.send(traveler, ColorUtil.parse(TpaManager.this.msg("countdown-actionbar", "&7Teleporting in &#37BFF8{seconds} &7seconds").replace("{seconds}", String.valueOf(this.left))));
                     TpaManager.this.play(traveler, "countdown");
                     --this.left;
                  }
               }
            }
         };
         __tid[0] = Tasks.runTimer(this.plugin.getCore(), __t, 0L, 20L);
         this.teleportTasks.put(traveler.getUniqueId(), __tid[0]);
      }
   }

   public void onMove(Player player) {
      if (this.teleporting.contains(player.getUniqueId())) {
         Location start = (Location)this.teleportStart.get(player.getUniqueId());
         if (start != null) {
            double maxDist = this.plugin.getConfig().getDouble("teleport.cancel-distance", (double)0.5F);
            if (player.getLocation().distanceSquared(start) > maxDist * maxDist) {
               this.cancelTeleport(player.getUniqueId());
               player.sendMessage(ColorUtil.parse(this.msg("tpa-moved-cancel", "&cTeleport cancelled: movement detected!")));
               this.play(player, "error");
            }

         }
      }
   }

   private void clearRequest(TpaRequest request) {
      Map<UUID, TpaRequest> map = (Map)this.incoming.get(request.getTarget());
      if (map != null) {
         map.remove(request.getSender());
         if (map.isEmpty()) {
            this.incoming.remove(request.getTarget());
         }
      }

      Set<UUID> out = (Set)this.outgoing.get(request.getSender());
      if (out != null) {
         out.remove(request.getTarget());
         if (out.isEmpty()) {
            this.outgoing.remove(request.getSender());
         }
      }

      this.cancelTimeout(request.getTarget(), request.getSender());
   }

   private void cancelTimeout(UUID target, UUID sender) {
      Integer t = (Integer)this.timeoutTasks.remove(pairKey(target, sender));
      if (t != null) {
         Tasks.cancel(t);
      }

   }

   private void cancelTeleport(UUID id) {
      Integer t = (Integer)this.teleportTasks.remove(id);
      if (t != null) {
         Tasks.cancel(t);
      }

      this.cleanupTeleport(id);
   }

   private void cleanupTeleport(UUID id) {
      this.teleporting.remove(id);
      this.teleportStart.remove(id);
      this.teleportTasks.remove(id);
   }

   public void startTpAutoBarTask() {
      this.stopTpAutoBarTask();
      this.tpAutoBarTask = Tasks.runTimer(this.plugin.getCore(), () -> {
         String bar = this.msg("tpauto-actionbar-on", "&aYou have tpauto on.");

         for(UUID id : new HashSet<>(this.tpAuto)) {
            Player p = Bukkit.getPlayer(id);
            if (p != null && p.isOnline()) {
               HotbarUtil.send(p, ColorUtil.parse(bar));
            }
         }

      }, 20L, 20L);
   }

   public void stopTpAutoBarTask() {
      if (this.tpAutoBarTask != -1) {
         Tasks.cancel(this.tpAutoBarTask);
         this.tpAutoBarTask = -1;
      }

   }

   public void playSound(Player player, String key) {
      this.play(player, key);
   }

   private String msg(String key, String def) {
      return this.plugin.getMessages().get(key, def);
   }

   private void play(Player player, String key) {
      try {
         String sound = this.plugin.getConfig().getString("sounds." + key, "");
         if (sound == null || sound.isBlank()) {
            return;
         }

         String normalized = sound.toUpperCase().replace('.', '_');
         float volume = (float)this.plugin.getConfig().getDouble("sounds." + key + "-volume", (double)1.0F);
         float pitch = (float)this.plugin.getConfig().getDouble("sounds." + key + "-pitch", (double)1.0F);
         SoundUtil.play(player, player.getLocation(), Sound.valueOf(normalized), volume, pitch);
      } catch (Exception var7) {
      }

   }
}
