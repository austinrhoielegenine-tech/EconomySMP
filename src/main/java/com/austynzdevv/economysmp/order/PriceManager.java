package com.austynzdevv.economysmp.order;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PriceManager {
   private final EconomyOrder plugin;
   private final Map<Material, Double> prices = new EnumMap(Material.class);

   public PriceManager(EconomyOrder plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.prices.clear();
      File file = new File(this.plugin.getDataFolder(), "prices.yml");
      if (!file.exists()) {
         this.generateFullFile(file);
      }

      FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
      boolean missing = false;

      for(Material material : Material.values()) {
         if (material.isItem() && !material.isAir() && !material.name().startsWith("LEGACY_")) {
            if (cfg.contains(material.name())) {
               this.prices.put(material, cfg.getDouble(material.name()));
            } else {
               this.prices.put(material, (double)1.0F);
               cfg.set(material.name(), (double)1.0F);
               missing = true;
            }
         }
      }

      if (missing) {
         try {
            cfg.save(file);
         } catch (IOException var8) {
            this.plugin.getLogger().warning("Could not update prices.yml");
         }
      }

   }

   private void generateFullFile(File file) {
      FileConfiguration cfg = new YamlConfiguration();
      cfg.options().header("EconomyOrder by xyr0\nSell prices per item. Change any value you want.\n");

      for(Material material : Material.values()) {
         if (material.isItem() && !material.isAir() && !material.name().startsWith("LEGACY_")) {
            cfg.set(material.name(), this.defaultPrice(material));
         }
      }

      try {
         file.getParentFile().mkdirs();
         cfg.save(file);
      } catch (IOException var7) {
         this.plugin.getLogger().warning("Could not create prices.yml");
      }

   }

   private double defaultPrice(Material material) {
      String n = material.name();
      if (n.contains("DIAMOND")) {
         return (double)50.0F;
      } else if (n.contains("EMERALD")) {
         return (double)25.0F;
      } else if (n.contains("NETHERITE")) {
         return (double)100.0F;
      } else if (n.contains("GOLD")) {
         return (double)8.0F;
      } else if (n.contains("IRON")) {
         return (double)10.0F;
      } else if (n.contains("COPPER")) {
         return (double)3.0F;
      } else if (n.contains("COAL")) {
         return (double)2.0F;
      } else if (n.contains("REDSTONE")) {
         return (double)2.0F;
      } else if (n.contains("LAPIS")) {
         return (double)3.0F;
      } else if (n.contains("QUARTZ")) {
         return (double)2.0F;
      } else if (n.contains("ANCIENT_DEBRIS")) {
         return (double)80.0F;
      } else if (n.contains("SHULKER")) {
         return (double)20.0F;
      } else if (n.contains("ELYTRA")) {
         return (double)200.0F;
      } else if (n.contains("TOTEM")) {
         return (double)150.0F;
      } else if (n.contains("ENDER_PEARL")) {
         return (double)10.0F;
      } else if (n.contains("BLAZE")) {
         return (double)7.0F;
      } else if (n.contains("GUNPOWDER")) {
         return (double)8.0F;
      } else if (n.contains("BONE")) {
         return (double)2.0F;
      } else if (n.contains("STRING")) {
         return (double)2.5F;
      } else if (n.contains("LEATHER")) {
         return (double)3.5F;
      } else if (!n.contains("LOG") && !n.contains("STEM")) {
         if (n.contains("ORE")) {
            return (double)5.0F;
         } else if (n.contains("INGOT")) {
            return (double)8.0F;
         } else if (n.contains("BLOCK")) {
            return (double)2.0F;
         } else if (!n.contains("SWORD") && !n.contains("PICKAXE") && !n.contains("AXE")) {
            if (!n.contains("HELMET") && !n.contains("CHESTPLATE") && !n.contains("LEGGINGS") && !n.contains("BOOTS")) {
               if (!n.contains("DIRT") && !n.contains("COBBLESTONE") && !n.contains("STONE")) {
                  return !n.contains("NETHERRACK") && !n.contains("END_STONE") ? (double)1.0F : 0.1;
               } else {
                  return 0.1;
               }
            } else {
               return (double)20.0F;
            }
         } else {
            return (double)15.0F;
         }
      } else {
         return (double)1.0F;
      }
   }

   public double get(Material material) {
      return (Double)this.prices.getOrDefault(material, (double)1.0F);
   }
}
