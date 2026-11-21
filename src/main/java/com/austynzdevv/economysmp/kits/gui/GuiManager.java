package com.austynzdevv.economysmp.kits.gui;

import java.io.File;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.austynzdevv.economysmp.kits.EconomyKits;
import com.austynzdevv.economysmp.kits.Kit;
import com.austynzdevv.economysmp.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiManager {
   private final EconomyKits host;
   private YamlConfiguration mainGui;
   private YamlConfiguration previewGui;

   public GuiManager(EconomyKits host) {
      this.host = host;
      this.reload();
   }

   public void reload() {
      File guiFolder = new File(this.host.getDataFolder(), "gui");
      if (!guiFolder.exists()) {
         guiFolder.mkdirs();
      }

      this.copyResource("kits/gui/main.yml", new File(guiFolder, "main.yml"));
      this.copyResource("kits/gui/preview.yml", new File(guiFolder, "preview.yml"));
      this.mainGui = YamlConfiguration.loadConfiguration(new File(guiFolder, "main.yml"));
      this.previewGui = YamlConfiguration.loadConfiguration(new File(guiFolder, "preview.yml"));
   }

   private void copyResource(String path, File dest) {
      if (!dest.exists()) {
         try {
            InputStream in = this.host.getCore().getResource(path);
            if (in != null) {
               Files.copy(in, dest.toPath(), new CopyOption[0]);
               in.close();
            }
         } catch (Exception var4) {
         }

      }
   }

   public void openMain(Player player) {
      String title = this.mainGui.getString("title", "&8ᴋɪᴛꜱ");
      int rows = Math.max(1, Math.min(6, this.mainGui.getInt("rows", 4)));
      Inventory inv = Bukkit.createInventory(new KitsHolder("main", (String)null), rows * 9, TextUtil.color(title));
      Map<String, Integer> slotMap = new HashMap();
      ConfigurationSection kitsSec = this.mainGui.getConfigurationSection("kits");
      if (kitsSec != null) {
         for(String id : kitsSec.getKeys(false)) {
            slotMap.put(id.toLowerCase(), kitsSec.getInt(id + ".slot", -1));
         }
      }

      for(Kit kit : this.host.getManager().all()) {
         int slot = (Integer)slotMap.getOrDefault(kit.getId(), kit.getSlot());
         if (slot >= 0 && slot < inv.getSize()) {
            long remaining = this.host.getManager().getRemaining(player, kit);
            inv.setItem(slot, kit.buildIcon(player, remaining));
         }
      }

      this.fill(inv, this.mainGui.getConfigurationSection("filler"));
      player.openInventory(inv);
   }

   public void openPreview(Player player, Kit kit) {
      String title = this.previewGui.getString("title", "&8ᴋɪᴛ ᴘʀᴇᴠɪᴇᴡ");
      int rows = Math.max(1, Math.min(6, this.previewGui.getInt("rows", 6)));
      Inventory inv = Bukkit.createInventory(new KitsHolder("preview", kit.getId()), rows * 9, TextUtil.color(title));
      int i = 0;

      for(ItemStack item : kit.getItems()) {
         if (i >= inv.getSize() - 9) {
            break;
         }

         if (item != null) {
            inv.setItem(i++, item.clone());
         }
      }

      this.fill(inv, this.previewGui.getConfigurationSection("filler"));
      this.placeButton(inv, this.previewGui.getConfigurationSection("claim-button"));
      this.placeButton(inv, this.previewGui.getConfigurationSection("back-button"));
      player.openInventory(inv);
   }

   public void openEditor(Player player, Kit kit) {
      Inventory inv = Bukkit.createInventory(new KitsHolder("editor", kit.getId()), 54, TextUtil.color("&8Edit kit: " + kit.getId()));
      int slot = 0;

      for(ItemStack item : kit.getItems()) {
         if (slot >= 45) {
            break;
         }

         if (item != null) {
            inv.setItem(slot++, item.clone());
         }
      }

      inv.setItem(45, this.button(Material.HOPPER, "&eImport inventory"));
      inv.setItem(47, this.button(Material.RED_STAINED_GLASS_PANE, "&cCancel changes"));
      inv.setItem(49, this.button(Material.LIME_STAINED_GLASS_PANE, "&aSave kit"));
      inv.setItem(51, this.button(Material.BARRIER, "&cDelete kit"));
      inv.setItem(53, this.button(Material.CLOCK, "&eSet cooldown (seconds)"));
      player.openInventory(inv);
   }

   private ItemStack button(Material material, String name) {
      ItemStack item = new ItemStack(material);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.setDisplayName(TextUtil.color(name));
         item.setItemMeta(meta);
      }

      return item;
   }

   private void placeButton(Inventory inv, ConfigurationSection sec) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            Material mat = Material.matchMaterial(sec.getString("material", "STONE"));
            if (mat == null) {
               mat = Material.STONE;
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               meta.setDisplayName(TextUtil.color(sec.getString("name", " ")));
               List<String> lore = new ArrayList();

               for(String line : sec.getStringList("lore")) {
                  lore.add(TextUtil.color(line));
               }

               meta.setLore(lore);
               item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
         }
      }
   }

   private void fill(Inventory inv, ConfigurationSection sec) {
      if (sec != null) {
         Material mat = Material.matchMaterial(sec.getString("material", "GRAY_STAINED_GLASS_PANE"));
         if (mat != null && mat != Material.AIR) {
            String name = sec.getString("name", " ");
            ItemStack filler = new ItemStack(mat);
            ItemMeta meta = filler.getItemMeta();
            if (meta != null) {
               meta.setDisplayName(TextUtil.color(name));
               filler.setItemMeta(meta);
            }

            for(int i = 0; i < inv.getSize(); ++i) {
               if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                  inv.setItem(i, filler.clone());
               }
            }

         }
      }
   }
}
