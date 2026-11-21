package me.xyr0.astralsmpcore.help.gui;

import java.util.List;
import me.xyr0.astralsmpcore.help.AstralHelp;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class HelpGUI {
   private final AstralHelp plugin;

   public HelpGUI(AstralHelp plugin) {
      this.plugin = plugin;
   }

   public void open(Player player) {
      String title = this.plugin.getConfig().getString("title", "&8Astral Info");
      int rows = this.plugin.getConfig().getInt("rows", 3);
      Inventory inventory = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.color(title));
      if (this.plugin.getConfig().getConfigurationSection("items") != null) {
         for(String key : this.plugin.getConfig().getConfigurationSection("items").getKeys(false)) {
            String path = "items." + key;
            String materialName = this.plugin.getConfig().getString(path + ".item");

            Material material;
            try {
               material = Material.valueOf(materialName.toUpperCase());
            } catch (Exception var13) {
               this.plugin.getLogger().warning("Invalid material: " + materialName);
               continue;
            }

            int slot = this.plugin.getConfig().getInt(path + ".slot");
            String name = this.plugin.getConfig().getString(path + ".name");
            List<String> lore = this.plugin.getConfig().getStringList(path + ".lore");
            inventory.setItem(slot, (new ItemBuilder(material)).name(name).lore(lore).build());
         }
      }

      player.openInventory(inventory);
   }
}
