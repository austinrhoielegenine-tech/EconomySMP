package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;

public final class AuctionOrderBotTask implements Runnable {

    private final EconomySMP plugin;

    private AuctionOrderBotTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    public static void start(EconomySMP plugin) {
        // Tick every 20 seconds
        long periodTicks = 400L; 
        plugin.getSpigotScheduler().runAsyncTimer(new AuctionOrderBotTask(plugin), periodTicks, periodTicks);
    }

    @Override
    public void run() {
        if (plugin.getAuctionOrderBotManager() != null) {
            plugin.getAuctionOrderBotManager().tick();
        }
    }
}
