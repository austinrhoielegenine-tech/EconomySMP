package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;

public class RTPCacheTask implements Runnable {

    private final EconomySMP plugin;

    public RTPCacheTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getRtpManager() == null || !plugin.getRtpManager().isEnabled()) {
            return;
        }
        plugin.getRtpManager().refillPreCacheAllWorlds();
    }

    public static void start(EconomySMP plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new RTPCacheTask(plugin), 100L, 20L);
    }
}
