package com.austynzdevv.economysmp.teams.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.austynzdevv.economysmp.teams.EconomyTeams;
import com.austynzdevv.economysmp.teams.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TeamsPlaceholders extends PlaceholderExpansion {
   private final EconomyTeams plugin;

   public TeamsPlaceholders(EconomyTeams plugin) {
      this.plugin = plugin;
   }

   public @NotNull String getIdentifier() {
      return "economyteams";
   }

   public @NotNull String getAuthor() {
      return "xyr0";
   }

   public @NotNull String getVersion() {
      return this.plugin.getDescription().getVersion();
   }

   public boolean persist() {
      return true;
   }

   public String onPlaceholderRequest(Player player, @NotNull String params) {
      if (player == null) {
         return "";
      } else {
         String key = params.toLowerCase().replace("-", "_");
         if (key.startsWith("teams_")) {
            key = key.substring(6);
         }

         if (key.startsWith("team_")) {
            key = key.substring(5);
         }

         Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (!key.equals("name") && !key.equals("team") && !key.equals("team_name") && !key.equals("teamname") && !key.isEmpty()) {
            if (!key.equals("leader") && !key.equals("owner")) {
               if (!key.equals("size") && !key.equals("members") && !key.equals("member_count")) {
                  if (!key.equals("online") && !key.equals("online_members")) {
                     if (key.equals("pvp")) {
                        return team == null ? "false" : String.valueOf(team.isPvpEnabled());
                     } else if (!key.equals("has_team") && !key.equals("in_team")) {
                        if (!key.equals("is_leader") && !key.equals("leader_status")) {
                           if (!key.equals("has_home")) {
                              return null;
                           } else {
                              return team != null && team.getHome() != null ? "true" : "false";
                           }
                        } else {
                           return team != null && team.isLeader(player.getUniqueId()) ? "true" : "false";
                        }
                     } else {
                        return team == null ? "false" : "true";
                     }
                  } else {
                     return team == null ? "0" : String.valueOf(team.onlineCount());
                  }
               } else {
                  return team == null ? "0" : String.valueOf(team.getMembers().size());
               }
            } else {
               return team == null ? "" : team.getMemberName(team.getLeader());
            }
         } else {
            return team == null ? "" : team.getName();
         }
      }
   }
}
