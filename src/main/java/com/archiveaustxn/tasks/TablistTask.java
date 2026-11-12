package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.managers.OptimizationManager;

/**
 * Updates tablist header/footer and entry names for all players every 40 ticks (2s).
 */
public class TablistTask implements Runnable {

    private final EconomySMP plugin;

    public TablistTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getOptimizationManager() != null
                && !plugin.getOptimizationManager().shouldRun(OptimizationManager.OptimizedTask.TABLIST)) {
            return;
        }
        plugin.getTablistManager().updateAll();
        plugin.getTablistManager().updateNamesAll();
    }

    public static void start(EconomySMP plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new TablistTask(plugin), 40L, 40L);
    }
}
