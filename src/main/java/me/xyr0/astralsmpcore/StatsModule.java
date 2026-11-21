package me.xyr0.astralsmpcore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.xyr0.astralsmpcore.economy.CoreEconomy;
import me.xyr0.astralsmpcore.lb.AstralLeaderboards;
import me.xyr0.astralsmpcore.lb.StatsManager;
import me.xyr0.astralsmpcore.sell.AstralSell;
import me.xyr0.astralsmpcore.shards.AstralShards;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StatsModule implements Module, Listener {
   private final AstralSMPcore plugin;
   private FileConfiguration gui;
   private final Map<UUID, Long> joinTime = new ConcurrentHashMap();
   private final Map<UUID, Long> playtimeBonus = new ConcurrentHashMap();
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().hexColors().character('§').build();

   public StatsModule(AstralSMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "stats";
   }

   public void onEnable() {
      this.reload();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.plugin.commands().registerHandler("stats", this::stats);

      for(Player p : Bukkit.getOnlinePlayers()) {
         this.joinTime.put(p.getUniqueId(), System.currentTimeMillis());
      }

   }

   public void onDisable() {
   }

   public void reload() {
      File f = new File(this.plugin.getDataFolder(), "stats/gui.yml");
      if (!f.exists()) {
         this.plugin.saveResource("stats/gui.yml", false);
      }

      this.gui = YamlConfiguration.loadConfiguration(f);
   }

   private boolean stats(CommandSender sender, String[] args) {
      if (sender instanceof Player viewer) {
         Player target = viewer;
         if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
               viewer.sendMessage("§cPlayer not found.");
               return true;
            }
         }

         this.openGui(viewer, target);
         return true;
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }

   public void openGui(Player viewer, Player target) {
      int rows = this.gui.getInt("gui.rows", 4);
      String titleRaw = target.equals(viewer) ? this.gui.getString("gui.title", "&8ᴍʏ sᴛᴀᴛs") : this.gui.getString("gui.title-other", "%player_name% &8sᴛᴀᴛs").replace("%player_name%", target.getName());
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, this.toComponent(titleRaw));
      ConfigurationSection items = this.gui.getConfigurationSection("items");
      if (items != null) {
         for(String key : items.getKeys(false)) {
            ConfigurationSection sec = items.getConfigurationSection(key);
            if (sec != null) {
               int slot = sec.getInt("slot", 0);

               Material mat;
               try {
                  mat = Material.valueOf(sec.getString("material", "STONE").toUpperCase());
               } catch (Exception var17) {
                  mat = Material.STONE;
               }

               ItemStack stack = new ItemStack(mat);
               ItemMeta meta = stack.getItemMeta();
               if (meta != null) {
                  meta.displayName(this.toComponent(sec.getString("name", key)));
                  List<Component> lore = new ArrayList();

                  for(String line : sec.getStringList("lore")) {
                     lore.add(this.toComponent(this.applyStats(line, target)));
                  }

                  meta.lore(lore);
                  stack.setItemMeta(meta);
               }

               if (slot >= 0 && slot < inv.getSize()) {
                  inv.setItem(slot, stack);
               }
            }
         }
      }

      viewer.openInventory(inv);
   }

   private String applyStats(String line, Player t) {
      return line.replace("%astralsmpcore_stats_kills%", String.valueOf(this.safeStat(t, Statistic.PLAYER_KILLS))).replace("%astralsmpcore_stats_deaths%", String.valueOf(this.safeStat(t, Statistic.DEATHS))).replace("%astralsmpcore_stats_blocks_placed%", String.valueOf(this.blocksPlaced(t))).replace("%astralsmpcore_stats_blocks_broken%", String.valueOf(this.blocksBroken(t))).replace("%astralsmpcore_stats_mobs_killed%", String.valueOf(this.safeStat(t, Statistic.MOB_KILLS))).replace("%astralsmpcore_stats_playtime%", this.formatPlaytime(t)).replace("%astralsmpcore_pay_balance%", this.balance(t)).replace("%astralsmpcore_shards_balance%", this.shardsBal(t)).replace("%astralsmpcore_shop_spent%", this.shopSpent(t)).replace("%astralsmpcore_sell_earned%", this.sellEarned(t));
   }

   private long blocksPlaced(Player t) {
      try {
         AstralLeaderboards lb = AstralLeaderboards.get();
         if (lb != null && lb.getStats() != null) {
            return lb.getStats().getBlocksPlaced(t.getUniqueId());
         }
      } catch (Exception var3) {
      }

      return 0L;
   }

   private long blocksBroken(Player t) {
      try {
         AstralLeaderboards lb = AstralLeaderboards.get();
         if (lb != null && lb.getStats() != null) {
            return lb.getStats().getBlocksBroken(t.getUniqueId());
         }
      } catch (Exception var3) {
      }

      return 0L;
   }

   private String shopSpent(Player t) {
      try {
         AstralLeaderboards lb = AstralLeaderboards.get();
         if (lb != null && lb.getStats() != null) {
            double v = lb.getStats().getValue(StatsManager.Board.SHOP_SPENT, t.getUniqueId());
            if (v > (double)0.0F) {
               return CoreEconomy.formatMoney(v);
            }
         }
      } catch (Exception var5) {
      }

      return "0";
   }

   private String sellEarned(Player t) {
      try {
         AstralSell sell = AstralSell.get();
         if (sell != null && sell.getProgress() != null) {
            double v = sell.getProgress().getMoneyMade(t.getUniqueId());
            if (v > (double)0.0F) {
               return CoreEconomy.formatMoney(v);
            }
         }
      } catch (Exception var6) {
      }

      try {
         AstralLeaderboards lb = AstralLeaderboards.get();
         if (lb != null && lb.getStats() != null) {
            double v = lb.getStats().getValue(StatsManager.Board.SELL_EARNED, t.getUniqueId());
            if (v > (double)0.0F) {
               return CoreEconomy.formatMoney(v);
            }
         }
      } catch (Exception var5) {
      }

      return "0";
   }

   private String shardsBal(Player t) {
      try {
         AstralShards s = AstralShards.get();
         if (s != null && s.getShards() != null) {
            return CoreEconomy.formatMoney((double)s.getShards().getBalance(t));
         }
      } catch (Exception var3) {
      }

      return "0";
   }

   private int safeStat(Player t, Statistic s) {
      try {
         return t.getStatistic(s);
      } catch (Exception var4) {
         return 0;
      }
   }

   private String formatPlaytime(Player t) {
      int ticks = t.getStatistic(Statistic.PLAY_ONE_MINUTE);
      long minutes = (long)(ticks / 20 / 60);
      long hours = minutes / 60L;
      minutes %= 60L;
      return hours + "h " + minutes + "m";
   }

   private String balance(Player t) {
      try {
         return CoreEconomy.formatMoney(CoreEconomy.balanceOf(t));
      } catch (Exception var3) {
         return "0";
      }
   }

   @EventHandler
   public void onClick(InventoryClickEvent e) {
      String title = e.getView().getTitle();
      if (title != null && (title.contains("sᴛᴀᴛs") || title.contains("stats") || title.contains("Stats"))) {
         e.setCancelled(true);
      }

   }

   private Component toComponent(String s) {
      return LEGACY.deserialize(this.color(s)).decoration(TextDecoration.ITALIC, false);
   }

   private String color(String s) {
      if (s == null) {
         return "";
      } else {
         s = s.replaceAll("<#([A-Fa-f0-9]{6})>", "&#$1");
         s = s.replaceAll("</#[A-Fa-f0-9]{6}>", "");
         Matcher m = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(s);
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
         s = sb.toString();
         m = Pattern.compile("(?<!&)#([A-Fa-f0-9]{6})").matcher(s);
         sb = new StringBuffer();

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
