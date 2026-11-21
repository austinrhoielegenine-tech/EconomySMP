package com.austynzdevv.novacore.auction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AuctionListing {
   private final UUID id;
   private final UUID sellerId;
   private final String sellerName;
   private final ItemStack item;
   private final double price;
   private final boolean shards;
   private final long createdAt;
   private final long expiresAt;
   private boolean sold;
   private boolean cancelled;

   public AuctionListing(UUID id, UUID sellerId, String sellerName, ItemStack item, double price, long createdAt, long expiresAt) {
      this(id, sellerId, sellerName, item, price, createdAt, expiresAt, false);
   }

   public AuctionListing(UUID id, UUID sellerId, String sellerName, ItemStack item, double price, long createdAt, long expiresAt, boolean shards) {
      this.id = id;
      this.sellerId = sellerId;
      this.sellerName = sellerName;
      this.item = item.clone();
      this.price = price;
      this.shards = shards;
      this.createdAt = createdAt;
      this.expiresAt = expiresAt;
      this.sold = false;
      this.cancelled = false;
   }

   private AuctionListing(UUID id, UUID sellerId, String sellerName, ItemStack item, double price, long createdAt, long expiresAt, boolean sold, boolean cancelled, boolean shards) {
      this.id = id;
      this.sellerId = sellerId;
      this.sellerName = sellerName;
      this.item = item;
      this.price = price;
      this.shards = shards;
      this.createdAt = createdAt;
      this.expiresAt = expiresAt;
      this.sold = sold;
      this.cancelled = cancelled;
   }

   public UUID getId() {
      return this.id;
   }

   public UUID getSellerId() {
      return this.sellerId;
   }

   public String getSellerName() {
      return this.sellerName;
   }

   public ItemStack getItem() {
      return this.item.clone();
   }

   public double getPrice() {
      return this.price;
   }

   public boolean isShards() {
      return this.shards;
   }

   public long getCreatedAt() {
      return this.createdAt;
   }

   public long getExpiresAt() {
      return this.expiresAt;
   }

   public boolean isSold() {
      return this.sold;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public boolean isActive() {
      return !this.sold && !this.cancelled && System.currentTimeMillis() < this.expiresAt;
   }

   public boolean isExpired() {
      return !this.sold && !this.cancelled && System.currentTimeMillis() >= this.expiresAt;
   }

   public void markSold() {
      this.sold = true;
   }

   public void markCancelled() {
      this.cancelled = true;
   }

   public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap();
      map.put("id", this.id.toString());
      map.put("sellerId", this.sellerId.toString());
      map.put("sellerName", this.sellerName);
      map.put("item", this.item == null ? null : this.item.serialize());
      map.put("price", this.price);
      map.put("shards", this.shards);
      map.put("createdAt", this.createdAt);
      map.put("expiresAt", this.expiresAt);
      map.put("sold", this.sold);
      map.put("cancelled", this.cancelled);
      return map;
   }

   public static AuctionListing deserialize(Map<String, Object> map) {
      ItemStack stack = null;
      Object raw = map.get("item");
      if (raw instanceof ItemStack is) {
         stack = is;
      } else if (raw instanceof Map<?, ?> m) {
         try {
            stack = ItemStack.deserialize((Map<String, Object>)m);
         } catch (Exception var6) {
            stack = null;
         }
      }

      if (stack == null) {
         stack = new ItemStack(Material.BARRIER);
      }

      return new AuctionListing(UUID.fromString(String.valueOf(map.get("id"))), UUID.fromString(String.valueOf(map.get("sellerId"))), String.valueOf(map.get("sellerName")), stack, ((Number)map.get("price")).doubleValue(), ((Number)map.get("createdAt")).longValue(), ((Number)map.get("expiresAt")).longValue(), Boolean.TRUE.equals(map.get("sold")), Boolean.TRUE.equals(map.get("cancelled")), Boolean.TRUE.equals(map.get("shards")));
   }
}
