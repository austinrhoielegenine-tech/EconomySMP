package com.austynzdevv.nyrethacore.auction.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GuiItems {
   private GuiItems() {
   }

   public static ItemStack fromSection(ConfigurationSection section, Map<String, String> ph) {
      if (section == null) {
         return new ItemStack(Material.STONE);
      } else {
         String matName = section.getString("material", "STONE");
         if (ph != null) {
            for(Map.Entry<String, String> e : ph.entrySet()) {
               matName = matName.replace("%" + (String)e.getKey() + "%", (CharSequence)e.getValue());
            }
         }

         Material mat;
         try {
            mat = Material.valueOf(matName.toUpperCase());
         } catch (Exception var9) {
            mat = Material.STONE;
         }

         String name = section.getString("name", section.getString("displayname", " "));
         List<String> lore = section.getStringList("lore");
         if (ph != null) {
            name = apply(name, ph);
            List<String> out = new ArrayList();

            for(String line : lore) {
               out.add(apply(line, ph));
            }

            lore = out;
         }

         return ItemUtil.named(mat, name, lore);
      }
   }

   public static ItemStack withLore(ItemStack base, String name, List<String> lore) {
      if (base == null || base.getType().isAir()) {
         base = new ItemStack(Material.BARRIER);
      }

      ItemStack item = base.clone();
      if (item.getAmount() < 1) {
         item.setAmount(1);
      }

      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         if (name != null && !name.isBlank()) {
            meta.displayName(ColorUtil.parse(name));
         }

         if (lore != null && !lore.isEmpty()) {
            List<Component> existing = meta.lore();
            List<Component> lines = new ArrayList();
            if (existing != null) {
               lines.addAll(existing);
            }

            for(String line : lore) {
               if (line != null) {
                  lines.add(ColorUtil.parse(line));
               }
            }

            meta.lore(lines);
         }

         item.setItemMeta(meta);
      }

      return item;
   }

   private static String apply(String text, Map<String, String> ph) {
      if (text == null) {
         return "";
      } else {
         String out = text;

         for(Map.Entry<String, String> e : ph.entrySet()) {
            out = out.replace("%" + (String)e.getKey() + "%", (CharSequence)(e.getValue() == null ? "" : (CharSequence)e.getValue()));
         }

         return out;
      }
   }

   public static Map<String, String> map(String... kv) {
      Map<String, String> m = new HashMap();

      for(int i = 0; i + 1 < kv.length; i += 2) {
         m.put(kv[i], kv[i + 1]);
      }

      return m;
   }
}
