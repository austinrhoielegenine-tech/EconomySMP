package me.xyr0.economysmpcore.hide;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class NameTagManager {
   private final AstralHide plugin;
   private final String TEAM_NAME = "AstralHide";

   public NameTagManager(AstralHide plugin) {
      this.plugin = plugin;
   }

   public void hideTag(Player player) {
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      Team team = scoreboard.getTeam("AstralHide");
      if (team == null) {
         team = scoreboard.registerNewTeam("AstralHide");
      }

      team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
      if (!team.hasEntry(player.getName())) {
         team.addEntry(player.getName());
      }

   }

   public void showTag(Player player) {
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      Team team = scoreboard.getTeam("AstralHide");
      if (team != null) {
         team.removeEntry(player.getName());
      }

   }
}
