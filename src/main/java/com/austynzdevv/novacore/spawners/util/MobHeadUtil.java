package com.austynzdevv.novacore.spawners.util;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public final class MobHeadUtil {
   private static final Map<EntityType, Material> HEADS = new EnumMap(EntityType.class);

   private MobHeadUtil() {
   }

   public static ItemStack headFor(EntityType type, String name, List<String> lore) {
      Material mat = (Material)HEADS.get(type);
      if (mat == null) {
         String egg = type.name() + "_SPAWN_EGG";

         try {
            mat = Material.valueOf(egg);
         } catch (IllegalArgumentException var6) {
            mat = Material.PLAYER_HEAD;
         }
      }

      return ItemBuilder.named(mat, name, lore);
   }

   public static Material materialFor(EntityType type) {
      Material mat = (Material)HEADS.get(type);
      if (mat != null) {
         return mat;
      } else {
         try {
            return Material.valueOf(type.name() + "_SPAWN_EGG");
         } catch (IllegalArgumentException var3) {
            return Material.PLAYER_HEAD;
         }
      }
   }

   static {
      HEADS.put(EntityType.SKELETON, Material.SKELETON_SKULL);
      HEADS.put(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SKULL);
      HEADS.put(EntityType.ZOMBIE, Material.ZOMBIE_HEAD);
      HEADS.put(EntityType.CREEPER, Material.CREEPER_HEAD);
      HEADS.put(EntityType.PIGLIN, Material.PIGLIN_HEAD);
      HEADS.put(EntityType.ENDER_DRAGON, Material.DRAGON_HEAD);
      HEADS.put(EntityType.PLAYER, Material.PLAYER_HEAD);
   }
}
