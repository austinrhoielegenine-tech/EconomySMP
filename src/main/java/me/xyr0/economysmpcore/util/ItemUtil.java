package me.xyr0.economysmpcore.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtil {
   private ItemUtil() {
   }

   public static ItemStack named(Material material, String name, List<String> lore) {
      ItemStack item = new ItemStack(material);
      ItemMeta meta = item.getItemMeta();
      if (meta == null) {
         return item;
      } else {
         meta.displayName(ColorUtil.parse(name));
         if (lore != null) {
            List<Component> lines = new ArrayList(lore.size());

            for(String line : lore) {
               lines.add(ColorUtil.parse(line));
            }

            meta.lore(lines);
         }

         item.setItemMeta(meta);
         return item;
      }
   }

   public static String pretty(Material material) {
      if (material == null) {
         return "Air";
      } else {
         String[] words = material.name().toLowerCase(Locale.ROOT).split("_");
         StringBuilder name = new StringBuilder();

         for(String word : words) {
            if (!word.isEmpty()) {
               if (name.length() > 0) {
                  name.append(' ');
               }

               name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
         }

         return name.toString();
      }
   }

   public static String pretty(ItemStack item) {
      if (item != null && item.getType() != Material.AIR) {
         ItemMeta meta = item.getItemMeta();
         return meta != null && meta.hasDisplayName() && meta.displayName() != null ? PlainTextComponentSerializer.plainText().serialize(meta.displayName()) : pretty(item.getType());
      } else {
         return "Air";
      }
   }

   public static boolean matches(ItemStack stack, ItemStack template, boolean materialOnly) {
      if (stack != null && template != null && stack.getType() != Material.AIR) {
         return materialOnly ? stack.getType() == template.getType() : stack.isSimilar(template);
      } else {
         return false;
      }
   }

   public static int countMatching(Player player, ItemStack template, boolean materialOnly) {
      int total = 0;

      for(ItemStack stack : player.getInventory().getContents()) {
         if (matches(stack, template, materialOnly)) {
            total += stack.getAmount();
         }
      }

      return total;
   }

   public static int takeMatching(Player player, ItemStack template, int amount, boolean materialOnly) {
      if (amount <= 0) {
         return 0;
      } else {
         int left = amount;
         ItemStack[] contents = player.getInventory().getContents();

         for(int i = 0; i < contents.length && left > 0; ++i) {
            ItemStack stack = contents[i];
            if (matches(stack, template, materialOnly)) {
               int take = Math.min(left, stack.getAmount());
               int next = stack.getAmount() - take;
               if (next == 0) {
                  contents[i] = null;
               } else {
                  stack.setAmount(next);
               }

               left -= take;
            }
         }

         player.getInventory().setContents(contents);
         return amount - left;
      }
   }
}
