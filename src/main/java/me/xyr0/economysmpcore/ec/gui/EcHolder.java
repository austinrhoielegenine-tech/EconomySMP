package me.xyr0.economysmpcore.ec.gui;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EcHolder implements InventoryHolder {
   private final UUID owner;
   private final boolean adminView;
   private Inventory inventory;

   public EcHolder(UUID owner, boolean adminView) {
      this.owner = owner;
      this.adminView = adminView;
   }

   public UUID getOwner() {
      return this.owner;
   }

   public boolean isAdminView() {
      return this.adminView;
   }

   public void setInventory(Inventory inventory) {
      this.inventory = inventory;
   }

   public Inventory getInventory() {
      return this.inventory;
   }
}
