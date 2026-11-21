package com.austynzdevv.economysmp.tpa.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.austynzdevv.economysmp.tpa.EconomyTPA;
import com.austynzdevv.economysmp.tpa.TpaRequest;
import com.austynzdevv.economysmp.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class TpaMenus {
   private static FileConfiguration acceptGui;
   private static FileConfiguration sendGui;

   private TpaMenus() {
   }

   public static void load(EconomyTPA plugin) {
      File dir = new File(plugin.getDataFolder(), "gui");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      File file = new File(dir, "accept.yml");
      if (!file.exists()) {
         plugin.saveResource("gui/accept.yml", false);
      }

      acceptGui = YamlConfiguration.loadConfiguration(file);
      File sendFile = new File(dir, "send.yml");
      if (!sendFile.exists()) {
         plugin.saveResource("gui/send.yml", false);
      }

      sendGui = YamlConfiguration.loadConfiguration(sendFile);
   }

   public static FileConfiguration getAcceptGui() {
      if (acceptGui == null) {
         load(EconomyTPA.get());
      }

      return acceptGui;
   }

   public static FileConfiguration getSendGui() {
      if (sendGui == null) {
         load(EconomyTPA.get());
      }

      return sendGui;
   }

   public static void openAccept(Player target, Player sender, TpaRequest.Type type) {
      FileConfiguration gui = getAcceptGui();
      String title = gui.getString("title", "&8ᴀᴄᴄᴇᴘᴛ ʀᴇǫᴜᴇꜱᴛ");
      int rows = Math.max(1, Math.min(6, gui.getInt("rows", 3)));
      Inventory inv = Bukkit.createInventory(target, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = gui.getConfigurationSection("items");
      if (items != null) {
         place(inv, items.getConfigurationSection("accept"), (String)null, sender.getName(), (String)null, 0);
         place(inv, items.getConfigurationSection("deny"), (String)null, sender.getName(), (String)null, 0);
         ConfigurationSection head = items.getConfigurationSection("head");
         if (head != null) {
            int slot = head.getInt("slot", 13);
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            if (meta != null) {
               meta.setOwningPlayer(sender);
               meta.displayName(ColorUtil.parse(head.getString("name", "&#37BFF8{sender_name}").replace("{sender_name}", sender.getName())));
               String loreKey = type == TpaRequest.Type.TPA ? "lore.tpa" : "lore.tpahere";
               String loreLine = head.getString(loreKey, "&7Request from &#37BFF8{sender_name}").replace("{sender_name}", sender.getName());
               meta.lore(List.of(ColorUtil.parse(loreLine)));
               item.setItemMeta(meta);
            }

            if (slot >= 0 && slot < inv.getSize()) {
               inv.setItem(slot, item);
            }
         }

         World world = sender.getWorld();
         World.Environment env = world.getEnvironment();
         String var10000;
         switch (env) {
            case NETHER -> var10000 = "world-nether";
            case THE_END -> var10000 = "world-end";
            case NORMAL -> var10000 = "world-overworld";
            default -> var10000 = "world-other";
         }

         String worldKey = var10000;
         Material var22;
         switch (env) {
            case NETHER -> var22 = Material.NETHERRACK;
            case THE_END -> var22 = Material.REDSTONE_BLOCK;
            case NORMAL -> var22 = Material.GRASS_BLOCK;
            default -> var22 = Material.IRON_BLOCK;
         }

         Material worldMat = var22;
         ConfigurationSection worldSec = items.getConfigurationSection(worldKey);
         if (worldSec == null) {
            worldSec = items.getConfigurationSection("world-other");
         }

         placeWorld(inv, worldSec, world.getName(), worldMat);
         ConfigurationSection region = items.getConfigurationSection("region");
         if (region != null) {
            int ping = sender.getPing();
            String regionName = region.getString("region-name", "EU");
            place(inv, region, (String)null, sender.getName(), regionName, ping);
         }
      }

      MenuSession.OPEN.put(target.getUniqueId(), true);
      MenuSession.MODE.put(target.getUniqueId(), MenuSession.Mode.ACCEPT);
      MenuSession.SENDER.put(target.getUniqueId(), sender.getUniqueId());
      MenuSession.TYPE.put(target.getUniqueId(), type);
      target.openInventory(inv);
   }

   public static void openSend(Player sender, Player target, TpaRequest.Type type) {
      FileConfiguration gui = getSendGui();
      String title = gui.getString("title", "&8ᴄᴏɴꜰɪʀᴍ ꜱᴇɴᴅ");
      int rows = Math.max(1, Math.min(6, gui.getInt("rows", 3)));
      Inventory inv = Bukkit.createInventory(sender, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = gui.getConfigurationSection("items");
      if (items != null) {
         place(inv, items.getConfigurationSection("confirm"), (String)null, target.getName(), (String)null, 0);
         place(inv, items.getConfigurationSection("cancel"), (String)null, target.getName(), (String)null, 0);
         ConfigurationSection head = items.getConfigurationSection("head");
         if (head != null) {
            int slot = head.getInt("slot", 13);
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            if (meta != null) {
               meta.setOwningPlayer(target);
               meta.displayName(ColorUtil.parse(head.getString("name", "&#37BFF8{target_name}").replace("{target_name}", target.getName()).replace("{sender_name}", target.getName())));
               String loreKey = type == TpaRequest.Type.TPA ? "lore.tpa" : "lore.tpahere";
               String loreLine = head.getString(loreKey, "&7Send request to &#37BFF8{target_name}").replace("{target_name}", target.getName()).replace("{sender_name}", target.getName());
               meta.lore(List.of(ColorUtil.parse(loreLine)));
               item.setItemMeta(meta);
            }

            if (slot >= 0 && slot < inv.getSize()) {
               inv.setItem(slot, item);
            }
         }

         World world = target.getWorld();
         World.Environment env = world.getEnvironment();
         String var10000;
         switch (env) {
            case NETHER -> var10000 = "world-nether";
            case THE_END -> var10000 = "world-end";
            case NORMAL -> var10000 = "world-overworld";
            default -> var10000 = "world-other";
         }

         String worldKey = var10000;
         Material var22;
         switch (env) {
            case NETHER -> var22 = Material.NETHERRACK;
            case THE_END -> var22 = Material.REDSTONE_BLOCK;
            case NORMAL -> var22 = Material.GRASS_BLOCK;
            default -> var22 = Material.IRON_BLOCK;
         }

         Material worldMat = var22;
         ConfigurationSection worldSec = items.getConfigurationSection(worldKey);
         if (worldSec == null) {
            worldSec = items.getConfigurationSection("world-other");
         }

         placeWorld(inv, worldSec, world.getName(), worldMat);
         ConfigurationSection region = items.getConfigurationSection("region");
         if (region != null) {
            int ping = target.getPing();
            String regionName = region.getString("region-name", "EU");
            place(inv, region, (String)null, target.getName(), regionName, ping);
         }
      }

      MenuSession.OPEN.put(sender.getUniqueId(), true);
      MenuSession.MODE.put(sender.getUniqueId(), MenuSession.Mode.SEND);
      MenuSession.TARGET.put(sender.getUniqueId(), target.getUniqueId());
      MenuSession.TYPE.put(sender.getUniqueId(), type);
      sender.openInventory(inv);
   }

   private static void placeWorld(Inventory inv, ConfigurationSection sec, String worldName, Material mat) {
      if (sec != null) {
         int slot = sec.getInt("slot", 12);
         if (slot >= 0 && slot < inv.getSize()) {
            try {
               String cfg = sec.getString("material");
               if (cfg != null && !cfg.isBlank()) {
                  mat = Material.valueOf(cfg.toUpperCase());
               }
            } catch (Exception var14) {
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               String name = sec.getString("name", "&aʟᴏᴄᴀᴛɪᴏɴ").replace("{world}", worldName).replace("{world_name}", worldName);
               meta.displayName(ColorUtil.parse(name));
               List<Component> lore = new ArrayList();
               Object loreObj = sec.get("lore");
               if (loreObj instanceof String) {
                  String s = (String)loreObj;
                  lore.add(ColorUtil.parse(s.replace("{world}", worldName).replace("{world_name}", worldName)));
               } else if (loreObj instanceof List) {
                  for(Object o : (List)loreObj) {
                     lore.add(ColorUtil.parse(String.valueOf(o).replace("{world}", worldName).replace("{world_name}", worldName)));
                  }
               } else {
                  lore.add(ColorUtil.parse("&7" + worldName));
               }

               meta.lore(lore);
               item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
         }
      }
   }

   private static void place(Inventory inv, ConfigurationSection sec, String extra, String senderName, String region, int ping) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            Material mat = Material.STONE;

            try {
               mat = Material.valueOf(sec.getString("material", "STONE").toUpperCase());
            } catch (Exception var18) {
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               String name = sec.getString("name", mat.name()).replace("{sender_name}", senderName == null ? "" : senderName).replace("{target_name}", senderName == null ? "" : senderName);
               if (extra != null) {
                  name = name.replace("{seconds}", extra).replace("{time}", extra);
               }

               if (region != null) {
                  name = name.replace("{region}", region).replace("{ping}", String.valueOf(ping));
               }

               meta.displayName(ColorUtil.parse(name));
               List<Component> lore = new ArrayList();
               Object loreObj = sec.get("lore");
               if (loreObj instanceof String) {
                  String s = (String)loreObj;
                  String line = s.replace("{sender_name}", senderName == null ? "" : senderName).replace("{target_name}", senderName == null ? "" : senderName);
                  if (region != null) {
                     line = line.replace("{region}", region).replace("{ping}", String.valueOf(ping));
                  }

                  lore.add(ColorUtil.parse(line));
               } else if (loreObj instanceof List) {
                  for(Object o : (List)loreObj) {
                     String line = String.valueOf(o).replace("{sender_name}", senderName == null ? "" : senderName).replace("{target_name}", senderName == null ? "" : senderName);
                     if (region != null) {
                        line = line.replace("{region}", region).replace("{ping}", String.valueOf(ping));
                     }

                     lore.add(ColorUtil.parse(line));
                  }
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
}
