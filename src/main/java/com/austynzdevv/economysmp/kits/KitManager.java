package com.austynzdevv.economysmp.kits;

import java.io.File;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitManager {
   private final EconomyKits host;
   private final Map<String, Kit> kits = new LinkedHashMap();
   private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap();
   private File kitsFolder;
   private File cooldownFile;

   public KitManager(EconomyKits host) {
      this.host = host;
   }

   public void load() {
      this.kits.clear();
      this.kitsFolder = new File(this.host.getDataFolder(), "kits");
      if (!this.kitsFolder.exists()) {
         this.kitsFolder.mkdirs();
      }

      this.cooldownFile = new File(this.host.getDataFolder(), "cooldowns.yml");
      File[] existing = this.kitsFolder.listFiles((d, n) -> n.endsWith(".yml"));
      if (existing == null || existing.length == 0) {
         for(String name : new String[]{"starter.yml", "food.yml", "tools.yml", "pvp.yml", "builder.yml"}) {
            try {
               InputStream in = this.host.getCore().getResource("kits/kits/" + name);
               if (in != null) {
                  Files.copy(in, (new File(this.kitsFolder, name)).toPath(), new CopyOption[0]);
                  in.close();
               }
            } catch (Exception var10) {
            }
         }
      }

      File[] files = this.kitsFolder.listFiles((d, n) -> n.endsWith(".yml"));
      if (files != null) {
         for(File f : files) {
            try {
               YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
               String id = f.getName().replace(".yml", "").toLowerCase();
               this.kits.put(id, Kit.fromConfig(id, cfg));
            } catch (Exception e) {
               Logger var10000 = this.host.getCore().getLogger();
               String var10001 = f.getName();
               var10000.warning("Failed to load kit " + var10001 + ": " + e.getMessage());
            }
         }
      }

      this.loadCooldowns();
      this.host.getCore().getLogger().info("Loaded " + this.kits.size() + " kit(s).");
   }

   private void loadCooldowns() {
      this.cooldowns.clear();
      if (this.cooldownFile.exists()) {
         try {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(this.cooldownFile);

            for(String uuidStr : cfg.getKeys(false)) {
               try {
                  UUID uuid = UUID.fromString(uuidStr);
                  Map<String, Long> map = new HashMap();
                  if (cfg.getConfigurationSection(uuidStr) != null) {
                     for(String kitId : cfg.getConfigurationSection(uuidStr).getKeys(false)) {
                        map.put(kitId, cfg.getLong(uuidStr + "." + kitId));
                     }
                  }

                  this.cooldowns.put(uuid, map);
               } catch (Exception var8) {
               }
            }
         } catch (Exception var9) {
         }

      }
   }

   public void saveCooldowns() {
      try {
         YamlConfiguration cfg = new YamlConfiguration();

         for(Map.Entry<UUID, Map<String, Long>> e : this.cooldowns.entrySet()) {
            for(Map.Entry<String, Long> k : e.getValue().entrySet()) {
               cfg.set(((UUID)e.getKey()).toString() + "." + (String)k.getKey(), k.getValue());
            }
         }

         cfg.save(this.cooldownFile);
      } catch (Exception var6) {
      }

   }

   public boolean create(String id, int slot, long cooldown, String permission) {
      if (id != null && id.matches("[a-zA-Z0-9_-]+") && !this.kits.containsKey(id.toLowerCase())) {
         Kit kit = new Kit(id);
         kit.setSlot(slot);
         kit.setCooldownSeconds(cooldown);
         kit.setPermission(permission);
         this.kits.put(kit.getId(), kit);
         this.saveKit(kit);
         return true;
      } else {
         return false;
      }
   }

   public void saveKit(Kit kit) {
      if (kit != null && this.kitsFolder != null) {
         File file = new File(this.kitsFolder, kit.getId() + ".yml");
         YamlConfiguration cfg = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
         cfg.set("display-name", kit.getDisplayName());
         cfg.set("slot", kit.getSlot());
         cfg.set("cooldown", kit.getCooldownSeconds());
         cfg.set("permission", kit.getPermission());
         cfg.set("permission-required", kit.isPermissionRequired());
         List<ItemStack> contents = new ArrayList();

         for(ItemStack item : kit.getItems()) {
            if (item != null && !item.getType().isAir()) {
               contents.add(item.clone());
            }
         }

         cfg.set("items", contents);

         try {
            cfg.save(file);
         } catch (Exception var7) {
            this.host.getCore().getLogger().warning("Could not save kit " + kit.getId());
         }

      }
   }

   public boolean delete(String id) {
      if (id == null) {
         return false;
      } else {
         Kit removed = (Kit)this.kits.remove(id.toLowerCase());
         if (removed == null) {
            return false;
         } else {
            if (this.kitsFolder != null) {
               (new File(this.kitsFolder, removed.getId() + ".yml")).delete();
            }

            for(Map<String, Long> values : this.cooldowns.values()) {
               values.remove(removed.getId());
            }

            this.saveCooldowns();
            return true;
         }
      }
   }

   public Kit get(String id) {
      return id == null ? null : (Kit)this.kits.get(id.toLowerCase());
   }

   public List<Kit> all() {
      return new ArrayList(this.kits.values());
   }

   public long getRemaining(Player player, Kit kit) {
      Map<String, Long> map = (Map)this.cooldowns.get(player.getUniqueId());
      if (map == null) {
         return 0L;
      } else {
         Long until = (Long)map.get(kit.getId());
         return until == null ? 0L : Math.max(0L, until - System.currentTimeMillis());
      }
   }

   public void setCooldown(Player player, Kit kit) {
      if (kit.getCooldownSeconds() > 0L) {
         if (!player.hasPermission("economysmpcore.kit.bypass") && !player.isOp()) {
            ((Map)this.cooldowns.computeIfAbsent(player.getUniqueId(), (u) -> new HashMap())).put(kit.getId(), System.currentTimeMillis() + kit.getCooldownSeconds() * 1000L);
         }
      }
   }

   public boolean claim(Player player, String id) {
      Kit kit = this.get(id);
      if (kit == null) {
         return false;
      } else if (!kit.canClaim(player)) {
         return false;
      } else if (this.getRemaining(player, kit) > 0L) {
         return false;
      } else if (!kit.give(player)) {
         return false;
      } else {
         this.setCooldown(player, kit);
         return true;
      }
   }
}
