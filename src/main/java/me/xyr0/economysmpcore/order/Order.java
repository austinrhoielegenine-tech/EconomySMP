package me.xyr0.economysmpcore.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class Order implements ConfigurationSerializable {
   private final UUID id;
   private final UUID owner;
   private final String ownerName;
   private final Material material;
   private final ItemStack display;
   private final int amount;
   private final double priceEach;
   private int delivered;
   private int collected;
   private final long createdAt;
   private double escrowLeft;
   private final List<DeliveryRecord> history = new ArrayList();

   public Order(UUID id, UUID owner, String ownerName, ItemStack template, int amount, double priceEach) {
      this.id = id;
      this.owner = owner;
      this.ownerName = ownerName;
      this.material = template.getType();
      ItemStack copy = template.clone();
      copy.setAmount(1);
      this.display = copy;
      this.amount = amount;
      this.priceEach = priceEach;
      this.delivered = 0;
      this.collected = 0;
      this.createdAt = System.currentTimeMillis();
      this.escrowLeft = (double)amount * priceEach;
   }

   private Order(UUID id, UUID owner, String ownerName, Material material, ItemStack display, int amount, double priceEach, int delivered, int collected, long createdAt, double escrowLeft, List<DeliveryRecord> history) {
      this.id = id;
      this.owner = owner;
      this.ownerName = ownerName;
      this.material = material;
      this.display = display;
      this.amount = amount;
      this.priceEach = priceEach;
      this.delivered = delivered;
      this.collected = collected;
      this.createdAt = createdAt;
      this.escrowLeft = escrowLeft;
      if (history != null) {
         this.history.addAll(history);
      }

   }

   public UUID getId() {
      return this.id;
   }

   public UUID getOwner() {
      return this.owner;
   }

   public String getOwnerName() {
      return this.ownerName;
   }

   public Material getMaterial() {
      return this.material;
   }

   public ItemStack getDisplay() {
      return this.display.clone();
   }

   public int getAmount() {
      return this.amount;
   }

   public double getPriceEach() {
      return this.priceEach;
   }

   public int getDelivered() {
      return this.delivered;
   }

   public int getCollected() {
      return this.collected;
   }

   public int getRemaining() {
      return Math.max(0, this.amount - this.delivered);
   }

   public int getUncollected() {
      return Math.max(0, this.delivered - this.collected);
   }

   public boolean isComplete() {
      return this.delivered >= this.amount;
   }

   public double totalValue() {
      return (double)this.amount * this.priceEach;
   }

   public double payFor(int qty) {
      double want = (double)qty * this.priceEach;
      double pay = Math.min(want, Math.max((double)0.0F, this.escrowLeft));
      this.escrowLeft = Math.max((double)0.0F, this.escrowLeft - pay);
      return pay;
   }

   public long getCreatedAt() {
      return this.createdAt;
   }

   public double getEscrowLeft() {
      return this.escrowLeft;
   }

   public List<DeliveryRecord> getHistory() {
      return this.history;
   }

   public int deliver(int qty) {
      int can = Math.min(qty, this.getRemaining());
      this.delivered += can;
      return can;
   }

   public void addHistory(UUID playerId, String name, int amount) {
      this.history.add(new DeliveryRecord(playerId, name, amount, System.currentTimeMillis()));
   }

   public double takeEscrowRefund() {
      double refund = Math.max((double)0.0F, this.escrowLeft);
      this.escrowLeft = (double)0.0F;
      return refund;
   }

   public int collect(int qty) {
      int can = Math.min(qty, this.getUncollected());
      this.collected += can;
      return can;
   }

   public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap();
      map.put("id", this.id.toString());
      map.put("owner", this.owner.toString());
      map.put("ownerName", this.ownerName);
      map.put("material", this.material.name());
      map.put("display", this.display == null ? null : this.display.serialize());
      map.put("amount", this.amount);
      map.put("priceEach", this.priceEach);
      map.put("delivered", this.delivered);
      map.put("collected", this.collected);
      map.put("createdAt", this.createdAt);
      map.put("escrowLeft", this.escrowLeft);
      List<Map<String, Object>> hist = new ArrayList();

      for(DeliveryRecord r : this.history) {
         hist.add(r.serialize());
      }

      map.put("history", hist);
      return map;
   }

   public static Order deserialize(Map<String, Object> map) {
      UUID id = UUID.fromString(String.valueOf(map.get("id")));
      UUID owner = UUID.fromString(String.valueOf(map.get("owner")));
      String ownerName = String.valueOf(map.get("ownerName"));
      Material material = Material.valueOf(String.valueOf(map.get("material")));
      ItemStack display = null;
      Object draw = map.get("display");
      if (draw instanceof ItemStack is) {
         display = is;
      } else if (draw instanceof Map<?, ?> dm) {
         try {
            display = ItemStack.deserialize((Map<String, Object>)dm);
         } catch (Exception var23) {
         }
      }

      if (display == null) {
         display = new ItemStack(material);
      }

      int amount = ((Number)map.get("amount")).intValue();
      double priceEach = ((Number)map.get("priceEach")).doubleValue();
      int delivered = ((Number)map.getOrDefault("delivered", 0)).intValue();
      int collected = ((Number)map.getOrDefault("collected", 0)).intValue();
      long createdAt = ((Number)map.getOrDefault("createdAt", System.currentTimeMillis())).longValue();
      double escrowLeft = map.containsKey("escrowLeft") ? ((Number)map.get("escrowLeft")).doubleValue() : Math.max((double)0.0F, (double)(amount - delivered) * priceEach);
      List<DeliveryRecord> history = new ArrayList();
      Object raw = map.get("history");
      if (raw instanceof List) {
         for(Object o : (List)raw) {
            if (o instanceof Map) {
               Map<?, ?> m = (Map)o;
               history.add(DeliveryRecord.deserialize((Map<String, Object>)m));
            }
         }
      }

      return new Order(id, owner, ownerName, material, display, amount, priceEach, delivered, collected, createdAt, escrowLeft, history);
   }
}
