package com.austynzdevv.economysmp.punishment2;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeManager {
   private final EconomyPunishment plugin;
   private final Set<UUID> frozen = new HashSet();

   public FreezeManager(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public void freeze(UUID uuid) {
      this.frozen.add(uuid);
   }

   public void unfreeze(UUID uuid) {
      this.frozen.remove(uuid);
   }

   public boolean isFrozen(UUID uuid) {
      return this.frozen.contains(uuid);
   }

   public Set<UUID> getFrozenPlayers() {
      return this.frozen;
   }
}
