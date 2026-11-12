package com.archiveaustxn.tasks;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.managers.ShardManager;
import org.bukkit.entity.Player;

/**
 * Runs every minute. Awards passive "everywhere" shards to players
 * who pass the configured eligibility checks for the perk.
 *
 * Spawn-cuboid shards are handled separately by {@link ShardCuboidTask}
 * which runs every second and drives the per-player countdown display.
 */
public class ShardTask implements Runnable {

    private final EconomySMP plugin;
    private int minuteTick = 0;

    public ShardTask(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ShardManager sm = plugin.getShardManager();

        int intervalMinutes = Math.max(1, sm.getEverywhereEveryMinutes());
        minuteTick = (minuteTick + 1) % intervalMinutes;

        if (!sm.isEverywhereEnabled() || minuteTick != 0) {
            return;
        }

        plugin.getSpigotScheduler().forEachOnlinePlayer((Player player) -> tickPlayer(player, sm));
    }

    private void tickPlayer(Player player, ShardManager sm) {
        if (sm.getEverywhereEligibility(player) != ShardManager.EverywhereEligibilityResult.ELIGIBLE) {
            return;
        }

        long multiplier = sm.getMultiplier(player.getUniqueId());
        long amount = sm.getEverywhereAmount() * multiplier;

        sm.giveShards(player, amount, false);
        sm.sendEverywhereRewardFeedback(player, amount, multiplier);
    }

    public static void start(EconomySMP plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new ShardTask(plugin), 60 * 20L, 60 * 20L); // every minute
    }
}
