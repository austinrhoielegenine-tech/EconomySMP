package me.xyr0.astralsmpcore.order.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GuiItems {
   private GuiItems() {
   }

   public static FileConfiguration load(File folder, String name) {
      File file = new File(folder, name);
      if (!file.exists()) {
         file = new File(folder, name);
      }

      return YamlConfiguration.loadConfiguration(file);
   }

   public static ItemStack fromSection(ConfigurationSection section, Map<String, String> placeholders) {
      if (section == null) {
         return new ItemStack(Material.STONE);
      } else {
         String matName = section.getString("material", section.getString("item", "STONE"));
         if (placeholders != null) {
            for(Map.Entry<String, String> e : placeholders.entrySet()) {
               matName = matName.replace("%" + (String)e.getKey() + "%", (CharSequence)e.getValue());
            }
         }

         if (matName.startsWith("%") || matName.isEmpty()) {
            matName = "STONE";
         }

         Material material;
         try {
            material = Material.valueOf(matName.toUpperCase());
         } catch (IllegalArgumentException var9) {
            material = Material.STONE;
         }

         String name = section.getString("name", section.getString("displayname", " "));
         List<String> lore = section.getStringList("lore");
         if (placeholders != null) {
            name = apply(name, placeholders);
            List<String> out = new ArrayList();

            for(String line : lore) {
               out.add(apply(line, placeholders));
            }

            lore = out;
         }

         return ItemUtil.named(material, name, lore);
      }
   }

   public static ItemStack applyMeta(ItemStack base, String name, List<String> lore) {
      ItemStack item = base.clone();
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(ColorUtil.parse(name));
         List<Component> lines = new ArrayList();

         for(String line : lore) {
            lines.add(ColorUtil.parse(line));
         }

         meta.lore(lines);
         item.setItemMeta(meta);
      }

      return item;
   }

   private static String apply(String text, Map<String, String> placeholders) {
      if (text == null) {
         return "";
      } else {
         String out = text;

         for(Map.Entry<String, String> e : placeholders.entrySet()) {
            out = out.replace("%" + (String)e.getKey() + "%", (CharSequence)(e.getValue() == null ? "" : (CharSequence)e.getValue()));
         }

         return out;
      }
   }

   public static int slot(ConfigurationSection root, String path, int def) {
      if (root == null) {
         return def;
      } else if (root.contains(path + ".slot")) {
         return root.getInt(path + ".slot", def);
      } else {
         return root.contains(path + "-slot") ? root.getInt(path + "-slot", def) : def;
      }
   }

   public static Map<String, String> map(String... kv) {
      Map<String, String> map = new HashMap();

      for(int i = 0; i + 1 < kv.length; i += 2) {
         map.put(kv[i], kv[i + 1]);
      }

      return map;
   }
}
