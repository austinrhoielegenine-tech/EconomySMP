package com.austynzdevv.economysmp.hide;

import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class NameManager {
   private final EconomyHide plugin;

   public NameManager(EconomyHide plugin) {
      this.plugin = plugin;
   }

   private String scramble(String base) {
      char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
      StringBuilder sb = new StringBuilder(base.length());

      for(int i = 0; i < Math.max(6, base.length()); ++i) {
         sb.append(chars[ThreadLocalRandom.current().nextInt(chars.length)]);
      }

      return sb.toString();
   }

   public void hideName(Player player) {
      String hidden = this.plugin.getHideManager().getHiddenName(player);
      if (hidden == null || hidden.isBlank()) {
         hidden = this.scramble(player.getName());
      }

      String tabName = "§7§k" + hidden;
      player.setDisplayName(tabName);
      player.setPlayerListName(tabName);

      try {
         Component c = ((TextComponent)Component.text(hidden).decorate(TextDecoration.OBFUSCATED)).color(NamedTextColor.GRAY);
         player.displayName(c);
         player.playerListName(c);
         player.customName(c);
         player.setCustomNameVisible(false);
      } catch (Exception var12) {
      }

      try {
         Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
         String var10000 = player.getUniqueId().toString().replace("-", "");
         String teamName = "ah_" + var10000.substring(0, 12);
         Team team = board.getTeam(teamName);
         if (team == null) {
            team = board.registerNewTeam(teamName);
         }

         team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);

         try {
            team.addEntry(player.getName());
         } catch (Exception var10) {
         }

         player.setScoreboard(board);
      } catch (Exception var11) {
      }

      try {
         if (Bukkit.getPluginManager().getPlugin("TAB") != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " tabprefix \"\"");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " tagsuffix \"\"");
            ConsoleCommandSender var14 = Bukkit.getConsoleSender();
            String var10001 = player.getName();
            Bukkit.dispatchCommand(var14, "tab player " + var10001 + " customtabname \"&7&k" + hidden + "\"");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " customtagname \"\"");
         }
      } catch (Exception var9) {
      }

      try {
         if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " meta setprefix 100 \"&7&k\"");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " meta setsuffix 100 \"\"");
         }
      } catch (Exception var8) {
      }

   }

   public void restoreName(Player player) {
      player.setDisplayName(player.getName());
      player.setPlayerListName(player.getName());

      try {
         Component c = Component.text(player.getName());
         player.displayName(c);
         player.playerListName(c);
         player.customName((Component)null);
      } catch (Exception var10) {
      }

      try {
         Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
         String var10000 = player.getUniqueId().toString().replace("-", "");
         String teamName = "ah_" + var10000.substring(0, 12);
         Team team = board.getTeam(teamName);
         if (team != null) {
            try {
               team.removeEntry(player.getName());
            } catch (Exception var8) {
            }

            team.unregister();
         }
      } catch (Exception var9) {
      }

      try {
         if (Bukkit.getPluginManager().getPlugin("TAB") != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " tabprefix");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " tagsuffix");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " customtabname");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " customtagname");
         }
      } catch (Exception var7) {
      }

      try {
         if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " meta removeprefix 100");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " meta removesuffix 100");
         }
      } catch (Exception var6) {
      }

   }
}
