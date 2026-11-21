package com.austynzdevv.novacore.warps.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.warps.EconomyWarps;
import com.austynzdevv.novacore.warps.NamedLocation;
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

public final class WarpMenus {
   private static FileConfiguration spawnGui;
   private static FileConfiguration afkGui;

   private WarpMenus() {
   }

   public static void load(EconomyWarps plugin) {
      File dir = new File(plugin.getDataFolder(), "gui");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      File spawnFile = new File(dir, "spawn-gui.yml");
      if (!spawnFile.exists()) {
         plugin.saveResource("gui/spawn-gui.yml", false);
      }

      File afkFile = new File(dir, "afk-gui.yml");
      if (!afkFile.exists()) {
         plugin.saveResource("gui/afk-gui.yml", false);
      }

      spawnGui = YamlConfiguration.loadConfiguration(spawnFile);
      afkGui = YamlConfiguration.loadConfiguration(afkFile);
   }

   public static FileConfiguration getSpawnGui() {
      if (spawnGui == null) {
         load(EconomyWarps.get());
      }

      return spawnGui;
   }

   public static FileConfiguration getAfkGui() {
      if (afkGui == null) {
         load(EconomyWarps.get());
      }

      return afkGui;
   }

   public static void openSpawn(Player player, int page) {
      open(player, MenuSession.Type.SPAWN, page);
   }

   public static void openAfk(Player player, int page) {
      open(player, MenuSession.Type.AFK, page);
   }

   private static void open(Player player, MenuSession.Type type, int page) {
      FileConfiguration gui = type == MenuSession.Type.SPAWN ? getSpawnGui() : getAfkGui();
      ConfigurationSection root = type == MenuSession.Type.SPAWN ? gui.getConfigurationSection("spawn-gui") : gui.getConfigurationSection("afk-gui");
      if (root == null) {
         root = gui;
      }

      String title = root.getString("title", type == MenuSession.Type.SPAWN ? "&8ꜱᴘᴀᴡɴs" : "&8ᴀꜰᴋ ᴀʀᴇᴀꜱ");
      int rows = Math.max(1, Math.min(6, root.getInt("row", 6)));
      Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtil.parse(title));
      Map<String, NamedLocation> locations = type == MenuSession.Type.SPAWN ? EconomyWarps.get().getWarps().getSpawns() : EconomyWarps.get().getWarps().getAfks();
      List<NamedLocation> list = new ArrayList(locations.values());
      ConfigurationSection settings = root.getConfigurationSection("gui-settings");
      int pageSize = Math.max(1, rows * 9 - 9);
      int maxPage = Math.max(0, (list.size() - 1) / pageSize);
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      int start = page * pageSize;
      int end = Math.min(list.size(), start + pageSize);
      int slot = 0;

      for(int i = start; i < end; ++i) {
         NamedLocation loc = (NamedLocation)list.get(i);
         boolean full = loc.isFull();
         ConfigurationSection itemSec = settings == null ? null : settings.getConfigurationSection(full ? "maxed-players" : "spawn");
         ItemStack item = buildLocationItem(itemSec, loc, full);
         if (slot < pageSize) {
            inv.setItem(slot, item);
         }

         ++slot;
      }

      if (settings != null) {
         placeStatic(inv, settings.getConfigurationSection(type == MenuSession.Type.SPAWN ? "random-spawn" : "random-afk"));
         if (page > 0) {
            placeStatic(inv, settings.getConfigurationSection("back"));
         }

         if (page < maxPage) {
            placeStatic(inv, settings.getConfigurationSection("next"));
         }
      }

      MenuSession.OPEN.put(player.getUniqueId(), true);
      MenuSession.MODE.put(player.getUniqueId(), type);
      MenuSession.PAGE.put(player.getUniqueId(), page);
      player.openInventory(inv);
   }

   private static ItemStack buildLocationItem(ConfigurationSection sec, NamedLocation loc, boolean full) {
      Material mat = full ? Material.REDSTONE_BLOCK : Material.ITEM_FRAME;
      String name = (full ? "&c" : "&b") + loc.getName();
      List<String> loreRaw = new ArrayList();
      if (sec != null) {
         try {
            mat = Material.valueOf(sec.getString("material", mat.name()).toUpperCase());
         } catch (Exception var11) {
         }

         name = sec.getString("displayName", name).replace("%name%", loc.getName()).replace("%current%", String.valueOf(loc.currentPlayers())).replace("%max%", loc.getMaxPlayers() < 0 ? "∞" : String.valueOf(loc.getMaxPlayers()));

         for(String line : sec.getStringList("lore")) {
            loreRaw.add(line.replace("%name%", loc.getName()).replace("%current%", String.valueOf(loc.currentPlayers())).replace("%max%", loc.getMaxPlayers() < 0 ? "∞" : String.valueOf(loc.getMaxPlayers())));
         }
      }

      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(ColorUtil.parse(name));
         List<Component> lore = new ArrayList();

         for(String line : loreRaw) {
            lore.add(ColorUtil.parse(line));
         }

         if (!lore.isEmpty()) {
            meta.lore(lore);
         }

         item.setItemMeta(meta);
      }

      return item;
   }

   private static void placeStatic(Inventory inv, ConfigurationSection sec) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            Material mat = Material.STONE;

            try {
               mat = Material.valueOf(sec.getString("material", "STONE").toUpperCase());
            } catch (Exception var9) {
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               meta.displayName(ColorUtil.parse(sec.getString("displayName", mat.name())));
               List<Component> lore = new ArrayList();

               for(String line : sec.getStringList("lore")) {
                  lore.add(ColorUtil.parse(line));
               }

               if (!lore.isEmpty()) {
                  meta.lore(lore);
               }

               item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
         }
      }
   }

   public static String locationNameAt(Player player, int slot) {
      MenuSession.Type type = (MenuSession.Type)MenuSession.MODE.get(player.getUniqueId());
      if (type == null) {
         return null;
      } else {
         FileConfiguration gui = type == MenuSession.Type.SPAWN ? getSpawnGui() : getAfkGui();
         ConfigurationSection root = type == MenuSession.Type.SPAWN ? gui.getConfigurationSection("spawn-gui") : gui.getConfigurationSection("afk-gui");
         if (root == null) {
            root = gui;
         }

         int rows = Math.max(1, Math.min(6, root.getInt("row", 6)));
         int pageSize = Math.max(1, rows * 9 - 9);
         if (slot >= 0 && slot < pageSize) {
            int page = (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0);
            Map<String, NamedLocation> locations = type == MenuSession.Type.SPAWN ? EconomyWarps.get().getWarps().getSpawns() : EconomyWarps.get().getWarps().getAfks();
            List<NamedLocation> list = new ArrayList(locations.values());
            int index = page * pageSize + slot;
            return index >= 0 && index < list.size() ? ((NamedLocation)list.get(index)).getName() : null;
         } else {
            return null;
         }
      }
   }
}
