package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;

public class FfaMatchTask implements Runnable {

    private final EconomySMP plugin;

    private FfaMatchTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    public static void start(EconomySMP plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new FfaMatchTask(plugin), 1L, 1L);
    }

    @Override
    public void run() {
        if (plugin.getFfaManager() != null && plugin.getFfaManager().isEnabled()) {
            plugin.getFfaManager().tick();
        }
    }
}
