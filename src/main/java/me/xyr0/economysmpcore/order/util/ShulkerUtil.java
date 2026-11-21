package me.xyr0.economysmpcore.order.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class ShulkerUtil {
   private ShulkerUtil() {
   }

   public static boolean isShulker(ItemStack stack) {
      return stack != null && stack.getType().name().endsWith("SHULKER_BOX");
   }

   public static int countMatching(ItemStack stack, Material target, boolean materialOnly, ItemStack template) {
      if (stack != null && stack.getType() != Material.AIR) {
         if (isShulker(stack)) {
            int total = 0;
            ItemStack[] contents = readContents(stack);

            for(ItemStack inside : contents) {
               if (inside != null && matches(inside, target, materialOnly, template)) {
                  total += inside.getAmount();
               }
            }

            return total;
         } else {
            return matches(stack, target, materialOnly, template) ? stack.getAmount() : 0;
         }
      } else {
         return 0;
      }
   }

   public static int takeMatching(ItemStack stack, Material target, boolean materialOnly, ItemStack template, int need) {
      if (need > 0 && stack != null) {
         if (!isShulker(stack)) {
            if (!matches(stack, target, materialOnly, template)) {
               return 0;
            } else {
               int take = Math.min(need, stack.getAmount());
               stack.setAmount(stack.getAmount() - take);
               return take;
            }
         } else {
            ItemStack[] contents = readContents(stack);
            int taken = 0;

            for(int i = 0; i < contents.length && taken < need; ++i) {
               ItemStack inside = contents[i];
               if (inside != null && matches(inside, target, materialOnly, template)) {
                  int take = Math.min(need - taken, inside.getAmount());
                  int left = inside.getAmount() - take;
                  if (left <= 0) {
                     contents[i] = null;
                  } else {
                     inside.setAmount(left);
                  }

                  taken += take;
               }
            }

            writeContents(stack, contents);
            return taken;
         }
      } else {
         return 0;
      }
   }

   private static boolean matches(ItemStack stack, Material target, boolean materialOnly, ItemStack template) {
      if (stack.getType() != target) {
         return false;
      } else if (materialOnly) {
         return true;
      } else {
         return template == null || stack.isSimilar(template);
      }
   }

   private static ItemStack[] readContents(ItemStack shulker) {
      ItemMeta var2 = shulker.getItemMeta();
      if (var2 instanceof BlockStateMeta meta) {
         BlockState var3 = meta.getBlockState();
         if (var3 instanceof ShulkerBox box) {
            ItemStack[] contents = box.getInventory().getContents();
            ItemStack[] copy = new ItemStack[contents.length];

            for(int i = 0; i < contents.length; ++i) {
               copy[i] = contents[i] == null ? null : contents[i].clone();
            }

            return copy;
         } else {
            return new ItemStack[0];
         }
      } else {
         return new ItemStack[0];
      }
   }

   private static void writeContents(ItemStack shulker, ItemStack[] contents) {
      ItemMeta var3 = shulker.getItemMeta();
      if (var3 instanceof BlockStateMeta meta) {
         BlockState var4 = meta.getBlockState();
         if (var4 instanceof ShulkerBox box) {
            box.getInventory().setContents(contents);
            meta.setBlockState(box);
            shulker.setItemMeta(meta);
         }
      }
   }

   public static List<ItemStack> flattenForDisplay(ItemStack[] contents, Material target, boolean materialOnly, ItemStack template) {
      List<ItemStack> list = new ArrayList();

      for(ItemStack stack : contents) {
         if (stack != null) {
            if (isShulker(stack)) {
               for(ItemStack inside : readContents(stack)) {
                  if (inside != null && matches(inside, target, materialOnly, template)) {
                     list.add(inside.clone());
                  }
               }
            } else if (matches(stack, target, materialOnly, template)) {
               list.add(stack.clone());
            }
         }
      }

      return list;
   }
}
