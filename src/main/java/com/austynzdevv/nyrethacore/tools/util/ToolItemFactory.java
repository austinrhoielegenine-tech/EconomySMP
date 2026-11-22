package com.austynzdevv.nyrethacore.tools.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.austynzdevv.nyrethacore.tools.EconomyTools;
import com.austynzdevv.nyrethacore.tools.ToolDefinition;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ToolItemFactory {
   private final EconomyTools plugin;
   private final NamespacedKey keyType;
   private final NamespacedKey keyUses;
   private final NamespacedKey keyExpire;

   public ToolItemFactory(EconomyTools plugin) {
      this.plugin = plugin;
      this.keyType = new NamespacedKey(plugin, "tool_type");
      this.keyUses = new NamespacedKey(plugin, "tool_uses");
      this.keyExpire = new NamespacedKey(plugin, "tool_expire");
   }

   public ItemStack create(ToolDefinition def) {
      ItemStack item = new ItemStack(def.getMaterial());
      ItemMeta meta = item.getItemMeta();
      if (meta == null) {
         return item;
      } else {
         meta.displayName(ColorUtil.parse(def.getName()));
         boolean firework = item.getType() == Material.FIREWORK_ROCKET;
         if (def.getMode() == ToolDefinition.DurabilityMode.INFINITE && !firework) {
            meta.setUnbreakable(true);
         }

         if (def.getCustomModelData() > 0) {
            meta.setCustomModelData(def.getCustomModelData());
         }

         if (!firework) {
            for(Map.Entry<Enchantment, Integer> e : def.getEnchants().entrySet()) {
               meta.addEnchant((Enchantment)e.getKey(), (Integer)e.getValue(), true);
            }

            meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         }

         long expire = 0L;
         int uses = def.getUses();
         if (def.getMode() == ToolDefinition.DurabilityMode.TIME) {
            expire = System.currentTimeMillis() + def.getDurationMs();
         }

         meta.getPersistentDataContainer().set(this.keyType, PersistentDataType.STRING, def.getId());
         meta.getPersistentDataContainer().set(this.keyUses, PersistentDataType.INTEGER, uses);
         meta.getPersistentDataContainer().set(this.keyExpire, PersistentDataType.LONG, expire);
         if (meta instanceof FireworkMeta) {
            FireworkMeta fm = (FireworkMeta)meta;
            int power = Math.max(1, Math.min(3, def.getRocketDuration() <= 0 ? 1 : def.getRocketDuration()));
            fm.setPower(power);
         }

         meta.lore(this.buildLore(def, uses, expire));
         item.setItemMeta(meta);
         return item;
      }
   }

   public void applyDefinition(ItemStack item, ToolDefinition def) {
      if (item != null && !item.getType().isAir()) {
         ItemMeta meta = item.getItemMeta();
         if (meta != null) {
            FileConfiguration settings = this.plugin.getSettingsConfig();
            boolean changeName = settings == null || settings.getBoolean("add.change-name", true);
            boolean changeLore = settings == null || settings.getBoolean("add.change-lore", true);
            boolean changeEffects = settings == null || settings.getBoolean("add.change-effects", true);
            if (changeName) {
               meta.displayName(ColorUtil.parse(def.getName()));
            }

            if (changeLore) {
               int uses = def.getUses();
               long expire = def.getMode() == ToolDefinition.DurabilityMode.TIME ? System.currentTimeMillis() + def.getDurationMs() : 0L;
               meta.lore(this.buildLore(def, uses, expire));
            }

            if (changeEffects) {
               boolean firework = item.getType() == Material.FIREWORK_ROCKET;
               if (def.getMode() == ToolDefinition.DurabilityMode.INFINITE && !firework) {
                  meta.setUnbreakable(true);
               }

               if (def.getCustomModelData() > 0) {
                  meta.setCustomModelData(def.getCustomModelData());
               }

               if (!firework) {
                  for(Map.Entry<Enchantment, Integer> e : def.getEnchants().entrySet()) {
                     meta.addEnchant((Enchantment)e.getKey(), (Integer)e.getValue(), true);
                  }

                  meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
               }

               if (meta instanceof FireworkMeta) {
                  FireworkMeta fm = (FireworkMeta)meta;
                  fm.setPower(Math.max(1, Math.min(3, def.getRocketDuration() <= 0 ? 1 : def.getRocketDuration())));
               }
            }

            meta.getPersistentDataContainer().set(this.keyType, PersistentDataType.STRING, def.getId());
            meta.getPersistentDataContainer().set(this.keyUses, PersistentDataType.INTEGER, def.getUses());
            long expire = def.getMode() == ToolDefinition.DurabilityMode.TIME ? System.currentTimeMillis() + def.getDurationMs() : 0L;
            meta.getPersistentDataContainer().set(this.keyExpire, PersistentDataType.LONG, expire);
            item.setItemMeta(meta);
         }
      }
   }

   public String getType(ItemStack item) {
      return item != null && item.hasItemMeta() ? (String)item.getItemMeta().getPersistentDataContainer().get(this.keyType, PersistentDataType.STRING) : null;
   }

   public boolean isTool(ItemStack item) {
      return this.getType(item) != null;
   }

   public boolean isExpired(ItemStack item, ToolDefinition def) {
      if (def.getMode() != ToolDefinition.DurabilityMode.TIME) {
         return false;
      } else {
         Long exp = (Long)item.getItemMeta().getPersistentDataContainer().get(this.keyExpire, PersistentDataType.LONG);
         return exp != null && exp > 0L && System.currentTimeMillis() > exp;
      }
   }

   public boolean consumeUse(ItemStack item, ToolDefinition def) {
      if (def.getMode() == ToolDefinition.DurabilityMode.INFINITE) {
         return true;
      } else if (def.getMode() == ToolDefinition.DurabilityMode.TIME) {
         return !this.isExpired(item, def);
      } else {
         ItemMeta meta = item.getItemMeta();
         Integer uses = (Integer)meta.getPersistentDataContainer().get(this.keyUses, PersistentDataType.INTEGER);
         if (uses == null) {
            uses = 0;
         }

         --uses;
         if (uses < 0) {
            return false;
         } else {
            meta.getPersistentDataContainer().set(this.keyUses, PersistentDataType.INTEGER, uses);
            Long exp = (Long)meta.getPersistentDataContainer().get(this.keyExpire, PersistentDataType.LONG);
            meta.lore(this.buildLore(def, uses, exp == null ? 0L : exp));
            item.setItemMeta(meta);
            return uses >= 0;
         }
      }
   }

   public void refreshLore(ItemStack item, ToolDefinition def) {
      ItemMeta meta = item.getItemMeta();
      Integer uses = (Integer)meta.getPersistentDataContainer().get(this.keyUses, PersistentDataType.INTEGER);
      Long exp = (Long)meta.getPersistentDataContainer().get(this.keyExpire, PersistentDataType.LONG);
      meta.lore(this.buildLore(def, uses == null ? 0 : uses, exp == null ? 0L : exp));
      item.setItemMeta(meta);
   }

   private List<Component> buildLore(ToolDefinition def, int uses, long expire) {
      String dur;
      switch (def.getMode()) {
         case INFINITE:
            dur = "Infinite";
            break;
         case USES:
            dur = uses + " uses";
            break;
         case TIME:
            long left = Math.max(0L, expire - System.currentTimeMillis());
            long days = TimeUnit.MILLISECONDS.toDays(left);
            long hours = TimeUnit.MILLISECONDS.toHours(left) % 24L;
            long mins = TimeUnit.MILLISECONDS.toMinutes(left) % 60L;
            dur = days + "d " + hours + "h " + mins + "m";
            break;
         default:
            dur = "?";
      }

      List<Component> lore = new ArrayList();

      for(String line : def.getLore()) {
         lore.add(ColorUtil.parse(line.replace("%durability%", dur).replace("%distance%", String.valueOf(def.getJumpDistance())).replace("%cooldown%", String.valueOf(def.getJumpCooldown()))));
      }

      return lore;
   }

   public NamespacedKey getKeyType() {
      return this.keyType;
   }
}
