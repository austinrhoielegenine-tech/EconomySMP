package com.austynzdevv.nyrethacore.crates;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public final class Reward {
   private final String id;
   private final int slot;
   private final ItemStack icon;
   private final double chance;
   private final List<String> commands;

   public Reward(String id, int slot, ItemStack icon, double chance, List<String> commands) {
      this.id = id;
      this.slot = slot;
      this.icon = icon;
      this.chance = chance;
      this.commands = (List<String>)(commands == null ? new ArrayList() : commands);
   }

   public String getId() {
      return this.id;
   }

   public int getSlot() {
      return this.slot;
   }

   public ItemStack getIcon() {
      return this.icon == null ? null : this.icon.clone();
   }

   public double getChance() {
      return this.chance;
   }

   public List<String> getCommands() {
      return this.commands;
   }

   public boolean hasCommands() {
      return this.commands != null && !this.commands.isEmpty();
   }
}
