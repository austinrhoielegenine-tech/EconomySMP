package com.austynzdevv.nyrethacore.spawners;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import com.austynzdevv.nyrethacore.spawners.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnerManager {
   private final EconomySpawners plugin;

   public SpawnerManager(EconomySpawners plugin) {
      this.plugin = plugin;
   }

   public void loadAll() {
   }

   public void saveAll() {
      this.plugin.getStorage().save();
   }

   public int size() {
      return this.plugin.getStorage().size();
   }

   public Collection<SpawnerData> getAll() {
      return this.plugin.getStorage().all().values();
   }

   public SpawnerData getAt(Location loc) {
      if (loc != null && loc.getWorld() != null) {
         String var10000 = loc.getWorld().getName();
         String key = var10000 + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
         return this.plugin.getStorage().getByLocation(key);
      } else {
         return null;
      }
   }

   public SpawnerData create(Location loc, EntityType type, int stack) {
      SpawnerData existing = this.getAt(loc);
      if (existing != null) {
         return existing;
      } else {
         SpawnerData data = new SpawnerData(UUID.randomUUID(), loc, type, stack);
         this.plugin.getStorage().put(data);
         this.applyBlock(loc.getBlock(), type);
         if (this.plugin.getSettings().isHolograms()) {
            this.plugin.getHologramManager().create(data);
         }

         return data;
      }
   }

   public void remove(SpawnerData data) {
      this.plugin.getStorage().remove(data);
      if (this.plugin.getSettings().isHolograms()) {
         this.plugin.getHologramManager().remove(data);
      }

   }

   public void applyBlock(Block block, EntityType type) {
      if (block.getType() != Material.SPAWNER) {
         block.setType(Material.SPAWNER);
      }

      BlockState var4 = block.getState();
      if (var4 instanceof CreatureSpawner spawner) {
         spawner.setSpawnedType(type);
         spawner.setSpawnCount(0);
         spawner.setMaxNearbyEntities(0);
         spawner.setRequiredPlayerRange(0);
         spawner.setMaxSpawnDelay(800);
         spawner.setMinSpawnDelay(800);
         spawner.setDelay(Integer.MAX_VALUE);
         spawner.update(true, false);
      }

   }

   public boolean tryStack(SpawnerData data, int amount) {
      int max = this.plugin.getSettings().getMaxStack();
      if (max > 0 && data.getStackSize() + amount > max) {
         int canAdd = max - data.getStackSize();
         if (canAdd <= 0) {
            return false;
         } else {
            data.setStackSize(data.getStackSize() + canAdd);
            if (this.plugin.getSettings().isHolograms()) {
               this.plugin.getHologramManager().update(data);
            }

            return true;
         }
      } else {
         data.setStackSize(data.getStackSize() + amount);
         if (this.plugin.getSettings().isHolograms()) {
            this.plugin.getHologramManager().update(data);
         }

         return true;
      }
   }

   public int tryStackPartial(SpawnerData data, int amount) {
      int max = this.plugin.getSettings().getMaxStack();
      if (max <= 0) {
         data.setStackSize(data.getStackSize() + amount);
         if (this.plugin.getSettings().isHolograms()) {
            this.plugin.getHologramManager().update(data);
         }

         return amount;
      } else {
         int canAdd = Math.min(amount, max - data.getStackSize());
         if (canAdd <= 0) {
            return 0;
         } else {
            data.setStackSize(data.getStackSize() + canAdd);
            if (this.plugin.getSettings().isHolograms()) {
               this.plugin.getHologramManager().update(data);
            }

            return canAdd;
         }
      }
   }

   public void generate(SpawnerData data) {
      EntityDefinition def = this.plugin.getEntities().get(data.getEntityType());
      if (def != null && !def.getDrops().isEmpty()) {
         int stack = Math.max(1, data.getStackSize());
         int maxSlots = this.plugin.getSettings().getMaxSlots();
         int maxExp = this.plugin.getSettings().getMaxExp();
         int lootLimit = this.plugin.getSettings().getLootLimit();
         int cycles = ThreadLocalRandom.current().nextInt(def.getMinMobs(), def.getMaxMobs() + 1);
         int totalRolls = cycles * stack;

         for(int i = 0; i < totalRolls && (lootLimit <= 0 || data.getTotalItems() < lootLimit); ++i) {
            for(DropEntry drop : def.getDrops()) {
               if (ThreadLocalRandom.current().nextInt(100) < drop.chance()) {
                  int amount = ThreadLocalRandom.current().nextInt(drop.min(), drop.max() + 1);
                  if (amount > 0) {
                     if (lootLimit > 0) {
                        int room = lootLimit - data.getTotalItems();
                        if (room <= 0) {
                           break;
                        }

                        amount = Math.min(amount, room);
                     }

                     data.addItem(drop.material(), amount, maxSlots);
                  }
               }
            }
         }

         int expGain = def.getExpPerCycle() * stack;
         if (expGain > 0) {
            data.addExp(expGain, maxExp);
         }

         data.setLastGenerate(System.currentTimeMillis());
         if (this.plugin.getSettings().isHolograms()) {
            this.plugin.getHologramManager().update(data);
         }

      }
   }

   public ItemStack toItem(SpawnerData data) {
      return ItemBuilder.createSpawner(data.getEntityType(), data.getStackSize());
   }

   public List<ItemStack> toItemStacks(SpawnerData data) {
      return ItemBuilder.createSpawnerStacks(data.getEntityType(), data.getStackSize());
   }

   public void giveTo(Player player, EntityType type, int amount) {
      for(ItemStack item : ItemBuilder.createSpawnerStacks(type, amount)) {
         player.getInventory().addItem(new ItemStack[]{item}).values().forEach((leftover) -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
      }

   }
}
