package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;

/**
 * Checks every 30 seconds whether the Billford trade rotation timer has
 * expired and, if so, advances to the next trade.
 */
public class BillfordTask implements Runnable {

    private final EconomySMP plugin;

    private BillfordTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    public static void start(EconomySMP plugin) {
        // 600 ticks = 30 seconds; first check after 30 s as well
        plugin.getSpigotScheduler().runGlobalTimer(new BillfordTask(plugin), 600L, 600L);
    }

    @Override
    public void run() {
        if (!plugin.getFeatureManager().isEnabled(com.archiveaustxn.managers.FeatureManager.Feature.BILLFORD)) {
            return;
        }
        if (plugin.getBillfordManager().isTimeToAdvance()) {
            plugin.getBillfordManager().advanceTrade();
        }
    }
}
