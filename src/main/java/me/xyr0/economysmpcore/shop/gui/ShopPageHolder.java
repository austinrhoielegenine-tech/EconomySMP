package me.xyr0.economysmpcore.shop.gui;

import java.util.List;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class ShopPageHolder implements InventoryHolder {
   private final ItemStack hand;
   private final double price;
   private final int slot;
   private final List<String> pages;

   public ShopPageHolder(ItemStack hand, double price, int slot, List<String> pages) {
      this.hand = hand.clone();
      this.price = price;
      this.slot = slot;
      this.pages = List.copyOf(pages);
   }

   public ItemStack hand() {
      return this.hand.clone();
   }

   public double price() {
      return this.price;
   }

   public int slot() {
      return this.slot;
   }

   public List<String> pages() {
      return this.pages;
   }

   public Inventory getInventory() {
      return null;
   }
}
