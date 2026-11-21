package com.austynzdevv.economysmp.billford.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.austynzdevv.economysmp.billford.EconomyBillford;
import com.austynzdevv.economysmp.billford.BillfordManager;
import com.austynzdevv.economysmp.util.ColorUtil;
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

public final class BillfordMenus {
   private BillfordMenus() {
   }

   public static FileConfiguration loadGui(EconomyBillford plugin) {
      File dir = new File(plugin.getDataFolder(), "gui");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      File file = new File(dir, "billford.yml");
      if (!file.exists()) {
         plugin.saveResource("gui/billford.yml", false);
      }

      return YamlConfiguration.loadConfiguration(file);
   }

   public static void open(Player player) {
      EconomyBillford plugin = EconomyBillford.get();
      FileConfiguration gui = loadGui(plugin);
      BillfordManager.Preset preset = plugin.getBillford().getCurrentPreset();
      if (preset == null) {
         player.sendMessage(ColorUtil.parse(plugin.getMessages().get("no-preset", "&cNo trade preset is configured.")));
      } else {
         String title = gui.getString("title", "&8ʙɪʟʟꜰᴏʀᴅ");
         int rows = Math.max(1, Math.min(6, gui.getInt("rows", 6)));
         Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtil.parse(title));
         ConfigurationSection filler = gui.getConfigurationSection("filler");
         if (filler != null && filler.getBoolean("enabled", true)) {
            ItemStack pane = named(material(filler.getString("material", "GRAY_STAINED_GLASS_PANE")), filler.getString("displayname", "&7 "), filler.getStringList("lore"));

            for(int slot : filler.getIntegerList("slot")) {
               if (slot >= 0 && slot < inv.getSize()) {
                  inv.setItem(slot, pane.clone());
               }
            }
         }

         ConfigurationSection items = gui.getConfigurationSection("items");
         if (items == null) {
            items = gui.getConfigurationSection("Items");
         }

         BillfordManager mgr = plugin.getBillford();
         BillfordManager.RequiredItem firstReq = (BillfordManager.RequiredItem)preset.required().get(0);
         if (items != null) {
            placeDynamic(inv, items.getConfigurationSection("required-item"), firstReq.material(), "&f" + firstReq.amount() + "x " + mgr.pretty(firstReq.material()), List.of("&7Required for trade"), firstReq.amount());
            placeDynamic(inv, items.getConfigurationSection("trade-result"), preset.tradeItem(), "&f" + preset.tradeAmount() + "x " + mgr.pretty(preset.tradeItem()), List.of("&7You will receive this"), preset.tradeAmount());
            ConfigurationSection confirm = items.getConfigurationSection("trade-confirm-item");
            if (confirm != null) {
               int slot = confirm.getInt("slot", 23);
               if (slot >= 0 && slot < inv.getSize()) {
                  inv.setItem(slot, named(material(confirm.getString("material", "HOPPER")), confirm.getString("displayname", "&8ᴛʀᴀᴅᴇ"), confirm.getStringList("lore")));
               }
            }

            ConfigurationSection book = items.getConfigurationSection("info-book");
            if (book != null) {
               int slot = book.getInt("slot", 49);
               if (slot >= 0 && slot < inv.getSize()) {
                  List<String> lore = new ArrayList();

                  for(String line : book.getStringList("lore")) {
                     String var10001 = line.replace("%required_item%", mgr.formatRequired(preset));
                     int var10003 = preset.tradeAmount();
                     lore.add(var10001.replace("%item%", var10003 + "x " + mgr.pretty(preset.tradeItem())).replace("%trade_item%", mgr.pretty(preset.tradeItem())));
                  }

                  inv.setItem(slot, named(material(book.getString("material", "BOOK")), book.getString("displayname", "&#7afc00Billford Trade"), lore));
               }
            }
         }

         MenuSession.OPEN.put(player.getUniqueId(), true);
         player.openInventory(inv);
      }
   }

   private static void placeDynamic(Inventory inv, ConfigurationSection sec, Material mat, String defName, List<String> defLore, int amount) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            String name = sec.getString("displayname", defName).replace("%required_item_displayname%", defName).replace("%trade_result_displayname%", defName).replace("%required_item_material%", mat.name()).replace("%trade_result_material%", mat.name());
            List<String> lore = sec.getStringList("lore");
            if (lore == null || lore.isEmpty()) {
               lore = defLore;
            }

            ItemStack item = named(mat, name, lore);
            item.setAmount(Math.min(64, Math.max(1, amount)));
            inv.setItem(slot, item);
         }
      }
   }

   private static ItemStack named(Material mat, String name, List<String> loreLines) {
      ItemStack item = new ItemStack(mat == null ? Material.STONE : mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(ColorUtil.parse(name == null ? mat.name() : name));
         if (loreLines != null && !loreLines.isEmpty()) {
            List<Component> lore = new ArrayList();

            for(String line : loreLines) {
               lore.add(ColorUtil.parse(line));
            }

            meta.lore(lore);
         }

         item.setItemMeta(meta);
      }

      return item;
   }

   private static Material material(String raw) {
      if (raw != null && !raw.isBlank() && !raw.contains("%")) {
         try {
            return Material.valueOf(raw.toUpperCase(Locale.ROOT));
         } catch (Exception var2) {
            return Material.STONE;
         }
      } else {
         return Material.STONE;
      }
   }
}
