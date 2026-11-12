package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;

/**
 * Checks every second if it's time for key-all.
 */
public class KeyAllTask implements Runnable {

    private final EconomySMP plugin;

    public KeyAllTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.getKeyAllManager().isEnabled()) return;
        plugin.getKeyAllManager().tickOnlinePlayers();
    }

    public static void start(EconomySMP plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new KeyAllTask(plugin), 20L, 20L);
    }
}
