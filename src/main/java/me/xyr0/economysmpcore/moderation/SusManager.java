package me.xyr0.economysmpcore.moderation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class SusManager {
   private final CopyOnWriteArrayList<SusFlag> flags = new CopyOnWriteArrayList();
   private final Map<UUID, Anticheat> viewerFilter = new ConcurrentHashMap();
   private int maxFlags = 500;
   private long expireMs = 3600000L;
   private final Map<Anticheat, Boolean> enabled = new EnumMap(Anticheat.class);

   public SusManager() {
      for(Anticheat ac : SusManager.Anticheat.values()) {
         this.enabled.put(ac, true);
      }

   }

   public void configure(int maxFlags, int expireMinutes, Map<String, Boolean> acConfig) {
      this.maxFlags = Math.max(50, maxFlags);
      this.expireMs = (long)Math.max(1, expireMinutes) * 60L * 1000L;

      for(Anticheat ac : SusManager.Anticheat.values()) {
         String key = ac.name().toLowerCase(Locale.ROOT);
         if (acConfig.containsKey(key)) {
            this.enabled.put(ac, Boolean.TRUE.equals(acConfig.get(key)));
         }
      }

   }

   public boolean isEnabled(Anticheat ac) {
      return Boolean.TRUE.equals(this.enabled.get(ac));
   }

   public void addFlag(UUID playerId, String playerName, Anticheat ac, String check, double vl) {
      if (this.isEnabled(ac)) {
         if (playerId != null) {
            String name = playerName != null ? playerName : "Unknown";
            String checkName = check != null && !check.isEmpty() ? check : "Unknown";
            this.flags.add(new SusFlag(playerId, name, ac.name(), checkName, vl, System.currentTimeMillis()));
            this.trim();
         }
      }
   }

   public void addFlag(Player player, Anticheat ac, String check, double vl) {
      if (player != null) {
         this.addFlag(player.getUniqueId(), player.getName(), ac, check, vl);
      }
   }

   private void trim() {
      long cutoff = System.currentTimeMillis() - this.expireMs;
      this.flags.removeIf((f) -> f.getTime() < cutoff);

      while(this.flags.size() > this.maxFlags) {
         this.flags.remove(0);
      }

   }

   public List<SusFlag> allFlags() {
      this.trim();
      return List.copyOf(this.flags);
   }

   public List<SusFlag> flagsFor(UUID playerId) {
      this.trim();
      List<SusFlag> out = new ArrayList();

      for(SusFlag f : this.flags) {
         if (f.getPlayerId().equals(playerId)) {
            out.add(f);
         }
      }

      return out;
   }

   public Map<UUID, List<SusFlag>> grouped(Anticheat filter) {
      this.trim();
      Map<UUID, List<SusFlag>> map = new LinkedHashMap();

      for(SusFlag f : this.flags) {
         if (filter == null || f.getAnticheat().equalsIgnoreCase(filter.name())) {
            ((List)map.computeIfAbsent(f.getPlayerId(), (k) -> new ArrayList())).add(f);
         }
      }

      List<Map.Entry<UUID, List<SusFlag>>> entries = new ArrayList(map.entrySet());
      entries.sort(Comparator.<Map.Entry<UUID, List<SusFlag>>>comparingLong((ex) -> {
         long latest = 0L;

         for(SusFlag f : ex.getValue()) {
            if (f.getTime() > latest) {
               latest = f.getTime();
            }
         }

         return latest;
      }).reversed());
      Map<UUID, List<SusFlag>> ordered = new LinkedHashMap();

      for(Map.Entry<UUID, List<SusFlag>> e : entries) {
         ordered.put((UUID)e.getKey(), (List)e.getValue());
      }

      return ordered;
   }

   public void clearPlayer(UUID id) {
      this.flags.removeIf((f) -> f.getPlayerId().equals(id));
   }

   public void clearAll() {
      this.flags.clear();
   }

   public Anticheat getFilter(UUID viewer) {
      return (Anticheat)this.viewerFilter.get(viewer);
   }

   public void setFilter(UUID viewer, Anticheat filter) {
      if (filter == null) {
         this.viewerFilter.remove(viewer);
      } else {
         this.viewerFilter.put(viewer, filter);
      }

   }

   public void clearFilter(UUID viewer) {
      this.viewerFilter.remove(viewer);
   }

   public static Anticheat fromName(String name) {
      if (name == null) {
         return null;
      } else {
         Anticheat var10000;
         switch (name.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "")) {
            case "grim":
            case "grimac":
               var10000 = SusManager.Anticheat.GRIM;
               break;
            case "vulcan":
               var10000 = SusManager.Anticheat.VULCAN;
               break;
            case "matrix":
               var10000 = SusManager.Anticheat.MATRIX;
               break;
            case "totemguard":
            case "totem":
               var10000 = SusManager.Anticheat.TOTEMGUARD;
               break;
            case "angelguard":
            case "angel":
               var10000 = SusManager.Anticheat.ANGELGUARD;
               break;
            default:
               var10000 = null;
         }

         return var10000;
      }
   }

   public String displayName(UUID id, List<SusFlag> list) {
      if (list != null && !list.isEmpty()) {
         return ((SusFlag)list.get(list.size() - 1)).getPlayerName();
      } else {
         OfflinePlayer p = Bukkit.getOfflinePlayer(id);
         return p.getName() != null ? p.getName() : id.toString().substring(0, 8);
      }
   }

   public static enum Anticheat {
      GRIM,
      VULCAN,
      MATRIX,
      TOTEMGUARD,
      ANGELGUARD;

      // $FF: synthetic method
      private static Anticheat[] $values() {
         return new Anticheat[]{GRIM, VULCAN, MATRIX, TOTEMGUARD, ANGELGUARD};
      }
   }
}
