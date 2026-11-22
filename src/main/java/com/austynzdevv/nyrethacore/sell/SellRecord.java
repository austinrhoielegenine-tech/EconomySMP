package com.austynzdevv.nyrethacore.sell;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;

public class SellRecord {
   private final UUID id;
   private final UUID playerId;
   private final Material material;
   private final int amount;
   private final double money;
   private final long time;

   public SellRecord(UUID id, UUID playerId, Material material, int amount, double money, long time) {
      this.id = id;
      this.playerId = playerId;
      this.material = material;
      this.amount = amount;
      this.money = money;
      this.time = time;
   }

   public UUID getId() {
      return this.id;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public Material getMaterial() {
      return this.material;
   }

   public int getAmount() {
      return this.amount;
   }

   public double getMoney() {
      return this.money;
   }

   public long getTime() {
      return this.time;
   }

   public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap();
      map.put("id", this.id.toString());
      map.put("playerId", this.playerId.toString());
      map.put("material", this.material.name());
      map.put("amount", this.amount);
      map.put("money", this.money);
      map.put("time", this.time);
      return map;
   }

   public static SellRecord deserialize(Map<String, Object> map) {
      return new SellRecord(UUID.fromString(String.valueOf(map.get("id"))), UUID.fromString(String.valueOf(map.get("playerId"))), Material.valueOf(String.valueOf(map.get("material"))), ((Number)map.get("amount")).intValue(), ((Number)map.get("money")).doubleValue(), ((Number)map.get("time")).longValue());
   }
}
