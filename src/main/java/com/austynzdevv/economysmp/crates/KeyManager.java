package com.austynzdevv.economysmp.crates;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.austynzdevv.economysmp.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class KeyManager {
   private final EconomyCrates plugin;
   private final Map<String, KeyTemplate> templates = new LinkedHashMap();
   private NamespacedKey pdcKey;

   public KeyManager(EconomyCrates plugin) {
      this.plugin = plugin;
   }

   public void load() {
      this.templates.clear();
      this.pdcKey = new NamespacedKey(this.plugin, "crate_key");
      File dir = new File(this.plugin.getDataFolder(), "keys");
      if (!dir.exists()) {
         dir.mkdirs();
         String[] defs = new String[]{"common-key.yml", "gold-key.yml", "vote-key.yml", "amethyst-key.yml", "crimson-key.yml", "prime-key.yml", "epic-key.yml"};

         for(String d : defs) {
            try {
               this.plugin.saveResource("keys/" + d, false);
            } catch (Exception var13) {
            }
         }
      }

      File[] files = dir.listFiles((f, n) -> n.endsWith(".yml"));
      if (files != null) {
         for(File file : files) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection sec = cfg.getConfigurationSection("key");
            if (sec != null) {
               String id = file.getName().replace("-key.yml", "").replace(".yml", "").toLowerCase(Locale.ROOT);
               Material mat = Material.TRIPWIRE_HOOK;

               try {
                  mat = Material.valueOf(sec.getString("item", "TRIPWIRE_HOOK").toUpperCase(Locale.ROOT));
               } catch (Exception var12) {
               }

               this.templates.put(id, new KeyTemplate(id, mat, sec.getString("name", id + " key"), sec.getStringList("lore")));
            }
         }

      }
   }

   public ItemStack createPhysical(String crateId, int amount) {
      KeyTemplate t = (KeyTemplate)this.templates.get(crateId.toLowerCase(Locale.ROOT));
      if (t == null) {
         t = new KeyTemplate(crateId, Material.TRIPWIRE_HOOK, crateId + " key", List.of());
      }

      ItemStack item = new ItemStack(t.material, Math.max(1, amount));
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(ColorUtil.parse(t.name));
         List<Component> lore = new ArrayList();

         for(String line : t.lore) {
            lore.add(ColorUtil.parse(line));
         }

         if (!lore.isEmpty()) {
            meta.lore(lore);
         }

         meta.getPersistentDataContainer().set(this.pdcKey, PersistentDataType.STRING, crateId.toLowerCase(Locale.ROOT));
         item.setItemMeta(meta);
      }

      return item;
   }

   public String crateFromKey(ItemStack item) {
      return item != null && item.hasItemMeta() ? (String)item.getItemMeta().getPersistentDataContainer().get(this.pdcKey, PersistentDataType.STRING) : null;
   }

   public int countPhysical(Player player, String crateId) {
      int total = 0;
      String id = crateId.toLowerCase(Locale.ROOT);

      for(ItemStack stack : player.getInventory().getContents()) {
         if (stack != null) {
            String crate = this.crateFromKey(stack);
            if (id.equals(crate)) {
               total += stack.getAmount();
            }
         }
      }

      return total;
   }

   public boolean takePhysical(Player player, String crateId, int amount) {
      String id = crateId.toLowerCase(Locale.ROOT);
      int left = amount;
      ItemStack[] contents = player.getInventory().getContents();

      for(int i = 0; i < contents.length; ++i) {
         ItemStack stack = contents[i];
         if (stack != null && id.equals(this.crateFromKey(stack))) {
            int take = Math.min(left, stack.getAmount());
            stack.setAmount(stack.getAmount() - take);
            if (stack.getAmount() <= 0) {
               contents[i] = null;
            }

            left -= take;
            if (left <= 0) {
               break;
            }
         }
      }

      player.getInventory().setContents(contents);
      return left <= 0;
   }

   public int totalKeys(Player player, String crateId) {
      return this.countPhysical(player, crateId) + this.plugin.getData().getVirtual(player.getUniqueId(), crateId);
   }

   public boolean consumeKey(Player player, String crateId) {
      if (this.countPhysical(player, crateId) > 0) {
         return this.takePhysical(player, crateId, 1);
      } else {
         int virtual = this.plugin.getData().getVirtual(player.getUniqueId(), crateId);
         if (virtual > 0) {
            this.plugin.getData().setVirtual(player.getUniqueId(), crateId, virtual - 1);
            return true;
         } else {
            return false;
         }
      }
   }

   public static record KeyTemplate(String id, Material material, String name, List<String> lore) {
   }
}
