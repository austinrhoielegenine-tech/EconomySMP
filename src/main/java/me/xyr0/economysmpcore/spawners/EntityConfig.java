package me.xyr0.economysmpcore.spawners;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public class EntityConfig {
   private final AstralSpawners plugin;
   private final Map<EntityType, EntityDefinition> definitions = new EnumMap(EntityType.class);

   public EntityConfig(AstralSpawners plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.definitions.clear();
      File folder = new File(this.plugin.getDataFolder(), "spawners");
      if (!folder.exists()) {
         folder.mkdirs();
      }

      this.saveDefaults(folder);
      File[] files = folder.listFiles((dir, n) -> n.toLowerCase(Locale.ROOT).endsWith(".yml"));
      if (files != null) {
         for(File file : files) {
            this.loadFile(file);
         }

         this.plugin.getLogger().info("Loaded " + this.definitions.size() + " spawner types from spawners/");
      }
   }

   private void saveDefaults(File folder) {
      try {
         URL url = this.plugin.getClass().getClassLoader().getResource("spawners");
         if (url == null) {
            return;
         }

         if (!url.getProtocol().equals("jar")) {
            File src = new File(url.toURI());
            File[] files = src.listFiles((d, n) -> n.endsWith(".yml"));
            if (files == null) {
               return;
            }

            for(File f : files) {
               File out = new File(folder, f.getName());
               if (!out.exists()) {
                  Files.copy(f.toPath(), out.toPath());
               }
            }

            return;
         }

         String path = url.getPath();
         String jarPath = path.substring(5, path.indexOf("!"));
         JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));

         try {
            Enumeration<JarEntry> entries = jar.entries();

            while(entries.hasMoreElements()) {
               JarEntry e = (JarEntry)entries.nextElement();
               String name = e.getName();
               if (name.startsWith("spawners/spawners/") && name.endsWith(".yml")) {
                  String fileName = name.substring("spawners/spawners/".length());
                  if (!fileName.contains("/")) {
                     File out = new File(folder, fileName);
                     if (!out.exists()) {
                        out.getParentFile().mkdirs();
                        InputStream in = jar.getInputStream(e);

                        try {
                           Files.copy(in, out.toPath(), new CopyOption[0]);
                        } catch (Throwable var17) {
                           if (in != null) {
                              try {
                                 in.close();
                              } catch (Throwable var15) {
                                 var17.addSuppressed(var15);
                              }
                           }

                           throw var17;
                        }

                        if (in != null) {
                           in.close();
                        }
                     }
                  }
               }
            }
         } catch (Throwable var20) {
            try {
               jar.close();
            } catch (Throwable var14) {
               var20.addSuppressed(var14);
            }

            throw var20;
         }

         jar.close();
      } catch (Exception ex) {
         this.plugin.getLogger().warning("Could not extract default spawners: " + ex.getMessage());
         String[] fallback = new String[]{"zombie", "skeleton", "spider", "creeper", "enderman", "blaze", "iron_golem", "cow", "pig", "sheep", "chicken", "rabbit", "slime", "magma_cube", "guardian", "phantom", "turtle", "squid", "glow_squid", "piglin", "sniffer", "armadillo"};

         for(String name : fallback) {
            File out = new File(folder, name + ".yml");
            if (!out.exists()) {
               try {
                  InputStream in = this.plugin.getResource("spawners/spawners/" + name + ".yml");

                  try {
                     if (in != null) {
                        Files.copy(in, out.toPath(), new CopyOption[0]);
                     }
                  } catch (Throwable var18) {
                     if (in != null) {
                        try {
                           in.close();
                        } catch (Throwable var16) {
                           var18.addSuppressed(var16);
                        }
                     }

                     throw var18;
                  }

                  if (in != null) {
                     in.close();
                  }
               } catch (Exception var19) {
               }
            }
         }
      }

   }

   private void loadFile(File file) {
      String key = file.getName().replace(".yml", "").replace(".YAML", "").toUpperCase(Locale.ROOT);

      EntityType type;
      try {
         type = EntityType.valueOf(key);
      } catch (IllegalArgumentException var22) {
         return;
      }

      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      String var10002 = this.pretty(type);
      String display = config.getString("display", var10002 + " Spawner");
      if (!display.contains("&#") && !display.contains("&")) {
         display = "&#1FFD98" + display;
      }

      int minMobs = config.getInt("min-mobs", 1);
      int maxMobs = Math.max(minMobs, config.getInt("max-mobs", 1));
      int delay = config.getInt("delay", 20);
      int exp = config.getInt("exp-per-cycle", 0);
      List<DropEntry> drops = new ArrayList();
      Map<Material, Double> prices = new EnumMap(Material.class);
      if (config.isConfigurationSection("items")) {
         ConfigurationSection items = config.getConfigurationSection("items");

         for(String id : items.getKeys(false)) {
            ConfigurationSection sec = items.getConfigurationSection(id);
            if (sec != null) {
               String matName = sec.getString("material");
               if (matName != null) {
                  Material mat;
                  try {
                     mat = Material.valueOf(matName.toUpperCase(Locale.ROOT));
                  } catch (IllegalArgumentException var23) {
                     continue;
                  }

                  drops.add(new DropEntry(mat, 1, 1, 100));
               }
            }
         }
      }

      for(Map<?, ?> map : config.getMapList("drops")) {
         Object matObj = map.get("material");
         if (matObj != null) {
            try {
               Material mat = Material.valueOf(matObj.toString().toUpperCase(Locale.ROOT));
               int min = map.containsKey("min") ? ((Number)map.get("min")).intValue() : 1;
               int max = map.containsKey("max") ? ((Number)map.get("max")).intValue() : 1;
               int chance = map.containsKey("chance") ? ((Number)map.get("chance")).intValue() : 100;
               drops.add(new DropEntry(mat, min, Math.max(min, max), chance));
            } catch (IllegalArgumentException var21) {
            }
         }
      }

      ConfigurationSection priceSec = config.getConfigurationSection("prices");
      if (priceSec == null) {
         priceSec = config.getConfigurationSection("sell-price");
      }

      if (priceSec != null) {
         for(String pKey : priceSec.getKeys(false)) {
            try {
               prices.put(Material.valueOf(pKey.toUpperCase(Locale.ROOT)), priceSec.getDouble(pKey));
            } catch (IllegalArgumentException var20) {
            }
         }
      }

      if (drops.isEmpty()) {
         this.plugin.getLogger().warning("No drops defined in " + file.getName());
      }

      this.definitions.put(type, new EntityDefinition(type, display, minMobs, maxMobs, delay, exp, drops, prices));
   }

   private String pretty(EntityType type) {
      String raw = type.name().toLowerCase(Locale.ROOT).replace('_', ' ');
      StringBuilder sb = new StringBuilder();

      for(String p : raw.split(" ")) {
         if (!p.isEmpty()) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
         }
      }

      return sb.toString();
   }

   public EntityDefinition get(EntityType type) {
      return (EntityDefinition)this.definitions.get(type);
   }

   public boolean isSupported(EntityType type) {
      return this.definitions.containsKey(type);
   }

   public Map<EntityType, EntityDefinition> all() {
      return Collections.unmodifiableMap(this.definitions);
   }
}
