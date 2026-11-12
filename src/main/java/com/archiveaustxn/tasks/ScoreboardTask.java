package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.managers.OptimizationManager;

/**
 * Updates all player scoreboards every 2 ticks (~10x/sec).
 */
public class ScoreboardTask implements Runnable {

    private final EconomySMP plugin;

    public ScoreboardTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getOptimizationManager() != null
                && !plugin.getOptimizationManager().shouldRun(OptimizationManager.OptimizedTask.SCOREBOARD)) {
            return;
        }
        plugin.getScoreboardManager().updateAll();
    }

    public static void start(EconomySMP plugin) {
        if (!plugin.getScoreboardManager().isRuntimeSupported()) {
            return;
        }
        long interval = plugin.getScoreboardManager().getUpdateIntervalTicks();
        plugin.getSpigotScheduler().runGlobalTimer(new ScoreboardTask(plugin), interval, interval);
    }
}
