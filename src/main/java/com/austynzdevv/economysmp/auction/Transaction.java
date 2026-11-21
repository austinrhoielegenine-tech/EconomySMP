package com.austynzdevv.economysmp.auction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class Transaction {
   private final UUID id;
   private final UUID playerId;
   private final String playerName;
   private final UUID otherId;
   private final String otherName;
   private final ItemStack item;
   private final double price;
   private final Type type;
   private final long time;

   public Transaction(UUID id, UUID playerId, String playerName, UUID otherId, String otherName, ItemStack item, double price, Type type, long time) {
      this.id = id;
      this.playerId = playerId;
      this.playerName = playerName;
      this.otherId = otherId;
      this.otherName = otherName;
      this.item = item.clone();
      this.price = price;
      this.type = type;
      this.time = time;
   }

   public UUID getId() {
      return this.id;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public UUID getOtherId() {
      return this.otherId;
   }

   public String getOtherName() {
      return this.otherName;
   }

   public ItemStack getItem() {
      return this.item.clone();
   }

   public double getPrice() {
      return this.price;
   }

   public Type getType() {
      return this.type;
   }

   public long getTime() {
      return this.time;
   }

   public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap();
      map.put("id", this.id.toString());
      map.put("playerId", this.playerId.toString());
      map.put("playerName", this.playerName);
      map.put("otherId", this.otherId.toString());
      map.put("otherName", this.otherName);
      map.put("item", this.item);
      map.put("price", this.price);
      map.put("type", this.type.name());
      map.put("time", this.time);
      return map;
   }

   public static Transaction deserialize(Map<String, Object> map) {
      return new Transaction(UUID.fromString(String.valueOf(map.get("id"))), UUID.fromString(String.valueOf(map.get("playerId"))), String.valueOf(map.get("playerName")), UUID.fromString(String.valueOf(map.get("otherId"))), String.valueOf(map.get("otherName")), (ItemStack)map.get("item"), ((Number)map.get("price")).doubleValue(), Transaction.Type.valueOf(String.valueOf(map.get("type"))), ((Number)map.get("time")).longValue());
   }

   public static enum Type {
      BUY,
      SELL;

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{BUY, SELL};
      }
   }
}
