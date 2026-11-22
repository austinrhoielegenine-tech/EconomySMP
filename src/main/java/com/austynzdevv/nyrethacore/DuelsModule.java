package com.austynzdevv.nyrethacore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.util.HotbarUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import com.austynzdevv.nyrethacore.util.Tasks;
import com.austynzdevv.nyrethacore.util.TextUtil;
import com.austynzdevv.nyrethacore.warps.EconomyWarps;
import com.austynzdevv.nyrethacore.warps.NamedLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

public class DuelsModule implements Module, Listener {
   private final NyrethaCore plugin;
   private FileConfiguration cfg;
   private final Set<UUID> queue = ConcurrentHashMap.newKeySet();
   private final Set<UUID> queueGuiOpen = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Integer> music = new ConcurrentHashMap();
   private final Map<UUID, UUID> inDuel = new ConcurrentHashMap();
   private final Set<UUID> spectators = ConcurrentHashMap.newKeySet();
   private final Set<UUID> winners = ConcurrentHashMap.newKeySet();
   private final Set<UUID> drawVotes = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Integer> lootTasks = new ConcurrentHashMap();
   private final Map<UUID, Integer> borderTasks = new ConcurrentHashMap();
   private final Map<UUID, Long> matchStart = new ConcurrentHashMap();
   private final Map<UUID, int[]> stats = new ConcurrentHashMap();
   private final Set<UUID> returnToSpawnOnJoin = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Location> pendingSpectator = new ConcurrentHashMap();
   private final Set<UUID> pvpDisabled = ConcurrentHashMap.newKeySet();
   private final Map<UUID, UUID> pendingDraw = new ConcurrentHashMap();
   private Location spawn1;
   private Location spawn2;
   private Location arenaPos1;
   private Location arenaPos2;
   private final Map<UUID, Location> wandPos1 = new ConcurrentHashMap();

   public DuelsModule(NyrethaCore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "duels";
   }

   public void onEnable() {
      File folder = new File(this.plugin.getDataFolder(), "duels");
      folder.mkdirs();
      File f = new File(folder, "config.yml");
      if (!f.exists()) {
         try {
            this.plugin.saveResource("duels/config.yml", false);
         } catch (Exception var14) {
         }
      }

      this.cfg = YamlConfiguration.loadConfiguration(f);
      File sounds = new File(folder, "sounds.yml");
      if (!sounds.exists()) {
         try {
            this.plugin.saveResource("duels/sounds.yml", false);
         } catch (Exception var13) {
         }
      }

      File messages = new File(folder, "messages.yml");
      if (!messages.exists()) {
         try {
            this.plugin.saveResource("duels/messages.yml", false);
         } catch (Exception var12) {
         }
      }

      this.loadStats();

      for(String g : new String[]{"queue.yml", "confirm-duel.yml", "direct-duel.yml"}) {
         File gf = new File(folder, g);
         if (!gf.exists()) {
            try {
               this.plugin.saveResource("duels/gui/" + g, false);
            } catch (Exception var11) {
            }
         }
      }

      this.loadSpawns();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      CommandRegistry reg = this.plugin.commands();
      reg.registerHandler("duels", this::queueCmd);
      reg.registerHandler("queue", this::queueCmd);
      reg.registerHandler("leave", this::leaveCmd);
      reg.registerHandler("draw", this::drawCmd);
      reg.registerHandler("spectate", this::spectateCmd);
      reg.registerHandler("duelsadmin", this::adminCmd);
   }

   public void onDisable() {
      for(UUID id : new HashSet<>(this.music.keySet())) {
         this.stopMusic(Bukkit.getPlayer(id));
      }

      for(Integer tid : new HashSet<>(this.lootTasks.values())) {
         if (tid != null) {
            Tasks.cancel(tid);
         }
      }

      for(Integer tid : new HashSet<>(this.borderTasks.values())) {
         if (tid != null) {
            Tasks.cancel(tid);
         }
      }

      this.lootTasks.clear();
      this.borderTasks.clear();
      this.music.clear();
      this.queue.clear();
      this.inDuel.clear();
      this.saveStats();
   }

   public void reload() {
      File f = new File(this.plugin.getDataFolder(), "duels/config.yml");
      if (f.exists()) {
         this.cfg = YamlConfiguration.loadConfiguration(f);
      }

      this.loadSpawns();
   }

   private void loadSpawns() {
      File sf = new File(this.plugin.getDataFolder(), "duels/spawn.yml");
      if (sf.exists()) {
         FileConfiguration s = YamlConfiguration.loadConfiguration(sf);
         this.spawn1 = this.readLoc(s, "spawn1");
         this.spawn2 = this.readLoc(s, "spawn2");
         this.arenaPos1 = this.readLoc(s, "pos1");
         this.arenaPos2 = this.readLoc(s, "pos2");
      }
   }

   private void saveSpawns() {
      File sf = new File(this.plugin.getDataFolder(), "duels/spawn.yml");
      FileConfiguration s = new YamlConfiguration();
      this.writeLoc(s, "spawn1", this.spawn1);
      this.writeLoc(s, "spawn2", this.spawn2);
      this.writeLoc(s, "pos1", this.arenaPos1);
      this.writeLoc(s, "pos2", this.arenaPos2);

      try {
         s.save(sf);
      } catch (Exception var4) {
      }

   }

   private Location readLoc(FileConfiguration s, String path) {
      if (!s.contains(path + ".world")) {
         return null;
      } else {
         World w = Bukkit.getWorld(s.getString(path + ".world", "world"));
         return w == null ? null : new Location(w, s.getDouble(path + ".x"), s.getDouble(path + ".y"), s.getDouble(path + ".z"), (float)s.getDouble(path + ".yaw"), (float)s.getDouble(path + ".pitch"));
      }
   }

   private void writeLoc(FileConfiguration s, String path, Location loc) {
      if (loc != null && loc.getWorld() != null) {
         s.set(path + ".world", loc.getWorld().getName());
         s.set(path + ".x", loc.getX());
         s.set(path + ".y", loc.getY());
         s.set(path + ".z", loc.getZ());
         s.set(path + ".yaw", loc.getYaw());
         s.set(path + ".pitch", loc.getPitch());
      }
   }

   private boolean queueCmd(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.inDuel.containsKey(p.getUniqueId())) {
            TextUtil.send(p, "&cYou are already in a duel.");
            return true;
         } else {
            this.openQueueGui(p);
            return true;
         }
      } else {
         return true;
      }
   }

   private void joinQueue(Player p) {
      if (this.inDuel.containsKey(p.getUniqueId())) {
         TextUtil.send(p, "&cYou are already in a duel.");
      } else if (this.queue.contains(p.getUniqueId())) {
         TextUtil.send(p, "&7Already searching...");
      } else {
         for(UUID id : new ArrayList<>(this.queue)) {
            Player other = Bukkit.getPlayer(id);
            if (other != null && other.isOnline() && !other.equals(p) && !SettingsModule.isDuelRequestsDisabled(other.getUniqueId())) {
               if (SettingsModule.isDuelRequestsDisabled(p.getUniqueId())) {
               }

               this.queue.remove(id);
               this.stopMusic(other);
               this.startDuel(p, other);
               return;
            }
         }

         this.queue.add(p.getUniqueId());
         this.startMusic(p);
         TextUtil.send(p, "&7Searching for a duel...");
      }
   }

   private void openQueueGui(Player p) {
      File guiFile = new File(this.plugin.getDataFolder(), "duels/gui/queue.yml");
      if (!guiFile.exists()) {
         try {
            this.plugin.saveResource("duels/gui/queue.yml", false);
         } catch (Exception var20) {
         }
      }

      FileConfiguration gui = guiFile.exists() ? YamlConfiguration.loadConfiguration(guiFile) : new YamlConfiguration();
      String title = gui.getString("title", "&8Select & Confirm");
      title = this.token(title);
      int rows = Math.max(1, Math.min(6, gui.getInt("rows", 3)));
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, TextUtil.component(title));
      ConfigurationSection items = gui.getConfigurationSection("items");
      if (items != null) {
         for(String key : items.getKeys(false)) {
            ConfigurationSection sec = items.getConfigurationSection(key);
            if (sec != null) {
               int slot = sec.getInt("slot", -1);
               if (slot >= 0 && slot < inv.getSize()) {
                  Material mat;
                  try {
                     mat = Material.valueOf(sec.getString("material", "STONE").toUpperCase());
                  } catch (Exception var19) {
                     mat = Material.STONE;
                  }

                  ItemStack item = new ItemStack(mat);
                  ItemMeta meta = item.getItemMeta();
                  if (meta != null) {
                     String name = this.token(sec.getString("name", key).replace("%amount_queued%", String.valueOf(this.queue.size())).replace("%player_ping%", String.valueOf(p.getPing())).replace("%estimated_wait%", "...").replace("%elo%", "1000").replace("%wins%", String.valueOf(this.getStats(p.getUniqueId())[0])).replace("%losses%", String.valueOf(this.getStats(p.getUniqueId())[1])).replace("%streak%", String.valueOf(this.getStats(p.getUniqueId())[3])).replace("%win_percentage%", this.getWinPercentage(p.getUniqueId())));
                     meta.displayName(TextUtil.component(name));
                     List<Component> lore = new ArrayList();

                     for(String line : sec.getStringList("lore")) {
                        lore.add(TextUtil.component(this.token(line.replace("%amount_queued%", String.valueOf(this.queue.size())).replace("%player_ping%", String.valueOf(p.getPing())).replace("%estimated_wait%", "...").replace("%elo%", "1000").replace("%wins%", String.valueOf(this.getStats(p.getUniqueId())[0])).replace("%losses%", String.valueOf(this.getStats(p.getUniqueId())[1])).replace("%streak%", String.valueOf(this.getStats(p.getUniqueId())[3])).replace("%win_percentage%", this.getWinPercentage(p.getUniqueId())))));
                     }

                     meta.lore(lore);
                     item.setItemMeta(meta);
                  }

                  inv.setItem(slot, item);
               }
            }
         }
      } else {
         inv.setItem(10, this.simple(Material.RED_STAINED_GLASS_PANE, "&cCancel", "&7Click to cancel"));
         inv.setItem(12, this.simple(Material.COMPASS, "&#37BFF9Wait Time", "&7Queued: &f" + this.queue.size()));
         inv.setItem(13, this.simple(Material.GRAY_DYE, "&#37BFF9Statistics", "&7Elo / Wins / Losses"));
         inv.setItem(14, this.simple(Material.FEATHER, "&#37BFF9Region", "&7Ping: &f" + p.getPing() + "ms"));
         inv.setItem(16, this.simple(Material.LIME_STAINED_GLASS_PANE, "&aSearch", "&7Click to search"));
      }

      this.queueGuiOpen.add(p.getUniqueId());
      p.openInventory(inv);
   }

   private String token(String s) {
      return s == null ? "" : s.replace("{dark-gray}", "&8").replace("{main}", "&#37BFF9").replace("{gray}", "&7").replace("{accent}", "&#37BFF9").replace("{white}", "&f").replace("{success}", "&#00FF00").replace("{error}", "&c");
   }

   private ItemStack simple(Material mat, String name, String lore) {
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(TextUtil.component(name));
         meta.lore(List.of(TextUtil.component(lore)));
         item.setItemMeta(meta);
      }

      return item;
   }

   private void startMusic(Player p) {
      if (!SettingsModule.isDuelSongsDisabled(p.getUniqueId())) {
         this.stopMusic(p);
         int t = Tasks.runTimer(this.plugin, () -> {
            if (this.queue.contains(p.getUniqueId()) && p.isOnline()) {
               this.play(p, "queue-music");
            } else {
               this.stopMusic(p);
            }
         }, 0L, 600L);
         this.music.put(p.getUniqueId(), t);
      }
   }

   private void stopMusic(Player p) {
      if (p != null) {
         Integer t = (Integer)this.music.remove(p.getUniqueId());
         if (t != null) {
            Tasks.cancel(t);
         }

         try {
            p.stopSound(Sound.MUSIC_DISC_OTHERSIDE);
         } catch (Exception var5) {
         }

         try {
            p.stopSound(Sound.MUSIC_DISC_PIGSTEP);
         } catch (Exception var4) {
         }

      }
   }

   private void startDuel(Player a, Player b) {
      this.stopMusic(a);
      this.stopMusic(b);
      a.closeInventory();
      b.closeInventory();
      int[] count = new int[]{3};
      int[] __duelTid = new int[]{-1};
      __duelTid[0] = Tasks.runTimer(this.plugin, () -> {
         if (a.isOnline() && b.isOnline()) {
            if (count[0] > 0) {
               String tTitle = this.token(this.message("game-startup-count-title", "{main}%seconds%").replace("%seconds%", String.valueOf(count[0])));
               this.showTitle(a, tTitle, "");
               this.showTitle(b, tTitle, "");
               this.play(a, "countdown");
               this.play(b, "countdown");
               String bar = this.message("duel-countdown-actionbar", "{gray}Duel starting in {accent}%amount%s{gray}!").replace("%amount%", String.valueOf(count[0]));
               TextUtil.actionBar(a, this.token(bar));
               TextUtil.actionBar(b, this.token(bar));
               int var10002 = count[0]--;
            } else {
               Tasks.cancel(__duelTid[0]);
               this.beginMatch(a, b);
            }
         } else {
            Tasks.cancel(__duelTid[0]);
            this.queue.remove(a.getUniqueId());
            this.queue.remove(b.getUniqueId());
         }
      }, 0L, 20L);
   }

   private void beginMatch(Player a, Player b) {
      this.inDuel.put(a.getUniqueId(), b.getUniqueId());
      this.inDuel.put(b.getUniqueId(), a.getUniqueId());
      this.drawVotes.remove(a.getUniqueId());
      this.drawVotes.remove(b.getUniqueId());
      this.pendingDraw.remove(a.getUniqueId());
      this.pendingDraw.remove(b.getUniqueId());
      this.pvpDisabled.remove(a.getUniqueId());
      this.pvpDisabled.remove(b.getUniqueId());
      this.matchStart.put(a.getUniqueId(), System.currentTimeMillis());
      this.matchStart.put(b.getUniqueId(), System.currentTimeMillis());
      Location s1 = this.spawn1 != null ? this.spawn1.clone() : a.getLocation().clone();
      Location s2 = this.spawn2 != null ? this.spawn2.clone() : b.getLocation().clone();
      a.teleport(s1);
      b.teleport(s2);
      this.play(a, "duel-start");
      this.play(b, "duel-start");
      if (this.cfg.getBoolean("match.heal-on-start", true)) {
         try {
            a.setHealth(a.getMaxHealth());
            b.setHealth(b.getMaxHealth());
         } catch (Exception var7) {
         }
      }

      if (this.cfg.getBoolean("match.feed-on-start", true)) {
         a.setFoodLevel(20);
         b.setFoodLevel(20);
         a.setSaturation(20.0F);
         b.setSaturation(20.0F);
      }

      if (this.cfg.getBoolean("match.set-survival-on-start", true)) {
         a.setGameMode(GameMode.SURVIVAL);
         b.setGameMode(GameMode.SURVIVAL);
      }

      String title = this.token(this.message("game-started-title", "{error}CASUAL DUEL"));
      String subtitle = this.token(this.message("game-started-subtitle", "{white}Fight players and steal their loot"));
      this.showTitle(a, title, subtitle);
      this.showTitle(b, title, subtitle);
      this.play(a, "duel-started");
      this.play(b, "duel-started");
      TextUtil.send(a, "&7Duel started against &#37BFF9" + b.getName());
      TextUtil.send(b, "&7Duel started against &#37BFF9" + a.getName());
      this.scheduleBorder(a.getUniqueId(), b.getUniqueId());
   }

   private void scheduleBorder(UUID a, UUID b) {
      Integer old = (Integer)this.borderTasks.remove(a);
      if (old != null) {
         Tasks.cancel(old);
      }

      int task = Tasks.runLater(this.plugin, () -> {
         if (this.inDuel.containsKey(a) && this.inDuel.containsKey(b)) {
            Player pa = Bukkit.getPlayer(a);
            Player pb = Bukkit.getPlayer(b);
            if (pa != null && pb != null) {
               if (this.arenaPos1 != null && this.arenaPos2 != null && this.arenaPos1.getWorld() != null) {
                  try {
                     World w = this.arenaPos1.getWorld();
                     double minX = Math.min(this.arenaPos1.getX(), this.arenaPos2.getX());
                     double maxX = Math.max(this.arenaPos1.getX(), this.arenaPos2.getX());
                     double minZ = Math.min(this.arenaPos1.getZ(), this.arenaPos2.getZ());
                     double maxZ = Math.max(this.arenaPos1.getZ(), this.arenaPos2.getZ());
                     double cx = (minX + maxX) / (double)2.0F;
                     double cz = (minZ + maxZ) / (double)2.0F;
                     double size = Math.max(Math.abs(maxX - minX), Math.abs(maxZ - minZ));
                     if (size < (double)20.0F) {
                        size = (double)50.0F;
                     }

                     WorldBorder border = w.getWorldBorder();
                     border.setCenter(cx, cz);
                     border.setSize(size);
                     border.setSize(Math.max((double)10.0F, size * 0.4), 60L);
                     String msg = this.token(this.message("border-shrink-150", "{gray}The border is shrinking to {accent}%border_size%{gray}.").replace("%border_size%", String.valueOf((int)(size * 0.4))));
                     TextUtil.send(pa, msg);
                     TextUtil.send(pb, msg);
                  } catch (Exception var22) {
                  }

               }
            }
         }
      }, 1200L);
      this.borderTasks.put(a, task);
      this.borderTasks.put(b, task);
   }

   private void clearBorder(UUID a, UUID b) {
      Integer t = (Integer)this.borderTasks.remove(a);
      if (t != null) {
         Tasks.cancel(t);
      }

      this.borderTasks.remove(b);

      try {
         if (this.arenaPos1 != null && this.arenaPos1.getWorld() != null) {
            this.arenaPos1.getWorld().getWorldBorder().reset();
         }
      } catch (Exception var5) {
      }

   }

   private boolean leaveCmd(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID id = p.getUniqueId();
         if (this.inDuel.containsKey(id)) {
            TextUtil.send(p, "&cYou cannot leave an active duel. Use /draw or fight until the duel ends.");
            return true;
         } else {
            this.queue.remove(id);
            this.stopMusic(p);
            this.spectators.remove(id);
            this.winners.remove(id);
            Integer loot = (Integer)this.lootTasks.remove(id);
            if (loot != null) {
               Tasks.cancel(loot);
            }

            if (p.getGameMode() == GameMode.SPECTATOR) {
               p.setGameMode(GameMode.SURVIVAL);
            }

            this.teleportToSpawn(p);
            TextUtil.send(p, this.token(this.message("leave-loot", "{gray}You left the duel arena.")));
            return true;
         }
      } else {
         return true;
      }
   }

   private void endLootPhase(UUID winnerId, UUID loserId) {
      Player winner = Bukkit.getPlayer(winnerId);
      Player loser = loserId == null ? null : Bukkit.getPlayer(loserId);
      this.winners.remove(winnerId);
      if (loserId != null) {
         this.spectators.remove(loserId);
      }

      this.lootTasks.remove(winnerId);
      if (loserId != null) {
         this.lootTasks.remove(loserId);
      }

      if (winner != null && winner.isOnline()) {
         HotbarUtil.send(winner, TextUtil.component("&7Loot time over."));
         this.showTitle(winner, this.token(this.message("game-won-title", "{success}You Won!")), this.token(this.message("game-won-subtitle", "{white}Loot time is over")));
         this.teleportToSpawn(winner);
      }

      if (loser != null && loser.isOnline()) {
         loser.setGameMode(GameMode.SURVIVAL);
         HotbarUtil.send(loser, TextUtil.component("&7Returning to spawn."));
         this.teleportToSpawn(loser);
      }

   }

   private void startLootPhase(final Player winner, final Player loser) {
      if (winner != null) {
         final int seconds = Math.max(1, this.cfg.getInt("match.loot-seconds", 240));
         this.winners.add(winner.getUniqueId());
         if (loser != null) {
            this.spectators.add(loser.getUniqueId());
         }

         this.showTitle(winner, this.token(this.message("game-won-title", "{success}You Won!")), this.token(this.message("game-won-subtitle", "{white}Get your loot before the time runs out")));
         if (loser != null) {
            this.showTitle(loser, this.token(this.message("game-lost-title", "{error}You lost...")), "");
         }

         this.play(winner, "win");
         if (loser != null) {
            this.play(loser, "lose");
         }

         Integer oldWinner = (Integer)this.lootTasks.remove(winner.getUniqueId());
         if (oldWinner != null) {
            Tasks.cancel(oldWinner);
         }

         if (loser != null) {
            Integer oldLoser = (Integer)this.lootTasks.remove(loser.getUniqueId());
            if (oldLoser != null) {
               Tasks.cancel(oldLoser);
            }
         }

         int task = Tasks.runTimer(this.plugin, new Runnable() {
            int left = seconds;

            public void run() {
               if (this.left <= 0 || loser != null && !winner.isOnline() && !loser.isOnline()) {
                  Integer current = (Integer)DuelsModule.this.lootTasks.remove(winner.getUniqueId());
                  if (current != null) {
                     Tasks.cancel(current);
                  }

                  if (loser != null) {
                     Integer other = (Integer)DuelsModule.this.lootTasks.remove(loser.getUniqueId());
                     if (other != null) {
                        Tasks.cancel(other);
                     }
                  }

                  DuelsModule.this.endLootPhase(winner.getUniqueId(), loser == null ? null : loser.getUniqueId());
               } else {
                  String bar = DuelsModule.this.message("loot-time", "{gray}You have {accent}%seconds%s {gray}to collect loot.").replace("%seconds%", String.valueOf(this.left));
                  if (winner.isOnline()) {
                     HotbarUtil.send(winner, TextUtil.component(DuelsModule.this.token(bar)));
                  }

                  if (loser != null && loser.isOnline()) {
                     HotbarUtil.send(loser, TextUtil.component(DuelsModule.this.token(bar)));
                  }

                  --this.left;
               }
            }
         }, 0L, 20L);
         this.lootTasks.put(winner.getUniqueId(), task);
         if (loser != null) {
            this.lootTasks.put(loser.getUniqueId(), task);
         }

      }
   }

   private boolean drawCmd(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID id = p.getUniqueId();
         UUID oppId = (UUID)this.inDuel.get(id);
         if (oppId == null) {
            TextUtil.send(p, "&cYou are not in a duel.");
            return true;
         } else {
            Player opp = Bukkit.getPlayer(oppId);
            if (this.pendingDraw.containsKey(oppId) && ((UUID)this.pendingDraw.get(oppId)).equals(id)) {
               TextUtil.send(p, this.token(this.message("draw-accepted", "{gray}Draw accepted.")));
               if (opp != null) {
                  TextUtil.send(opp, this.token(this.message("draw-accepted", "{gray}Draw accepted.")));
               }

               this.finishDraw(p, opp);
               return true;
            } else if (this.pendingDraw.containsKey(id)) {
               TextUtil.send(p, this.token(this.message("draw-already-sent", "{gray}You already requested a draw. Waiting for opponent...")));
               return true;
            } else {
               this.pendingDraw.put(id, oppId);
               TextUtil.send(p, this.token(this.message("draw-sent", "{gray}Draw request sent. Waiting for opponent...")));
               if (opp != null) {
                  TextUtil.send(opp, this.token(this.message("draw-received", "{accent}%player% {gray}requested a draw. Use {accent}/draw {gray}to accept.").replace("%player%", p.getName())));
               }

               return true;
            }
         }
      } else {
         return true;
      }
   }

   private void finishDraw(Player a, Player b) {
      if (a != null) {
         this.inDuel.remove(a.getUniqueId());
         this.pendingDraw.remove(a.getUniqueId());
         this.drawVotes.remove(a.getUniqueId());
         this.addStat(a.getUniqueId(), 2);
         this.pvpDisabled.add(a.getUniqueId());
      }

      if (b != null) {
         this.inDuel.remove(b.getUniqueId());
         this.pendingDraw.remove(b.getUniqueId());
         this.drawVotes.remove(b.getUniqueId());
         this.addStat(b.getUniqueId(), 2);
         this.pvpDisabled.add(b.getUniqueId());
      }

      UUID aid = a != null ? a.getUniqueId() : null;
      UUID bid = b != null ? b.getUniqueId() : null;
      if (aid != null && bid != null) {
         this.clearBorder(aid, bid);
      }

      String title = this.token(this.message("game-draw-title", "{gray}DRAW"));
      String sub = this.token(this.message("game-draw-subtitle", "{gray}The duel ended with no winner."));
      if (a != null) {
         this.showTitle(a, title, sub);
         this.play(a, "draw");
      }

      if (b != null) {
         this.showTitle(b, title, sub);
         this.play(b, "draw");
      }

      Tasks.runLater(this.plugin, () -> {
         if (a != null && a.isOnline()) {
            this.pvpDisabled.remove(a.getUniqueId());
            a.setGameMode(GameMode.SURVIVAL);
            this.teleportToSpawn(a);
         }

         if (b != null && b.isOnline()) {
            this.pvpDisabled.remove(b.getUniqueId());
            b.setGameMode(GameMode.SURVIVAL);
            this.teleportToSpawn(b);
         }

      }, 100L);
   }

   private int[] getStats(UUID id) {
      return (int[])this.stats.computeIfAbsent(id, (k) -> new int[4]);
   }

   private String getWinPercentage(UUID id) {
      int[] s = this.getStats(id);
      int games = s[0] + s[1];
      return games <= 0 ? "0.0" : String.format(Locale.US, "%.1f", (double)s[0] * (double)100.0F / (double)games);
   }

   private void addStat(UUID id, int type) {
      int[] s = this.getStats(id);
      if (type == 0) {
         int var10002 = s[0]++;
         var10002 = s[3]++;
      } else if (type == 1) {
         int var5 = s[1]++;
         s[3] = 0;
      } else if (type == 2) {
         int var6 = s[2]++;
      }

      this.saveStats();
   }

   private void loadStats() {
      File f = new File(this.plugin.getDataFolder(), "duels/stats.yml");
      if (f.exists()) {
         FileConfiguration data = YamlConfiguration.loadConfiguration(f);
         ConfigurationSection sec = data.getConfigurationSection("players");
         if (sec != null) {
            for(String raw : sec.getKeys(false)) {
               try {
                  UUID id = UUID.fromString(raw);
                  int[] s = new int[4];
                  s[0] = sec.getInt(raw + ".wins");
                  s[1] = sec.getInt(raw + ".losses");
                  s[2] = sec.getInt(raw + ".draws");
                  s[3] = sec.getInt(raw + ".streak", 0);
                  this.stats.put(id, s);
               } catch (IllegalArgumentException var8) {
               }
            }

         }
      }
   }

   private void saveStats() {
      File f = new File(this.plugin.getDataFolder(), "duels/stats.yml");
      FileConfiguration data = new YamlConfiguration();

      for(Map.Entry<UUID, int[]> entry : this.stats.entrySet()) {
         int[] s = (int[])entry.getValue();
         data.set("players." + String.valueOf(entry.getKey()) + ".wins", s[0]);
         data.set("players." + String.valueOf(entry.getKey()) + ".losses", s[1]);
         data.set("players." + String.valueOf(entry.getKey()) + ".draws", s[2]);
         data.set("players." + String.valueOf(entry.getKey()) + ".streak", s[3]);
      }

      try {
         f.getParentFile().mkdirs();
         data.save(f);
      } catch (Exception var6) {
      }

   }

   private boolean spectateCmd(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (args.length < 1) {
            TextUtil.send(p, "&cUsage: /spectate <player>");
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(args[0]);
            if (t != null && this.inDuel.containsKey(t.getUniqueId())) {
               this.spectators.add(p.getUniqueId());
               p.setGameMode(GameMode.SPECTATOR);
               p.teleport(t.getLocation());
               TextUtil.send(p, "&7Spectating &#37BFF9" + t.getName());
               return true;
            } else {
               TextUtil.send(p, "&cThat player is not in a duel.");
               return true;
            }
         }
      } else {
         return true;
      }
   }

   private boolean adminCmd(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (!p.hasPermission("nyrethacore.system.duels.admin") && !p.isOp()) {
            TextUtil.send(p, "&cNo permission.");
            return true;
         } else if (args.length < 1) {
            TextUtil.send(p, "&c/duelsadmin wand|setspawn1|setspawn2|setpos1|setpos2");
            return true;
         } else {
            switch (args[0].toLowerCase()) {
               case "wand":
                  ItemStack wand = new ItemStack(Material.GOLDEN_AXE);
                  ItemMeta meta = wand.getItemMeta();
                  if (meta != null) {
                     meta.displayName(TextUtil.component("&6Duels Wand"));
                     wand.setItemMeta(meta);
                  }

                  p.getInventory().addItem(new ItemStack[]{wand});
                  TextUtil.send(p, "&7Given duels wand. Left click pos1, right click pos2.");
                  break;
               case "setspawn1":
                  this.spawn1 = p.getLocation();
                  this.saveSpawns();
                  TextUtil.send(p, "&aSpawn 1 set.");
                  break;
               case "setspawn2":
                  this.spawn2 = p.getLocation();
                  this.saveSpawns();
                  TextUtil.send(p, "&aSpawn 2 set.");
                  break;
               case "setpos1":
                  this.arenaPos1 = p.getLocation();
                  this.saveSpawns();
                  TextUtil.send(p, "&aPos1 set.");
                  break;
               case "setpos2":
                  this.arenaPos2 = p.getLocation();
                  this.saveSpawns();
                  TextUtil.send(p, "&aPos2 set.");
                  break;
               default:
                  TextUtil.send(p, "&cUnknown subcommand.");
            }

            return true;
         }
      } else {
         return true;
      }
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent e) {
      Player dead = e.getEntity();
      UUID id = dead.getUniqueId();
      UUID oppId = (UUID)this.inDuel.get(id);
      if (oppId != null) {
         Player opp = Bukkit.getPlayer(oppId);
         this.returnToSpawnOnJoin.add(id);
         this.inDuel.remove(id);
         this.inDuel.remove(oppId);
         this.pendingDraw.remove(id);
         this.pendingDraw.remove(oppId);
         this.drawVotes.remove(id);
         this.drawVotes.remove(oppId);
         this.clearBorder(id, oppId);
         this.addStat(id, 1);
         if (oppId != null) {
            this.addStat(oppId, 0);
         }

         Location deathLoc = dead.getLocation().clone();
         this.pendingSpectator.put(id, deathLoc);
         this.spectators.add(id);
         e.setKeepInventory(false);
         if (opp != null && opp.isOnline()) {
            String winnerTitle = this.token(this.message("game-won-title", "{success}You Won!"));
            String winnerSubtitle = this.token(this.message("game-won-subtitle", "{white}Get your loot before the time runs out"));
            String loserTitle = this.token(this.message("game-lost-title", "{error}You lost..."));
            this.showTitle(opp, winnerTitle, winnerSubtitle);
            this.showTitle(dead, loserTitle, "");
            this.play(opp, "win");
            this.play(dead, "lose");
            TextUtil.send(opp, this.token(this.message("game-won", "{success}Congrats! You won the duel.")));
            TextUtil.send(dead, this.token(this.message("game-lost", "{error}You lost the duel.")));
            Tasks.run(this.plugin, () -> {
               if (dead.isOnline()) {
                  dead.spigot().respawn();
               }

            });
            this.startLootPhase(opp, dead);
         } else {
            this.pendingSpectator.remove(id);
            Tasks.run(this.plugin, () -> {
               if (dead.isOnline()) {
                  dead.spigot().respawn();
                  dead.setGameMode(GameMode.SURVIVAL);
                  this.teleportToSpawn(dead);
               }
            });
         }
      }
   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent e) {
      UUID id = e.getPlayer().getUniqueId();
      Location loc = (Location)this.pendingSpectator.remove(id);
      if (loc != null) {
         e.setRespawnLocation(loc);
         Tasks.run(this.plugin, () -> {
            Player p = e.getPlayer();
            if (p.isOnline()) {
               p.setGameMode(GameMode.SPECTATOR);
               p.teleport(loc);
               this.spectators.add(id);
            }
         });
      }
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      Player p = e.getPlayer();
      UUID id = p.getUniqueId();
      if (this.returnToSpawnOnJoin.remove(id)) {
         Tasks.run(this.plugin, () -> {
            if (p.isOnline()) {
               this.spectators.remove(id);
               this.winners.remove(id);
               p.setGameMode(GameMode.SURVIVAL);
               this.teleportToSpawn(p);
            }
         });
      } else {
         if (this.spectators.remove(id) || this.winners.remove(id)) {
            Tasks.run(this.plugin, () -> {
               if (p.isOnline()) {
                  p.setGameMode(GameMode.SURVIVAL);
                  this.teleportToSpawn(p);
               }
            });
         }

      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onDamage(EntityDamageByEntityEvent e) {
      Entity var3 = e.getEntity();
      if (var3 instanceof Player victim) {
         if (this.pvpDisabled.contains(victim.getUniqueId())) {
            e.setCancelled(true);
         } else {
            Player damager = null;
            Entity var7 = e.getDamager();
            if (var7 instanceof Player) {
               Player d = (Player)var7;
               damager = d;
            } else {
               var7 = e.getDamager();
               if (var7 instanceof Projectile) {
                  Projectile proj = (Projectile)var7;
                  ProjectileSource var10 = proj.getShooter();
                  if (var10 instanceof Player) {
                     Player d = (Player)var10;
                     damager = d;
                  }
               }
            }

            if (damager != null && this.pvpDisabled.contains(damager.getUniqueId())) {
               e.setCancelled(true);
            }

         }
      }
   }

   @EventHandler
   public void onCmd(PlayerCommandPreprocessEvent e) {
      Player p = e.getPlayer();
      UUID id = p.getUniqueId();
      String msg = e.getMessage().toLowerCase(Locale.ROOT);
      String root = msg.length() > 1 ? msg.substring(1).split(" ")[0] : "";
      boolean fighting = this.inDuel.containsKey(id);
      boolean looting = this.winners.contains(id) || this.spectators.contains(id) || this.lootTasks.containsKey(id);
      if (fighting || looting) {
         if (fighting) {
            if (!root.equals("draw") && !root.equals("report") && !root.equals("r")) {
               e.setCancelled(true);
               TextUtil.send(p, "&cCommands are blocked during a duel. Allowed: /draw, /report");
            }
         } else {
            Set<String> allowed = new HashSet(List.of("leave", "home", "homes", "shop", "ah", "auction", "sell", "order", "orders", "draw", "report", "spectate"));
            if (!allowed.contains(root)) {
               e.setCancelled(true);
               TextUtil.send(p, "&cDuring loot you can only use /leave /home /shop /ah /sell /order");
            }

         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent e) {
      Player p = e.getPlayer();
      UUID id = p.getUniqueId();
      if (this.queue.remove(id)) {
         this.stopMusic(p);
      }

      UUID oppId = (UUID)this.inDuel.get(id);
      if (oppId == null) {
         Integer loot = (Integer)this.lootTasks.remove(id);
         boolean wasLooting = loot != null || this.winners.contains(id) || this.spectators.contains(id);
         if (loot != null) {
            Tasks.cancel(loot);
         }

         this.winners.remove(id);
         this.spectators.remove(id);
         if (wasLooting) {
            this.returnToSpawnOnJoin.add(id);
         }

      } else {
         Player opp = Bukkit.getPlayer(oppId);
         this.returnToSpawnOnJoin.add(id);
         this.inDuel.remove(id);
         this.inDuel.remove(oppId);
         this.pendingDraw.remove(id);
         this.pendingDraw.remove(oppId);
         this.clearBorder(id, oppId);
         this.addStat(id, 1);
         this.addStat(oppId, 0);

         try {
            for(ItemStack item : p.getInventory().getContents()) {
               if (item != null && !item.getType().isAir()) {
                  p.getWorld().dropItemNaturally(p.getLocation(), item);
               }
            }

            p.getInventory().clear();
         } catch (Exception var10) {
         }

         if (opp != null && opp.isOnline()) {
            this.showTitle(opp, this.token(this.message("game-won-title", "{success}You Won!")), this.token(this.message("game-won-subtitle", "{white}Your opponent left")));
            this.play(opp, "win");
            TextUtil.send(opp, "&aYour opponent left the duel. You win!");
            this.startLootPhase(opp, (Player)null);
         }

      }
   }

   public boolean isInDuel(UUID id) {
      return this.inDuel.containsKey(id);
   }

   private String message(String key, String def) {
      File f = new File(this.plugin.getDataFolder(), "duels/messages.yml");
      if (!f.exists()) {
         return def;
      } else {
         FileConfiguration m = YamlConfiguration.loadConfiguration(f);
         return m.getString(key, def);
      }
   }

   private void showTitle(Player player, String title, String subtitle) {
      if (player != null && player.isOnline()) {
         try {
            player.sendTitle(TextUtil.color(title), TextUtil.color(subtitle), 10, 60, 10);
         } catch (Exception var5) {
         }

      }
   }

   private void play(Player player, String key) {
      if (player != null && player.isOnline()) {
         File f = new File(this.plugin.getDataFolder(), "duels/sounds.yml");
         if (f.exists()) {
            FileConfiguration sounds = YamlConfiguration.loadConfiguration(f);
            if (sounds.getBoolean(key + ".enabled", true)) {
               String raw = sounds.getString(key + ".sound", "");
               if (raw != null && !raw.isBlank()) {
                  try {
                     Sound sound = Sound.valueOf(raw.toUpperCase(Locale.ROOT));
                     float volume = (float)sounds.getDouble(key + ".volume", (double)1.0F);
                     float pitch = (float)sounds.getDouble(key + ".pitch", (double)1.0F);
                     SoundUtil.play(player, player.getLocation(), sound, volume, pitch);
                  } catch (Exception var9) {
                  }

               }
            }
         }
      }
   }

   private void teleportToSpawn(Player player) {
      if (player != null && player.isOnline()) {
         Location target = null;

         try {
            EconomyWarps warps = EconomyWarps.get();
            if (warps != null && warps.getWarps() != null) {
               NamedLocation spawn = warps.getWarps().defaultSpawn();
               if (spawn != null) {
                  target = spawn.toLocation();
               }
            }
         } catch (Exception var5) {
         }

         if (target == null) {
            target = this.plugin.getServer().getWorlds().isEmpty() ? null : ((World)this.plugin.getServer().getWorlds().get(0)).getSpawnLocation();
         }

         if (target != null) {
            player.teleport(target.clone());
         }

      }
   }

   @EventHandler
   public void onQueueClose(InventoryCloseEvent e) {
      HumanEntity var3 = e.getPlayer();
      if (var3 instanceof Player p) {
         this.queueGuiOpen.remove(p.getUniqueId());
      }

   }

   @EventHandler
   public void onQueueClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player p) {
         if (this.queueGuiOpen.contains(p.getUniqueId())) {
            if (e.getClickedInventory() != null && e.getClickedInventory() == e.getView().getTopInventory()) {
               e.setCancelled(true);
               int slot = e.getRawSlot();
               ItemStack cur = e.getCurrentItem();
               if (cur != null && !cur.getType().isAir()) {
                  Material type = cur.getType();
                  if (type != Material.RED_STAINED_GLASS_PANE && slot != 10) {
                     if (type == Material.LIME_STAINED_GLASS_PANE || slot == 16) {
                        this.queueGuiOpen.remove(p.getUniqueId());
                        p.closeInventory();
                        this.joinQueue(p);
                     }

                  } else {
                     if (this.queue.remove(p.getUniqueId())) {
                        this.stopMusic(p);
                        TextUtil.send(p, "&7Left duel queue.");
                     }

                     this.queueGuiOpen.remove(p.getUniqueId());
                     p.closeInventory();
                  }
               }
            } else {
               e.setCancelled(true);
            }
         }
      }
   }
}
