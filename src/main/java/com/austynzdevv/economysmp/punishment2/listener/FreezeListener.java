package com.austynzdevv.economysmp.punishment2.listener;

import com.austynzdevv.economysmp.punishment2.EconomyPunishment;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class FreezeListener implements Listener {
   private final EconomyPunishment plugin;

   public FreezeListener(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   private boolean frozen(PlayerEvent event) {
      Player player = event.getPlayer();
      if (player == null) {
         return false;
      } else {
         return this.plugin.getFreezeManager().isFrozen(player.getUniqueId()) && !player.hasPermission("economysmpcore.system.punishment.bypass");
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public void onMove(PlayerMoveEvent event) {
      if (this.plugin.getFreezeManager().isFrozen(event.getPlayer().getUniqueId())) {
         if (!event.getPlayer().hasPermission("economysmpcore.system.punishment.bypass")) {
            if (event.getTo() != null) {
               if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ()) {
                  event.setCancelled(true);
                  event.getPlayer().setVelocity(new Vector(0, 0, 0));
                  event.getPlayer().teleport(event.getFrom());
               }

            }
         }
      }
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
      if (this.plugin.getFreezeManager().isFrozen(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("economysmpcore.system.punishment.bypass")) {
         event.setCancelled(true);
         event.getPlayer().sendMessage(ColorUtil.color("&cYou are frozen."));
      }

   }

   @EventHandler
   public void onCommand(PlayerCommandPreprocessEvent event) {
      if (this.plugin.getFreezeManager().isFrozen(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("economysmpcore.system.punishment.bypass")) {
         event.setCancelled(true);
         event.getPlayer().sendMessage(ColorUtil.color("&cYou cannot use commands while frozen."));
      }

   }

   @EventHandler
   public void onInventory(InventoryOpenEvent event) {
      if (this.plugin.getFreezeManager().isFrozen(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("economysmpcore.system.punishment.bypass")) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (this.plugin.getFreezeManager().isFrozen(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("economysmpcore.system.punishment.bypass")) {
         event.setCancelled(true);
      }

   }
}
