package com.austynzdevv.nyrethacore.moderation.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.austynzdevv.nyrethacore.moderation.SusFlag;
import com.austynzdevv.nyrethacore.moderation.SusManager;
import com.austynzdevv.nyrethacore.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class SusMenus {
   private SusMenus() {
   }

   public static void openMain(Player viewer, SusManager manager, FileConfiguration cfg, int page) {
      ConfigurationSection gui = cfg.getConfigurationSection("sus.gui");
      String title = gui != null ? gui.getString("title", "&8Suspected Players") : "&8Suspected Players";
      int rows = gui != null ? Math.min(6, Math.max(3, gui.getInt("rows", 6))) : 6;
      int size = rows * 9;
      Inventory inv = Bukkit.createInventory(new MainHolder(page), size, TextUtil.component(title));
      SusManager.Anticheat filter = manager.getFilter(viewer.getUniqueId());
      Map<UUID, List<SusFlag>> grouped = manager.grouped(filter);
      List<UUID> ids = new ArrayList(grouped.keySet());
      int pageSize = Math.max(1, size - 9);
      int totalPages = Math.max(1, (int)Math.ceil((double)ids.size() / (double)pageSize));
      if (page < 0) {
         page = 0;
      }

      if (page >= totalPages) {
         page = totalPages - 1;
      }

      int start = page * pageSize;
      int end = Math.min(ids.size(), start + pageSize);
      List<String> headLore = gui != null ? gui.getStringList("player-head-lore") : List.of();
      if (headLore.isEmpty()) {
         headLore = List.of("&7Flags: &f%flags%", "&7Last: &f%last_check% &7(%last_ac%)", "&7Click for details");
      }

      int slot = 0;

      for(int i = start; i < end; ++i) {
         UUID id = (UUID)ids.get(i);
         List<SusFlag> list = (List)grouped.get(id);
         SusFlag last = (SusFlag)list.get(list.size() - 1);
         ItemStack head = new ItemStack(Material.PLAYER_HEAD);
         SkullMeta meta = (SkullMeta)head.getItemMeta();
         if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
            meta.displayName(TextUtil.component("&#34eb9b" + last.getPlayerName()));
            List<Component> lore = new ArrayList();

            for(String line : headLore) {
               lore.add(TextUtil.component(line.replace("%flags%", String.valueOf(list.size())).replace("%last_check%", shortCheck(last.getCheck())).replace("%last_ac%", last.getAnticheat()).replace("%player%", last.getPlayerName()).replace("%vl%", String.valueOf(last.getVl()))));
            }

            meta.lore(lore);
            head.setItemMeta(meta);
         }

         inv.setItem(slot++, head);
      }

      int bottom = size - 9;
      placeNav(inv, gui, "back", bottom + 0, Material.ARROW, "&#34eb9bPrevious", page > 0);
      int var10003 = bottom + 3;
      String var10006 = filter == null ? "ALL" : filter.name();
      placeControl(inv, gui, "clock", var10003, Material.CLOCK, "&#34eb9bFilter", List.of("&7Current: &f" + var10006, "&7Click to change anti-cheat filter"));
      var10003 = bottom + 4;
      Material var10004 = Material.NETHER_STAR;
      int var10007 = ids.size();
      placeControl(inv, gui, "nether-star", var10003, var10004, "&#34eb9bRefresh", List.of("&7Click to refresh the list", "&7Players: &f" + var10007));
      placeNav(inv, gui, "next", bottom + 8, Material.ARROW, "&#34eb9bNext", page < totalPages - 1);
      viewer.openInventory(inv);
   }

   public static void openFilter(Player viewer, SusManager manager, FileConfiguration cfg) {
      ConfigurationSection gui = cfg.getConfigurationSection("sus.gui.filter-gui");
      String title = gui != null ? gui.getString("title", "&8Filter Anti-Cheats") : "&8Filter Anti-Cheats";
      int rows = gui != null ? Math.min(6, Math.max(1, gui.getInt("rows", 3))) : 3;
      Inventory inv = Bukkit.createInventory(new FilterHolder(), rows * 9, TextUtil.component(title));
      SusManager.Anticheat current = manager.getFilter(viewer.getUniqueId());
      placeFilterItem(inv, 11, Material.BARRIER, "ALL", current == null);
      placeFilterItem(inv, 12, Material.IRON_SWORD, "GRIM", current == SusManager.Anticheat.GRIM);
      placeFilterItem(inv, 13, Material.DIAMOND_SWORD, "VULCAN", current == SusManager.Anticheat.VULCAN);
      placeFilterItem(inv, 14, Material.GOLDEN_SWORD, "MATRIX", current == SusManager.Anticheat.MATRIX);
      placeFilterItem(inv, 15, Material.TOTEM_OF_UNDYING, "TOTEMGUARD", current == SusManager.Anticheat.TOTEMGUARD);
      placeFilterItem(inv, 16, Material.NETHERITE_SWORD, "ANGELGUARD", current == SusManager.Anticheat.ANGELGUARD);
      ItemStack back = new ItemStack(Material.ARROW);
      ItemMeta bm = back.getItemMeta();
      if (bm != null) {
         bm.displayName(TextUtil.component("&#34eb9bBack"));
         back.setItemMeta(bm);
      }

      inv.setItem(rows * 9 - 9, back);
      viewer.openInventory(inv);
   }

   public static void openDetail(Player viewer, SusManager manager, FileConfiguration cfg, UUID target) {
      List<SusFlag> list = manager.flagsFor(target);
      String name = manager.displayName(target, list);
      Inventory inv = Bukkit.createInventory(new DetailHolder(target), 54, TextUtil.component("&8Flags: " + name));
      int slot = 0;

      for(int i = list.size() - 1; i >= 0 && slot < 45; --i) {
         SusFlag f = (SusFlag)list.get(i);
         ItemStack paper = new ItemStack(Material.PAPER);
         ItemMeta meta = paper.getItemMeta();
         if (meta != null) {
            meta.displayName(TextUtil.component("&#34eb9b" + shortCheck(f.getCheck())));
            List<Component> lore = new ArrayList();
            lore.add(TextUtil.component("&7AntiCheat: &f" + f.getAnticheat()));
            lore.add(TextUtil.component("&7VL: &f" + f.getVl()));
            long ago = Math.max(0L, (System.currentTimeMillis() - f.getTime()) / 1000L);
            lore.add(TextUtil.component("&7When: &f" + ago + "s ago"));
            meta.lore(lore);
            paper.setItemMeta(meta);
         }

         inv.setItem(slot++, paper);
      }

      ItemStack clear = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta cm = clear.getItemMeta();
      if (cm != null) {
         cm.displayName(TextUtil.component("&#e00202Clear flags"));
         clear.setItemMeta(cm);
      }

      inv.setItem(49, clear);
      ItemStack back = new ItemStack(Material.ARROW);
      ItemMeta bm = back.getItemMeta();
      if (bm != null) {
         bm.displayName(TextUtil.component("&#34eb9bBack"));
         back.setItemMeta(bm);
      }

      inv.setItem(45, back);
      viewer.openInventory(inv);
   }

   public static String shortCheck(String check) {
      if (check != null && !check.isEmpty()) {
         String s = check.trim();
         int slash = Math.max(s.lastIndexOf(46), s.lastIndexOf(58));
         if (slash >= 0 && slash + 1 < s.length()) {
            s = s.substring(slash + 1).trim();
         }

         if (s.contains(" ")) {
            String[] parts = s.split("\\s+");
            String last = parts[parts.length - 1].trim();
            if (!last.isEmpty()) {
               s = last;
            }
         }

         if (s.regionMatches(true, 0, "GrimAC", 0, 6) && s.length() > 6) {
            s = s.substring(6).replaceAll("^[_\\-\\s]+", "");
         }

         if (s.regionMatches(true, 0, "Vulcan", 0, 6) && s.length() > 6) {
            s = s.substring(6).replaceAll("^[_\\-\\s]+", "");
         }

         return s.isEmpty() ? "Unknown" : s;
      } else {
         return "Unknown";
      }
   }

   private static void placeFilterItem(Inventory inv, int slot, Material mat, String label, boolean selected) {
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(TextUtil.component((selected ? "&#02de4f" : "&#34eb9b") + label));
         List<Component> lore = new ArrayList();
         lore.add(TextUtil.component(selected ? "&aSelected" : "&7Click to select"));
         meta.lore(lore);
         item.setItemMeta(meta);
      }

      inv.setItem(slot, item);
   }

   private static void placeControl(Inventory inv, ConfigurationSection gui, String key, int defSlot, Material defMat, String defName, List<String> defLore) {
      ConfigurationSection sec = gui != null ? gui.getConfigurationSection(key) : null;
      int slot = sec != null ? sec.getInt("slot", defSlot) : defSlot;
      if (slot < 0 || slot >= inv.getSize()) {
         slot = defSlot;
      }

      Material mat = defMat;
      if (sec != null) {
         try {
            mat = Material.valueOf(sec.getString("material", defMat.name()).toUpperCase());
         } catch (Exception var17) {
         }
      }

      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         String name = sec != null ? sec.getString("name", defName) : defName;
         meta.displayName(TextUtil.component(name));
         List<String> loreCfg = sec != null ? sec.getStringList("lore") : List.of();
         List<Component> lore = new ArrayList();

         for(String line : loreCfg.isEmpty() ? defLore : loreCfg) {
            lore.add(TextUtil.component(line));
         }

         meta.lore(lore);
         item.setItemMeta(meta);
      }

      inv.setItem(slot, item);
   }

   private static void placeNav(Inventory inv, ConfigurationSection gui, String key, int defSlot, Material mat, String defName, boolean enabled) {
      if (enabled) {
         ConfigurationSection sec = gui != null ? gui.getConfigurationSection(key) : null;
         int slot = sec != null ? sec.getInt("slot", defSlot) : defSlot;
         if (slot < 0 || slot >= inv.getSize()) {
            slot = defSlot;
         }

         ItemStack item = new ItemStack(mat);
         ItemMeta meta = item.getItemMeta();
         if (meta != null) {
            meta.displayName(TextUtil.component(sec != null ? sec.getString("name", defName) : defName));
            item.setItemMeta(meta);
         }

         inv.setItem(slot, item);
      }
   }

   public static final class MainHolder implements InventoryHolder {
      private final int page;

      public MainHolder(int page) {
         this.page = page;
      }

      public int getPage() {
         return this.page;
      }

      public Inventory getInventory() {
         return null;
      }
   }

   public static final class FilterHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static final class DetailHolder implements InventoryHolder {
      private final UUID target;

      public DetailHolder(UUID target) {
         this.target = target;
      }

      public UUID getTarget() {
         return this.target;
      }

      public Inventory getInventory() {
         return null;
      }
   }
}
