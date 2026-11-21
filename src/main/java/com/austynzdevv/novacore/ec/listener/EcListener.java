package com.austynzdevv.novacore.ec.listener;

import com.austynzdevv.novacore.ec.EconomyEC;
import com.austynzdevv.novacore.ec.gui.EcGui;
import com.austynzdevv.novacore.ec.gui.EcHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class EcListener implements Listener {
   private final EconomyEC plugin;

   public EcListener(EconomyEC plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onInteract(PlayerInteractEvent event) {
      if (event.getHand() == EquipmentSlot.HAND) {
         if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.ENDER_CHEST) {
               Player player = event.getPlayer();
               if (player.hasPermission("novacorecore.system.ec.open")) {
                  event.setCancelled(true);
                  EcGui.openOwn(player, this.plugin);
               }
            }
         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         Inventory inv = event.getInventory();
         InventoryHolder var5 = inv.getHolder();
         if (var5 instanceof EcHolder holder) {
            ItemStack[] var9 = inv.getContents();
            OfflinePlayer op = Bukkit.getOfflinePlayer(holder.getOwner());
            String n = op.getName();
            if (n != null) {
               this.plugin.getStorage().save(holder.getOwner(), var9, n);
            } else {
               this.plugin.getStorage().saveAsync(holder.getOwner(), var9);
            }

            EcGui.play(player, this.plugin, false);
         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      if (player.getOpenInventory() != null) {
         InventoryHolder var4 = player.getOpenInventory().getTopInventory().getHolder();
         if (var4 instanceof EcHolder) {
            EcHolder holder = (EcHolder)var4;
            this.plugin.getStorage().save(holder.getOwner(), player.getOpenInventory().getTopInventory().getContents());
         }
      }

   }
}
