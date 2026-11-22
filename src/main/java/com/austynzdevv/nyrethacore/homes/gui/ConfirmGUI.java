package com.austynzdevv.nyrethacore.homes.gui;

import java.util.List;
import java.util.stream.Collectors;
import com.austynzdevv.nyrethacore.homes.EconomyHomes;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmGUI {
   private final EconomyHomes plugin;

   public ConfirmGUI(EconomyHomes plugin) {
      this.plugin = plugin;
   }

   public void open(Player player, String homeId) {
      this.openInternal(player, homeId, false);
   }

   public void openTeam(Player player) {
      this.openInternal(player, "Team Home", true);
   }

   private void openInternal(Player player, String homeId, boolean teamHome) {
      FileConfiguration gui = this.plugin.configs().getConfirmGui();
      MainGUI.Holder holder = new MainGUI.Holder(player.getUniqueId(), true);
      holder.homeId = homeId;
      holder.teamHome = teamHome;
      Inventory inv = Bukkit.createInventory(holder, gui.getInt("size", 3) * 9, ColorUtil.color(gui.getString("menu-title", "&8ᴄᴏɴꜰɪʀᴍ")));
      this.put(inv, gui, "slots.info", homeId);
      this.put(inv, gui, "slots.no", homeId);
      this.put(inv, gui, "slots.yes", homeId);
      player.openInventory(inv);
      this.plugin.homes().sound(player, "interact");
   }

   private void put(Inventory inv, FileConfiguration gui, String path, String homeId) {
      int slot = gui.getInt(path + ".position", 0);

      Material mat;
      try {
         mat = Material.valueOf(gui.getString(path + ".item", "STONE").toUpperCase());
      } catch (Exception var10) {
         mat = Material.STONE;
      }

      ItemStack stack = new ItemStack(mat);
      ItemMeta meta = stack.getItemMeta();
      if (meta != null) {
         meta.setDisplayName(ColorUtil.color(gui.getString(path + ".label", "").replace("%home%", homeId)));
         List<String> lore = gui.getStringList(path + ".extra");
         if (lore != null && !lore.isEmpty()) {
            meta.setLore((List)lore.stream().map((l) -> ColorUtil.color(l.replace("%home%", homeId))).collect(Collectors.toList()));
         }

         stack.setItemMeta(meta);
      }

      if (slot >= 0 && slot < inv.getSize()) {
         inv.setItem(slot, stack);
      }

   }
}
