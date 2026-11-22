package com.austynzdevv.nyrethacore.media.gui;

import com.austynzdevv.nyrethacore.media.EconomyMedia;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class MediaGUI {
   public static void open(Player player) {
      EconomyMedia plugin = EconomyMedia.getInstance();
      String title = plugin.getConfig().getString("media-gui.title", "&8media");
      int rows = plugin.getConfig().getInt("media-gui.rows", 3);
      Inventory inventory = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.color(title));
      Material material = Material.valueOf(plugin.getConfig().getString("media-gui.item.material", "LEATHER_HELMET"));
      ItemStack item = (new ItemBuilder(material)).name(plugin.getConfig().getString("media-gui.item.name")).lore(plugin.getConfig().getStringList("media-gui.item.lore")).build();
      ItemMeta var8 = item.getItemMeta();
      if (var8 instanceof LeatherArmorMeta meta) {
         String hex = plugin.getConfig().getString("media-gui.item.color", "#C74375").replace("#", "");

         try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            meta.setColor(Color.fromRGB(r, g, b));
            item.setItemMeta(meta);
         } catch (Exception var12) {
         }
      }

      int slot = plugin.getConfig().getInt("media-gui.item.slot", 13);
      inventory.setItem(slot, item);
      player.openInventory(inventory);
   }
}
