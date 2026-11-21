package com.austynzdevv.novacore.spawners;

import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class EntityDefinition {
   private final EntityType type;
   private final String display;
   private final int minMobs;
   private final int maxMobs;
   private final int delay;
   private final int expPerCycle;
   private final List<DropEntry> drops;
   private final Map<Material, Double> sellPrices;

   public EntityDefinition(EntityType type, String display, int minMobs, int maxMobs, int delay, int expPerCycle, List<DropEntry> drops, Map<Material, Double> sellPrices) {
      this.type = type;
      this.display = display;
      this.minMobs = minMobs;
      this.maxMobs = maxMobs;
      this.delay = delay;
      this.expPerCycle = expPerCycle;
      this.drops = List.copyOf(drops);
      this.sellPrices = Map.copyOf(sellPrices);
   }

   public EntityType getType() {
      return this.type;
   }

   public String getDisplay() {
      return this.display;
   }

   public int getMinMobs() {
      return this.minMobs;
   }

   public int getMaxMobs() {
      return this.maxMobs;
   }

   public int getDelay() {
      return this.delay;
   }

   public int getExpPerCycle() {
      return this.expPerCycle;
   }

   public List<DropEntry> getDrops() {
      return this.drops;
   }

   public Map<Material, Double> getSellPrices() {
      return this.sellPrices;
   }

   public double getPrice(Material material) {
      return (Double)this.sellPrices.getOrDefault(material, (double)0.0F);
   }
}
