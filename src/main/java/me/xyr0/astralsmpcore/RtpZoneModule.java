package me.xyr0.astralsmpcore;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import me.xyr0.astralsmpcore.util.Tasks;
import me.xyr0.astralsmpcore.util.TextUtil;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RtpZoneModule implements Module, Listener {
   private final AstralSMPcore plugin;
   private FileConfiguration cfg;
   private final Set<UUID> inZone = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Integer> timers = new ConcurrentHashMap();
   private int tickTask = -1;

   public RtpZoneModule(AstralSMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "rtp";
   }

   public void onEnable() {
      File folder = new File(this.plugin.getDataFolder(), "rtp");
      folder.mkdirs();
      File f = new File(folder, "zone.yml");
      if (!f.exists()) {
         try {
            this.plugin.saveResource("rtp/zone.yml", false);
         } catch (Exception var4) {
         }
      }

      this.cfg = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.tickTask = Tasks.runTimer(this.plugin, this::tick, 20L, 20L);
   }

   public void onDisable() {
      if (this.tickTask != -1) {
         Tasks.cancel(this.tickTask);
         this.tickTask = -1;
      }

      this.inZone.clear();
      this.timers.clear();
   }

   public void reload() {
      File f = new File(this.plugin.getDataFolder(), "rtp/zone.yml");
      if (f.exists()) {
         this.cfg = YamlConfiguration.loadConfiguration(f);
      }

   }

   public int getTimer(UUID id) {
      return (Integer)this.timers.getOrDefault(id, -1);
   }

   private boolean inConfiguredRegion(Player p) {
      String regionName = this.cfg.getString("region", "rtpzone");
      String worldName = this.cfg.getString("world", "");
      if (worldName != null && !worldName.isBlank() && !p.getWorld().getName().equalsIgnoreCase(worldName)) {
         return false;
      } else {
         try {
            if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
               Class<?> wgClass = Class.forName("com.sk89q.worldguard.WorldGuard");
               Object wg = wgClass.getMethod("getInstance").invoke((Object)null);
               Object platform = wg.getClass().getMethod("getPlatform").invoke(wg);
               Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);
               Object query = container.getClass().getMethod("createQuery").invoke(container);
               Class<?> adapt = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
               Object weLoc = adapt.getMethod("adapt", Location.class).invoke((Object)null, p.getLocation());
               Object regions = null;

               for(Method m : query.getClass().getMethods()) {
                  if (m.getName().equals("getApplicableRegions") && m.getParameterCount() == 1) {
                     regions = m.invoke(query, weLoc);
                     break;
                  }
               }

               if (regions != null) {
                  Method getRegionsM = regions.getClass().getMethod("getRegions");

                  for(Object reg : (Collection)getRegionsM.invoke(regions)) {
                     Method getId = reg.getClass().getMethod("getId");
                     String id = String.valueOf(getId.invoke(reg));
                     if (id.equalsIgnoreCase(regionName)) {
                        return true;
                     }
                  }
               }

               return false;
            }
         } catch (Exception t) {
            this.plugin.getLogger().fine("WG check: " + t.getMessage());
         }

         if (this.cfg.contains("bounds.min-x") && this.cfg.contains("bounds.max-x")) {
            int minX = this.cfg.getInt("bounds.min-x");
            int minY = this.cfg.getInt("bounds.min-y", Integer.MIN_VALUE);
            int minZ = this.cfg.getInt("bounds.min-z");
            int maxX = this.cfg.getInt("bounds.max-x");
            int maxY = this.cfg.getInt("bounds.max-y", Integer.MAX_VALUE);
            int maxZ = this.cfg.getInt("bounds.max-z");
            Location l = p.getLocation();
            return l.getBlockX() >= Math.min(minX, maxX) && l.getBlockX() <= Math.max(minX, maxX) && l.getBlockY() >= Math.min(minY, maxY) && l.getBlockY() <= Math.max(minY, maxY) && l.getBlockZ() >= Math.min(minZ, maxZ) && l.getBlockZ() <= Math.max(minZ, maxZ);
         } else {
            return false;
         }
      }
   }

   @EventHandler
   public void onMove(PlayerMoveEvent e) {
      if (e.getTo() != null) {
         if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            Player p = e.getPlayer();
            boolean now = this.inConfiguredRegion(p);
            boolean was = this.inZone.contains(p.getUniqueId());
            if (now && !was) {
               this.inZone.add(p.getUniqueId());
               p.showTitle(Title.title(TextUtil.component(this.cfg.getString("title.enter-main", "&cRTP Zone")), TextUtil.component(this.cfg.getString("title.enter-sub", "&7Fight players and steal their loot"))));
               this.maybeStartCountdown();
            } else if (!now && was) {
               this.inZone.remove(p.getUniqueId());
               this.timers.remove(p.getUniqueId());
            }

         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent e) {
      this.inZone.remove(e.getPlayer().getUniqueId());
      this.timers.remove(e.getPlayer().getUniqueId());
   }

   private void maybeStartCountdown() {
      if (this.inZone.size() >= 2) {
         int secs = this.cfg.getInt("countdown-seconds", 30);

         for(UUID id : this.inZone) {
            this.timers.putIfAbsent(id, secs);
         }

      }
   }

   private void tick() {
      if (this.inZone.size() < 2) {
         this.timers.clear();
      } else {
         int secs = this.cfg.getInt("countdown-seconds", 30);

         for(UUID id : new HashSet<>(this.inZone)) {
            int left = (Integer)this.timers.getOrDefault(id, secs) - 1;
            this.timers.put(id, left);
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
               p.showTitle(Title.title(TextUtil.component(this.cfg.getString("title.main", "&cRTP Zone")), TextUtil.component(this.cfg.getString("title.subtitle", "&7Teleporting in {seconds}").replace("{seconds}", String.valueOf(Math.max(0, left))))));
               if (left <= 0) {
                  this.teleportAll();
                  return;
               }
            }
         }

      }
   }

   private void teleportAll() {
      World world = Bukkit.getWorld(this.cfg.getString("rtp-world", "world"));
      if (world == null && !Bukkit.getWorlds().isEmpty()) {
         world = (World)Bukkit.getWorlds().get(0);
      }

      if (world != null) {
         int min = this.cfg.getInt("rtp.min-range", 500);
         int max = this.cfg.getInt("rtp.max-range", 4000);

         for(UUID id : new HashSet<>(this.inZone)) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
               int x = ThreadLocalRandom.current().nextInt(min, max + 1) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
               int z = ThreadLocalRandom.current().nextInt(min, max + 1) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
               int y = world.getHighestBlockYAt(x, z) + 1;
               p.teleport(new Location(world, (double)x + (double)0.5F, (double)y, (double)z + (double)0.5F));
               this.timers.remove(id);
            }
         }

         this.inZone.clear();
      }
   }
}
