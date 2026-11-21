package me.xyr0.economysmpcore;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import me.clip.placeholderapi.PlaceholderAPI;
import me.xyr0.economysmpcore.economy.CoreEconomy;
import me.xyr0.economysmpcore.util.Tasks;
import me.xyr0.economysmpcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TabModule implements Module, Listener {
   private final EconomySMPcore plugin;
   private FileConfiguration cfg;
   private int taskId = -1;
   private int nameTaskId = -1;
   private final List<String> groupOrder = new ArrayList();
   private boolean tabEnabled = true;
   private boolean nameFormatting = true;
   private String disableCondition = "";
   private final Map<Scoreboard, Map<UUID, String>> teamState = new WeakHashMap();
   private final Map<Scoreboard, Map<String, String>> objectiveState = new WeakHashMap();
   private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap();

   public TabModule(EconomySMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "tab";
   }

   public void onEnable() {
      this.loadConfig();
      if (!this.tabEnabled) {
         this.plugin.getLogger().info("TAB disabled in TAB.yml (scoreboard unaffected).");
      } else {
         this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
         this.taskId = Tasks.runTimer(this.plugin, this::refreshAll, 20L, 40L);
         this.nameTaskId = Tasks.runTimer(this.plugin, this::refreshNameTags, 100L, 200L);

         for(Player p : Bukkit.getOnlinePlayers()) {
            this.apply(p);
         }

         this.refreshNameTags();
      }
   }

   public void onDisable() {
      if (this.taskId != -1) {
         Tasks.cancel(this.taskId);
      }

      if (this.nameTaskId != -1) {
         Tasks.cancel(this.nameTaskId);
      }

      this.taskId = -1;
      this.nameTaskId = -1;

      for(Player p : Bukkit.getOnlinePlayers()) {
         p.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
         this.clearTeam(p);
         BossBar bar = (BossBar)this.bossBars.remove(p.getUniqueId());
         if (bar != null) {
            bar.removeAll();
         }
      }

      this.bossBars.clear();
      this.teamState.clear();
      this.objectiveState.clear();
   }

   public void reload() {
      this.onDisable();
      this.onEnable();
   }

   private void loadConfig() {
      File f = new File(this.plugin.getDataFolder(), "TAB.yml");
      if (!f.exists()) {
         try {
            this.plugin.saveResource("TAB.yml", false);
         } catch (Exception var11) {
         }
      }

      this.cfg = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
      this.groupOrder.clear();
      this.tabEnabled = this.cfg.getBoolean("tab.enabled", true);
      this.nameFormatting = this.cfg.getBoolean("tab.tablist-name-formatting.enabled", true);
      this.disableCondition = this.cfg.getString("tab.tablist-name-formatting.disable-condition", "");

      for(String line : this.cfg.getStringList("tab.tablist-name-formatting.sorting-types")) {
         if (line != null && line.toUpperCase(Locale.ROOT).startsWith("GROUPS:")) {
            String body = line.substring(7);

            for(String g : body.split(",")) {
               String t = g.trim().toLowerCase(Locale.ROOT);
               if (!t.isEmpty()) {
                  this.groupOrder.add(t);
               }
            }
         }
      }

      if (this.groupOrder.isEmpty()) {
         this.groupOrder.add("default");
      }

   }

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      Tasks.run(this.plugin, () -> {
         this.refreshAll();
         this.refreshNameTags();
      });
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent e) {
      this.clearTeam(e.getPlayer());
      Tasks.run(this.plugin, () -> {
         this.refreshAll();
         this.refreshNameTags();
      });
   }

   private void refreshAll() {
      for(Player p : Bukkit.getOnlinePlayers()) {
         this.apply(p);
      }

   }

   private void apply(Player player) {
      if (player != null && player.isOnline() && this.cfg != null) {
         if (this.tabEnabled) {
            this.applyHeaderFooter(player);
            this.applyDisplayOptions(player);
            if (this.nameFormatting && !this.isDisabled(player)) {
               this.applySort(player);
            }

         }
      }
   }

   private void refreshNameTags() {
      List<Player> players = new ArrayList(Bukkit.getOnlinePlayers());
      Map<UUID, NameTagData> data = new HashMap();

      for(Player target : players) {
         String group = this.primaryGroup(target).toLowerCase(Locale.ROOT);
         int priority = this.groupOrder.indexOf(group);
         if (priority < 0) {
            priority = this.groupOrder.size();
         }

         String id = target.getUniqueId().toString().replace("-", "").substring(0, 11);
         String teamName = String.format(Locale.ROOT, "0%02d%s", Math.min(priority, 99), id);
         String prefix = this.groupPrefix(target);
         String suffix = this.groupSuffix(target);
         ChatColor color = this.prefixColor(prefix);
         String signature = teamName + "\\u0000" + (prefix == null ? "" : prefix) + "\\u0000" + (suffix == null ? "" : suffix) + "\\u0000" + color.name();
         data.put(target.getUniqueId(), new NameTagData(teamName, prefix, suffix, color, signature));
      }

      Map<Scoreboard, Boolean> updatedBoards = new IdentityHashMap();

      for(Player viewer : players) {
         Scoreboard board = viewer.getScoreboard();
         if (board == null) {
            board = Bukkit.getScoreboardManager().getMainScoreboard();
         }

         if (updatedBoards.put(board, Boolean.TRUE) == null) {
            Map<UUID, String> states = (Map)this.teamState.computeIfAbsent(board, (key) -> new HashMap());

            for(Player target : players) {
               NameTagData tag = (NameTagData)data.get(target.getUniqueId());
               if (tag != null) {
                  Team team = board.getTeam(tag.teamName());
                  if (team == null) {
                     try {
                        team = board.registerNewTeam(tag.teamName());
                     } catch (IllegalArgumentException var14) {
                        team = board.getTeam(tag.teamName());
                     }
                  }

                  if (team != null) {
                     String previous = (String)states.get(target.getUniqueId());
                     if (!tag.signature().equals(previous)) {
                        team.prefix(TextUtil.component(tag.prefix() == null ? "" : tag.prefix()));
                        team.suffix(TextUtil.component(tag.suffix() == null ? "" : tag.suffix()));
                        team.setColor(tag.color());
                        states.put(target.getUniqueId(), tag.signature());
                     }

                     Team current = board.getEntryTeam(target.getName());
                     if (current != team) {
                        if (current != null) {
                           current.removeEntry(target.getName());
                        }

                        if (!team.hasEntry(target.getName())) {
                           team.addEntry(target.getName());
                        }
                     }
                  }
               }
            }

            states.keySet().removeIf((idx) -> !data.containsKey(idx));
         }
      }

   }

   private void applyDisplayOptions(Player player) {
      int online = Bukkit.getOnlinePlayers().size();
      if (this.cfg.getBoolean("tab.bossbar.enabled", false)) {
         String title = this.cfg.getString("tab.bossbar.title", "&bEconomySMP &8» &f%online% players").replace("%online%", String.valueOf(online)).replace("%player%", player.getName()).replace("&#00FC00&l$&#00FC00%economysmpcore_pay_balance%", String.valueOf((int)Math.ceil(player.getHealth()))).replace("%ping%", String.valueOf(player.getPing()));
         BossBar bar = (BossBar)this.bossBars.computeIfAbsent(player.getUniqueId(), (id) -> Bukkit.createBossBar(TextUtil.color(title), this.barColor(this.cfg.getString("tab.bossbar.color", "BLUE")), this.barStyle(this.cfg.getString("tab.bossbar.style", "SOLID")), new BarFlag[0]));
         bar.setTitle(TextUtil.color(title));
         bar.setColor(this.barColor(this.cfg.getString("tab.bossbar.color", "BLUE")));
         bar.setStyle(this.barStyle(this.cfg.getString("tab.bossbar.style", "SOLID")));
         if (!bar.getPlayers().contains(player)) {
            bar.addPlayer(player);
         }

         bar.setProgress((double)1.0F);
      } else {
         BossBar bar = (BossBar)this.bossBars.remove(player.getUniqueId());
         if (bar != null) {
            bar.removeAll();
         }
      }

      Scoreboard board = player.getScoreboard();
      if (board == null) {
         board = Bukkit.getScoreboardManager().getMainScoreboard();
      }

      this.updateObjective(board, "belowname-objective", "astral_below", DisplaySlot.BELOW_NAME, player);
      this.updateObjective(board, "playerlist-objective", "astral_list", DisplaySlot.PLAYER_LIST, player);
   }

   private void updateObjective(Scoreboard board, String path, String name, DisplaySlot slot, Player viewer) {
      Map<String, String> states = (Map)this.objectiveState.computeIfAbsent(board, (key) -> new HashMap());
      if (!this.cfg.getBoolean("tab." + path + ".enabled", false)) {
         Objective old = board.getObjective(name);
         if (old != null && old.getDisplaySlot() == slot) {
            old.setDisplaySlot((DisplaySlot)null);
         }

         states.keySet().removeIf((key) -> key.startsWith(path + ":"));
      } else {
         Objective objective = board.getObjective(name);
         if (objective == null) {
            states.keySet().removeIf((key) -> key.startsWith(path + ":"));
         }

         String title = this.cfg.getString("tab." + path + ".title", path.equals("belowname-objective") ? "&c❤" : "&aPing");
         if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            title = PlaceholderAPI.setPlaceholders(viewer, title);
         }

         if (objective == null) {
            try {
               objective = board.registerNewObjective(name, Criteria.DUMMY, TextUtil.component(title));
            } catch (IllegalArgumentException var21) {
               objective = board.getObjective(name);
            }
         }

         if (objective != null) {
            String titleKey = path + ":title";
            if (!title.equals(states.get(titleKey))) {
               objective.displayName(TextUtil.component(title));
               states.put(titleKey, title);
            }

            if (objective.getDisplaySlot() != slot) {
               objective.setDisplaySlot(slot);
            }

            for(Player target : Bukkit.getOnlinePlayers()) {
               String format = this.cfg.getString("tab." + path + ".score", path.equals("belowname-objective") ? "&#00FC00&l$&#00FC00%economysmpcore_pay_balance%" : "%ping%");
               int value;
               String shown;
               if (path.equals("belowname-objective") && format.toLowerCase(Locale.ROOT).contains("balance")) {
                  double balance;
                  try {
                     balance = Math.max((double)0.0F, CoreEconomy.balanceOf(target));
                  } catch (Exception var19) {
                     balance = (double)0.0F;
                  }

                  value = (int)Math.min((double)Integer.MAX_VALUE, Math.floor(balance));
                  shown = format.replace("%economysmpcore_pay_balance%", CoreEconomy.formatMoney(balance));
                  if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                     shown = PlaceholderAPI.setPlaceholders(target, shown);
                  }
               } else {
                  value = path.equals("belowname-objective") ? (int)Math.ceil(target.getHealth()) : target.getPing();
                  shown = format.replace("%ping%", String.valueOf(target.getPing())).replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("%player%", target.getName());
                  if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                     shown = PlaceholderAPI.setPlaceholders(target, shown);
                  }

                  try {
                     value = Integer.parseInt(shown.replaceAll("[^0-9-]", ""));
                  } catch (Exception var20) {
                  }
               }

               String stateKey = path + ":" + String.valueOf(target.getUniqueId());
               String state = value + "|" + shown;
               String previous = (String)states.get(stateKey);
               if (!state.equals(previous)) {
                  Score score = objective.getScore(target.getName());
                  if (previous == null || score.getScore() != value) {
                     score.setScore(value);
                  }

                  if (path.equals("belowname-objective")) {
                     this.applyFixedScoreFormat(score, shown);
                  }

                  states.put(stateKey, state);
               }
            }

         }
      }
   }

   private void applyFixedScoreFormat(Object score, String shown) {
      try {
         Class<?> numberFormatClass = Class.forName("org.bukkit.scoreboard.NumberFormat");
         Object fixed = numberFormatClass.getMethod("fixed", Component.class).invoke((Object)null, TextUtil.component(shown));
         Class.forName("org.bukkit.scoreboard.Score").getMethod("setNumberFormat", numberFormatClass).invoke(score, fixed);
      } catch (Throwable var5) {
      }

   }

   private BarColor barColor(String value) {
      try {
         return BarColor.valueOf(value.toUpperCase(Locale.ROOT));
      } catch (Exception var3) {
         return BarColor.BLUE;
      }
   }

   private BarStyle barStyle(String value) {
      try {
         return BarStyle.valueOf(value.toUpperCase(Locale.ROOT));
      } catch (Exception var3) {
         return BarStyle.SOLID;
      }
   }

   private void applyHeaderFooter(Player player) {
      List<String> headerLines = this.cfg.getStringList("tab.header");
      List<String> footerLines = this.cfg.getStringList("tab.footer");
      String online = String.valueOf(Bukkit.getOnlinePlayers().size());
      Component header = this.joinLines(headerLines, player, online);
      Component footer = this.joinLines(footerLines, player, online);
      player.sendPlayerListHeaderAndFooter(header, footer);
   }

   private Component joinLines(List<String> lines, Player player, String online) {
      if (lines != null && !lines.isEmpty()) {
         List<Component> parts = new ArrayList();

         for(int i = 0; i < lines.size(); ++i) {
            String line = (String)lines.get(i);
            if (line == null) {
               line = "";
            }

            line = line.replace("%online%", online).replace("%player%", player.getName()).replace("%world%", player.getWorld() != null ? player.getWorld().getName() : "");

            try {
               if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                  line = PlaceholderAPI.setPlaceholders(player, line);
               }
            } catch (Exception var8) {
            }

            parts.add(TextUtil.component(line));
            if (i < lines.size() - 1) {
               parts.add(Component.newline());
            }
         }

         Component out = Component.empty();

         for(Component c : parts) {
            out = out.append(c);
         }

         return out;
      } else {
         return Component.empty();
      }
   }

   private boolean isDisabled(Player player) {
      if (this.disableCondition != null && !this.disableCondition.isEmpty()) {
         String cond = this.disableCondition;
         String world = player.getWorld() != null ? player.getWorld().getName() : "";
         cond = cond.replace("%world%", world).replace("%player%", player.getName());
         if (!cond.contains("=")) {
            return false;
         } else {
            String[] parts = cond.split("=", 2);
            return parts.length == 2 && parts[0].trim().equalsIgnoreCase(parts[1].trim());
         }
      } else {
         return false;
      }
   }

   private void applySort(Player player) {
      String prefix = this.groupPrefix(player);
      String suffix = this.groupSuffix(player);
      String formatted = (prefix == null ? "" : prefix) + player.getName() + (suffix == null ? "" : suffix);

      try {
         player.playerListName(TextUtil.component(formatted));
      } catch (Exception var6) {
         player.setPlayerListName(TextUtil.color(formatted));
      }

   }

   private void clearTeam(Player player) {
      try {
         player.playerListName((Component)null);
      } catch (Exception var9) {
         player.setPlayerListName((String)null);
      }

      String entry = player.getName();
      UUID id = player.getUniqueId();

      for(Player viewer : Bukkit.getOnlinePlayers()) {
         Scoreboard board = viewer.getScoreboard();
         if (board != null) {
            Team team = board.getEntryTeam(entry);
            if (team != null) {
               team.removeEntry(entry);
            }

            Map<UUID, String> states = (Map)this.teamState.get(board);
            if (states != null) {
               states.remove(id);
            }
         }
      }

      this.teamState.values().forEach((statesx) -> statesx.remove(id));
   }

   private String sanitize(String s) {
      return s.replaceAll("[^a-zA-Z0-9_]", "");
   }

   private ChatColor prefixColor(String prefix) {
      if (prefix != null && !prefix.isEmpty()) {
         ChatColor color = ChatColor.WHITE;

         for(int i = 0; i + 1 < prefix.length(); ++i) {
            char marker = prefix.charAt(i);
            if (marker == '&' || marker == 167) {
               if (i + 7 < prefix.length() && prefix.charAt(i + 1) == '#') {
                  String hex = prefix.substring(i + 2, i + 8);

                  try {
                     int rgb = Integer.parseInt(hex, 16);
                     color = this.closestLegacyColor(rgb);
                     i += 7;
                     continue;
                  } catch (NumberFormatException var7) {
                  }
               }

               char code = Character.toLowerCase(prefix.charAt(i + 1));
               if ("0123456789abcdef".indexOf(code) >= 0) {
                  ChatColor parsed = ChatColor.getByChar(code);
                  if (parsed != null) {
                     color = parsed;
                  }
               } else if (code == 'r') {
                  color = ChatColor.WHITE;
               }
            }
         }

         return color;
      } else {
         return ChatColor.WHITE;
      }
   }

   private ChatColor closestLegacyColor(int rgb) {
      ChatColor[] colors = new ChatColor[]{ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE};
      int bestDistance = Integer.MAX_VALUE;
      ChatColor best = ChatColor.WHITE;

      for(ChatColor candidate : colors) {
         Color c = Color.decode(candidate == ChatColor.BLACK ? "#000000" : this.legacyRgb(candidate));
         int r = rgb >> 16 & 255;
         int g = rgb >> 8 & 255;
         int b = rgb & 255;
         int dr = r - c.getRed();
         int dg = g - c.getGreen();
         int db = b - c.getBlue();
         int distance = dr * dr + dg * dg + db * db;
         if (distance < bestDistance) {
            bestDistance = distance;
            best = candidate;
         }
      }

      return best;
   }

   private String legacyRgb(ChatColor color) {
      switch (color) {
         case BLACK -> {
            return "#000000";
         }
         case DARK_BLUE -> {
            return "#0000AA";
         }
         case DARK_GREEN -> {
            return "#00AA00";
         }
         case DARK_AQUA -> {
            return "#00AAAA";
         }
         case DARK_RED -> {
            return "#AA0000";
         }
         case DARK_PURPLE -> {
            return "#AA00AA";
         }
         case GOLD -> {
            return "#FFAA00";
         }
         case GRAY -> {
            return "#AAAAAA";
         }
         case DARK_GRAY -> {
            return "#555555";
         }
         case BLUE -> {
            return "#5555FF";
         }
         case GREEN -> {
            return "#55FF55";
         }
         case AQUA -> {
            return "#55FFFF";
         }
         case RED -> {
            return "#FF5555";
         }
         case LIGHT_PURPLE -> {
            return "#FF55FF";
         }
         case YELLOW -> {
            return "#FFFF55";
         }
         default -> {
            return "#FFFFFF";
         }
      }
   }

   private String primaryGroup(Player player) {
      try {
         if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            return "default";
         } else {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
               return "default";
            } else {
               String g = user.getPrimaryGroup();
               return g != null ? g : "default";
            }
         }
      } catch (Exception var5) {
         return "default";
      }
   }

   private String groupSuffix(Player player) {
      try {
         if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            return "";
         } else {
            User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(player);
            String suffix = user.getCachedData().getMetaData().getSuffix();
            return suffix == null ? "" : suffix;
         }
      } catch (Throwable var4) {
         return "";
      }
   }

   private String groupPrefix(Player player) {
      try {
         if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            return "";
         } else {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
               return "";
            } else {
               String prefix = user.getCachedData().getMetaData().getPrefix();
               return prefix != null ? prefix : "";
            }
         }
      } catch (Exception var5) {
         return "";
      }
   }

   private static record NameTagData(String teamName, String prefix, String suffix, ChatColor color, String signature) {
   }
}
