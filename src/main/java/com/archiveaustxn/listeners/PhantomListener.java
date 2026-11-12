package com.archiveaustxn.listeners;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.models.PlayerData;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class PhantomListener implements Listener {

    private final EconomySMP plugin;

    public PhantomListener(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPhantomTarget(EntityTargetEvent event) {
        if (!plugin.getFeatureManager().isEnabled(com.archiveaustxn.managers.FeatureManager.Feature.PHANTOM)) return;
        if (!(event.getEntity() instanceof Phantom)) return;
        if (!(event.getTarget() instanceof Player player)) return;

        PlayerData data = plugin.getPlayerDataManager().get(player);
        if (data != null && !data.isPhantomEnabled()) {
            event.setCancelled(true);
        }
    }
}
