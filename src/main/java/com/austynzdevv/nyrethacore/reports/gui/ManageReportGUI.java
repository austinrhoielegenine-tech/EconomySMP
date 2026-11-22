package com.austynzdevv.nyrethacore.reports.gui;

import java.util.List;
import java.util.UUID;
import com.austynzdevv.nyrethacore.reports.EconomyReports;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ManageReportGUI {
   public static void open(Player staff, UUID target, EconomyReports plugin) {
      Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 27, "§bManage Player");
      ItemStack ban = new ItemStack(Material.NETHERITE_AXE);
      ItemMeta banMeta = ban.getItemMeta();
      if (banMeta != null) {
         banMeta.setDisplayName(plugin.getConfig().getString("items.ban.name").replace("&", "§"));
         banMeta.setLore(List.of("§7Ban player"));
         ban.setItemMeta(banMeta);
      }

      ItemStack mute = new ItemStack(Material.PAPER);
      ItemMeta muteMeta = mute.getItemMeta();
      if (muteMeta != null) {
         muteMeta.setDisplayName(plugin.getConfig().getString("items.mute.name").replace("&", "§"));
         muteMeta.setLore(List.of("§7Mute player"));
         mute.setItemMeta(muteMeta);
      }

      ItemStack teleport = new ItemStack(Material.ENDER_PEARL);
      ItemMeta tpMeta = teleport.getItemMeta();
      if (tpMeta != null) {
         tpMeta.setDisplayName(plugin.getConfig().getString("items.teleport.name").replace("&", "§"));
         tpMeta.setLore(List.of("§7Teleport to player"));
         teleport.setItemMeta(tpMeta);
      }

      inventory.setItem(11, ban);
      inventory.setItem(13, mute);
      inventory.setItem(15, teleport);
      ReportsGUI.setSelected(staff, target);
      staff.openInventory(inventory);
   }
}
