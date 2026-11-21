package me.xyr0.economysmpcore.kits.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class KitsHolder implements InventoryHolder {
   private final String type;
   private final String kitId;

   public KitsHolder(String type, String kitId) {
      this.type = type;
      this.kitId = kitId;
   }

   public String getType() {
      return this.type;
   }

   public String getKitId() {
      return this.kitId;
   }

   public Inventory getInventory() {
      return null;
   }
}
