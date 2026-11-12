package com.archiveaustxn.listeners;

import com.archiveaustxn.EconomySMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InvseeListener implements Listener {

    private final EconomySMP plugin;

    public InvseeListener(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.getInvseeManager().isInvseeView(event.getView())) {
            return;
        }

        plugin.getInvseeManager().handleInventoryClick(event);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!plugin.getInvseeManager().isInvseeView(event.getView())) {
            return;
        }

        plugin.getInvseeManager().handleInventoryDrag(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        plugin.getInvseeManager().handleViewerClose(player, event.getInventory());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getInvseeManager().handleViewerQuit(event.getPlayer());
        plugin.getInvseeManager().handleTargetQuit(event.getPlayer());
    }
}
