package com.austynzdevv.novacore.sell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.novacore.data.PlayerDataStore;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ProgressManager {
   public static final String[] CATEGORIES = new String[]{"crops", "ores", "mobs", "natural", "tools", "fish", "books", "potions", "blocks"};
   private final EconomySell plugin;
   private FileConfiguration levels;
   private final Map<UUID, Map<String, Double>> sold = new ConcurrentHashMap();
   private final Map<UUID, Double> moneyMade = new ConcurrentHashMap();

   public ProgressManager(EconomySell plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "level.yml");
      if (!file.exists()) {
         this.plugin.saveResource("level.yml", false);
      }

      this.levels = YamlConfiguration.loadConfiguration(file);
   }

   public void loadPlayer(UUID id) {
      Map<String, Double> map = new ConcurrentHashMap();

      for(String c : CATEGORIES) {
         map.put(c, (double)0.0F);
      }

      double totalMoney = (double)0.0F;
      YamlConfiguration cfg = PlayerDataStore.get().config();

      for(String c : CATEGORIES) {
         map.put(c, cfg.getDouble("players." + String.valueOf(id) + ".sell.progress." + c, (double)0.0F));
      }

      totalMoney = cfg.getDouble("players." + String.valueOf(id) + ".sell.money-made", (double)0.0F);
      this.sold.put(id, map);
      this.moneyMade.put(id, totalMoney);
   }

   public void savePlayer(UUID id) {
      Map<String, Double> map = (Map)this.sold.get(id);
      if (map != null) {
         PlayerDataStore.get().editAndSave((cfg) -> {
            cfg.set("players." + String.valueOf(id) + ".sell.progress", (Object)null);

            for(Map.Entry<String, Double> e : map.entrySet()) {
               cfg.set("players." + String.valueOf(id) + ".sell.progress." + (String)e.getKey(), e.getValue());
            }

            cfg.set("players." + String.valueOf(id) + ".sell.money-made", this.moneyMade.getOrDefault(id, (double)0.0F));
         });
      }
   }

   public void saveAll() {
      PlayerDataStore.get().editAndSave((cfg) -> {
         for(Map.Entry<UUID, Map<String, Double>> player : this.sold.entrySet()) {
            String path = "players." + String.valueOf(player.getKey()) + ".sell.progress";
            cfg.set(path, (Object)null);

            for(Map.Entry<String, Double> entry : player.getValue().entrySet()) {
               cfg.set(path + "." + (String)entry.getKey(), entry.getValue());
            }

            cfg.set("players." + String.valueOf(player.getKey()) + ".sell.money-made", this.moneyMade.getOrDefault(player.getKey(), (double)0.0F));
         }

      });
   }

   private Map<String, Double> data(UUID id) {
      if (!this.sold.containsKey(id)) {
         this.loadPlayer(id);
      }

      return (Map)this.sold.get(id);
   }

   public double getSold(UUID id, String category) {
      return (Double)this.data(id).getOrDefault(category, (double)0.0F);
   }

   public void addSold(UUID id, String category, double amount) {
      Map<String, Double> map = this.data(id);
      map.put(category, (Double)map.getOrDefault(category, (double)0.0F) + amount);
   }

   public int getLevel(UUID id, String category) {
      double have = this.getSold(id, category);
      ConfigurationSection sec = this.levels.getConfigurationSection(category);
      if (sec == null) {
         return 0;
      } else {
         int level = 0;

         for(String key : sec.getKeys(false)) {
            try {
               int lvl = Integer.parseInt(key);
               double need = sec.getDouble(key + ".amountNeeded");
               if (have >= need) {
                  level = Math.max(level, lvl);
               }
            } catch (Exception var12) {
            }
         }

         return level;
      }
   }

   public double getCategoryMulti(UUID id, String category) {
      double base = this.levels.getDouble("base-multiplier", (double)1.0F);
      ConfigurationSection sec = this.levels.getConfigurationSection(category);
      if (sec == null) {
         return base;
      } else {
         double multi = base;
         double have = this.getSold(id, category);

         for(String key : sec.getKeys(false)) {
            try {
               double need = sec.getDouble(key + ".amountNeeded");
               double m = sec.getDouble(key + ".multi");
               if (have >= need) {
                  multi = Math.max(multi, m);
               }
            } catch (Exception var16) {
            }
         }

         return multi;
      }
   }

   public double getNextNeeded(UUID id, String category) {
      double have = this.getSold(id, category);
      ConfigurationSection sec = this.levels.getConfigurationSection(category);
      if (sec == null) {
         return (double)0.0F;
      } else {
         double next = (double)-1.0F;

         for(String key : sec.getKeys(false)) {
            try {
               double need = sec.getDouble(key + ".amountNeeded");
               if (need > have && (next < (double)0.0F || need < next)) {
                  next = need;
               }
            } catch (Exception var12) {
            }
         }

         return next < (double)0.0F ? have : next;
      }
   }

   public double getNextMulti(UUID id, String category) {
      double have = this.getSold(id, category);
      ConfigurationSection sec = this.levels.getConfigurationSection(category);
      if (sec == null) {
         return this.getCategoryMulti(id, category);
      } else {
         double best = this.getCategoryMulti(id, category);
         Double nextMulti = null;
         double nextNeed = Double.MAX_VALUE;

         for(String key : sec.getKeys(false)) {
            try {
               double need = sec.getDouble(key + ".amountNeeded");
               double m = sec.getDouble(key + ".multi");
               if (need > have && need < nextNeed) {
                  nextNeed = need;
                  nextMulti = m;
               }
            } catch (Exception var17) {
            }
         }

         return nextMulti == null ? best : nextMulti;
      }
   }

   public double getOverallMulti(UUID id) {
      double sum = (double)0.0F;
      int n = 0;

      for(String c : CATEGORIES) {
         sum += this.getCategoryMulti(id, c);
         ++n;
      }

      return n == 0 ? (double)1.0F : sum / (double)n;
   }

   public List<Material> materialsInCategory(String category) {
      List<Material> list = new ArrayList();

      for(Material m : Material.values()) {
         if (m.isItem() && !m.isAir() && !m.name().startsWith("LEGACY_") && this.categoryOf(m).equalsIgnoreCase(category)) {
            list.add(m);
         }
      }

      return list;
   }

   public String categoryOf(Material material) {
      String n = material.name();
      if (!n.contains("WHEAT") && !n.contains("CARROT") && !n.contains("POTATO") && !n.contains("BEETROOT") && !n.contains("MELON") && !n.contains("PUMPKIN") && !n.contains("SUGAR_CANE") && !n.contains("BAMBOO") && !n.contains("COCOA") && !n.contains("NETHER_WART") && !n.contains("CHORUS") && !n.contains("SWEET_BERRIES") && !n.contains("GLOW_BERRIES") && !n.contains("APPLE") && !n.contains("SEED") && !n.contains("CROPS")) {
         if (!n.contains("ORE") && !n.contains("RAW_IRON") && !n.contains("RAW_GOLD") && !n.contains("RAW_COPPER") && !n.contains("INGOT") && !n.contains("DIAMOND") && !n.contains("EMERALD") && !n.contains("COAL") && !n.contains("LAPIS") && !n.contains("REDSTONE") && !n.contains("QUARTZ") && !n.contains("NETHERITE") && !n.contains("ANCIENT_DEBRIS") && !n.contains("AMETHYST") && !n.contains("NUGGET")) {
            if (!n.contains("BONE") && !n.contains("ROTTEN") && !n.contains("STRING") && !n.contains("SPIDER") && !n.contains("GUNPOWDER") && !n.contains("ENDER_PEARL") && !n.contains("BLAZE") && !n.contains("GHAST") && !n.contains("MAGMA_CREAM") && !n.contains("SLIME") && !n.contains("LEATHER") && !n.contains("FEATHER") && !n.contains("ARROW") && !n.contains("MEMBRANE") && !n.contains("SHULKER_SHELL") && !n.contains("TOTEM") && !n.contains("HEAD") && !n.contains("SKULL") && (!n.contains("EGG") || n.contains("DRAGON_EGG"))) {
               if (!n.contains("LOG") && !n.contains("STEM") && !n.contains("WOOD") && !n.contains("LEAVES") && !n.contains("SAPLING") && !n.contains("DIRT") && !n.contains("GRASS") && !n.contains("SAND") && !n.contains("GRAVEL") && !n.contains("FLINT") && !n.contains("CLAY") && !n.contains("SNOW") && !n.contains("ICE") && !n.contains("VINE") && !n.contains("MOSS") && !n.contains("FLOWER") && !n.contains("MUSHROOM")) {
                  if (!n.contains("SWORD") && !n.contains("PICKAXE") && !n.contains("AXE") && !n.contains("SHOVEL") && !n.contains("HOE") && !n.contains("HELMET") && !n.contains("CHESTPLATE") && !n.contains("LEGGINGS") && !n.contains("BOOTS") && !n.contains("SHIELD") && !n.contains("BOW") && !n.contains("CROSSBOW") && !n.contains("TRIDENT") && !n.contains("SHEARS") && !n.contains("FLINT_AND_STEEL") && !n.contains("FISHING_ROD")) {
                     if (!n.contains("FISH") && !n.contains("COD") && !n.contains("SALMON") && !n.contains("PUFFER") && !n.contains("TROPICAL") && !n.contains("KELP") && !n.contains("PRISMARINE") && !n.contains("SPONGE") && !n.contains("INK_SAC") && !n.contains("NAUTILUS") && !n.contains("HEART_OF_THE_SEA") && !n.contains("SCUTE")) {
                        if (!n.contains("BOOK") && !n.contains("PAPER") && !n.contains("MAP") && !n.contains("ENCHANTED_BOOK")) {
                           return !n.contains("POTION") && !n.contains("BREWING") && !n.contains("GLASS_BOTTLE") && !n.contains("BLAZE_POWDER") && !n.contains("FERMENTED") && !n.contains("GLISTERING") && !n.contains("RABBIT_FOOT") && !n.contains("DRAGON_BREATH") ? "blocks" : "potions";
                        } else {
                           return "books";
                        }
                     } else {
                        return "fish";
                     }
                  } else {
                     return "tools";
                  }
               } else {
                  return "natural";
               }
            } else {
               return "mobs";
            }
         } else {
            return "ores";
         }
      } else {
         return "crops";
      }
   }

   public static String bar(double progress) {
      int total = 10;
      int filled = (int)Math.round(Math.min((double)1.0F, Math.max((double)0.0F, progress)) * (double)total);
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < total; ++i) {
         sb.append(i < filled ? "&#00fc88▌" : "&8▌");
      }

      return sb.toString();
   }

   public void addMoneyMade(UUID id, double amount) {
      if (!(amount <= (double)0.0F)) {
         if (!this.moneyMade.containsKey(id)) {
            this.loadPlayer(id);
         }

         double next = (Double)this.moneyMade.getOrDefault(id, (double)0.0F) + amount;
         this.moneyMade.put(id, next);
         if (this.plugin.getPlaceholders() != null) {
            this.plugin.getPlaceholders().update(id, next);
         }

      }
   }

   public double getMoneyMade(UUID id) {
      if (!this.moneyMade.containsKey(id)) {
         this.loadPlayer(id);
      }

      return (Double)this.moneyMade.getOrDefault(id, (double)0.0F);
   }
}
