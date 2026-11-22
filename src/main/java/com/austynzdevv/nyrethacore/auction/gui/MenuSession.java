package com.austynzdevv.nyrethacore.auction.gui;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.auction.AuctionListing;
import com.austynzdevv.nyrethacore.util.SessionUtil;
import org.bukkit.inventory.ItemStack;

public final class MenuSession {
   public static final Map<UUID, View> VIEW = new ConcurrentHashMap();
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();
   public static final Map<UUID, Integer> SORT = new ConcurrentHashMap();
   public static final Map<UUID, Integer> FILTER = new ConcurrentHashMap();
   public static final Map<UUID, String> SEARCH = new ConcurrentHashMap();
   public static final Map<UUID, AuctionListing> SELECTED = new ConcurrentHashMap();
   public static final Map<UUID, ItemStack> PENDING_ITEM = new ConcurrentHashMap();
   public static final Map<UUID, Double> PENDING_PRICE = new ConcurrentHashMap();
   public static final Map<UUID, Boolean> PENDING_SHARDS = new ConcurrentHashMap();
   public static final Map<UUID, Boolean> SHARD_VIEW = new ConcurrentHashMap();
   public static final Map<UUID, Boolean> FAST_BUY = new ConcurrentHashMap();
   public static final Map<UUID, Boolean> FAST_SELL = new ConcurrentHashMap();
   public static final Map<UUID, Boolean> CHAT_SEARCH = new ConcurrentHashMap();
   public static final Map<UUID, Boolean> SIGN_SEARCH = new ConcurrentHashMap();
   public static final Map<UUID, UUID> ADMIN_TARGET = new ConcurrentHashMap();
   public static final Set<UUID> SWITCHING = ConcurrentHashMap.newKeySet();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, VIEW, PAGE, SELECTED, PENDING_ITEM, PENDING_PRICE, CHAT_SEARCH, SIGN_SEARCH, ADMIN_TARGET);
   }

   public static enum View {
      MAIN,
      MY_ITEMS,
      CONFIRM_BUY,
      CONFIRM_LIST,
      TRANSACTIONS,
      SHULKER,
      ADMIN,
      ADMIN_LISTINGS;

      // $FF: synthetic method
      private static View[] $values() {
         return new View[]{MAIN, MY_ITEMS, CONFIRM_BUY, CONFIRM_LIST, TRANSACTIONS, SHULKER, ADMIN, ADMIN_LISTINGS};
      }
   }
}
