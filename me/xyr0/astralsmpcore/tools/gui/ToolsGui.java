package me.xyr0.astralsmpcore.tools.gui;

import java.util.ArrayList;
import java.util.List;
import me.xyr0.astralsmpcore.tools.AstralTools;
import me.xyr0.astralsmpcore.tools.ToolDefinition;
import me.xyr0.astralsmpcore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ToolsGui implements InventoryHolder {
   private final AstralTools plugin;
   private final Inventory inventory;

   public ToolsGui(AstralTools plugin) {
      this.plugin = plugin;
      FileConfiguration gui = plugin.getGuiConfig();
      int rows = Math.max(1, Math.min(6, gui.getInt("rows", 3)));
      String title = gui.getString("title", "&8Tools");
      this.inventory = Bukkit.createInventory(this, rows * 9, ColorUtil.parse(title));
      if (gui.getBoolean("fill.enabled", true)) {
         Material fillMat = Material.matchMaterial(gui.getString("fill.material", "GRAY_STAINED_GLASS_PANE"));
         if (fillMat == null) {
            fillMat = Material.GRAY_STAINED_GLASS_PANE;
         }

         ItemStack fill = new ItemStack(fillMat);
         ItemMeta fm = fill.getItemMeta();
         if (fm != null) {
            fm.displayName(ColorUtil.parse(gui.getString("fill.name", " ")));
            fill.setItemMeta(fm);
         }

         for(int i = 0; i < this.inventory.getSize(); ++i) {
            this.inventory.setItem(i, fill);
         }
      }

      for(ToolDefinition def : plugin.getToolManager().all()) {
         int slot = gui.getInt("slots." + def.getId(), -1);
         if (slot >= 0 && slot < this.inventory.getSize()) {
            this.inventory.setItem(slot, plugin.getToolFactory().create(def));
         }
      }

      int closeSlot = gui.getInt("close.slot", this.inventory.getSize() - 5);
      if (closeSlot >= 0 && closeSlot < this.inventory.getSize()) {
         Material cm = Material.matchMaterial(gui.getString("close.material", "RED_STAINED_GLASS_PANE"));
         if (cm == null) {
            cm = Material.RED_STAINED_GLASS_PANE;
         }

         ItemStack close = new ItemStack(cm);
         ItemMeta meta = close.getItemMeta();
         if (meta != null) {
            meta.displayName(ColorUtil.parse(gui.getString("close.name", "&cClose")));
            List<Component> lore = new ArrayList();

            for(String l : gui.getStringList("close.lore")) {
               lore.add(ColorUtil.parse(l));
            }

            meta.lore(lore);
            close.setItemMeta(meta);
         }

         this.inventory.setItem(closeSlot, close);
      }

   }

   public void open(Player player) {
      player.openInventory(this.inventory);
   }

   public Inventory getInventory() {
      return this.inventory;
   }
}
