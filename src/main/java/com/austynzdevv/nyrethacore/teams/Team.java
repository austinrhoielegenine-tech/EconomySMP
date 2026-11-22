package com.austynzdevv.nyrethacore.teams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Team {
   private final String name;
   private UUID leader;
   private final List<UUID> members = new ArrayList();
   private final Map<UUID, Long> joinDates = new HashMap();
   private final Map<UUID, String> names = new HashMap();
   private final Map<UUID, Set<Perm>> permissions = new HashMap();
   private boolean pvpEnabled;
   private Location home;
   private File file;

   public Team(String name, UUID leader, String leaderName) {
      this.name = name;
      this.leader = leader;
      this.members.add(leader);
      this.joinDates.put(leader, System.currentTimeMillis());
      this.names.put(leader, leaderName);
      this.pvpEnabled = false;
   }

   private Team(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public UUID getLeader() {
      return this.leader;
   }

   public void setLeader(UUID leader) {
      this.leader = leader;
   }

   public List<UUID> getMembers() {
      return this.members;
   }

   public boolean isMember(UUID id) {
      return id != null && (this.isLeader(id) || this.members.contains(id));
   }

   public boolean isLeader(UUID id) {
      return this.leader != null && this.leader.equals(id);
   }

   public boolean isPvpEnabled() {
      return this.pvpEnabled;
   }

   public void setPvpEnabled(boolean pvpEnabled) {
      this.pvpEnabled = pvpEnabled;
   }

   public Location getHome() {
      return this.home;
   }

   public void setHome(Location home) {
      this.home = home;
   }

   public String getMemberName(UUID id) {
      String n = (String)this.names.get(id);
      if (n != null) {
         return n;
      } else {
         OfflinePlayer p = Bukkit.getOfflinePlayer(id);
         return p.getName() != null ? p.getName() : id.toString().substring(0, 8);
      }
   }

   public long getJoinDate(UUID id) {
      return (Long)this.joinDates.getOrDefault(id, 0L);
   }

   public void addMember(UUID id, String name) {
      if (!this.members.contains(id)) {
         this.members.add(id);
      }

      this.joinDates.put(id, System.currentTimeMillis());
      this.names.put(id, name);
      this.permissions.putIfAbsent(id, EnumSet.noneOf(Perm.class));
   }

   public void removeMember(UUID id) {
      this.members.remove(id);
      this.joinDates.remove(id);
      this.names.remove(id);
      this.permissions.remove(id);
   }

   public void updateName(UUID id, String name) {
      this.names.put(id, name);
   }

   public boolean hasPerm(UUID id, Perm perm) {
      if (this.isLeader(id)) {
         return true;
      } else {
         Set<Perm> set = (Set)this.permissions.get(id);
         return set != null && set.contains(perm);
      }
   }

   public void setPerm(UUID id, Perm perm, boolean value) {
      if (id != null && perm != null && !this.isLeader(id) && this.members.contains(id)) {
         Set<Perm> set = (Set)this.permissions.computeIfAbsent(id, (k) -> EnumSet.noneOf(Perm.class));
         if (value) {
            set.add(perm);
         } else {
            set.remove(perm);
         }

      }
   }

   public boolean togglePerm(UUID id, Perm perm) {
      if (id != null && perm != null && this.members.contains(id)) {
         if (this.isLeader(id)) {
            return true;
         } else {
            boolean now = !this.hasPerm(id, perm);
            this.setPerm(id, perm, now);
            return now;
         }
      } else {
         return false;
      }
   }

   public void save(File folder) {
      if (this.file == null) {
         this.file = new File(folder, sanitize(this.name) + ".yml");
      }

      FileConfiguration cfg = new YamlConfiguration();
      cfg.set("name", this.name);
      cfg.set("leader", this.leader.toString());
      List<String> memberStr = new ArrayList();

      for(UUID id : this.members) {
         memberStr.add(id.toString());
         cfg.set("names." + String.valueOf(id), this.names.getOrDefault(id, this.getMemberName(id)));
         cfg.set("joinDates." + String.valueOf(id), this.joinDates.getOrDefault(id, System.currentTimeMillis()));
         Set<Perm> set = (Set)this.permissions.get(id);
         if (set != null && !set.isEmpty()) {
            List<String> perms = new ArrayList();

            for(Perm p : set) {
               perms.add(p.name());
            }

            cfg.set("permissions." + String.valueOf(id), perms);
         }
      }

      cfg.set("members", memberStr);
      cfg.set("pvp-enabled", this.pvpEnabled);
      if (this.home != null && this.home.getWorld() != null) {
         cfg.set("home.world", this.home.getWorld().getName());
         cfg.set("home.x", this.home.getX());
         cfg.set("home.y", this.home.getY());
         cfg.set("home.z", this.home.getZ());
         cfg.set("home.yaw", this.home.getYaw());
         cfg.set("home.pitch", this.home.getPitch());
      }

      try {
         cfg.save(this.file);
      } catch (IOException var10) {
      }

   }

   public static Team load(File file) {
      FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
      String name = cfg.getString("name");
      if (name == null) {
         return null;
      } else {
         Team team = new Team(name);
         team.file = file;

         try {
            team.leader = UUID.fromString(cfg.getString("leader"));
         } catch (Exception var13) {
            return null;
         }

         team.pvpEnabled = cfg.getBoolean("pvp-enabled", false);

         for(String raw : cfg.getStringList("members")) {
            try {
               UUID id = UUID.fromString(raw);
               team.members.add(id);
               team.names.put(id, cfg.getString("names." + String.valueOf(id), team.getMemberName(id)));
               team.joinDates.put(id, cfg.getLong("joinDates." + String.valueOf(id), System.currentTimeMillis()));
               Set<Perm> set = EnumSet.noneOf(Perm.class);

               for(String ps : cfg.getStringList("permissions." + String.valueOf(id))) {
                  try {
                     set.add(Team.Perm.valueOf(ps));
                  } catch (Exception var12) {
                  }
               }

               team.permissions.put(id, set);
            } catch (Exception var14) {
            }
         }

         if (cfg.contains("home.world")) {
            World world = Bukkit.getWorld(cfg.getString("home.world", "world"));
            if (world != null) {
               team.home = new Location(world, cfg.getDouble("home.x"), cfg.getDouble("home.y"), cfg.getDouble("home.z"), (float)cfg.getDouble("home.yaw"), (float)cfg.getDouble("home.pitch"));
            }
         }

         return team;
      }
   }

   public void deleteFile() {
      if (this.file != null && this.file.exists()) {
         this.file.delete();
      }

   }

   private static String sanitize(String name) {
      return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
   }

   public int onlineCount() {
      int c = 0;

      for(UUID id : this.members) {
         if (Bukkit.getPlayer(id) != null) {
            ++c;
         }
      }

      return c;
   }

   public static enum Perm {
      PVP_TOGGLE,
      TEAM_HOME_ACCESS,
      TEAM_HOME_SET,
      TEAM_ADMIN,
      MANAGE_MEMBERS;

      // $FF: synthetic method
      private static Perm[] $values() {
         return new Perm[]{PVP_TOGGLE, TEAM_HOME_ACCESS, TEAM_HOME_SET, TEAM_ADMIN, MANAGE_MEMBERS};
      }
   }
}
