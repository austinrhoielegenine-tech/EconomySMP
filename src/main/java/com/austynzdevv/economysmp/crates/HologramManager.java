package com.austynzdevv.economysmp.crates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.clip.placeholderapi.PlaceholderAPI;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public final class HologramManager implements Listener {
   public static final String HOLO_TAG = "economycrates_holo";
   private final EconomyCrates plugin;
   private final Map<String, List<UUID>> holograms = new HashMap();
   private int updateTask = -1;
   private int spawnTask = -1;
   private final Map<String, List<String>> renderedText = new HashMap();

   public HologramManager(EconomyCrates plugin) {
      this.plugin = plugin;
   }

   public void start() {
      Bukkit.getPluginManager().registerEvents(this, this.plugin.getCore());
      this.purgeOrphanHolograms();
      this.removeAll();
      this.scheduleSpawnAll();
      if (this.updateTask != -1) {
         Tasks.cancel(this.updateTask);
      }

      this.updateTask = Tasks.runTimer(this.plugin.getCore(), this::refreshAll, 20L, 20L);
   }

   public void stop() {
      if (this.updateTask != -1) {
         Tasks.cancel(this.updateTask);
      }

      if (this.spawnTask != -1) {
         Tasks.cancel(this.spawnTask);
      }

      this.updateTask = -1;
      this.spawnTask = -1;
      this.removeAll();
   }

   public void reload() {
      if (this.spawnTask != -1) {
         Tasks.cancel(this.spawnTask);
      }

      this.purgeOrphanHolograms();
      this.removeAll();
      this.scheduleSpawnAll();
   }

   private void scheduleSpawnAll() {
      this.spawnTask = Tasks.runLater(this.plugin.getCore(), this::spawnAll, 20L);
   }

   public void spawnAll() {
      this.removeAll();
      this.purgeOrphanHolograms();

      for(Map.Entry<String, String> e : this.plugin.getData().boundEntries()) {
         this.spawnAt((String)e.getKey(), (String)e.getValue());
      }

   }

   @EventHandler
   public void onWorldLoad(WorldLoadEvent event) {
      String worldName = event.getWorld().getName();

      for(Map.Entry<String, String> e : this.plugin.getData().boundEntries()) {
         if (((String)e.getKey()).startsWith(worldName + ";")) {
            List<UUID> ids = (List)this.holograms.get(e.getKey());
            if (ids == null || !this.allDisplaysValid(ids)) {
               this.holograms.remove(e.getKey());
               this.spawnAt((String)e.getKey(), (String)e.getValue());
            }
         }
      }

   }

   public void spawnAt(String locKey, String crateId) {
      this.removeAt(locKey);
      Crate crate = this.plugin.getCrates().get(crateId);
      if (crate != null && crate.isHologramEnabled()) {
         Location base = this.parseLoc(locKey);
         if (base != null && base.getWorld() != null) {
            for(Player viewer : base.getWorld().getPlayers()) {
               this.spawnAtForPlayer(locKey, crateId, base, crate, viewer);
            }

         }
      }
   }

   private void spawnAtForPlayer(String locKey, String crateId, Location base, Crate crate, Player viewer) {
      String displayKey = locKey + "|" + String.valueOf(viewer.getUniqueId());
      this.removeDisplayKey(displayKey);
      int chunkX = base.getBlockX() >> 4;
      int chunkZ = base.getBlockZ() >> 4;
      if (base.getWorld().isChunkLoaded(chunkX, chunkZ)) {
         this.removeLegacyAt(base);
         List<String> lines = new ArrayList();
         List<Double> heights = new ArrayList();
         List<Crate.HologramLayer> layers = crate.getHolograms();
         if (layers != null) {
            for(Crate.HologramLayer layer : layers) {
               if (layer.lines != null) {
                  double offset = (double)0.0F;

                  for(String line : layer.lines) {
                     if (line != null && !line.isBlank()) {
                        lines.add(line);
                        heights.add(layer.height - offset);
                        offset += 0.28;
                     }
                  }
               }
            }
         }

         if (lines.isEmpty()) {
            lines.add(crate.getDisplayName());
            heights.add((double)1.5F);
            lines.add("&fKeys: &#7afc00%crate_keys_" + crate.getId() + "%");
            heights.add(1.2);
            lines.add("&7Right-click to open");
            heights.add(0.9);
         }

         List<UUID> ids = new ArrayList();

         for(int i = 0; i < Math.min(lines.size(), 8); ++i) {
            Location at = base.clone().add((double)0.5F, (Double)heights.get(i), (double)0.5F);
            String text = this.applyPlaceholders((String)lines.get(i), crate, viewer);

            try {
               TextDisplay display = (TextDisplay)base.getWorld().spawn(at, TextDisplay.class, (d) -> {
                  d.text(ColorUtil.parse(text));
                  d.setBillboard(Billboard.CENTER);
                  d.setAlignment(TextAlignment.CENTER);
                  d.setDefaultBackground(false);
                  d.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
                  d.setShadowed(false);
                  d.setSeeThrough(false);
                  d.setViewRange(64.0F);
                  d.setGravity(false);
                  d.setInvulnerable(true);
                  d.setPersistent(false);
               });
               display.addScoreboardTag("economycrates_holo");

               for(Player other : Bukkit.getOnlinePlayers()) {
                  if (!other.getUniqueId().equals(viewer.getUniqueId())) {
                     other.hideEntity(this.plugin.getCore(), display);
                  }
               }

               viewer.showEntity(this.plugin.getCore(), display);
               ids.add(display.getUniqueId());
            } catch (Exception var19) {
            }
         }

         this.holograms.put(displayKey, ids);
         this.renderedText.remove(displayKey);
      }
   }

   public void removeAt(String locKey) {
      for(String key : new ArrayList<>(this.holograms.keySet())) {
         if (key.equals(locKey) || key.startsWith(locKey + "|")) {
            this.removeDisplayKey(key);
         }
      }

   }

   private void removeDisplayKey(String key) {
      List<UUID> ids = (List)this.holograms.remove(key);
      if (ids != null) {
         for(UUID id : ids) {
            Entity entity = Bukkit.getEntity(id);
            if (entity != null) {
               entity.remove();
            }
         }
      }

      this.renderedText.remove(key);
   }

   public void removeAll() {
      for(String key : new ArrayList<>(this.holograms.keySet())) {
         this.removeAt(key);
      }

      this.holograms.clear();
   }

   public void purgeOrphanHolograms() {
      for(World world : Bukkit.getWorlds()) {
         for(Entity entity : new ArrayList<>(world.getEntities())) {
            if (entity.getScoreboardTags().contains("economycrates_holo")) {
               try {
                  entity.remove();
               } catch (Exception var7) {
               }
            } else if (entity instanceof ArmorStand) {
               ArmorStand stand = (ArmorStand)entity;
               if (this.isLegacyCrateStand(stand)) {
                  try {
                     stand.remove();
                  } catch (Exception var8) {
                  }
               }
            }
         }
      }

      for(Map.Entry<String, String> entry : this.plugin.getData().boundEntries()) {
         Location loc = this.parseLoc((String)entry.getKey());
         if (loc != null) {
            this.removeLegacyAt(loc);
         }
      }

   }

   private boolean isLegacyCrateStand(ArmorStand stand) {
      return stand.isMarker() && !stand.isVisible() && stand.isCustomNameVisible() && !stand.hasGravity();
   }

   private void removeLegacyAt(Location base) {
      if (base != null && base.getWorld() != null) {
         for(Entity entity : new ArrayList<>(base.getWorld().getNearbyEntities(base, (double)2.75F, (double)3.0F, (double)2.75F))) {
            if (entity instanceof ArmorStand) {
               ArmorStand stand = (ArmorStand)entity;
               if (this.isLegacyCrateStand(stand)) {
                  try {
                     stand.remove();
                  } catch (Exception var6) {
                  }
               }
            }
         }

      }
   }

   private void refreshAll() {
      for(Map.Entry<String, String> binding : this.plugin.getData().boundEntries()) {
         String locKey = (String)binding.getKey();
         String crateId = (String)binding.getValue();
         Crate crate = this.plugin.getCrates().get(crateId);
         if (crate != null) {
            Location base = this.parseLoc(locKey);
            if (base != null && base.getWorld() != null) {
               List<String> lines = this.collectLines(crate);

               for(Player viewer : base.getWorld().getPlayers()) {
                  String displayKey = locKey + "|" + String.valueOf(viewer.getUniqueId());
                  List<UUID> ids = (List)this.holograms.get(displayKey);
                  if (ids != null && ids.size() == Math.min(lines.size(), 8) && this.allDisplaysValid(ids)) {
                     List<String> previous = (List)this.renderedText.get(displayKey);
                     List<String> current = new ArrayList(ids.size());

                     for(int i = 0; i < ids.size(); ++i) {
                        Entity entity = Bukkit.getEntity((UUID)ids.get(i));
                        if (entity instanceof TextDisplay) {
                           TextDisplay display = (TextDisplay)entity;
                           String text = this.applyPlaceholders((String)lines.get(i), crate, viewer);
                           current.add(text);
                           if (previous == null || i >= previous.size() || !text.equals(previous.get(i))) {
                              display.text(ColorUtil.parse(text));
                           }
                        }
                     }

                     this.renderedText.put(displayKey, current);
                  } else {
                     this.spawnAtForPlayer(locKey, crateId, base, crate, viewer);
                  }
               }
            }
         }
      }

      for(String key : new ArrayList<>(this.holograms.keySet())) {
         int split = key.lastIndexOf(124);
         String locKey = split < 0 ? key : key.substring(0, split);
         String uuid = split < 0 ? "" : key.substring(split + 1);
         boolean bound = this.plugin.getData().getBoundByKey(locKey) != null;
         boolean online = false;

         try {
            online = Bukkit.getPlayer(UUID.fromString(uuid)) != null;
         } catch (Exception var18) {
         }

         if (!bound || !online) {
            this.removeDisplayKey(key);
         }
      }

   }

   private boolean allDisplaysValid(List<UUID> ids) {
      for(UUID id : ids) {
         if (!(Bukkit.getEntity(id) instanceof TextDisplay)) {
            return false;
         }
      }

      return true;
   }

   private List<String> collectLines(Crate crate) {
      List<String> lines = new ArrayList();
      List<Crate.HologramLayer> layers = crate.getHolograms();
      if (layers != null) {
         for(Crate.HologramLayer layer : layers) {
            if (layer.lines != null) {
               for(String line : layer.lines) {
                  if (line != null && !line.isBlank()) {
                     lines.add(line);
                  }
               }
            }
         }
      }

      if (lines.isEmpty()) {
         lines.add(crate.getDisplayName());
         lines.add("&fKeys: &#7afc00%crate_keys_" + crate.getId() + "%");
         lines.add("&7Right-click to open");
      }

      return lines;
   }

   private String applyPlaceholders(String line, Crate crate, Player viewer) {
      String out = line.replace("%crate%", crate.getId()).replace("%crate_name%", ColorUtil.strip(crate.getDisplayName()));
      if (viewer != null) {
         if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
               out = PlaceholderAPI.setPlaceholders(viewer, out);
            } catch (Exception var6) {
            }
         }

         out = out.replace("%crate_keys_" + crate.getId() + "%", String.valueOf(this.plugin.getKeys().totalKeys(viewer, crate.getId())));
         out = out.replace("%crate_keys%", String.valueOf(this.plugin.getKeys().totalKeys(viewer, crate.getId())));
      } else {
         out = out.replaceAll("%crate_keys_[a-zA-Z0-9_]+%", "0").replace("%crate_keys%", "0");
      }

      return out;
   }

   private Player nearestPlayer(Location loc) {
      if (loc.getWorld() == null) {
         return null;
      } else {
         Player best = null;
         double bestDist = Double.MAX_VALUE;

         for(Player p : loc.getWorld().getPlayers()) {
            double d = p.getLocation().distanceSquared(loc);
            if (d < bestDist) {
               bestDist = d;
               best = p;
            }
         }

         return best;
      }
   }

   private Location parseLoc(String key) {
      if (key == null) {
         return null;
      } else {
         String[] p = key.split(";");
         if (p.length != 4) {
            return null;
         } else {
            World world = Bukkit.getWorld(p[0]);
            if (world == null) {
               return null;
            } else {
               try {
                  return new Location(world, (double)Integer.parseInt(p[1]), (double)Integer.parseInt(p[2]), (double)Integer.parseInt(p[3]));
               } catch (Exception var5) {
                  return null;
               }
            }
         }
      }
   }
}
