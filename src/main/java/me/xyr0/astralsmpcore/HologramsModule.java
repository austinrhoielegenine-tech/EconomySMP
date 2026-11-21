package me.xyr0.astralsmpcore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.xyr0.astralsmpcore.util.Tasks;
import me.xyr0.astralsmpcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class HologramsModule implements Module {
   private final AstralSMPcore plugin;
   private FileConfiguration cfg;
   private File file;
   private int updateTask = -1;
   private final AstralPlaceholders placeholders;
   private final Map<String, List<TextDisplay>> active = new ConcurrentHashMap();
   private final Map<String, Map<UUID, List<TextDisplay>>> personal = new ConcurrentHashMap();
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().hexColors().character('&').build();

   public HologramsModule(AstralSMPcore plugin) {
      this.plugin = plugin;
      this.placeholders = new AstralPlaceholders(plugin);
   }

   public String getName() {
      return "holograms";
   }

   public void onEnable() {
      this.reload();
      CommandRegistry reg = this.plugin.commands();
      reg.registerHandler("aholo", this::cmd);
      reg.registerHandler("aholograms", this::cmd);
      reg.registerHandler("holograms", this::cmd);
      this.plugin.getLogger().info("Holograms module loaded (" + this.hologramNames().size() + " entries) — native TextDisplay (no FancyHolograms required).");
   }

   public void onDisable() {
      if (this.updateTask != -1) {
         Tasks.cancel(this.updateTask);
         this.updateTask = -1;
      }

      this.despawnAll();
   }

   public void reload() {
      if (this.updateTask != -1) {
         Tasks.cancel(this.updateTask);
         this.updateTask = -1;
      }

      this.despawnAll();
      this.file = new File(this.plugin.getDataFolder(), "holograms.yml");
      if (!this.file.exists()) {
         try {
            this.plugin.saveResource("holograms.yml", false);
         } catch (Exception var2) {
         }
      }

      this.cfg = YamlConfiguration.loadConfiguration(this.file);
      Tasks.runLater(this.plugin, () -> {
         this.spawnAll();
         Tasks.runLater(this.plugin, this::ensureSpawned, 40L);
      }, 1L);
      int interval = this.cfg.getInt("defaults.update-text-interval", 300);
      if (interval < 20) {
         interval = 20;
      }

      this.updateTask = Tasks.runTimer(this.plugin, this::refreshAllText, (long)interval, (long)interval);
   }

   private Set<String> hologramNames() {
      ConfigurationSection sec = this.cfg.getConfigurationSection("holograms");
      return sec == null ? Set.of() : sec.getKeys(false);
   }

   private void despawnAll() {
      for(List<TextDisplay> list : this.active.values()) {
         for(TextDisplay d : list) {
            try {
               if (d != null && !d.isDead()) {
                  d.remove();
               }
            } catch (Exception var9) {
            }
         }
      }

      this.active.clear();

      for(Map<UUID, List<TextDisplay>> byPlayer : this.personal.values()) {
         for(List<TextDisplay> list : byPlayer.values()) {
            for(TextDisplay d : list) {
               try {
                  if (d != null && !d.isDead()) {
                     d.remove();
                  }
               } catch (Exception var8) {
               }
            }
         }
      }

      this.personal.clear();
   }

   private void spawnAll() {
      this.removeLoadedHolograms();
      ConfigurationSection sec = this.cfg.getConfigurationSection("holograms");
      if (sec != null) {
         for(String name : sec.getKeys(false)) {
            this.spawnOne(name);
         }

      }
   }

   private void ensureSpawned() {
      ConfigurationSection sec = this.cfg.getConfigurationSection("holograms");
      if (sec != null) {
         for(String name : sec.getKeys(false)) {
            List<TextDisplay> list = (List)this.active.get(name);
            boolean live = list != null && list.stream().anyMatch((d) -> d != null && !d.isDead());
            if (!live) {
               this.spawnOne(name);
            }
         }

      }
   }

   private void removeLoadedHolograms() {
      for(World world : Bukkit.getWorlds()) {
         for(Entity entity : new ArrayList<>(world.getEntities())) {
            try {
               if (entity.getScoreboardTags().contains("astralsmpcore_holo") || entity.getScoreboardTags().contains("astralsmpcore_holo_personal")) {
                  entity.remove();
               }
            } catch (Exception var6) {
            }
         }
      }

   }

   private Location readLocation(ConfigurationSection holo) {
      ConfigurationSection loc = holo.getConfigurationSection("location");
      if (loc == null) {
         return null;
      } else {
         String worldName = loc.getString("world", "world");
         World world = Bukkit.getWorld(worldName);
         if (world == null) {
            this.plugin.getLogger().warning("[Holograms] World not loaded: " + worldName);
            return null;
         } else {
            return new Location(world, loc.getDouble("x"), loc.getDouble("y"), loc.getDouble("z"), (float)loc.getDouble("yaw", (double)0.0F), (float)loc.getDouble("pitch", (double)0.0F));
         }
      }
   }

   private void spawnOne(String name) {
      List<TextDisplay> old = (List)this.active.remove(name);
      if (old != null) {
         for(TextDisplay d : old) {
            try {
               if (d != null && !d.isDead()) {
                  d.remove();
               }
            } catch (Exception var17) {
            }
         }
      }

      ConfigurationSection holo = this.cfg.getConfigurationSection("holograms." + name);
      if (holo != null) {
         Location base = this.readLocation(holo);
         if (base != null && base.getWorld() != null) {
            List<String> lines = holo.getStringList("text");
            if (lines != null && !lines.isEmpty()) {
               double lineHeight = holo.getDouble("line-height", 0.28);
               float scale = (float)holo.getDouble("scale_x", holo.getDouble("scale", (double)1.0F));
               boolean shadow = holo.getBoolean("text_shadow", false);
               boolean seeThrough = holo.getBoolean("see_through", false);
               int viewRange = holo.getInt("visibility_distance", 64);
               List<TextDisplay> spawned = new ArrayList();

               for(int i = 0; i < lines.size(); ++i) {
                  Location lineLoc = base.clone().add((double)0.0F, (double)(-i) * lineHeight, (double)0.0F);

                  try {
                     TextDisplay display = (TextDisplay)base.getWorld().spawn(lineLoc, TextDisplay.class, (td) -> {
                        td.setBillboard(Billboard.CENTER);
                        td.setShadowed(shadow);
                        td.setSeeThrough(seeThrough);
                        td.setDefaultBackground(false);
                        td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
                        td.setAlignment(TextAlignment.CENTER);
                        td.setViewRange((float)Math.max(8, viewRange) / 16.0F);
                        td.setPersistent(true);
                        td.setInvulnerable(true);
                        td.setGravity(false);
                        td.text(Component.empty());

                        try {
                           td.setTransformation(new Transformation(new Vector3f(0.0F, 0.0F, 0.0F), new AxisAngle4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(scale, scale, scale), new AxisAngle4f(0.0F, 0.0F, 0.0F, 1.0F)));
                        } catch (Exception var6) {
                        }

                     });
                     display.addScoreboardTag("astralsmpcore_holo");
                     display.addScoreboardTag("holo:" + name);
                     display.addScoreboardTag("line:" + i);
                     spawned.add(display);
                  } catch (Exception t) {
                     this.plugin.getLogger().warning("[Holograms] Failed to spawn line " + i + " of " + name + ": " + t.getMessage());
                  }
               }

               this.active.put(name, spawned);
               this.refreshTextShared(name);
            }
         }
      }
   }

   private float viewRangeBlocks(ConfigurationSection holo) {
      double v = holo == null ? (double)64.0F : holo.getDouble("visibility_distance", (double)64.0F);
      return v <= (double)0.0F ? 128.0F : (float)Math.max((double)8.0F, v);
   }

   private boolean isPlayerSpecific(String raw) {
      if (raw == null) {
         return false;
      } else {
         String s = raw.toLowerCase(Locale.ROOT);
         if (!s.contains("%player_name%") && !s.contains("%player%")) {
            return s.contains("%astralsmpcore_lb_") && !s.contains("_top_") && (s.contains("_rank%") || s.contains("_value%"));
         } else {
            return true;
         }
      }
   }

   private void refreshAllText() {
      for(String name : this.active.keySet()) {
         this.refreshTextShared(name);
         ConfigurationSection holo = this.cfg.getConfigurationSection("holograms." + name);
         if (holo != null) {
            Location base = this.readLocation(holo);
            if (base != null && base.getWorld() != null) {
               double range = holo.getDouble("visibility_distance", (double)48.0F);
               boolean unlimited = range <= (double)0.0F;
               double rangeSq = unlimited ? Double.MAX_VALUE : range * range;

               for(Player p : Bukkit.getOnlinePlayers()) {
                  if (p.getWorld() != null && p.getWorld().equals(base.getWorld())) {
                     if (!unlimited && p.getLocation().distanceSquared(base) > rangeSq) {
                        this.clearPersonal(name, p.getUniqueId());
                     } else {
                        this.refreshPersonal(name, p);
                     }
                  }
               }
            }
         }
      }

      for(String name : this.personal.keySet()) {
         Map<UUID, List<TextDisplay>> map = (Map)this.personal.get(name);
         if (map != null) {
            for(UUID id : new ArrayList<>(map.keySet())) {
               if (Bukkit.getPlayer(id) == null) {
                  this.clearPersonal(name, id);
               }
            }
         }
      }

   }

   private void refreshTextShared(String name) {
      ConfigurationSection holo = this.cfg.getConfigurationSection("holograms." + name);
      if (holo != null) {
         List<String> lines = holo.getStringList("text");
         List<TextDisplay> displays = (List)this.active.get(name);
         if (displays != null && lines != null) {
            for(int i = 0; i < displays.size() && i < lines.size(); ++i) {
               TextDisplay d = (TextDisplay)displays.get(i);
               if (d != null && !d.isDead()) {
                  String raw = lines.get(i) == null ? "" : (String)lines.get(i);
                  if (this.isPlayerSpecific(raw)) {
                     try {
                        d.text(Component.empty());
                        d.setViewRange(0.0F);
                     } catch (Exception var10) {
                     }
                  } else {
                     try {
                        d.setViewRange(this.viewRangeBlocks(holo) / 16.0F);
                        d.text(this.toComponent(this.applyPlaceholders(raw, (Player)null)));
                     } catch (Exception var9) {
                     }
                  }
               }
            }

         }
      }
   }

   private void refreshPersonal(String name, Player viewer) {
      ConfigurationSection holo = this.cfg.getConfigurationSection("holograms." + name);
      if (holo != null && viewer != null) {
         List<String> lines = holo.getStringList("text");
         if (lines != null && !lines.isEmpty()) {
            Location base = this.readLocation(holo);
            if (base != null && base.getWorld() != null) {
               double lineHeight = holo.getDouble("line-height", 0.28);
               float scale = (float)holo.getDouble("scale_x", holo.getDouble("scale", (double)1.0F));
               boolean shadow = holo.getBoolean("text_shadow", false);
               boolean seeThrough = holo.getBoolean("see_through", false);
               List<Integer> playerLines = new ArrayList();

               for(int i = 0; i < lines.size(); ++i) {
                  if (this.isPlayerSpecific((String)lines.get(i))) {
                     playerLines.add(i);
                  }
               }

               if (!playerLines.isEmpty()) {
                  Map<UUID, List<TextDisplay>> byPlayer = (Map)this.personal.computeIfAbsent(name, (k) -> new ConcurrentHashMap());
                  List<TextDisplay> list = (List)byPlayer.get(viewer.getUniqueId());
                  if (list == null || list.size() != playerLines.size() || list.stream().anyMatch((dx) -> dx == null || dx.isDead())) {
                     this.clearPersonal(name, viewer.getUniqueId());
                     this.removeOrphanPersonal(name, viewer.getUniqueId(), base);
                     list = new ArrayList();

                     for(int lineIdx : playerLines) {
                        Location lineLoc = base.clone().add((double)0.0F, (double)(-lineIdx) * lineHeight, (double)0.0F);

                        try {
                           TextDisplay display = (TextDisplay)base.getWorld().spawn(lineLoc, TextDisplay.class, (td) -> {
                              td.setBillboard(Billboard.CENTER);
                              td.setShadowed(shadow);
                              td.setSeeThrough(seeThrough);
                              td.setDefaultBackground(false);
                              td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
                              td.setAlignment(TextAlignment.CENTER);
                              td.setViewRange(this.viewRangeBlocks(holo) / 16.0F);
                              td.setPersistent(false);
                              td.setInvulnerable(true);
                              td.setGravity(false);
                              td.setVisibleByDefault(false);

                              try {
                                 td.setTransformation(new Transformation(new Vector3f(0.0F, 0.0F, 0.0F), new AxisAngle4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(scale, scale, scale), new AxisAngle4f(0.0F, 0.0F, 0.0F, 1.0F)));
                              } catch (Exception var7) {
                              }

                           });
                           display.addScoreboardTag("astralsmpcore_holo_personal");
                           display.addScoreboardTag("holo:" + name);
                           display.addScoreboardTag("owner:" + String.valueOf(viewer.getUniqueId()));

                           try {
                              viewer.showEntity(this.plugin, display);
                           } catch (Exception var21) {
                           }

                           list.add(display);
                        } catch (Exception t) {
                           this.plugin.getLogger().warning("[Holograms] personal spawn failed: " + t.getMessage());
                        }
                     }

                     byPlayer.put(viewer.getUniqueId(), list);
                  }

                  for(int i = 0; i < playerLines.size() && i < list.size(); ++i) {
                     TextDisplay d = (TextDisplay)list.get(i);
                     if (d != null && !d.isDead()) {
                        int lineIdx = (Integer)playerLines.get(i);
                        String raw = lines.get(lineIdx) == null ? "" : (String)lines.get(lineIdx);

                        try {
                           d.teleport(base.clone().add((double)0.0F, (double)(-lineIdx) * lineHeight, (double)0.0F));
                           d.text(this.toComponent(this.applyPlaceholders(raw, viewer)));

                           try {
                              viewer.showEntity(this.plugin, d);
                           } catch (Exception var19) {
                           }
                        } catch (Exception var20) {
                        }
                     }
                  }

               }
            }
         }
      }
   }

   private void removeOrphanPersonal(String name, UUID uuid, Location base) {
      if (base != null && base.getWorld() != null) {
         for(Entity entity : new ArrayList<>(base.getWorld().getNearbyEntities(base, (double)3.0F, (double)4.0F, (double)3.0F))) {
            if (entity.getScoreboardTags().contains("astralsmpcore_holo_personal") && entity.getScoreboardTags().contains("holo:" + name) && entity.getScoreboardTags().contains("owner:" + String.valueOf(uuid))) {
               try {
                  entity.remove();
               } catch (Exception var7) {
               }
            }
         }

      }
   }

   private void clearPersonal(String name, UUID uuid) {
      Map<UUID, List<TextDisplay>> byPlayer = (Map)this.personal.get(name);
      if (byPlayer != null) {
         List<TextDisplay> list = (List)byPlayer.remove(uuid);
         if (list != null) {
            for(TextDisplay d : list) {
               try {
                  if (d != null && !d.isDead()) {
                     d.remove();
                  }
               } catch (Exception var8) {
               }
            }

         }
      }
   }

   private String applyPlaceholders(String input, Player viewer) {
      String out = input == null ? "" : input;

      try {
         AstralPlaceholders exp = this.placeholders;
         Pattern pat = Pattern.compile("%astralsmpcore_([^%]+)%", 2);
         Matcher m = pat.matcher(out);

         StringBuffer sb;
         String val;
         for(sb = new StringBuffer(); m.find(); m.appendReplacement(sb, Matcher.quoteReplacement(val))) {
            String key = m.group(1);
            val = exp.onPlaceholderRequest(viewer, key);
            if (val == null) {
               val = "";
            }
         }

         m.appendTail(sb);
         out = sb.toString();
      } catch (Exception var11) {
      }

      try {
         if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Class<?> papi = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            out = (String)papi.getMethod("setPlaceholders", OfflinePlayer.class, String.class).invoke((Object)null, viewer, out);
         }
      } catch (Exception var10) {
      }

      if (viewer != null) {
         out = out.replace("%player_name%", viewer.getName()).replace("%player%", viewer.getName());
      } else {
         out = out.replace("%player_name%", "").replace("%player%", "");
      }

      return out;
   }

   private Component toComponent(String legacy) {
      if (legacy != null && !legacy.isEmpty()) {
         try {
            String s = legacy;
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < s.length(); ++i) {
               if (i + 7 < s.length() && s.charAt(i) == '&' && s.charAt(i + 1) == '#') {
                  String hex = s.substring(i + 2, i + 8);
                  if (hex.matches("(?i)[0-9a-f]{6}")) {
                     sb.append("&x");

                     for(char c : hex.toCharArray()) {
                        sb.append('&').append(c);
                     }

                     i += 7;
                     continue;
                  }
               }

               sb.append(s.charAt(i));
            }

            return LEGACY.deserialize(sb.toString());
         } catch (Exception var10) {
            return Component.text(legacy);
         }
      } else {
         return Component.empty();
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      UUID id = event.getPlayer().getUniqueId();

      for(String name : new ArrayList<>(this.personal.keySet())) {
         this.clearPersonal(name, id);
      }

   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      UUID id = event.getPlayer().getUniqueId();

      for(String name : this.hologramNames()) {
         this.clearPersonal(name, id);
      }

   }

   private boolean cmd(CommandSender sender, String[] args) {
      if (!sender.hasPermission("astralsmpcore.system.holograms.use") && !sender.hasPermission("astralsmpcore.system.admin")) {
         TextUtil.send(sender, "&cNo permission.");
         return true;
      } else if (args.length == 0) {
         TextUtil.send(sender, "&7Usage: /aholo <list|movehere <name>|reload|near>");
         return true;
      } else {
         String sub = args[0].toLowerCase(Locale.ROOT);
         if (sub.equals("reload")) {
            this.reload();
            TextUtil.send(sender, "&aHolograms reloaded. Spawned &f" + this.active.size() + " &ahologram(s).");
            return true;
         } else if (!sub.equals("list")) {
            if (sub.equals("near") && sender instanceof Player) {
               Player p = (Player)sender;
               Location pl = p.getLocation();

               for(String name : this.hologramNames()) {
                  ConfigurationSection h = this.cfg.getConfigurationSection("holograms." + name);
                  if (h != null) {
                     Location loc = this.readLocation(h);
                     if (loc != null && loc.getWorld() != null && pl.getWorld() != null && loc.getWorld().equals(pl.getWorld())) {
                        double dist = loc.distance(pl);
                        if (dist <= (double)30.0F) {
                           TextUtil.send(p, "&7" + name + " &8- &f" + String.format("%.1f", dist) + "m");
                        }
                     }
                  }
               }

               return true;
            } else if (!sub.equals("movehere") && !sub.equals("tp") && !sub.equals("here")) {
               TextUtil.send(sender, "&7Usage: /aholo <list|movehere <name>|reload|near>");
               return true;
            } else if (sender instanceof Player) {
               Player p = (Player)sender;
               if (args.length < 2) {
                  TextUtil.send(sender, "&cUsage: /aholo movehere <name>");
                  return true;
               } else {
                  String name = args[1];
                  if (!this.hologramNames().contains(name)) {
                     TextUtil.send(sender, "&cUnknown hologram &f" + name + "&c. Use /aholo list");
                     return true;
                  } else {
                     Location loc = p.getLocation();
                     String base = "holograms." + name + ".location.";
                     this.cfg.set(base + "world", loc.getWorld() != null ? loc.getWorld().getName() : "world");
                     this.cfg.set(base + "x", loc.getX());
                     this.cfg.set(base + "y", loc.getY());
                     this.cfg.set(base + "z", loc.getZ());
                     this.cfg.set(base + "yaw", loc.getYaw());
                     this.cfg.set(base + "pitch", loc.getPitch());

                     try {
                        this.cfg.save(this.file);
                     } catch (Exception ex) {
                        TextUtil.send(p, "&cFailed to save holograms.yml: " + ex.getMessage());
                        return true;
                     }

                     this.spawnOne(name);
                     TextUtil.send(p, "&aMoved hologram &f" + name + " &ato your location (native TextDisplay).");
                     return true;
                  }
               }
            } else {
               TextUtil.send(sender, "&cPlayers only.");
               return true;
            }
         } else {
            Set<String> names = this.hologramNames();
            if (names.isEmpty()) {
               TextUtil.send(sender, "&7No holograms in holograms.yml");
            } else {
               int var10001 = names.size();
               TextUtil.send(sender, "&7Holograms (&f" + var10001 + "&7): &f" + String.join("&7, &f", names));

               for(String n : names) {
                  boolean live = this.active.containsKey(n) && this.active.get(n).stream().anyMatch((d) -> d != null && !d.isDead());
                  TextUtil.send(sender, " &8- &f" + n + (live ? " &a[spawned]" : " &c[not spawned]"));
               }
            }

            return true;
         }
      }
   }
}
