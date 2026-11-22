package com.austynzdevv.nyrethacore.bounties.gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.austynzdevv.nyrethacore.bounties.EconomyBounties;
import com.austynzdevv.nyrethacore.bounties.BountyManager;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class BountyMenus {
   private static final Map<String, FileConfiguration> GUI = new HashMap();

   private BountyMenus() {
   }

   public static void load(EconomyBounties plugin) {
      GUI.clear();
      File dir = new File(plugin.getDataFolder(), "gui");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      for(String name : List.of("main.yml", "confirm.yml", "admin.yml")) {
         File out = new File(dir, name);
         if (!out.exists()) {
            try {
               plugin.saveResource("gui/" + name, false);
            } catch (Exception var7) {
            }

            if (!out.exists()) {
               try {
                  plugin.getCore().saveResource("bounties/gui/" + name, false);
                  Path src = Path.of(plugin.getCore().getDataFolder().getPath(), "bounties", "gui", name);
                  if (src.toFile().exists()) {
                     Files.copy(src, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
                  }
               } catch (Exception var6) {
               }
            }
         }

         if (out.exists()) {
            GUI.put(name.replace(".yml", ""), YamlConfiguration.loadConfiguration(out));
         }
      }

   }

   public static void openMain(Player player, int page, BountyManager.Sort sort) {
      FileConfiguration gui = (FileConfiguration)GUI.get("main");
      if (gui != null) {
         List<BountyManager.Bounty> list = EconomyBounties.get().getBounties().list(sort);
         int pageSize = 45;
         int totalPages = Math.max(1, (int)Math.ceil((double)list.size() / (double)pageSize));
         if (page < 0) {
            page = 0;
         }

         if (page >= totalPages) {
            page = totalPages - 1;
         }

         String title = gui.getString("title", "&8ʙᴏᴜɴᴛʏ (Page %page%/%max%)").replace("%page%", String.valueOf(page + 1)).replace("%max%", String.valueOf(totalPages));
         int rows = Math.max(1, Math.min(6, gui.getInt("rows", 6)));
         Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtil.parse(title));
         ConfigurationSection items = gui.getConfigurationSection("items");
         ConfigurationSection headSec = items != null ? items.getConfigurationSection("bounty-head") : null;
         int start = page * pageSize;
         int end = Math.min(start + pageSize, list.size());

         for(int i = start; i < end; ++i) {
            BountyManager.Bounty b = (BountyManager.Bounty)list.get(i);
            inv.setItem(i - start, headItem(headSec, b));
         }

         if (items != null) {
            place(inv, items.getConfigurationSection("previous-page"));
            place(inv, items.getConfigurationSection("next-page"));
            place(inv, items.getConfigurationSection("refresh"));
            place(inv, items.getConfigurationSection("search"));
            placeSort(inv, items.getConfigurationSection("sort"), sort);
         }

         MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.MAIN);
         MenuSession.PAGE.put(player.getUniqueId(), page);
         MenuSession.SORT.put(player.getUniqueId(), sort);
         player.openInventory(inv);
      }
   }

   public static void openConfirm(Player player, OfflinePlayer target, double amount) {
      FileConfiguration gui = (FileConfiguration)GUI.get("confirm");
      if (gui != null) {
         String title = gui.getString("title", gui.getString("titel", "&8ᴄᴏɴꜰɪʀᴍ ʙᴏᴜɴᴛʏ"));
         int rows = Math.max(1, Math.min(6, gui.getInt("rows", 3)));
         Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtil.parse(title));
         ConfigurationSection items = gui.getConfigurationSection("items");
         String amountFmt = BountyManager.formatMoney(amount);
         String targetName = target.getName() != null ? target.getName() : "Unknown";
         if (items != null) {
            placeReplaced(inv, items.getConfigurationSection("confirm"), amountFmt, targetName);
            placeReplaced(inv, items.getConfigurationSection("cancel"), amountFmt, targetName);
            ConfigurationSection playerSec = items.getConfigurationSection("player");
            if (playerSec != null) {
               int slot = playerSec.getInt("slot", 13);
               ItemStack head = new ItemStack(Material.PLAYER_HEAD);
               SkullMeta meta = (SkullMeta)head.getItemMeta();
               if (meta != null) {
                  meta.setOwningPlayer(target);
                  String name = playerSec.getString("name", "&#00fc88%target_name%").replace("%target_name%", targetName).replace("%amount_format%", amountFmt);
                  meta.displayName(ColorUtil.parse(name));
                  List<Component> lore = new ArrayList();

                  for(String line : playerSec.getStringList("lore")) {
                     lore.add(ColorUtil.parse(line.replace("%target_name%", targetName).replace("%amount_format%", amountFmt)));
                  }

                  meta.lore(lore);
                  head.setItemMeta(meta);
               }

               if (slot >= 0 && slot < inv.getSize()) {
                  inv.setItem(slot, head);
               }
            }
         }

         MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.CONFIRM);
         MenuSession.TARGET.put(player.getUniqueId(), target.getUniqueId());
         MenuSession.AMOUNT.put(player.getUniqueId(), amount);
         player.openInventory(inv);
      }
   }

   public static void openAdmin(Player player, OfflinePlayer target) {
      FileConfiguration gui = (FileConfiguration)GUI.get("admin");
      if (gui != null) {
         double amount = EconomyBounties.get().getBounties().getAmount(target.getUniqueId());
         String targetName = target.getName() != null ? target.getName() : "Unknown";
         String amountFmt = BountyManager.formatMoney(amount);
         String title = gui.getString("title", "&8ᴀᴅᴍɪɴ ʙᴏᴜɴᴛʏ").replace("%target_name%", targetName);
         int rows = Math.max(1, Math.min(6, gui.getInt("rows", 3)));
         Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtil.parse(title));
         ConfigurationSection items = gui.getConfigurationSection("items");
         if (items != null) {
            placeReplaced(inv, items.getConfigurationSection("remove"), amountFmt, targetName);
            placeReplaced(inv, items.getConfigurationSection("back"), amountFmt, targetName);
            ConfigurationSection playerSec = items.getConfigurationSection("player");
            if (playerSec != null) {
               int slot = playerSec.getInt("slot", 13);
               ItemStack head = new ItemStack(Material.PLAYER_HEAD);
               SkullMeta meta = (SkullMeta)head.getItemMeta();
               if (meta != null) {
                  meta.setOwningPlayer(target);
                  meta.displayName(ColorUtil.parse(playerSec.getString("name", "&#00fc88%target_name%").replace("%target_name%", targetName).replace("%amount_format%", amountFmt).replace("%bounty%", amountFmt)));
                  List<Component> lore = new ArrayList();

                  for(String line : playerSec.getStringList("lore")) {
                     lore.add(ColorUtil.parse(line.replace("%target_name%", targetName).replace("%amount_format%", amountFmt).replace("%bounty%", amountFmt)));
                  }

                  meta.lore(lore);
                  head.setItemMeta(meta);
               }

               if (slot >= 0 && slot < inv.getSize()) {
                  inv.setItem(slot, head);
               }
            }
         }

         MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.ADMIN);
         MenuSession.TARGET.put(player.getUniqueId(), target.getUniqueId());
         player.openInventory(inv);
      }
   }

   private static ItemStack headItem(ConfigurationSection sec, BountyManager.Bounty b) {
      ItemStack item = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta)item.getItemMeta();
      if (meta != null) {
         meta.setOwningPlayer(Bukkit.getOfflinePlayer(b.target()));
         String name = sec != null ? sec.getString("name", "&#00fc88%name%") : "&#00fc88%name%";
         name = name.replace("%name%", b.name()).replace("%bounty%", BountyManager.formatMoney(b.amount()));
         meta.displayName(ColorUtil.parse(name));
         List<String> loreCfg = sec != null ? sec.getStringList("lore") : List.of("&fBounty: &7$%bounty%");
         List<Component> lore = new ArrayList();

         for(String line : loreCfg) {
            lore.add(ColorUtil.parse(line.replace("%name%", b.name()).replace("%bounty%", BountyManager.formatMoney(b.amount()))));
         }

         meta.lore(lore);
         item.setItemMeta(meta);
      }

      return item;
   }

   private static void place(Inventory inv, ConfigurationSection sec) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            inv.setItem(slot, simple(sec, (String)null, (String)null));
         }
      }
   }

   private static void placeReplaced(Inventory inv, ConfigurationSection sec, String amountFmt, String targetName) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            inv.setItem(slot, simple(sec, amountFmt, targetName));
         }
      }
   }

   private static void placeSort(Inventory inv, ConfigurationSection sec, BountyManager.Sort sort) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            ItemStack item = simple(sec, (String)null, (String)null);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               List<String> keys = sec.getStringList("keys");
               List<String> options = sec.getStringList("options");
               String active = sec.getString("active_prefix", "&#00fc88● ");
               String inactive = sec.getString("inactive_prefix", "&f● ");
               List<Component> lore = new ArrayList();

               for(String line : sec.getStringList("lore")) {
                  lore.add(ColorUtil.parse(line));
               }

               for(int i = 0; i < options.size(); ++i) {
                  String key = i < keys.size() ? (String)keys.get(i) : "";
                  boolean on = matchesSort(key, sort);
                  lore.add(ColorUtil.parse((on ? active : inactive) + (String)options.get(i)));
               }

               meta.lore(lore);
               item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
         }
      }
   }

   private static boolean matchesSort(String key, BountyManager.Sort sort) {
      boolean var10000;
      switch (key.toLowerCase()) {
         case "newest":
            var10000 = sort == BountyManager.Sort.NEWEST;
            break;
         case "oldest":
            var10000 = sort == BountyManager.Sort.OLDEST;
            break;
         case "highest_price":
         case "highest":
            var10000 = sort == BountyManager.Sort.HIGHEST;
            break;
         case "lowest_price":
         case "lowest":
            var10000 = sort == BountyManager.Sort.LOWEST;
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   private static ItemStack simple(ConfigurationSection sec, String amountFmt, String targetName) {
      Material mat = Material.STONE;

      try {
         mat = Material.valueOf(sec.getString("material", "STONE").toUpperCase());
      } catch (Exception var11) {
      }

      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         String name = sec.getString("name", mat.name());
         if (amountFmt != null) {
            name = name.replace("%amount_format%", amountFmt).replace("%bounty%", amountFmt);
         }

         if (targetName != null) {
            name = name.replace("%target_name%", targetName);
         }

         meta.displayName(ColorUtil.parse(name));
         List<Component> lore = new ArrayList();

         for(String line : sec.getStringList("lore")) {
            String l = line;
            if (amountFmt != null) {
               l = line.replace("%amount_format%", amountFmt).replace("%bounty%", amountFmt);
            }

            if (targetName != null) {
               l = l.replace("%target_name%", targetName);
            }

            lore.add(ColorUtil.parse(l));
         }

         if (!lore.isEmpty()) {
            meta.lore(lore);
         }

         item.setItemMeta(meta);
      }

      return item;
   }
}
