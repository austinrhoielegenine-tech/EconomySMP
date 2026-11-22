package com.austynzdevv.nyrethacore.order.gui;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.order.Order;
import com.austynzdevv.nyrethacore.util.SessionUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class MenuSession {
   public static final Map<UUID, View> VIEW = new ConcurrentHashMap();
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();
   public static final Map<UUID, Integer> SORT = new ConcurrentHashMap();
   public static final Map<UUID, Integer> FILTER = new ConcurrentHashMap();
   public static final Map<UUID, String> SEARCH = new ConcurrentHashMap();
   public static final Map<UUID, View> INPUT_RETURN = new ConcurrentHashMap();
   public static final Map<UUID, Order> SELECTED = new ConcurrentHashMap();
   public static final Map<UUID, ItemStack> DRAFT_ITEM = new ConcurrentHashMap();
   public static final Map<UUID, Integer> DRAFT_AMOUNT = new ConcurrentHashMap();
   public static final Map<UUID, Double> DRAFT_PRICE = new ConcurrentHashMap();
   public static final Map<UUID, Integer> DRAFT_DELIVER = new ConcurrentHashMap();
   public static final Map<UUID, ChatMode> CHAT = new ConcurrentHashMap();
   public static final Map<UUID, ItemStack[]> CHEST_SNAPSHOT = new ConcurrentHashMap();
   public static final Map<UUID, Set<Enchantment>> ENCHANT_PICK = new ConcurrentHashMap();
   public static final Map<UUID, ItemStack> ENCHANT_BASE = new ConcurrentHashMap();
   public static final Map<UUID, UUID> PROFILE_TARGET = new ConcurrentHashMap();
   public static final Set<UUID> SWITCHING = ConcurrentHashMap.newKeySet();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, VIEW, PAGE, SELECTED, DRAFT_ITEM, DRAFT_AMOUNT, DRAFT_PRICE, DRAFT_DELIVER, CHAT, SEARCH, INPUT_RETURN, CHEST_SNAPSHOT, ENCHANT_PICK, ENCHANT_BASE, PROFILE_TARGET);
   }

   public static ItemStack draftItem(UUID id) {
      return (ItemStack)DRAFT_ITEM.getOrDefault(id, new ItemStack(Material.STONE));
   }

   public static Set<Enchantment> enchantPick(UUID id) {
      return (Set)ENCHANT_PICK.computeIfAbsent(id, (k) -> new HashSet());
   }

   public static enum View {
      BROWSE,
      YOUR_ORDERS,
      NEW_ORDER,
      MATERIALS,
      CONFIRM_DELIVER,
      CONFIRM_CANCEL,
      EDIT_ORDER,
      COLLECT,
      DELIVER_CHEST,
      CONFIRM_SELL,
      ADMIN,
      ADMIN_PROFILE,
      ADMIN_HISTORY,
      ENCHANTS;

      // $FF: synthetic method
      private static View[] $values() {
         return new View[]{BROWSE, YOUR_ORDERS, NEW_ORDER, MATERIALS, CONFIRM_DELIVER, CONFIRM_CANCEL, EDIT_ORDER, COLLECT, DELIVER_CHEST, CONFIRM_SELL, ADMIN, ADMIN_PROFILE, ADMIN_HISTORY, ENCHANTS};
      }
   }

   public static enum ChatMode {
      NONE,
      AMOUNT,
      PRICE,
      SEARCH;

      // $FF: synthetic method
      private static ChatMode[] $values() {
         return new ChatMode[]{NONE, AMOUNT, PRICE, SEARCH};
      }
   }
}
