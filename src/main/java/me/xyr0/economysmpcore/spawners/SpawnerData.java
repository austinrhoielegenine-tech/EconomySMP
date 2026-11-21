package me.xyr0.economysmpcore.spawners;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SpawnerData {
   private final UUID id;
   private final String worldName;
   private final int x;
   private final int y;
   private final int z;
   private EntityType entityType;
   private int stackSize;
   private int storedExp;
   private final Map<Material, Integer> storage = new EnumMap(Material.class);
   private long lastGenerate;
   private int ticksUntilGenerate;
   private boolean dirty;

   public SpawnerData(UUID id, Location loc, EntityType type, int stack) {
      this.id = id;
      this.worldName = loc.getWorld().getName();
      this.x = loc.getBlockX();
      this.y = loc.getBlockY();
      this.z = loc.getBlockZ();
      this.entityType = type;
      this.stackSize = Math.max(1, stack);
      this.lastGenerate = System.currentTimeMillis();
      this.ticksUntilGenerate = 0;
      this.dirty = true;
   }

   public SpawnerData(UUID id, String world, int x, int y, int z, EntityType type, int stack, int exp, Map<Material, Integer> items, long lastGen, int ticksLeft) {
      this.id = id;
      this.worldName = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.entityType = type;
      this.stackSize = Math.max(1, stack);
      this.storedExp = exp;
      if (items != null) {
         this.storage.putAll(items);
      }

      this.lastGenerate = lastGen;
      this.ticksUntilGenerate = ticksLeft;
      this.dirty = false;
   }

   public UUID getId() {
      return this.id;
   }

   public Location getLocation() {
      World world = Bukkit.getWorld(this.worldName);
      return world == null ? null : new Location(world, (double)this.x, (double)this.y, (double)this.z);
   }

   public String getWorldName() {
      return this.worldName;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public EntityType getEntityType() {
      return this.entityType;
   }

   public void setEntityType(EntityType entityType) {
      this.entityType = entityType;
      this.dirty = true;
   }

   public int getStackSize() {
      return this.stackSize;
   }

   public void setStackSize(int stackSize) {
      this.stackSize = Math.max(1, stackSize);
      this.dirty = true;
   }

   public int getStoredExp() {
      return this.storedExp;
   }

   public void setStoredExp(int storedExp) {
      this.storedExp = Math.max(0, storedExp);
      this.dirty = true;
   }

   public void addExp(int amount, int max) {
      this.storedExp = Math.min(max, this.storedExp + amount);
      this.dirty = true;
   }

   public Map<Material, Integer> getStorage() {
      return this.storage;
   }

   public int getTotalItems() {
      int total = 0;

      for(int v : this.storage.values()) {
         total += v;
      }

      return total;
   }

   public boolean addItem(Material mat, int amount, int maxSlots) {
      if (amount <= 0) {
         return true;
      } else if (!this.storage.containsKey(mat) && this.storage.size() >= maxSlots) {
         return false;
      } else {
         this.storage.merge(mat, amount, Integer::sum);
         this.dirty = true;
         return true;
      }
   }

   public ItemStack[] extractAll() {
      ItemStack[] items = new ItemStack[this.storage.size()];
      int i = 0;

      for(Map.Entry<Material, Integer> e : this.storage.entrySet()) {
         items[i++] = new ItemStack((Material)e.getKey(), (Integer)e.getValue());
      }

      this.storage.clear();
      this.dirty = true;
      return items;
   }

   public long getLastGenerate() {
      return this.lastGenerate;
   }

   public void setLastGenerate(long lastGenerate) {
      this.lastGenerate = lastGenerate;
      this.dirty = true;
   }

   public int getTicksUntilGenerate() {
      return this.ticksUntilGenerate;
   }

   public void setTicksUntilGenerate(int ticks) {
      this.ticksUntilGenerate = ticks;
      this.dirty = true;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   public String locationKey() {
      return this.worldName + ":" + this.x + ":" + this.y + ":" + this.z;
   }
}
