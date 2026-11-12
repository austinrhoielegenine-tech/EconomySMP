package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;

public class SpawnerGenerationTask implements Runnable {

    private final EconomySMP plugin;

    private SpawnerGenerationTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    public static void start(EconomySMP plugin) {
        long configuredSeconds = plugin.getConfigManager().getSpawners()
                .getLong("SETTINGS.GENERATION_INTERVAL_SECONDS", 5L);
        long periodTicks = Math.max(20L, configuredSeconds * 20L);
        plugin.getSpigotScheduler().runGlobalTimer(new SpawnerGenerationTask(plugin), periodTicks, periodTicks);

        // 1-second auto-refresh task for open Spawner GUI windows (storage and main menu)
        plugin.getSpigotScheduler().runGlobalTimer(() -> {
            if (plugin.getSpawnerManager() != null && plugin.getSpawnerManager().isEnabled()) {
                plugin.getSpawnerManager().refreshOpenStorageMenus();
                plugin.getSpawnerManager().refreshOpenMainMenus();
            }
        }, 20L, 20L);
    }

    @Override
    public void run() {
        if (plugin.getSpawnerManager() != null && plugin.getSpawnerManager().isEnabled()) {
            plugin.getSpawnerManager().processGeneration();
        }
    }
}
