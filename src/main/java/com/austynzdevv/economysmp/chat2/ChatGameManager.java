package com.austynzdevv.economysmp.chat2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ChatGameManager {
   private final EconomyChat plugin;
   private FileConfiguration cfg;
   private boolean running;
   private boolean activeRound;
   private String answer;
   private long startMs;
   private int intervalTask = -1;
   private int timeoutTask = -1;
   private Economy economy;

   public ChatGameManager(EconomyChat plugin) {
      this.plugin = plugin;
      this.setupEconomy();
      this.reload();
   }

   private void setupEconomy() {
      if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
         RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
         if (rsp != null) {
            this.economy = (Economy)rsp.getProvider();
         }

      }
   }

   public void reload() {
      this.stopInterval();
      File file = new File(this.plugin.getDataFolder(), "chatgames.yml");
      if (!file.exists()) {
         this.plugin.saveResource("chatgames.yml", false);
      }

      this.cfg = YamlConfiguration.loadConfiguration(file);
      if (this.cfg.getBoolean("enabled", true) && this.cfg.getBoolean("auto-start", true)) {
         this.startInterval();
      }

   }

   public void startInterval() {
      this.stopInterval();
      this.running = true;
      long interval = Math.max(1L, this.cfg.getLong("interval-minutes", 12L)) * 60L * 20L;
      this.intervalTask = Tasks.runTimer(this.plugin.getCore(), this::startRandomGame, 100L, interval);
   }

   public void stopInterval() {
      this.running = false;
      if (this.intervalTask != -1) {
         Tasks.cancel(this.intervalTask);
         this.intervalTask = -1;
      }

      this.cancelTimeout();
      this.activeRound = false;
      this.answer = null;
   }

   public boolean isRunning() {
      return this.running;
   }

   public boolean isActiveRound() {
      return this.activeRound;
   }

   public void forceGame() {
      if (!this.activeRound) {
         this.startRandomGame();
      }
   }

   private void cancelTimeout() {
      if (this.timeoutTask != -1) {
         Tasks.cancel(this.timeoutTask);
         this.timeoutTask = -1;
      }

   }

   private String msg(String key) {
      return this.cfg.getString("messages." + key, key);
   }

   private String prefix() {
      return this.cfg.getString("prefix", "&#00fc88ᴄʜᴀᴛ ɢᴀᴍᴇ &8» ");
   }

   private void broadcast(String text) {
      Server var10000 = Bukkit.getServer();
      String var10001 = this.prefix();
      var10000.sendMessage(ColorUtil.colorComponent(var10001 + text));
   }

   private void broadcastHover(String visible, String hover) {
      String var10000 = this.prefix();
      Component base = ColorUtil.colorComponent(var10000 + visible);
      Component tip = ColorUtil.colorComponent(hover);
      Component out = base.hoverEvent(HoverEvent.showText(tip));
      Bukkit.getServer().sendMessage(out);
   }

   public synchronized void startRandomGame() {
      if (!this.activeRound) {
         if (this.cfg.getBoolean("enabled", true)) {
            List<String> types = new ArrayList();
            if (this.cfg.getBoolean("games.reaction.enabled", true)) {
               types.add("reaction");
            }

            if (this.cfg.getBoolean("games.math.enabled", true)) {
               types.add("math");
            }

            if (this.cfg.getBoolean("games.unscramble.enabled", true)) {
               types.add("unscramble");
            }

            if (this.cfg.getBoolean("games.trivia.enabled", true)) {
               types.add("trivia");
            }

            if (this.cfg.getBoolean("games.reverse.enabled", true)) {
               types.add("reverse");
            }

            if (this.cfg.getBoolean("games.sequence.enabled", true)) {
               types.add("sequence");
            }

            if (this.cfg.getBoolean("games.color.enabled", true)) {
               types.add("color");
            }

            if (this.cfg.getBoolean("games.typo.enabled", true)) {
               types.add("typo");
            }

            if (!types.isEmpty()) {
               switch ((String)types.get(ThreadLocalRandom.current().nextInt(types.size()))) {
                  case "math" -> this.startMath();
                  case "unscramble" -> this.startUnscramble();
                  case "trivia" -> this.startTrivia();
                  case "reverse" -> this.startReverse();
                  case "sequence" -> this.startSequence();
                  case "color" -> this.startColor();
                  case "typo" -> this.startTypo();
                  default -> this.startReaction();
               }

            }
         }
      }
   }

   private void beginRound(String answer, String visibleMsg, String hover) {
      this.answer = answer == null ? "" : answer.trim();
      this.activeRound = true;
      this.startMs = System.currentTimeMillis();
      if (hover != null && !hover.isEmpty()) {
         this.broadcastHover(visibleMsg, hover);
      } else {
         this.broadcast(visibleMsg);
      }

      int secs = Math.max(5, this.cfg.getInt("answer-time-seconds", 15));
      this.cancelTimeout();
      this.timeoutTask = Tasks.runLater(this.plugin.getCore(), () -> {
         if (this.activeRound) {
            this.activeRound = false;
            this.answer = null;
            this.broadcast(this.msg("nobody"));
         }
      }, (long)secs * 20L);
   }

   private void startReaction() {
      int min = this.cfg.getInt("games.reaction.min-length", 4);
      int max = this.cfg.getInt("games.reaction.max-length", 8);
      if (max < min) {
         max = min;
      }

      int len = ThreadLocalRandom.current().nextInt(min, max + 1);
      String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < len; ++i) {
         sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
      }

      String ans = sb.toString();
      this.beginRound(ans, this.msg("start-reaction"), "&e" + ans);
   }

   private void startMath() {
      String[] ops = new String[]{"+", "-", "*", "/"};
      String op = ops[ThreadLocalRandom.current().nextInt(ops.length)];
      int result;
      String display;
      if (op.equals("+")) {
         int a = this.rand("games.math.sum-min", 1, "games.math.sum-max", 250);
         int b = this.rand("games.math.sum-min", 1, "games.math.sum-max", 250);
         result = a + b;
         display = a + " + " + b;
      } else if (op.equals("-")) {
         int a = this.rand("games.math.sub-min", 1, "games.math.sub-max", 250);
         int b = this.rand("games.math.sub-min", 1, "games.math.sub-max", 250);
         if (b > a) {
            int t = a;
            a = b;
            b = t;
         }

         result = a - b;
         display = a + " - " + b;
      } else if (op.equals("*")) {
         int a = this.rand("games.math.mult-min", 2, "games.math.mult-max", 12);
         int b = this.rand("games.math.mult-min", 2, "games.math.mult-max", 12);
         result = a * b;
         display = a + " * " + b;
      } else {
         int b = this.rand("games.math.div-min", 2, "games.math.div-max", 12);
         result = this.rand("games.math.div-min", 2, "games.math.div-max", 12);
         int a = b * result;
         display = a + " / " + b;
      }

      this.beginRound(String.valueOf(result), this.msg("start-math"), "&e" + display + " &6= &e???");
   }

   private int rand(String minKey, int minDef, String maxKey, int maxDef) {
      int min = this.cfg.getInt(minKey, minDef);
      int max = this.cfg.getInt(maxKey, maxDef);
      if (max < min) {
         max = min;
      }

      return ThreadLocalRandom.current().nextInt(min, max + 1);
   }

   private void startUnscramble() {
      List<String> words = this.cfg.getStringList("games.unscramble.words");
      if (words != null && !words.isEmpty()) {
         String word = ((String)words.get(ThreadLocalRandom.current().nextInt(words.size()))).toLowerCase(Locale.ROOT);
         List<Character> chars = new ArrayList();

         for(char c : word.toCharArray()) {
            chars.add(c);
         }

         Collections.shuffle(chars);
         StringBuilder scr = new StringBuilder();

         for(char c : chars) {
            scr.append(c);
         }

         if (scr.toString().equals(word) && word.length() > 1) {
            Collections.shuffle(chars);
            scr = new StringBuilder();

            for(char c : chars) {
               scr.append(c);
            }
         }

         this.beginRound(word, this.msg("start-unscramble"), "&e" + String.valueOf(scr) + " &7→ &e???");
      } else {
         this.startReaction();
      }
   }

   private void startTrivia() {
      List<?> list = this.cfg.getMapList("games.trivia.questions");
      if (list != null && !list.isEmpty()) {
         Object raw = list.get(ThreadLocalRandom.current().nextInt(list.size()));
         if (raw instanceof Map) {
            Map<?, ?> map = (Map)raw;
            String q = String.valueOf(map.get("question"));
            String a = String.valueOf(map.get("answer"));
            this.beginRound(a, this.msg("start-trivia") + " &f" + q, "&e" + q);
         } else {
            this.startReaction();
         }
      } else {
         this.startReaction();
      }
   }

   private void startReverse() {
      List<String> words = this.cfg.getStringList("games.reverse.words");
      if (words != null && !words.isEmpty()) {
         String word = (String)words.get(ThreadLocalRandom.current().nextInt(words.size()));
         this.beginRound((new StringBuilder(word)).reverse().toString(), this.msg("start-reverse"), "&e" + word);
      } else {
         this.startReaction();
      }
   }

   private void startSequence() {
      int start = ThreadLocalRandom.current().nextInt(1, 20);
      int step = ThreadLocalRandom.current().nextInt(1, 6);
      int missing = ThreadLocalRandom.current().nextInt(2, 5);
      StringBuilder seq = new StringBuilder();
      int ans = 0;

      for(int i = 0; i < 5; ++i) {
         int val = start + i * step;
         if (i == missing) {
            seq.append("?");
            ans = val;
         } else {
            seq.append(val);
         }

         if (i < 4) {
            seq.append(", ");
         }
      }

      this.beginRound(String.valueOf(ans), this.msg("start-sequence"), "&e" + String.valueOf(seq));
   }

   private void startColor() {
      ConfigurationSection sec = this.cfg.getConfigurationSection("games.color.colors");
      if (sec != null && !sec.getKeys(false).isEmpty()) {
         List<String> keys = new ArrayList(sec.getKeys(false));
         String key = (String)keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
         String ans = sec.getString(key, key);
         String var10002 = this.msg("start-color");
         char var10003 = key.charAt(0);
         this.beginRound(ans, var10002, "&eType: &" + var10003 + ans);
      } else {
         this.startReaction();
      }
   }

   private void startTypo() {
      List<?> list = this.cfg.getMapList("games.typo.pairs");
      if (list != null && !list.isEmpty()) {
         Object raw = list.get(ThreadLocalRandom.current().nextInt(list.size()));
         if (raw instanceof Map) {
            Map<?, ?> map = (Map)raw;
            String broken = String.valueOf(map.get("broken"));
            String ans = String.valueOf(map.get("answer"));
            this.beginRound(ans, this.msg("start-typo"), "&c" + broken + " &7→ &e???");
         } else {
            this.startReaction();
         }
      } else {
         this.startReaction();
      }
   }

   public boolean matchesAnswer(String message) {
      if (this.activeRound && this.answer != null) {
         return message != null && message.trim().equalsIgnoreCase(this.answer);
      } else {
         return false;
      }
   }

   public synchronized boolean tryAnswer(Player player, String message) {
      if (this.activeRound && this.answer != null) {
         String msg = message.trim();
         if (!msg.equalsIgnoreCase(this.answer)) {
            return false;
         } else {
            this.activeRound = false;
            this.cancelTimeout();
            double seconds = (double)(System.currentTimeMillis() - this.startMs) / (double)1000.0F;
            String win = this.msg("winner").replace("%player%", player.getName()).replace("%time%", String.format(Locale.US, "%.1f", seconds)).replace("%answer%", this.answer);
            this.answer = null;
            this.broadcast(win);
            this.giveReward(player);
            return true;
         }
      } else {
         return false;
      }
   }

   private void giveReward(Player player) {
      List<?> rewards = this.cfg.getMapList("rewards");
      if (rewards != null && !rewards.isEmpty()) {
         List<Map<?, ?>> pool = new ArrayList();

         for(Object o : rewards) {
            if (o instanceof Map) {
               Map<?, ?> m = (Map)o;
               pool.add(m);
            }
         }

         if (!pool.isEmpty()) {
            int roll = ThreadLocalRandom.current().nextInt(100) + 1;
            Map<?, ?> chosen = null;
            int cursor = 0;

            for(Map<?, ?> m : pool) {
               int chance = 10;
               Object c = m.get("chance");
               if (c instanceof Number) {
                  Number n = (Number)c;
                  chance = n.intValue();
               }

               cursor += chance;
               if (roll <= cursor) {
                  chosen = m;
                  break;
               }
            }

            if (chosen == null) {
               chosen = (Map)pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
            }

            String type = String.valueOf(chosen.get("type")).toLowerCase(Locale.ROOT);
            if (type.equals("money")) {
               double amount = (double)0.0F;
               Object a = chosen.get("amount");
               if (a instanceof Number) {
                  Number n = (Number)a;
                  amount = n.doubleValue();
               }

               if (this.economy != null && amount > (double)0.0F) {
                  this.economy.depositPlayer(player, amount);
               } else if (amount > (double)0.0F) {
                  ConsoleCommandSender var10000 = Bukkit.getConsoleSender();
                  String var10001 = player.getName();
                  Bukkit.dispatchCommand(var10000, "eco give " + var10001 + " " + (int)amount);
               }

            } else {
               Object cmd = chosen.get("command");
               if (cmd != null) {
                  String c = String.valueOf(cmd).replace("%player%", player.getName());
                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
               }

            }
         }
      }
   }

   public FileConfiguration getConfig() {
      return this.cfg;
   }
}
