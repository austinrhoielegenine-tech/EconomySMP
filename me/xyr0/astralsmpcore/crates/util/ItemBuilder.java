package me.xyr0.astralsmpcore.crates.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.xyr0.astralsmpcore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public final class ItemBuilder {
   private ItemBuilder() {
   }

   public static ItemStack fromSection(ConfigurationSection sec) {
      if (sec == null) {
         return new ItemStack(Material.STONE);
      } else {
         Material mat = Material.STONE;

         try {
            mat = Material.valueOf(sec.getString("material", "STONE").toUpperCase(Locale.ROOT));
         } catch (Exception var10) {
         }

         ItemStack item = new ItemStack(mat);
         ItemMeta meta = item.getItemMeta();
         if (meta == null) {
            return item;
         } else {
            if (sec.contains("name")) {
               meta.displayName(ColorUtil.parse(sec.getString("name")));
            }

            if (sec.isList("lore")) {
               List<Component> lore = new ArrayList();

               for(String line : sec.getStringList("lore")) {
                  lore.add(ColorUtil.parse(line));
               }

               meta.lore(lore);
            }

            ConfigurationSection ench = sec.getConfigurationSection("enchantments");
            if (ench != null) {
               for(String key : ench.getKeys(false)) {
                  Enchantment e = matchEnchant(key);
                  if (e != null) {
                     meta.addEnchant(e, ench.getInt(key), true);
                  }
               }
            }

            ConfigurationSection trim = sec.getConfigurationSection("trim");
            if (trim != null && meta instanceof ArmorMeta) {
               ArmorMeta armorMeta = (ArmorMeta)meta;

               try {
                  TrimMaterial tm = (TrimMaterial)Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(trim.getString("material", "emerald").toLowerCase(Locale.ROOT)));
                  TrimPattern tp = (TrimPattern)Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(trim.getString("pattern", "wild").toLowerCase(Locale.ROOT)));
                  if (tm != null && tp != null) {
                     armorMeta.setTrim(new ArmorTrim(tm, tp));
                  }
               } catch (Exception var9) {
               }
            }

            item.setItemMeta(meta);
            return item;
         }
      }
   }

   private static Enchantment matchEnchant(String key) {
      String k = key.toLowerCase(Locale.ROOT).replace(" ", "_");

      for(Enchantment e : Registry.ENCHANTMENT) {
         String keyName = e.getKey().getKey();
         if (keyName.equalsIgnoreCase(k) || keyName.replace("_", "").equalsIgnoreCase(k.replace("_", ""))) {
            return e;
         }
      }

      try {
         return Enchantment.getByName(k.toUpperCase(Locale.ROOT));
      } catch (Exception var5) {
         return null;
      }
   }
}
