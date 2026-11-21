package com.austynzdevv.economysmp.sell;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class PriceManager {
   private final EconomySell plugin;
   private final Map<Material, Double> prices = new EnumMap(Material.class);
   private File file;
   private FileConfiguration cfg;
   private double pricesMultiplier = (double)1.0F;
   private double defaultPrice = (double)1.0F;

   public PriceManager(EconomySell plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.prices.clear();
      this.pricesMultiplier = this.plugin.getConfig().getDouble("prices-multiplier", (double)1.0F);
      this.defaultPrice = this.plugin.getConfig().getDouble("default-price", (double)1.0F);
      this.file = new File(this.plugin.getDataFolder(), "prices.yml");
      if (!this.file.exists()) {
         this.plugin.saveResource("prices.yml", false);
      }

      this.cfg = YamlConfiguration.loadConfiguration(this.file);

      for(String key : this.cfg.getKeys(false)) {
         if (!key.startsWith("#")) {
            try {
               Material mat = Material.valueOf(key.toUpperCase());
               this.prices.put(mat, this.cfg.getDouble(key));
            } catch (Exception var15) {
            }
         }
      }

      File folder = new File(this.plugin.getDataFolder(), "prices");
      if (!folder.exists()) {
         folder.mkdirs();
         new File(this.plugin.getDataFolder(), "prices");
         String[] names = new String[]{"blocks.yml", "books.yml", "crops.yml", "fish.yml", "mobs.yml", "natural.yml", "ores.yml", "potions.yml", "tools.yml"};

         for(String name : names) {
            try {
               this.plugin.saveResource("prices/" + name, false);
            } catch (Exception var14) {
            }
         }
      }

      if (folder.isDirectory()) {
         File[] files = folder.listFiles((d, n) -> n.endsWith(".yml"));
         if (files != null) {
            for(File f : files) {
               FileConfiguration sub = YamlConfiguration.loadConfiguration(f);
               ConfigurationSection sec = sub.getConfigurationSection("prices");
               if (sec != null) {
                  for(String key : sec.getKeys(false)) {
                     try {
                        Material mat = Material.valueOf(key.toUpperCase());
                        this.prices.put(mat, sec.getDouble(key));
                     } catch (Exception var13) {
                     }
                  }
               } else {
                  for(String key : sub.getKeys(false)) {
                     try {
                        Material mat = Material.valueOf(key.toUpperCase());
                        this.prices.put(mat, sub.getDouble(key));
                     } catch (Exception var12) {
                     }
                  }
               }
            }
         }
      }

   }

   public double getBase(Material material) {
      return (Double)this.prices.getOrDefault(material, this.defaultPrice) * this.pricesMultiplier;
   }

   public double getPlayerMultiplier(Player player) {
      return this.plugin.getProgress().getOverallMulti(player.getUniqueId());
   }

   public double getSellPrice(Player player, ItemStack stack) {
      return this.getSellPrice(player, stack, this.getPlayerMultiplier(player));
   }

   public double getSellPrice(Player player, ItemStack stack, double multiplier) {
      return stack != null && !stack.getType().isAir() ? this.getSellPriceRaw(stack) * multiplier : (double)0.0F;
   }

   private double getSellPriceRaw(ItemStack stack) {
      if (stack != null && !stack.getType().isAir()) {
         double total = this.getBase(stack.getType()) * (double)stack.getAmount();
         if (isShulker(stack.getType()) && stack.hasItemMeta()) {
            ItemMeta var5 = stack.getItemMeta();
            if (var5 instanceof BlockStateMeta) {
               BlockStateMeta meta = (BlockStateMeta)var5;
               BlockState var6 = meta.getBlockState();
               if (var6 instanceof ShulkerBox) {
                  ShulkerBox box = (ShulkerBox)var6;

                  for(ItemStack content : box.getInventory().getContents()) {
                     if (content != null && !content.getType().isAir()) {
                        if (isShulker(content.getType())) {
                           total += this.getSellPriceRaw(content);
                        } else {
                           total += this.getBase(content.getType()) * (double)content.getAmount();
                        }
                     }
                  }
               }
            }
         }

         return total;
      } else {
         return (double)0.0F;
      }
   }

   private static boolean isShulker(Material material) {
      return material != null && material.name().endsWith("SHULKER_BOX");
   }

   public double getUnitPrice(Player player, Material material) {
      return this.getBase(material) * this.getPlayerMultiplier(player);
   }

   public void setPrice(Material material, double price) {
      this.prices.put(material, price);
      this.cfg.set(material.name(), price);

      try {
         this.cfg.save(this.file);
      } catch (IOException var5) {
         this.plugin.getLogger().warning("Could not save prices.yml");
      }

   }
}
