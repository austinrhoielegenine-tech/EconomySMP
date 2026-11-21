package com.austynzdevv.economysmp.ec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.economysmp.data.PlayerDataStore;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EcStorage {
   private final EconomyEC plugin;
   private final Map<UUID, ItemStack[]> cache = new ConcurrentHashMap();

   public EcStorage(EconomyEC plugin) {
      this.plugin = plugin;
      this.migrateLegacy();
   }

   public File fileFor(UUID uuid) {
      return PlayerDataStore.get().file();
   }

   public ItemStack[] loadForPlayer(Player player, int size) {
      UUID uuid = player.getUniqueId();
      this.bindName(player.getName(), uuid);
      ItemStack[] data = this.load(uuid, size);
      if (this.hasItems(data)) {
         return data;
      } else {
         ItemStack[] vanilla = player.getEnderChest().getContents();
         boolean vanillaHas = this.hasItems(vanilla);
         if (vanillaHas) {
            ItemStack[] contents = this.expand(vanilla, size);
            this.save(uuid, contents, player.getName());
            return contents;
         } else {
            return data;
         }
      }
   }

   public ItemStack[] load(UUID uuid, int size) {
      ItemStack[] cached = (ItemStack[])this.cache.get(uuid);
      if (cached != null) {
         return this.expand(cached, size);
      } else {
         List<?> list = PlayerDataStore.get().config().getList("players." + String.valueOf(uuid) + ".enderchest.inventory");
         ItemStack[] out = new ItemStack[size];
         if (list != null) {
            out = new ItemStack[Math.max(size, list.size())];

            for(int i = 0; i < list.size(); ++i) {
               Object var8 = list.get(i);
               if (var8 instanceof ItemStack) {
                  ItemStack stack = (ItemStack)var8;
                  out[i] = stack;
               }
            }
         }

         out = this.expand(out, size);
         this.cache.put(uuid, out);
         return out;
      }
   }

   public ItemStack[] load(UUID uuid, int size, String knownName) {
      if (knownName != null && !knownName.isEmpty()) {
         this.bindName(knownName, uuid);
      }

      return this.load(uuid, size);
   }

   public void bindName(String name, UUID uuid) {
      if (name != null && !name.isEmpty()) {
         PlayerDataStore.get().config().set("names." + name.toLowerCase(Locale.ROOT), uuid.toString());
         PlayerDataStore.get().config().set("players." + String.valueOf(uuid) + ".name", name);
      }
   }

   public void save(UUID uuid, ItemStack[] contents) {
      OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
      this.save(uuid, contents, op.getName());
   }

   public void save(UUID uuid, ItemStack[] contents, String name) {
      if (contents != null) {
         ItemStack[] copy = this.cloneContents(contents);
         this.cache.put(uuid, copy);
         if (name != null) {
            this.bindName(name, uuid);
         }

         List<ItemStack> list = new ArrayList();

         for(ItemStack stack : copy) {
            list.add(stack);
         }

         YamlConfiguration cfg = PlayerDataStore.get().config();
         cfg.set("players." + String.valueOf(uuid) + ".enderchest.inventory", list);
         cfg.set("players." + String.valueOf(uuid) + ".enderchest.name", name);
         PlayerDataStore.get().save();
      }
   }

   public void saveAsync(UUID uuid, ItemStack[] contents) {
      ItemStack[] copy = this.cloneContents(contents);
      this.cache.put(uuid, copy);
      String name = null;
      Player p = Bukkit.getPlayer(uuid);
      if (p != null) {
         name = p.getName();
      } else if (Bukkit.getOfflinePlayer(uuid).getName() != null) {
         name = Bukkit.getOfflinePlayer(uuid).getName();
      }

      String finalName = name;
      Tasks.runAsync(this.plugin.getCore(), () -> this.save(uuid, copy, finalName));
   }

   public ItemStack[] seedFromVanilla(Player player, int size) {
      return this.loadForPlayer(player, size);
   }

   public void flushAll() {
      for(Map.Entry<UUID, ItemStack[]> e : this.cache.entrySet()) {
         this.save((UUID)e.getKey(), (ItemStack[])e.getValue());
      }

      PlayerDataStore.get().save();
   }

   private boolean hasItems(ItemStack[] contents) {
      if (contents == null) {
         return false;
      } else {
         for(ItemStack stack : contents) {
            if (stack != null && !stack.getType().isAir()) {
               return true;
            }
         }

         return false;
      }
   }

   private ItemStack[] expand(ItemStack[] current, int size) {
      if (current == null) {
         return new ItemStack[size];
      } else if (current.length == size) {
         return current;
      } else {
         ItemStack[] out = new ItemStack[size];
         System.arraycopy(current, 0, out, 0, Math.min(current.length, size));
         return out;
      }
   }

   private ItemStack[] cloneContents(ItemStack[] contents) {
      ItemStack[] copy = new ItemStack[contents.length];

      for(int i = 0; i < contents.length; ++i) {
         if (contents[i] != null) {
            copy[i] = contents[i].clone();
         }
      }

      return copy;
   }

   private void migrateLegacy() {
      File dir = new File(this.plugin.getDataFolder(), "data");
      if (dir.exists()) {
         File[] files = dir.listFiles((d, n) -> n.endsWith(".yml"));
         if (files != null) {
            for(File f : files) {
               try {
                  YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
                  String raw = y.getString("uuid");
                  if (raw == null) {
                     raw = f.getName().replace(".yml", "");
                  }

                  UUID id = UUID.fromString(raw);
                  if (!PlayerDataStore.get().config().contains("players." + String.valueOf(id) + ".enderchest.inventory")) {
                     PlayerDataStore.get().config().set("players." + String.valueOf(id) + ".name", y.getString("name"));
                     PlayerDataStore.get().config().set("players." + String.valueOf(id) + ".enderchest.inventory", y.getList("inventory"));
                  }

                  f.delete();
               } catch (Exception var10) {
               }
            }
         }

         dir.delete();
         File names = new File(this.plugin.getDataFolder(), "names.yml");
         if (names.exists()) {
            names.delete();
         }

         PlayerDataStore.get().save();
      }
   }
}
