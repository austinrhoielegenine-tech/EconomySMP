package com.austynzdevv.economysmp.crates;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public final class Crate {
   private final String id;
   private final String displayName;
   private String effect;
   private final int rows;
   private final boolean filler;
   private final ItemStack fillerItem;
   private final List<Reward> rewards = new ArrayList();
   private final List<HologramLayer> holograms = new ArrayList();
   private boolean hologramEnabled = true;

   public Crate(String id, String displayName, String effect, int rows, boolean filler, ItemStack fillerItem) {
      this.id = id;
      this.displayName = displayName;
      this.effect = effect == null ? "CHOOSE" : effect.toUpperCase();
      this.rows = rows;
      this.filler = filler;
      this.fillerItem = fillerItem;
   }

   public String getId() {
      return this.id;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getEffect() {
      return this.effect;
   }

   public void setEffect(String effect) {
      this.effect = effect == null ? "CHOOSE" : effect.toUpperCase();
   }

   public int getRows() {
      return this.rows;
   }

   public boolean isFiller() {
      return this.filler;
   }

   public ItemStack getFillerItem() {
      return this.fillerItem == null ? null : this.fillerItem.clone();
   }

   public List<Reward> getRewards() {
      return this.rewards;
   }

   public void addReward(Reward reward) {
      this.rewards.add(reward);
   }

   public Reward getRewardBySlot(int slot) {
      for(Reward r : this.rewards) {
         if (r.getSlot() == slot) {
            return r;
         }
      }

      return null;
   }

   public List<HologramLayer> getHolograms() {
      return this.holograms;
   }

   public void addHologram(HologramLayer layer) {
      this.holograms.add(layer);
   }

   public boolean isHologramEnabled() {
      return this.hologramEnabled;
   }

   public void setHologramEnabled(boolean hologramEnabled) {
      this.hologramEnabled = hologramEnabled;
   }

   public static final class HologramLayer {
      public final double height;
      public final List<String> lines;

      public HologramLayer(double height, List<String> lines) {
         this.height = height;
         this.lines = lines == null ? List.of() : lines;
      }
   }
}
