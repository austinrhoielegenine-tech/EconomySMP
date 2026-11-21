package com.austynzdevv.economysmp;

import java.util.List;
import java.util.Locale;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.austynzdevv.economysmp.crates.EconomyCrates;
import com.austynzdevv.economysmp.economy.CoreEconomy;
import com.austynzdevv.economysmp.keyall.EconomyKeyall;
import com.austynzdevv.economysmp.lb.EconomyLeaderboards;
import com.austynzdevv.economysmp.lb.StatsManager;
import com.austynzdevv.economysmp.sell.EconomySell;
import com.austynzdevv.economysmp.shards.EconomyShards;
import com.austynzdevv.economysmp.shop.EconomyShop;
import com.austynzdevv.economysmp.teams.EconomyTeams;
import com.austynzdevv.economysmp.teams.Team;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyPlaceholders extends PlaceholderExpansion {
   private final EconomySMP plugin;

   public EconomyPlaceholders(EconomySMP plugin) {
      this.plugin = plugin;
   }

   public String getIdentifier() {
      return "economysmp";
   }

   public String getAuthor() {
      return "Xyr0";
   }

   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }

   public boolean persist() {
      return true;
   }

   public String onPlaceholderRequest(Player player, String params) {
      if (params == null) {
         return "";
      } else {
         String p = params.toLowerCase(Locale.ROOT).replace("-", "_");
         if (p.startsWith("lb_")) {
            String lb = this.resolveLb(p, player);
            if (lb != null) {
               return lb;
            }
         }

         if (p.startsWith("crate_keys_")) {
            try {
               EconomyCrates crates = EconomyCrates.get();
               if (crates != null) {
                  return String.valueOf(crates.getKeys().totalKeys(player, p.substring(11)));
               }
            } catch (Exception var6) {
            }

            return "0";
         } else if (p.equals("teams_name")) {
            try {
               EconomyTeams teams = EconomyTeams.get();
               if (teams != null) {
                  Team team = teams.getTeams().getByPlayer(player.getUniqueId());
                  return team == null ? "" : team.getName();
               }
            } catch (Exception var7) {
            }

            return "";
         } else if (p.equals("keyall_time")) {
            try {
               EconomyKeyall keyall = EconomyKeyall.get();
               if (keyall != null) {
                  return keyall.getTimer().format(player);
               }
            } catch (Exception var8) {
            }

            return "";
         } else if (p.equals("keyall_seconds")) {
            try {
               EconomyKeyall keyall = EconomyKeyall.get();
               if (keyall != null) {
                  return String.valueOf(keyall.getTimer().getRemaining(player));
               }
            } catch (Exception var9) {
            }

            return "0";
         } else if (p.equals("ping")) {
            return String.valueOf(player.getPing());
         } else if (p.equals("sell_earned")) {
            try {
               EconomySell sell = EconomySell.get();
               if (sell != null) {
                  return String.valueOf(sell.getProgress().getMoneyMade(player.getUniqueId()));
               }
            } catch (Exception var10) {
            }

            return "0";
         } else if (p.equals("shop_spent")) {
            try {
               EconomyShop shop = EconomyShop.get();
               if (shop != null) {
                  return shop.getSpend().formatVaultSpent(player.getUniqueId());
               }
            } catch (Exception var11) {
            }

            return "0";
         } else if (player == null) {
            return "";
         } else if (!p.equals("pay_balance") && !p.equals("balance") && !p.equals("money")) {
            if (!p.equals("pay_balance_raw") && !p.equals("balance_raw")) {
               if (p.startsWith("stats_")) {
                  String var10000;
                  switch (p) {
                     case "stats_kills" -> var10000 = String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS));
                     case "stats_deaths" -> var10000 = String.valueOf(player.getStatistic(Statistic.DEATHS));
                     case "stats_mobs_killed" -> var10000 = String.valueOf(player.getStatistic(Statistic.MOB_KILLS));
                     case "stats_playtime" -> var10000 = this.playtime(player);
                     case "stats_blocks_broken" -> var10000 = String.valueOf(this.safeStat(player, Statistic.MINE_BLOCK));
                     case "stats_blocks_placed" -> var10000 = String.valueOf(this.safeStat(player, Statistic.USE_ITEM));
                     default -> var10000 = "";
                  }

                  return var10000;
               } else if (!p.equals("shards_balance") && !p.equals("shards") && !p.equals("shards_short") && !p.equals("shards_bal")) {
                  return !p.equals("shards_balance_raw") && !p.equals("shards_raw") ? "" : String.valueOf(this.shardsBal(player));
               } else {
                  return this.formatCompact(this.shardsBal(player));
               }
            } else {
               return String.valueOf((long)this.balRaw(player));
            }
         } else {
            return this.formatMoney(this.balRaw(player));
         }
      }
   }

   private String resolveLb(String p, Player player) {
      String rest = p.substring(3);
      int topNameIdx = rest.indexOf("_top_name_");
      int topValueIdx = rest.indexOf("_top_value_");
      if (topNameIdx <= 0 && topValueIdx <= 0) {
         if (!rest.endsWith("_rank") && !rest.endsWith("_value")) {
            return null;
         } else {
            boolean isRank = rest.endsWith("_rank");
            String boardKey = rest.substring(0, rest.length() - (isRank ? 5 : 6));
            StatsManager.Board board = this.parseBoard(boardKey);
            if (board == null) {
               return "";
            } else {
               StatsManager stats = this.stats();
               if (stats != null && player != null) {
                  if (isRank) {
                     int r = stats.getRank(board, player.getUniqueId());
                     return r > 0 ? String.valueOf(r) : "-";
                  } else {
                     return this.formatBoardValue(board, stats.getValue(board, player.getUniqueId()));
                  }
               } else {
                  return isRank ? "-" : "0";
               }
            }
         }
      } else {
         boolean isName = topNameIdx > 0;
         int idx = isName ? topNameIdx : topValueIdx;
         String boardKey = rest.substring(0, idx);
         String numStr = rest.substring(idx + (isName ? "_top_name_".length() : "_top_value_".length()));

         int rank;
         try {
            rank = Integer.parseInt(numStr);
         } catch (Exception var15) {
            return "";
         }

         StatsManager.Board board = this.parseBoard(boardKey);
         if (board == null) {
            return "";
         } else {
            StatsManager stats = this.stats();
            if (stats == null) {
               return "";
            } else {
               List<StatsManager.Entry> list = stats.getBoard(board);
               if (rank >= 1 && rank <= list.size()) {
                  StatsManager.Entry e = (StatsManager.Entry)list.get(rank - 1);
                  return isName ? (e.name() == null ? "---" : e.name()) : this.formatBoardValue(board, e.value());
               } else {
                  return isName ? "---" : "0";
               }
            }
         }
      }
   }

   private StatsManager.Board parseBoard(String key) {
      if (key == null) {
         return null;
      } else {
         StatsManager.Board var10000;
         switch (key.toLowerCase(Locale.ROOT)) {
            case "money":
            case "bal":
            case "balance":
               var10000 = StatsManager.Board.MONEY;
               break;
            case "shards":
            case "shard":
               var10000 = StatsManager.Board.SHARDS;
               break;
            case "deaths":
            case "death":
               var10000 = StatsManager.Board.DEATHS;
               break;
            case "playtime":
            case "time":
               var10000 = StatsManager.Board.PLAYTIME;
               break;
            case "blocks_placed":
            case "blocksplaced":
            case "placed":
               var10000 = StatsManager.Board.BLOCKS_PLACED;
               break;
            case "blocks_broken":
            case "blocksbroken":
            case "broken":
               var10000 = StatsManager.Board.BLOCKS_BROKEN;
               break;
            case "mobs_killed":
            case "mobkills":
            case "mobs":
               var10000 = StatsManager.Board.MOBS_KILLED;
               break;
            case "player_kills":
            case "playerkills":
            case "kills":
               var10000 = StatsManager.Board.PLAYER_KILLS;
               break;
            case "shop_spent":
            case "topspent":
            case "spent":
               var10000 = StatsManager.Board.SHOP_SPENT;
               break;
            case "sell_earned":
            case "topsell":
            case "sell":
               var10000 = StatsManager.Board.SELL_EARNED;
               break;
            default:
               var10000 = null;
         }

         return var10000;
      }
   }

   private StatsManager stats() {
      try {
         EconomyLeaderboards lb = EconomyLeaderboards.get();
         if (lb != null) {
            return lb.getStats();
         }
      } catch (Exception var2) {
      }

      return null;
   }

   private String formatBoardValue(StatsManager.Board board, double value) {
      if (board == StatsManager.Board.PLAYTIME) {
         long sec = (long)value;
         long h = sec / 3600L;
         long m = sec % 3600L / 60L;
         return h + "h " + m + "m";
      } else {
         return board != StatsManager.Board.MONEY && board != StatsManager.Board.SHOP_SPENT && board != StatsManager.Board.SELL_EARNED ? this.formatCompact((long)value) : this.formatMoney(value);
      }
   }

   private double balRaw(Player player) {
      try {
         RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
         if (rsp != null) {
            return ((Economy)rsp.getProvider()).getBalance(player);
         }
      } catch (Exception var4) {
      }

      try {
         return CoreEconomy.balanceOf(player);
      } catch (Exception var3) {
         return (double)0.0F;
      }
   }

   private long shardsBal(Player player) {
      try {
         Module var2 = this.plugin.modules().get("shards");
      } catch (Exception var4) {
      }

      try {
         EconomyShards h = EconomyShards.get();
         if (h != null && h.getShards() != null) {
            return h.getShards().getBalance(player.getUniqueId());
         }
      } catch (Exception var3) {
      }

      return 0L;
   }

   private int safeStat(Player player, Statistic stat) {
      try {
         return player.getStatistic(stat);
      } catch (Exception var4) {
         return 0;
      }
   }

   private String playtime(Player player) {
      try {
         int ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
         long sec = (long)ticks / 20L;
         return sec / 3600L + "h " + sec % 3600L / 60L + "m";
      } catch (Exception var5) {
         return "0h 0m";
      }
   }

   private String formatCompact(long amount) {
      if (amount < 1000L) {
         return String.valueOf(amount);
      } else if (amount < 1000000L) {
         return String.format("%.1fk", (double)amount / (double)1000.0F).replace(".0k", "k");
      } else {
         return amount < 1000000000L ? String.format("%.1fm", (double)amount / (double)1000000.0F).replace(".0m", "m") : String.format("%.1fb", (double)amount / (double)1.0E9F).replace(".0b", "b");
      }
   }

   private String formatMoney(double amount) {
      if (amount < (double)1000.0F) {
         return String.format("%.0f", amount);
      } else if (amount < (double)1000000.0F) {
         return String.format("%.1fk", amount / (double)1000.0F).replace(".0k", "k");
      } else {
         return amount < (double)1.0E9F ? String.format("%.1fm", amount / (double)1000000.0F).replace(".0m", "m") : String.format("%.1fb", amount / (double)1.0E9F).replace(".0b", "b");
      }
   }
}
