package com.austynzdevv.economysmp.rtp.gui;

import java.util.ArrayList;
import java.util.List;
import com.austynzdevv.economysmp.rtp.EconomyRTP;
import com.austynzdevv.economysmp.rtp.placeholders.PlaceholderUtil;
import com.austynzdevv.economysmp.rtp.util.ConfigUtil;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RTPGUI {
   private final EconomyRTP plugin;

   public RTPGUI(EconomyRTP plugin) {
      this.plugin = plugin;
   }

   public void open(Player player) {
      FileConfiguration gui = ConfigUtil.getFile(this.plugin, "guis.yml");
      boolean nested = gui.contains("main-menu");
      String base = nested ? "main-menu." : "";
      String title = PlaceholderUtil.replace(player, gui.getString(base + "title", "&8Random Teleport"));
      int size = gui.getInt(nested ? "main-menu.size" : "rows", 27);
      if (size <= 6) {
         size *= 9;
      }

      Inventory inv = Bukkit.createInventory(new Holder(), size, ColorUtil.color(title));
      String itemsPath = nested ? "main-menu.items" : "worlds";
      ConfigurationSection section = gui.getConfigurationSection(itemsPath);
      if (section == null) {
         player.openInventory(inv);
      } else {
         for(String key : section.getKeys(false)) {
            String path = itemsPath + "." + key;
            Material mat = Material.matchMaterial(gui.getString(path + ".material", "STONE"));
            if (mat == null) {
               mat = Material.STONE;
            }

            String worldKey = gui.getString(path + ".world", key);
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               meta.setDisplayName(ColorUtil.color(PlaceholderUtil.replace(player, gui.getString(path + ".name", "&fWorld"), worldKey)));
               List<String> lore = new ArrayList();

               for(String line : gui.getStringList(path + ".lore")) {
                  lore.add(ColorUtil.color(PlaceholderUtil.replace(player, line, worldKey)));
               }

               meta.setLore(lore);
               item.setItemMeta(meta);
            }

            inv.setItem(gui.getInt(path + ".slot"), item);
         }

         player.openInventory(inv);
      }
   }

   public static class Holder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class RTPHolder extends Holder {
   }
}
