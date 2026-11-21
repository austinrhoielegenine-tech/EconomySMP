package com.austynzdevv.economysmp.baltop.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.austynzdevv.economysmp.baltop.EconomyBaltop;
import com.austynzdevv.economysmp.baltop.BalanceCache;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.EconomyUtil;
import com.austynzdevv.economysmp.util.SoundUtil;
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

public final class BaltopMenus {
   private BaltopMenus() {
   }

   private static FileConfiguration gui() {
      File file = new File(EconomyBaltop.get().getDataFolder(), "gui/main-gui.yml");
      if (!file.exists()) {
         EconomyBaltop.get().saveResource("gui/main-gui.yml", false);
      }

      return YamlConfiguration.loadConfiguration(file);
   }

   public static void open(Player player, int page) {
      FileConfiguration cfg = gui();
      ConfigurationSection menu = cfg.getConfigurationSection("menu");
      if (menu != null) {
         String search = (String)MenuSession.SEARCH.getOrDefault(player.getUniqueId(), "");
         List<BalanceCache.Entry> list = search.isBlank() ? EconomyBaltop.get().getCache().getRanked() : EconomyBaltop.get().getCache().search(search);
         int perPage = EconomyBaltop.get().getConfig().getInt("players-per-page", 45);
         int maxPagesCfg = Math.max(1, EconomyBaltop.get().getConfig().getInt("max-pages", 10));
         int totalPages = Math.max(1, Math.min(maxPagesCfg, list.isEmpty() ? 1 : (list.size() - 1) / perPage + 1));
         if (page < 0) {
            page = 0;
         }

         if (page >= totalPages) {
            page = totalPages - 1;
         }

         MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.MAIN);
         MenuSession.PAGE.put(player.getUniqueId(), page);
         int rows = Math.max(1, Math.min(6, menu.getInt("row", 6)));
         String title = menu.getString("title", "&8ʙᴀʟᴛᴏᴘ (Page %page%/%total%)").replace("%page%", String.valueOf(page + 1)).replace("%total%", String.valueOf(totalPages));
         Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtil.parse(title));
         ConfigurationSection items = menu.getConfigurationSection("items");
         if (items == null) {
            player.openInventory(inv);
         } else {
            ConfigurationSection playerItem = items.getConfigurationSection("player");
            int from = page * perPage;
            int to = Math.min(list.size(), from + perPage);

            for(int i = from; i < to; ++i) {
               int slot = i - from;
               if (slot > 44) {
                  break;
               }

               BalanceCache.Entry entry = (BalanceCache.Entry)list.get(i);
               inv.setItem(slot, headItem(playerItem, entry, i + 1));
            }

            placeButton(inv, items.getConfigurationSection("back"));
            placeButton(inv, items.getConfigurationSection("refresh"));
            placeButton(inv, items.getConfigurationSection("search"));
            placeButton(inv, items.getConfigurationSection("next"));
            ConfigurationSection self = items.getConfigurationSection("self");
            if (self != null) {
               double bal = EconomyBaltop.get().getCache().getBalance(player.getUniqueId());
               if (bal <= (double)0.0F) {
                  bal = EconomyBaltop.get().getEconomy().getBalance(player);
               }

               int rank = EconomyBaltop.get().getCache().getRank(player.getUniqueId());
               String rankStr = rank > 0 ? "#" + rank : "-";
               BalanceCache.Entry selfEntry = new BalanceCache.Entry(player.getUniqueId(), player.getName(), bal);
               ItemStack selfItem = headItem(self, selfEntry, rank > 0 ? rank : 0);
               inv.setItem(self.getInt("slot", 48), selfItem);
               ItemMeta meta = selfItem.getItemMeta();
               if (meta != null) {
                  String name = self.getString("display_name", "&#04fc84%name%").replace("%name%", player.getName()).replace("%player%", player.getName());
                  meta.displayName(ColorUtil.parse(name));
                  List<String> loreCfg = self.getStringList("lore");
                  List<Component> lore = new ArrayList();

                  for(String line : loreCfg) {
                     lore.add(ColorUtil.parse(line.replace("%value%", EconomyUtil.formatBaltop(bal)).replace("%rank%", rankStr).replace("%name%", player.getName())));
                  }

                  meta.lore(lore);
                  selfItem.setItemMeta(meta);
                  inv.setItem(self.getInt("slot", 48), selfItem);
               }
            }

            MenuSession.SWITCHING.add(player.getUniqueId());
            player.openInventory(inv);
            MenuSession.SWITCHING.remove(player.getUniqueId());
            SoundUtil.playConfigured(player, EconomyBaltop.get().getCore(), "sounds.open");
         }
      }
   }

   private static void placeButton(Inventory inv, ConfigurationSection section) {
      if (section != null) {
         int slot = section.getInt("slot", 0);
         Material mat = Material.ARROW;

         try {
            mat = Material.valueOf(section.getString("material", "ARROW"));
         } catch (Exception var9) {
         }

         ItemStack item = new ItemStack(mat);
         ItemMeta meta = item.getItemMeta();
         if (meta != null) {
            meta.displayName(ColorUtil.parse(section.getString("display_name", "")));
            List<Component> lore = new ArrayList();

            for(String line : section.getStringList("lore")) {
               lore.add(ColorUtil.parse(line));
            }

            meta.lore(lore);
            item.setItemMeta(meta);
         }

         inv.setItem(slot, item);
      }
   }

   private static ItemStack headItem(ConfigurationSection section, BalanceCache.Entry entry, int rank) {
      Material mat = Material.PLAYER_HEAD;
      if (section != null) {
         try {
            mat = Material.valueOf(section.getString("material", "PLAYER_HEAD"));
         } catch (Exception var13) {
         }
      }

      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta instanceof SkullMeta skull) {
         try {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(entry.uuid));
         } catch (Exception var12) {
         }

         meta = skull;
      }

      if (meta != null) {
         String name = section != null ? section.getString("display_name", "&#04fc84%name%") : "&#04fc84%name%";
         name = name.replace("%name%", entry.name).replace("%player%", entry.name);
         meta.displayName(ColorUtil.parse(name));
         List<String> loreCfg = section != null ? section.getStringList("lore") : List.of("&fMoney: &7$%value%");
         List<Component> lore = new ArrayList();
         String rankStr = rank > 0 ? "#" + rank : "-";

         for(String line : loreCfg) {
            lore.add(ColorUtil.parse(line.replace("%value%", EconomyUtil.formatBaltop(entry.balance)).replace("%rank%", rankStr).replace("%name%", entry.name).replace("%player%", entry.name)));
         }

         meta.lore(lore);
         item.setItemMeta(meta);
      }

      return item;
   }
}
