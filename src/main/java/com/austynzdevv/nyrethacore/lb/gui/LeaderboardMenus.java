package com.austynzdevv.nyrethacore.lb.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.austynzdevv.nyrethacore.lb.EconomyLeaderboards;
import com.austynzdevv.nyrethacore.lb.StatsManager;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class LeaderboardMenus {
   private static final Map<String, FileConfiguration> GUI = new HashMap();

   private LeaderboardMenus() {
   }

   public static void loadGuis(EconomyLeaderboards plugin) {
      GUI.clear();
      File dir = new File(plugin.getDataFolder(), "gui");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      String[] names = new String[]{"main.yml", "money.yml", "deaths.yml", "playtime.yml", "blocks-placed.yml", "blocks-broken.yml", "mobs-killed.yml", "player-kills.yml", "shards.yml", "shop_spent.yml", "sell_earned.yml"};

      for(String name : names) {
         File out = new File(dir, name);
         if (!out.exists()) {
            plugin.saveResource("gui/" + name, false);
         }

         GUI.put(name.replace(".yml", ""), YamlConfiguration.loadConfiguration(out));
      }

   }

   public static void openMain(Player player) {
      FileConfiguration gui = (FileConfiguration)GUI.get("main");
      if (gui != null) {
         String title = gui.getString("title", "&8ʟᴇᴀᴅᴇʀʙᴏᴀʀᴅꜱ");
         int rows = Math.max(1, Math.min(6, gui.getInt("rows", 3)));
         Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtil.parse(title));
         ConfigurationSection items = gui.getConfigurationSection("items");
         if (items != null) {
            for(String key : items.getKeys(false)) {
               ConfigurationSection sec = items.getConfigurationSection(key);
               if (sec != null && sec.getBoolean("enabled", true)) {
                  int slot = sec.getInt("slot", -1);
                  if (slot >= 0 && slot < inv.getSize()) {
                     inv.setItem(slot, buildItem(sec, (String)null, (String)null, (String)null, (String)null));
                  }
               }
            }
         }

         MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.MAIN);
         MenuSession.BOARD.remove(player.getUniqueId());
         MenuSession.PAGE.remove(player.getUniqueId());
         player.openInventory(inv);
      }
   }

   public static void openBoard(Player player, StatsManager.Board board, int page) {
      String id = StatsManager.toId(board);
      FileConfiguration gui = (FileConfiguration)GUI.get(id);
      if (gui == null) {
         File dir = new File(EconomyLeaderboards.get().getDataFolder(), "gui");
         File f = new File(dir, id + ".yml");
         if (f.exists()) {
            gui = YamlConfiguration.loadConfiguration(f);
            GUI.put(id, gui);
         } else {
            gui = new YamlConfiguration();
            gui.set("title", "&8" + id);
            gui.set("rows", 6);
            GUI.put(id, gui);
         }
      }

      List<StatsManager.Entry> list = EconomyLeaderboards.get().getStats().getBoard(board);
      int pageSize = 45;
      int totalPages = Math.max(1, (int)Math.ceil((double)list.size() / (double)pageSize));
      if (page < 0) {
         page = 0;
      }

      if (page >= totalPages) {
         page = totalPages - 1;
      }

      String title = gui.getString("title", "&8Leaderboard").replace("%page%", String.valueOf(page + 1)).replace("%total%", String.valueOf(totalPages));
      int rows = Math.max(1, Math.min(6, gui.getInt("row", gui.getInt("rows", 6))));
      Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = gui.getConfigurationSection("items");
      ConfigurationSection playerSec = items != null ? items.getConfigurationSection("player") : null;
      int start = page * pageSize;
      int end = Math.min(start + pageSize, list.size());

      for(int i = start; i < end; ++i) {
         StatsManager.Entry entry = (StatsManager.Entry)list.get(i);
         int slot = i - start;
         inv.setItem(slot, buildPlayerHead(playerSec, entry, i + 1, board));
      }

      if (items != null) {
         placeControl(inv, items.getConfigurationSection("back"));
         placeControl(inv, items.getConfigurationSection("next"));
         placeControl(inv, items.getConfigurationSection("refresh"));
         placeControl(inv, items.getConfigurationSection("search"));
         ConfigurationSection self = items.getConfigurationSection("self");
         if (self != null) {
            int slot = self.getInt("slot", 48);
            if (slot >= 0 && slot < inv.getSize()) {
               UUIDSelf(player, board, self, inv, slot);
            }
         }
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.BOARD);
      MenuSession.BOARD.put(player.getUniqueId(), board);
      MenuSession.PAGE.put(player.getUniqueId(), page);
      player.openInventory(inv);
   }

   private static void UUIDSelf(Player player, StatsManager.Board board, ConfigurationSection self, Inventory inv, int slot) {
      StatsManager stats = EconomyLeaderboards.get().getStats();
      int rank = stats.getRank(board, player.getUniqueId());
      double value = stats.getValue(board, player.getUniqueId());
      StatsManager.Entry entry = new StatsManager.Entry(player.getUniqueId(), player.getName(), value);
      inv.setItem(slot, buildPlayerHead(self, entry, rank > 0 ? rank : listRankFallback(rank), board));
   }

   private static int listRankFallback(int rank) {
      return rank > 0 ? rank : 0;
   }

   private static void placeControl(Inventory inv, ConfigurationSection sec) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            inv.setItem(slot, buildItem(sec, (String)null, (String)null, (String)null, (String)null));
         }
      }
   }

   private static ItemStack buildPlayerHead(ConfigurationSection sec, StatsManager.Entry entry, int rank, StatsManager.Board board) {
      ItemStack item = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta)item.getItemMeta();
      if (meta != null) {
         try {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.uuid()));
         } catch (Exception var11) {
         }

         String name = sec != null ? sec.getString("display-name", "&#04fc84%name%") : "&#04fc84%name%";
         name = name.replace("%name%", entry.name()).replace("%player%", entry.name()).replace("%rank%", rank > 0 ? "#" + rank : "-").replace("%value%", StatsManager.formatValue(board, entry.value()));
         meta.displayName(ColorUtil.parse(name));
         List<String> loreCfg = sec != null ? sec.getStringList("lore") : List.of("&f%value% &#04fc84(%rank%)");
         List<Component> lore = new ArrayList();

         for(String line : loreCfg) {
            lore.add(ColorUtil.parse(line.replace("%name%", entry.name()).replace("%player%", entry.name()).replace("%rank%", rank > 0 ? "#" + rank : "-").replace("%value%", StatsManager.formatValue(board, entry.value()))));
         }

         meta.lore(lore);
         item.setItemMeta(meta);
      }

      return item;
   }

   private static ItemStack buildItem(ConfigurationSection sec, String nameR, String valueR, String rankR, String playerR) {
      Material mat = Material.STONE;

      try {
         mat = Material.valueOf(sec.getString("material", "STONE").toUpperCase());
      } catch (Exception var14) {
      }

      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         String name = sec.getString("display-name", mat.name());
         if (nameR != null) {
            name = name.replace("%name%", nameR);
         }

         if (valueR != null) {
            name = name.replace("%value%", valueR);
         }

         if (rankR != null) {
            name = name.replace("%rank%", rankR);
         }

         if (playerR != null) {
            name = name.replace("%player%", playerR);
         }

         meta.displayName(ColorUtil.parse(name));
         List<String> loreCfg = sec.getStringList("lore");
         if (loreCfg != null && !loreCfg.isEmpty()) {
            List<Component> lore = new ArrayList();

            for(String line : loreCfg) {
               String l = line;
               if (nameR != null) {
                  l = line.replace("%name%", nameR);
               }

               if (valueR != null) {
                  l = l.replace("%value%", valueR);
               }

               if (rankR != null) {
                  l = l.replace("%rank%", rankR);
               }

               if (playerR != null) {
                  l = l.replace("%player%", playerR);
               }

               lore.add(ColorUtil.parse(l));
            }

            meta.lore(lore);
         }

         item.setItemMeta(meta);
      }

      return item;
   }
}
