package me.xyr0.astralsmpcore.sell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import me.xyr0.astralsmpcore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SellAxeManager {
   private final AstralSell plugin;
   private FileConfiguration cfg;
   private final NamespacedKey keyExpire;
   private final NamespacedKey keyMarker;

   public SellAxeManager(AstralSell plugin) {
      this.plugin = plugin;
      this.keyExpire = new NamespacedKey("astralsmpcore", "sellaxe_expire");
      this.keyMarker = new NamespacedKey("astralsmpcore", "sellaxe");
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "sellaxe.yml");
      if (!file.exists()) {
         this.plugin.saveResource("sellaxe.yml", false);
      }

      this.cfg = YamlConfiguration.loadConfiguration(file);
   }

   public ItemStack create(long durationMs) {
      Material mat = Material.matchMaterial(this.cfg.getString("material", "NETHERITE_AXE"));
      if (mat == null) {
         mat = Material.NETHERITE_AXE;
      }

      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta == null) {
         return item;
      } else {
         meta.displayName(ColorUtil.parse(this.cfg.getString("name", "&#A20AD6Sell Axe")));
         int cmd = this.cfg.getInt("custom-model-data", 0);
         if (cmd > 0) {
            meta.setCustomModelData(cmd);
         }

         for(String line : this.cfg.getStringList("enchantments")) {
            String[] p = line.split(":");
            if (p.length >= 1) {
               Enchantment ench = Enchantment.getByName(p[0].toUpperCase(Locale.ROOT));
               if (ench != null) {
                  int lvl = 1;

                  try {
                     if (p.length > 1) {
                        lvl = Integer.parseInt(p[1]);
                     }
                  } catch (Exception var13) {
                  }

                  meta.addEnchant(ench, lvl, true);
               }
            }
         }

         meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         long expire = System.currentTimeMillis() + Math.max(1000L, durationMs);
         meta.getPersistentDataContainer().set(this.keyMarker, PersistentDataType.BYTE, (byte)1);
         meta.getPersistentDataContainer().set(this.keyExpire, PersistentDataType.LONG, expire);
         meta.lore(this.buildLore(expire));
         item.setItemMeta(meta);
         return item;
      }
   }

   public boolean isSellAxe(ItemStack item) {
      return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(this.keyMarker, PersistentDataType.BYTE);
   }

   public boolean isExpired(ItemStack item) {
      if (!this.isSellAxe(item)) {
         return false;
      } else {
         Long exp = (Long)item.getItemMeta().getPersistentDataContainer().get(this.keyExpire, PersistentDataType.LONG);
         return exp != null && System.currentTimeMillis() > exp;
      }
   }

   public boolean updateLore(ItemStack item) {
      if (!this.isSellAxe(item)) {
         return false;
      } else {
         ItemMeta meta = item.getItemMeta();
         if (meta == null) {
            return false;
         } else {
            Long exp = (Long)meta.getPersistentDataContainer().get(this.keyExpire, PersistentDataType.LONG);
            if (exp != null && System.currentTimeMillis() <= exp) {
               meta.lore(this.buildLore(exp));
               item.setItemMeta(meta);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private List<Component> buildLore(long expire) {
      String time = formatLeft(expire - System.currentTimeMillis());
      List<Component> lore = new ArrayList();

      for(String line : this.cfg.getStringList("lore")) {
         lore.add(ColorUtil.parse(line.replace("%time%", time)));
      }

      return lore;
   }

   public static String formatLeft(long ms) {
      if (ms < 0L) {
         ms = 0L;
      }

      long days = TimeUnit.MILLISECONDS.toDays(ms);
      long hours = TimeUnit.MILLISECONDS.toHours(ms) % 24L;
      long mins = TimeUnit.MILLISECONDS.toMinutes(ms) % 60L;
      return days + "d " + hours + "h " + mins + "m";
   }

   public static long parseDuration(String raw) {
      if (raw != null && !raw.isEmpty()) {
         raw = raw.trim().toLowerCase(Locale.ROOT);
         long total = 0L;
         StringBuilder num = new StringBuilder();

         for(int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (Character.isDigit(c)) {
               num.append(c);
            } else if ("dhms".indexOf(c) >= 0 && num.length() > 0) {
               long n = Long.parseLong(num.toString());
               num.setLength(0);
               long var10001;
               switch (c) {
                  case 'd' -> var10001 = TimeUnit.DAYS.toMillis(n);
                  case 'h' -> var10001 = TimeUnit.HOURS.toMillis(n);
                  case 'm' -> var10001 = TimeUnit.MINUTES.toMillis(n);
                  case 's' -> var10001 = TimeUnit.SECONDS.toMillis(n);
                  default -> var10001 = 0L;
               }

               total += var10001;
            }
         }

         if (num.length() > 0) {
            total += TimeUnit.SECONDS.toMillis(Long.parseLong(num.toString()));
         }

         return total <= 0L ? TimeUnit.DAYS.toMillis(1L) : total;
      } else {
         return TimeUnit.DAYS.toMillis(1L);
      }
   }

   public FileConfiguration getConfig() {
      return this.cfg;
   }
}
