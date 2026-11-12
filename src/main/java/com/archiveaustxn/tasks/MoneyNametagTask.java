package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;

/**
 * Keeps the balance under every player's name up to date. The client draws the line itself, so
 * there is no position to maintain and this only has to keep up with money changing hands.
 */
public class MoneyNametagTask implements Runnable {

    private final EconomySMP plugin;

    public MoneyNametagTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getMoneyNametagManager().updateAll();
    }

    public static void start(EconomySMP plugin) {
        plugin.getMoneyNametagManager().purgeOrphanedDisplays();
        long interval = plugin.getMoneyNametagManager().getUpdateIntervalTicks();
        plugin.getSpigotScheduler().runGlobalTimer(new MoneyNametagTask(plugin), interval, interval);
    }
}
