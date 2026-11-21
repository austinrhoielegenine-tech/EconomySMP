package com.austynzdevv.economysmp.teams;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public final class TeamManager {
   private final EconomyTeams plugin;
   private final Map<String, Team> byName = new ConcurrentHashMap();
   private final Map<UUID, String> playerTeam = new ConcurrentHashMap();
   private final Map<UUID, String> invites = new ConcurrentHashMap();
   private final Map<UUID, Boolean> chatToggle = new ConcurrentHashMap();
   private File teamsFolder;

   public TeamManager(EconomyTeams plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.saveAll();
      this.byName.clear();
      this.playerTeam.clear();
      this.teamsFolder = new File(this.plugin.getDataFolder(), "teams");
      if (!this.teamsFolder.exists()) {
         this.teamsFolder.mkdirs();
      }

      File[] files = this.teamsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
      if (files != null) {
         for(File file : files) {
            Team team = Team.load(file);
            if (team != null) {
               this.byName.put(team.getName().toLowerCase(Locale.ROOT), team);

               for(UUID id : team.getMembers()) {
                  this.playerTeam.put(id, team.getName());
               }
            }
         }

      }
   }

   public void saveAll() {
      for(Team team : this.byName.values()) {
         team.save(this.teamsFolder);
      }

   }

   public Collection<Team> getTeams() {
      return this.byName.values();
   }

   public Team getByName(String name) {
      return name == null ? null : (Team)this.byName.get(name.toLowerCase(Locale.ROOT));
   }

   public Team getByPlayer(UUID id) {
      String name = (String)this.playerTeam.get(id);
      return name == null ? null : this.getByName(name);
   }

   public boolean isInTeam(UUID id) {
      return this.playerTeam.containsKey(id);
   }

   public Team create(Player leader, String name) {
      Team team = new Team(name, leader.getUniqueId(), leader.getName());
      this.byName.put(name.toLowerCase(Locale.ROOT), team);
      this.playerTeam.put(leader.getUniqueId(), name);
      team.save(this.teamsFolder);
      return team;
   }

   public void disband(Team team) {
      for(UUID id : new ArrayList<>(team.getMembers())) {
         this.playerTeam.remove(id);
         this.chatToggle.remove(id);
      }

      this.byName.remove(team.getName().toLowerCase(Locale.ROOT));
      team.deleteFile();
      this.invites.entrySet().removeIf((e) -> ((String)e.getValue()).equalsIgnoreCase(team.getName()));
   }

   public void leave(Player player, Team team) {
      team.removeMember(player.getUniqueId());
      this.playerTeam.remove(player.getUniqueId());
      this.chatToggle.remove(player.getUniqueId());
      if (!team.isLeader(player.getUniqueId()) && !team.getMembers().isEmpty()) {
         team.save(this.teamsFolder);
      } else {
         this.disband(team);
      }
   }

   public void kick(Team team, UUID target) {
      team.removeMember(target);
      this.playerTeam.remove(target);
      this.chatToggle.remove(target);
      team.save(this.teamsFolder);
   }

   public void join(Player player, Team team) {
      team.addMember(player.getUniqueId(), player.getName());
      this.playerTeam.put(player.getUniqueId(), team.getName());
      this.invites.remove(player.getUniqueId());
      team.save(this.teamsFolder);
   }

   public void invite(UUID target, String teamName) {
      this.invites.put(target, teamName);
   }

   public String getInvite(UUID target) {
      return (String)this.invites.get(target);
   }

   public void clearInvite(UUID target) {
      this.invites.remove(target);
   }

   public boolean isChatToggled(UUID id) {
      return Boolean.TRUE.equals(this.chatToggle.get(id));
   }

   public void setChatToggled(UUID id, boolean value) {
      if (value) {
         this.chatToggle.put(id, true);
      } else {
         this.chatToggle.remove(id);
      }

   }

   public List<Team> search(String query) {
      List<Team> out = new ArrayList();
      String q = query.toLowerCase(Locale.ROOT);

      for(Team team : this.byName.values()) {
         if (team.getName().toLowerCase(Locale.ROOT).contains(q)) {
            out.add(team);
         }
      }

      return out;
   }

   public boolean isValidName(String name) {
      int min = this.plugin.getConfig().getInt("min-team-name-length", this.plugin.getConfig().getInt("mind-team-name-length", 3));
      int max = this.plugin.getConfig().getInt("max-team-name-length", 9);
      return name.length() >= min && name.length() <= max ? name.matches("[a-zA-Z0-9_\\-]+") : false;
   }

   public boolean isBlockedName(String name) {
      for(String b : this.plugin.getConfig().getStringList("blocked-team-names")) {
         if (b.equalsIgnoreCase(name)) {
            return true;
         }
      }

      return false;
   }

   public File getTeamsFolder() {
      return this.teamsFolder;
   }
}
