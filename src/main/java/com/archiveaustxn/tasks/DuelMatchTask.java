package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;

public class DuelMatchTask implements Runnable {

    private final EconomySMP plugin;

    private DuelMatchTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    public static void start(EconomySMP plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new DuelMatchTask(plugin), 1L, 1L);
    }

    @Override
    public void run() {
        if (plugin.getDuelManager() != null && plugin.getDuelManager().isEnabled()) {
            plugin.getDuelManager().tick();
        }
    }
}
