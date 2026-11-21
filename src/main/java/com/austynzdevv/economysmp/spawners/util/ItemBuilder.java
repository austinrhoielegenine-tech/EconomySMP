package com.austynzdevv.economysmp.spawners.util;

import java.util.ArrayList;
import java.util.List;
import com.austynzdevv.economysmp.spawners.EconomySpawners;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class ItemBuilder {
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().hexColors().character('&').build();
   private static NamespacedKey TYPE_KEY;
   private static NamespacedKey STACK_KEY;

   private ItemBuilder() {
   }

   private static NamespacedKey typeKey() {
      if (TYPE_KEY == null) {
         TYPE_KEY = new NamespacedKey(EconomySpawners.get(), "spawner_type");
      }

      return TYPE_KEY;
   }

   private static NamespacedKey stackKey() {
      if (STACK_KEY == null) {
         STACK_KEY = new NamespacedKey(EconomySpawners.get(), "spawner_stack");
      }

      return STACK_KEY;
   }

   private static Component plain(String text) {
      return LEGACY.deserialize(text).decoration(TextDecoration.ITALIC, false);
   }

   public static ItemStack createSpawner(EntityType type, int stack) {
      ItemStack item = new ItemStack(Material.SPAWNER);
      ItemMeta meta = item.getItemMeta();
      if (meta == null) {
         return item;
      } else {
         String display = EconomySpawners.get().getEntities().get(type) != null ? EconomySpawners.get().getEntities().get(type).getDisplay() : type.name();
         String name = display;
         if (stack > 1) {
            name = "&#1FFD98" + stack + "x &r" + display;
         }

         meta.displayName(plain(name));
         List<Component> lore = new ArrayList();
         lore.add(plain("&7Type: &#1FFD98" + formatType(type)));
         if (stack > 1) {
            lore.add(plain("&7Stack: &#1FFD98" + stack));
         }

         meta.lore(lore);
         meta.getPersistentDataContainer().set(typeKey(), PersistentDataType.STRING, type.name());
         meta.getPersistentDataContainer().set(stackKey(), PersistentDataType.INTEGER, Math.max(1, stack));
         item.setItemMeta(meta);
         return item;
      }
   }

   public static ItemStack createSpawnerUnit(EntityType type) {
      return createSpawner(type, 1);
   }

   public static List<ItemStack> createSpawnerStacks(EntityType type, int totalUnits) {
      List<ItemStack> list = new ArrayList();

      int amount;
      for(int left = Math.max(1, totalUnits); left > 0; left -= amount) {
         amount = Math.min(64, left);
         ItemStack item = createSpawnerUnit(type);
         item.setAmount(amount);
         list.add(item);
      }

      return list;
   }

   private static String formatType(EntityType type) {
      String raw = type.name().toLowerCase().replace('_', ' ');
      String[] parts = raw.split(" ");
      StringBuilder sb = new StringBuilder();

      for(String p : parts) {
         if (!p.isEmpty()) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
         }
      }

      return sb.toString();
   }

   public static EntityType getType(ItemStack item) {
      if (item != null && item.hasItemMeta()) {
         String raw = (String)item.getItemMeta().getPersistentDataContainer().get(typeKey(), PersistentDataType.STRING);
         if (raw == null) {
            return null;
         } else {
            try {
               return EntityType.valueOf(raw);
            } catch (IllegalArgumentException var3) {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   public static int getStack(ItemStack item) {
      if (item != null && item.hasItemMeta()) {
         Integer val = (Integer)item.getItemMeta().getPersistentDataContainer().get(stackKey(), PersistentDataType.INTEGER);
         return val == null ? 1 : Math.max(1, val);
      } else {
         return 1;
      }
   }

   public static boolean isEconomySpawner(ItemStack item) {
      return item != null && item.getType() == Material.SPAWNER && getType(item) != null;
   }

   public static int totalSpawnerUnits(ItemStack item) {
      return !isEconomySpawner(item) ? 0 : getStack(item) * Math.max(1, item.getAmount());
   }

   public static ItemStack named(Material mat, String name, List<String> loreLines) {
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta == null) {
         return item;
      } else {
         meta.displayName(plain(name));
         if (loreLines != null && !loreLines.isEmpty()) {
            List<Component> lore = new ArrayList();

            for(String line : loreLines) {
               lore.add(plain(line));
            }

            meta.lore(lore);
         }

         item.setItemMeta(meta);
         return item;
      }
   }
}
