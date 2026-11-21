package me.xyr0.economysmpcore.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import me.xyr0.economysmpcore.EconomySMPcore;
import me.xyr0.economysmpcore.Module;
import me.xyr0.economysmpcore.SettingsModule;
import me.xyr0.economysmpcore.economy.CoreEconomy;
import me.xyr0.economysmpcore.keyall.AstralKeyall;
import me.xyr0.economysmpcore.shards.AstralShards;
import me.xyr0.economysmpcore.teams.AstralTeams;
import me.xyr0.economysmpcore.util.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardModule implements Module, Listener {
   private final EconomySMPcore plugin;
   private FileConfiguration cfg;
   private final Set<UUID> hidden = new HashSet();
   private final Map<UUID, Scoreboard> boards = new HashMap();
   private int taskId = -1;

   public ScoreboardModule(EconomySMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "scoreboard";
   }

   public void onEnable() {
      File folder = this.plugin.getDataFolder();
      if (!folder.exists()) {
         folder.mkdirs();
      }

      File f = new File(this.plugin.getDataFolder(), "TAB.yml");
      if (!f.exists()) {
         try {
            this.plugin.saveResource("TAB.yml", false);
         } catch (Exception var7) {
         }
      }

      if (!f.exists()) {
         File legacy = new File(this.plugin.getDataFolder(), "scoreboard.yml");
         if (legacy.exists()) {
            f = legacy;
         }
      }

      this.cfg = (FileConfiguration)(f.exists() ? YamlConfiguration.loadConfiguration(f) : this.plugin.getConfig());
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.plugin.commands().registerHandler("sb", this::toggle);
      FoliaSidebar.init(this.plugin);
      this.taskId = Tasks.runTimer(this.plugin, this::refreshAll, 20L, 20L);

      for(Player p : Bukkit.getOnlinePlayers()) {
         if (Tasks.isFolia()) {
            Tasks.runAtEntity(this.plugin, p, () -> {
               try {
                  this.show(p);
               } catch (Exception var3) {
               }

            });
         } else {
            try {
               this.show(p);
            } catch (Exception var6) {
            }
         }
      }

   }

   public void onDisable() {
      if (this.taskId != -1) {
         Tasks.cancel(this.taskId);
      }

      this.taskId = -1;

      for(Player p : Bukkit.getOnlinePlayers()) {
         p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
      }

      this.boards.clear();
   }

   public void reload() {
      this.onDisable();
      this.onEnable();
   }

   public boolean isHidden(UUID id) {
      return this.hidden.contains(id);
   }

   public void setHidden(UUID id, boolean hide) {
      if (hide) {
         this.hidden.add(id);
         this.boards.remove(id);
         Player p = Bukkit.getPlayer(id);
         if (p != null && p.isOnline()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
         }
      } else {
         this.hidden.remove(id);
         Player p = Bukkit.getPlayer(id);
         if (p != null && p.isOnline()) {
            this.show(p);
         }
      }

   }

   private boolean toggle(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         Runnable act = () -> {
            try {
               if (this.hidden.contains(p.getUniqueId())) {
                  this.hidden.remove(p.getUniqueId());
                  this.show(p);
                  p.sendMessage("§aScoreboard enabled.");
                  this.syncSettings(p.getUniqueId(), true);
               } else {
                  this.hidden.add(p.getUniqueId());
                  this.boards.remove(p.getUniqueId());

                  try {
                     FoliaSidebar.remove(p);
                  } catch (Exception var4) {
                  }

                  try {
                     p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                  } catch (Exception var3) {
                  }

                  p.sendMessage("§cScoreboard disabled.");
                  this.syncSettings(p.getUniqueId(), false);
               }
            } catch (Exception t) {
               Logger var10000 = this.plugin.getLogger();
               String var10001 = p.getName();
               var10000.warning("Scoreboard toggle failed for " + var10001 + ": " + String.valueOf(t));
               t.printStackTrace();
               p.sendMessage("§cScoreboard error. Check console.");
            }

         };
         if (Tasks.isFolia()) {
            Tasks.runAtEntity(this.plugin, p, act);
         } else {
            act.run();
         }

         return true;
      } else {
         return true;
      }
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      if (!this.hidden.contains(e.getPlayer().getUniqueId())) {
         Player pl = e.getPlayer();
         Tasks.runLater(this.plugin, () -> {
            if (Tasks.isFolia()) {
               Tasks.runAtEntity(this.plugin, pl, () -> {
                  try {
                     this.show(pl);
                  } catch (Exception var3) {
                  }

               });
            } else {
               try {
                  this.show(pl);
               } catch (Exception var3) {
               }
            }

         }, 40L);
      }

   }

   @EventHandler
   public void onQuit(PlayerQuitEvent e) {
      this.hidden.remove(e.getPlayer().getUniqueId());
      this.boards.remove(e.getPlayer().getUniqueId());

      try {
         FoliaSidebar.remove(e.getPlayer());
      } catch (Exception var3) {
      }

   }

   private void refreshAll() {
      for(Player p : Bukkit.getOnlinePlayers()) {
         if (!this.hidden.contains(p.getUniqueId())) {
            if (Tasks.isFolia()) {
               Tasks.runAtEntity(this.plugin, p, () -> {
                  try {
                     this.update(p);
                  } catch (Exception var3) {
                  }

               });
            } else {
               try {
                  this.update(p);
               } catch (Exception var4) {
               }
            }
         }
      }

   }

   private void syncSettings(UUID id, boolean enabled) {
      try {
         Module mod = this.plugin.modules().get("settings");
         if (mod instanceof SettingsModule sm) {
            sm.setScoreboardEnabled(id, enabled);
         }
      } catch (Exception var5) {
      }

   }

   private boolean isEnabled() {
      if (this.cfg == null) {
         return true;
      } else if (this.cfg.contains("scoreboard.enabled")) {
         return this.cfg.getBoolean("scoreboard.enabled", true);
      } else {
         return this.cfg.contains("enabled") ? this.cfg.getBoolean("enabled", true) : true;
      }
   }

   private void show(Player p) {
      if (p != null && p.isOnline()) {
         try {
            Module mod = this.plugin.modules().get("settings");
            if (mod instanceof SettingsModule) {
               SettingsModule sm = (SettingsModule)mod;
               if (sm.isScoreboardOff(p.getUniqueId())) {
                  return;
               }
            }
         } catch (Exception var20) {
         }

         if (this.isEnabled()) {
            if (!this.hidden.contains(p.getUniqueId())) {
               try {
                  if (Tasks.isFolia()) {
                     this.showFolia(p);
                     return;
                  }

                  Scoreboard board = (Scoreboard)this.boards.get(p.getUniqueId());
                  if (board != null && p.getScoreboard() == board) {
                     this.update(p);
                  } else {
                     board = Bukkit.getScoreboardManager().getNewScoreboard();
                     this.boards.put(p.getUniqueId(), board);
                     String title = this.titleFor(p);

                     Objective obj;
                     try {
                        obj = board.registerNewObjective("asmp", Criteria.DUMMY, this.legacy(title));
                     } catch (Throwable var19) {
                        try {
                           obj = board.registerNewObjective("asmp", "dummy", this.color(title));
                        } catch (Throwable var18) {
                           obj = board.registerNewObjective("asmp", "dummy");

                           try {
                              obj.setDisplayName(this.color(title));
                           } catch (Exception var17) {
                           }
                        }
                     }

                     obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                     this.applyNumberFormat(obj);
                     List<String> lines = this.linesFor(p);
                     if (lines == null || lines.isEmpty()) {
                        lines = List.of("&7EconomySMP");
                     }

                     int score = Math.min(lines.size(), 15);
                     int i = 0;

                     for(String raw : lines) {
                        if (i >= 15) {
                           break;
                        }

                        String entry = this.entryKey(i);
                        obj.getScore(entry).setScore(score--);
                        Team team = board.getTeam("sb" + i);
                        if (team == null) {
                           team = board.registerNewTeam("sb" + i);
                        }

                        team.addEntry(entry);
                        String text = this.apply(raw == null ? "" : raw, p);
                        if (text == null) {
                           text = "";
                        }

                        if (text.length() > 64) {
                           text = text.substring(0, 64);
                        }

                        try {
                           team.prefix(this.legacy(text));
                        } catch (Exception var16) {
                           try {
                              team.setPrefix(this.color(text));
                           } catch (Exception var15) {
                           }
                        }

                        ++i;
                     }

                     p.setScoreboard(board);
                  }
               } catch (Exception t) {
                  Logger var10000 = this.plugin.getLogger();
                  String var10001 = p.getName();
                  var10000.warning("Scoreboard show failed for " + var10001 + ": " + String.valueOf(t));
               }

            }
         }
      }
   }

   private void showFolia(Player p) {
      try {
         if (Bukkit.getPluginManager().getPlugin("packetevents") == null && Bukkit.getPluginManager().getPlugin("PacketEvents") == null) {
            this.plugin.getLogger().warning("Folia scoreboard needs packetevents installed.");
            return;
         }

         List<String> rawLines = this.linesFor(p);
         if (rawLines == null || rawLines.isEmpty()) {
            rawLines = List.of("&7EconomySMP", "&fOnline: %online%");
         }

         List<String> lines = new ArrayList();

         for(String raw : rawLines) {
            if (lines.size() >= 15) {
               break;
            }

            String text = this.apply(raw == null ? "" : raw, p);
            if (text == null) {
               text = "";
            }

            lines.add(text);
         }

         String title = this.titleFor(p);
         FoliaSidebar.of(p).set(title == null ? "&bEconomySMP" : title, lines);
      } catch (Exception t) {
         this.plugin.getLogger().warning("showFolia failed: " + String.valueOf(t));
         t.printStackTrace();
      }

   }

   private void update(Player p) {
      if (p != null && p.isOnline()) {
         try {
            Module mod = this.plugin.modules().get("settings");
            if (mod instanceof SettingsModule) {
               SettingsModule sm = (SettingsModule)mod;
               if (sm.isScoreboardOff(p.getUniqueId())) {
                  return;
               }
            }
         } catch (Exception var12) {
         }

         if (this.isEnabled()) {
            if (!this.hidden.contains(p.getUniqueId())) {
               if (Tasks.isFolia()) {
                  this.showFolia(p);
               } else {
                  Scoreboard board = (Scoreboard)this.boards.get(p.getUniqueId());
                  if (board != null && p.getScoreboard() == board) {
                     Objective obj = board.getObjective("asmp");
                     if (obj == null) {
                        this.boards.remove(p.getUniqueId());
                        this.show(p);
                     } else {
                        obj.displayName(this.legacy(this.titleFor(p)));
                        this.applyNumberFormat(obj);
                        List<String> lines = this.linesFor(p);
                        int needed = Math.min(lines.size(), 15);

                        for(int i = 0; i < 15; ++i) {
                           String teamName = "sb" + i;
                           Team team = board.getTeam(teamName);
                           String entry = this.entryKey(i);
                           if (i < needed) {
                              if (team == null) {
                                 team = board.registerNewTeam(teamName);
                                 team.addEntry(entry);
                                 obj.getScore(entry).setScore(needed - i);
                              } else {
                                 if (!team.hasEntry(entry)) {
                                    team.addEntry(entry);
                                 }

                                 obj.getScore(entry).setScore(needed - i);
                              }

                              team.prefix(this.legacy(this.apply((String)lines.get(i), p)));
                           } else if (team != null) {
                              for(String e : team.getEntries()) {
                                 board.resetScores(e);
                              }

                              team.unregister();
                           }
                        }

                     }
                  } else {
                     this.show(p);
                  }
               }
            }
         }
      }
   }

   private void applyNumberFormat(Objective obj) {
      boolean show = true;
      if (this.cfg != null) {
         if (this.cfg.contains("scoreboard.show-numbers")) {
            show = this.cfg.getBoolean("scoreboard.show-numbers", true);
         } else if (this.cfg.contains("show-numbers")) {
            show = this.cfg.getBoolean("show-numbers", true);
         } else if (this.cfg.contains("scoreboard.use-numbers")) {
            show = this.cfg.getBoolean("scoreboard.use-numbers", true);
         }
      }

      try {
         if (show) {
            obj.numberFormat((NumberFormat)null);
         } else {
            obj.numberFormat(NumberFormat.blank());
         }
      } catch (Exception var4) {
      }

   }

   private String entryKey(int i) {
      char c = (char)(97 + i % 26);
      String var10000 = Integer.toHexString(i % 16);
      return "§" + var10000 + "§" + c;
   }

   private String titleFor(Player p) {
      boolean inTeam = !this.teamName(p).equals("None") && !this.teamName(p).isEmpty();
      return inTeam && this.cfg.contains("scoreboard.scoreboards.in-team.title") ? this.cfg.getString("scoreboard.scoreboards.in-team.title", this.firstTitle()) : this.firstTitle();
   }

   private List<String> linesFor(Player p) {
      boolean inTeam = !this.teamName(p).equals("None") && !this.teamName(p).isEmpty();
      if (inTeam && this.cfg.contains("scoreboard.scoreboards.in-team.lines")) {
         List<String> l = this.cfg.getStringList("scoreboard.scoreboards.in-team.lines");
         if (!l.isEmpty()) {
            return l;
         }
      }

      return this.firstLines();
   }

   private String firstTitle() {
      return this.cfg.contains("scoreboard.scoreboards.not-in-team.title") ? this.cfg.getString("scoreboard.scoreboards.not-in-team.title", "&bSMP") : this.cfg.getString("scoreboard.title", this.cfg.getString("title", "&bEconomySMP"));
   }

   private List<String> firstLines() {
      if (this.cfg.contains("scoreboard.scoreboards.not-in-team.lines")) {
         return this.cfg.getStringList("scoreboard.scoreboards.not-in-team.lines");
      } else {
         List<String> lines = this.cfg.getStringList("scoreboard.lines");
         if (lines.isEmpty()) {
            lines = this.cfg.getStringList("lines");
         }

         return lines.isEmpty() ? List.of("&7&m---------------", "&fPlayer: &b%player%", "&fBalance: &a%economysmpcore_pay_balance%", "&fKills: &c%economysmpcore_stats_kills%", "&fDeaths: &c%economysmpcore_stats_deaths%", "&7&m---------------") : lines;
      }
   }

   private String apply(String line, Player p) {
      String out = line.replace("%economysmpcore_pay_balance%", this.bal(p)).replace("%economysmpcore_shards_balance%", this.shards(p)).replace("%economysmpcore_stats_kills%", String.valueOf(p.getStatistic(Statistic.PLAYER_KILLS))).replace("%economysmpcore_stats_deaths%", String.valueOf(p.getStatistic(Statistic.DEATHS))).replace("%economysmpcore_keyall_time%", this.keyallTime(p)).replace("%economysmpcore_stats_playtime%", this.playtime(p)).replace("%economysmpcore_teams_name%", this.teamName(p)).replace("%economysmpcore_team_name%", this.teamName(p)).replace("%astralteams_name%", this.teamName(p)).replace("%team%", this.teamName(p)).replace("%economysmpcore_ping%", String.valueOf(p.getPing())).replace("%ping%", String.valueOf(p.getPing())).replace("%player%", p.getName());

      try {
         if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            out = PlaceholderAPI.setPlaceholders(p, out);
         }
      } catch (Exception var5) {
      }

      return out;
   }

   private String shards(Player p) {
      try {
         AstralShards s = AstralShards.get();
         if (s != null && s.getShards() != null) {
            long bal = s.getShards().getBalance(p);
            return CoreEconomy.formatMoney((double)bal);
         }
      } catch (Exception var7) {
      }

      try {
         File f = new File(this.plugin.getDataFolder(), "shards/data.yml");
         if (f.exists()) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            long bal = cfg.getLong("players." + String.valueOf(p.getUniqueId()), 0L);
            return CoreEconomy.formatMoney((double)bal);
         }
      } catch (Exception var6) {
      }

      return "0";
   }

   private String teamName(Player p) {
      try {
         AstralTeams t = AstralTeams.get();
         if (t != null && t.getTeams() != null) {
            me.xyr0.economysmpcore.teams.Team team = t.getTeams().getByPlayer(p.getUniqueId());
            if (team != null) {
               return team.getName();
            }
         }
      } catch (Exception var4) {
      }

      return "None";
   }

   private String keyallTime(Player p) {
      try {
         AstralKeyall k = AstralKeyall.get();
         if (k != null && k.getTimer() != null) {
            return k.getTimer().format(p);
         }
      } catch (Exception var3) {
      }

      return "--";
   }

   private String bal(Player p) {
      try {
         return CoreEconomy.formatMoney(CoreEconomy.balanceOf(p));
      } catch (Exception var3) {
         return "$0";
      }
   }

   private String playtime(Player p) {
      int ticks = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
      long m = (long)(ticks / 20 / 60);
      return m / 60L + "h " + m % 60L + "m";
   }

   private Component legacy(String s) {
      return LegacyComponentSerializer.legacySection().deserialize(this.color(s));
   }

   private String color(String s) {
      if (s == null) {
         return "";
      } else {
         s = s.replaceAll("<#([A-Fa-f0-9]{6})>", "&#$1");
         s = s.replaceAll("</#[A-Fa-f0-9]{6}>", "");
         Pattern HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");
         Matcher m = HEX.matcher(s);
         StringBuffer sb = new StringBuffer();

         while(m.find()) {
            String hex = m.group(1);
            StringBuilder rep = new StringBuilder("§x");

            for(char ch : hex.toCharArray()) {
               rep.append('§').append(ch);
            }

            m.appendReplacement(sb, Matcher.quoteReplacement(rep.toString()));
         }

         m.appendTail(sb);
         return sb.toString().replace('&', '§');
      }
   }
}
