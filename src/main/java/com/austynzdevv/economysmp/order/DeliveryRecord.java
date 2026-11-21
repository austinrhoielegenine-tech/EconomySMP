package com.austynzdevv.economysmp.order;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeliveryRecord {
   private final UUID playerId;
   private final String playerName;
   private final int amount;
   private final long time;

   public DeliveryRecord(UUID playerId, String playerName, int amount, long time) {
      this.playerId = playerId;
      this.playerName = playerName;
      this.amount = amount;
      this.time = time;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public int getAmount() {
      return this.amount;
   }

   public long getTime() {
      return this.time;
   }

   public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap();
      map.put("playerId", this.playerId.toString());
      map.put("playerName", this.playerName);
      map.put("amount", this.amount);
      map.put("time", this.time);
      return map;
   }

   public static DeliveryRecord deserialize(Map<String, Object> map) {
      return new DeliveryRecord(UUID.fromString(String.valueOf(map.get("playerId"))), String.valueOf(map.get("playerName")), ((Number)map.get("amount")).intValue(), ((Number)map.get("time")).longValue());
   }
}
