package me.xyr0.economysmpcore.crates;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import me.xyr0.economysmpcore.crates.util.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public final class CrateManager {
   private final AstralCrates plugin;
   private final Map<String, Crate> crates = new LinkedHashMap();

   public CrateManager(AstralCrates plugin) {
      this.plugin = plugin;
   }

   public void load() {
      this.crates.clear();
      File dir = new File(this.plugin.getDataFolder(), "crates");
      if (!dir.exists()) {
         dir.mkdirs();
         String[] defaults = new String[]{"common.yml", "gold.yml", "vote.yml", "amethyst.yml", "crimson.yml", "prime.yml", "epic.yml"};

         for(String d : defaults) {
            try {
               this.plugin.saveResource("crates/" + d, false);
            } catch (Exception var29) {
            }
         }
      }

      File[] files = dir.listFiles((f, n) -> n.endsWith(".yml"));
      if (files != null) {
         for(File file : files) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            for(String root : cfg.getKeys(false)) {
               ConfigurationSection sec = cfg.getConfigurationSection(root);
               if (sec != null) {
                  String id = root.toLowerCase(Locale.ROOT).replace("-crate", "").replace("_crate", "");
                  ConfigurationSection inv = sec.getConfigurationSection("inventory");
                  String name = inv != null ? inv.getString("name", id) : id;
                  String effect = inv != null ? inv.getString("effect", "CHOOSE") : "CHOOSE";
                  int rows = inv != null ? inv.getInt("rows", 3) : 3;
                  ConfigurationSection fillerSec = inv != null ? inv.getConfigurationSection("filler-item") : null;
                  boolean filler = fillerSec != null && fillerSec.getBoolean("enable", true);
                  ItemStack fillerItem = fillerSec != null ? ItemBuilder.fromSection(fillerSec) : null;
                  Crate crate = new Crate(id, name, effect, rows, filler, fillerItem);
                  ConfigurationSection hologram = sec.getConfigurationSection("hologram");
                  if (hologram != null) {
                     boolean holoOn = true;
                     if (hologram.contains("enabled")) {
                        holoOn = hologram.getBoolean("enabled", true);
                     } else if (hologram.contains("Enabled")) {
                        holoOn = hologram.getBoolean("Enabled", true);
                     }

                     crate.setHologramEnabled(holoOn);
                     if (holoOn) {
                        for(String hk : hologram.getKeys(false)) {
                           if (!hk.equalsIgnoreCase("enabled")) {
                              ConfigurationSection hs = hologram.getConfigurationSection(hk);
                              if (hs != null) {
                                 double height = hs.getDouble("height", (double)1.0F);
                                 List<String> lines = hs.getStringList("lines");
                                 crate.addHologram(new Crate.HologramLayer(height, lines));
                              }
                           }
                        }
                     }
                  }

                  ConfigurationSection rewards = sec.getConfigurationSection("rewards");
                  if (rewards != null) {
                     for(String rid : rewards.getKeys(false)) {
                        ConfigurationSection r = rewards.getConfigurationSection(rid);
                        if (r != null) {
                           int slot = r.getInt("slot", 0);
                           ItemStack icon = ItemBuilder.fromSection(r.getConfigurationSection("icon"));
                           double chance = this.parseChance(r.getString("chance", "100"));
                           crate.addReward(new Reward(rid, slot, icon, chance, r.getStringList("commands")));
                        }
                     }
                  }

                  this.crates.put(id, crate);
               }
            }
         }

      }
   }

   private double parseChance(String raw) {
      if (raw == null) {
         return (double)100.0F;
      } else {
         raw = raw.replace("%", "").trim();

         try {
            return Double.parseDouble(raw);
         } catch (Exception var3) {
            return (double)100.0F;
         }
      }
   }

   public Crate get(String id) {
      return id == null ? null : (Crate)this.crates.get(id.toLowerCase(Locale.ROOT));
   }

   public Collection<Crate> all() {
      return this.crates.values();
   }

   public Map<String, Crate> map() {
      return this.crates;
   }

   public Reward pickWeighted(Crate crate) {
      double total = (double)0.0F;

      for(Reward r : crate.getRewards()) {
         total += Math.max(0.01, r.getChance());
      }

      double roll = ThreadLocalRandom.current().nextDouble() * total;
      double cursor = (double)0.0F;

      for(Reward r : crate.getRewards()) {
         cursor += Math.max(0.01, r.getChance());
         if (roll <= cursor) {
            return r;
         }
      }

      if (crate.getRewards().isEmpty()) {
         return null;
      } else {
         return (Reward)crate.getRewards().get(crate.getRewards().size() - 1);
      }
   }
}
