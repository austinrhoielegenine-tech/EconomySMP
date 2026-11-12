package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;

/**
 * Runs every N minutes. Broadcasts countdown warnings then clears entities.
 */
public class ClearLagTask implements Runnable {

    private static final int[] WARN_AT = {60, 30, 15, 10, 5, 4, 3, 2, 1};

    private final EconomySMP plugin;
    private int secondsLeft;

    public ClearLagTask(EconomySMP plugin) {
        this.plugin = plugin;
        this.secondsLeft = plugin.getClearLagManager().getIntervalMinutes() * 60;
    }

    @Override
    public void run() {
        if (!plugin.getClearLagManager().isEnabled()) return;
        secondsLeft--;
        for (int w : WARN_AT) {
            if (secondsLeft == w) {
                plugin.getClearLagManager().broadcastCountdown(w);
                return;
            }
        }
        if (secondsLeft <= 0) {
            plugin.getClearLagManager().clearEntities();
            secondsLeft = plugin.getClearLagManager().getIntervalMinutes() * 60;
        }
    }

    public static void start(EconomySMP plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new ClearLagTask(plugin), 20L, 20L); // every second
    }
}
