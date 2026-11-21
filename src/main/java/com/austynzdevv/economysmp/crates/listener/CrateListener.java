package com.austynzdevv.economysmp.crates.listener;

import com.austynzdevv.economysmp.crates.EconomyCrates;
import com.austynzdevv.economysmp.crates.Crate;
import com.austynzdevv.economysmp.crates.OpenManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public final class CrateListener implements Listener {
   private final EconomyCrates plugin;
   private final OpenManager openManager;

   public CrateListener(EconomyCrates plugin) {
      this.plugin = plugin;
      this.openManager = new OpenManager(plugin);
   }

   public OpenManager getOpenManager() {
      return this.openManager;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onInteract(PlayerInteractEvent event) {
      if (event.getHand() == EquipmentSlot.HAND) {
         if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null) {
               String crateId = this.plugin.getData().getBound(block.getLocation());
               if (crateId != null) {
                  event.setCancelled(true);
                  Player player = event.getPlayer();
                  Crate crate = this.plugin.getCrates().get(crateId);
                  if (crate == null) {
                     this.plugin.getMessages().send(player, "crate-not-found", "&cCrate %crate% does not exist!", "%crate%", crateId);
                  } else if (!player.hasPermission("economysmpcore.system.crates.use")) {
                     this.plugin.getMessages().send(player, "no-permission", "&cYou don't have permission to do this!");
                  } else {
                     boolean preview = event.getAction() == Action.LEFT_CLICK_BLOCK || player.isSneaking();
                     this.openManager.tryOpen(player, crate, preview);
                  }
               }
            }
         }
      }
   }
}
