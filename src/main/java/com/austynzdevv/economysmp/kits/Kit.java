package com.austynzdevv.economysmp.kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.austynzdevv.economysmp.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {
   private final String id;
   private String displayName;
   private long cooldownSeconds;
   private String permission;
   private boolean permissionRequired;
   private int slot;
   private final List<ItemStack> items = new ArrayList();
   private DisplayState ready;
   private DisplayState cooldown;
   private DisplayState noPermission;

   public Kit(String id) {
      this.id = id.toLowerCase();
      this.displayName = id;
      this.cooldownSeconds = 0L;
      this.permission = "economysmpcore.kit." + this.id;
      this.permissionRequired = false;
      this.slot = -1;
   }

   public static Kit fromConfig(String id, YamlConfiguration cfg) {
      Kit kit = new Kit(id);
      kit.displayName = cfg.getString("display-name", id);
      kit.cooldownSeconds = cfg.getLong("cooldown", 0L);
      kit.permission = cfg.getString("permission", "economysmpcore.kit." + id);
      kit.permissionRequired = cfg.getBoolean("permission-required", false);
      kit.slot = cfg.getInt("slot", -1);
      kit.ready = readDisplay(cfg.getConfigurationSection("display.ready"));
      kit.cooldown = readDisplay(cfg.getConfigurationSection("display.cooldown"));
      kit.noPermission = readDisplay(cfg.getConfigurationSection("display.no-permission"));
      List<?> raw = cfg.getList("items");
      if (raw != null) {
         for(Object o : raw) {
            ItemStack is = parseItem(o);
            if (is != null) {
               kit.items.add(is);
            }
         }
      }

      return kit;
   }

   private static DisplayState readDisplay(ConfigurationSection sec) {
      DisplayState d = new DisplayState();
      if (sec == null) {
         return d;
      } else {
         try {
            Material m = Material.matchMaterial(sec.getString("material", "CHEST"));
            if (m != null) {
               d.material = m;
            }
         } catch (Exception var3) {
         }

         d.name = sec.getString("name", "");
         d.lore = sec.getStringList("lore");
         d.glow = sec.getBoolean("glow", false);
         return d;
      }
   }

   private static ItemStack parseItem(Object o) {
      if (o instanceof ItemStack is) {
         return is.clone();
      } else if (o instanceof ConfigurationSection sec) {
         try {
            Material mat = Material.matchMaterial(sec.getString("material", "STONE"));
            return mat == null ? null : new ItemStack(mat, Math.max(1, sec.getInt("amount", 1)));
         } catch (Exception var7) {
            return null;
         }
      } else if (o instanceof Map<?, ?> map) {
         try {
            Object matObj = map.get("material");
            if (matObj == null) {
               return null;
            } else {
               Material mat = Material.matchMaterial(String.valueOf(matObj));
               if (mat == null) {
                  return null;
               } else {
                  int amount = 1;
                  Object a = map.get("amount");
                  if (a instanceof Number) {
                     Number n = (Number)a;
                     amount = Math.max(1, n.intValue());
                  }

                  return new ItemStack(mat, amount);
               }
            }
         } catch (Exception var8) {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean canClaim(Player player) {
      if (!this.permissionRequired) {
         return true;
      } else {
         return player.hasPermission(this.permission) || player.hasPermission("economysmpcore.kit.*") || player.isOp();
      }
   }

   public boolean give(Player player) {
      if (!this.canClaim(player)) {
         return false;
      } else {
         int empty = 0;

         for(ItemStack s : player.getInventory().getStorageContents()) {
            if (s == null || s.getType() == Material.AIR) {
               ++empty;
            }
         }

         if (empty < this.items.size()) {
            return false;
         } else {
            for(ItemStack item : this.items) {
               if (item != null && item.getType() != Material.AIR) {
                  player.getInventory().addItem(new ItemStack[]{item.clone()});
               }
            }

            return true;
         }
      }
   }

   public ItemStack buildIcon(Player player, long remainingMs) {
      DisplayState state;
      if (!this.canClaim(player)) {
         state = this.noPermission != null ? this.noPermission : this.ready;
      } else if (remainingMs > 0L) {
         state = this.cooldown != null ? this.cooldown : this.ready;
      } else {
         state = this.ready != null ? this.ready : new DisplayState();
      }

      if (state == null) {
         state = new DisplayState();
      }

      ItemStack icon = new ItemStack(state.material != null ? state.material : Material.CHEST);
      ItemMeta meta = icon.getItemMeta();
      if (meta != null) {
         String name = state.name != null && !state.name.isEmpty() ? state.name : this.displayName;
         meta.setDisplayName(TextUtil.color(name));
         List<String> lore = new ArrayList();
         String cd = formatTime(Math.max(0L, (remainingMs + 999L) / 1000L));
         if (state.lore != null) {
            for(String line : state.lore) {
               lore.add(TextUtil.color(line.replace("%cooldown%", cd).replace("{cooldown_formatted}", cd)));
            }
         }

         meta.setLore(lore);
         if (state.glow) {
            Enchantment glowEnc = null;

            try {
               glowEnc = Enchantment.getByName("DURABILITY");
               if (glowEnc == null) {
                  glowEnc = Enchantment.getByName("UNBREAKING");
               }
            } catch (Exception var12) {
            }

            if (glowEnc != null) {
               meta.addEnchant(glowEnc, 1, true);
               meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
         }

         icon.setItemMeta(meta);
      }

      return icon;
   }

   private static String formatTime(long totalSeconds) {
      long h = totalSeconds / 3600L;
      long m = totalSeconds % 3600L / 60L;
      long s = totalSeconds % 60L;
      if (h > 0L) {
         return h + "h " + m + "m";
      } else {
         return m > 0L ? m + "m " + s + "s" : s + "s";
      }
   }

   public String getId() {
      return this.id;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public long getCooldownSeconds() {
      return this.cooldownSeconds;
   }

   public String getPermission() {
      return this.permission;
   }

   public boolean isPermissionRequired() {
      return this.permissionRequired;
   }

   public int getSlot() {
      return this.slot;
   }

   public List<ItemStack> getItems() {
      return this.items;
   }

   public void setSlot(int slot) {
      this.slot = slot;
   }

   public void setCooldownSeconds(long seconds) {
      this.cooldownSeconds = Math.max(0L, seconds);
   }

   public void setPermission(String permission) {
      this.permission = permission != null && !permission.isBlank() ? permission : "economysmpcore.kit." + this.id;
      this.permissionRequired = !this.permission.equalsIgnoreCase("none") && !this.permission.equalsIgnoreCase("false");
   }

   public void setItems(List<ItemStack> contents) {
      this.items.clear();
      if (contents != null) {
         for(ItemStack item : contents) {
            if (item != null && !item.getType().isAir()) {
               this.items.add(item.clone());
            }
         }
      }

   }

   public static class DisplayState {
      public Material material;
      public String name;
      public List<String> lore;
      public boolean glow;

      public DisplayState() {
         this.material = Material.CHEST;
         this.name = "";
         this.lore = new ArrayList();
      }
   }
}
