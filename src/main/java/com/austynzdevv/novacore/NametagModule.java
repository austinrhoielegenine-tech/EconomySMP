package com.austynzdevv.novacore;

import com.austynzdevv.novacore.economy.CoreEconomy;
import com.austynzdevv.novacore.teams.EconomyTeams;
import com.austynzdevv.novacore.teams.Team;
import com.austynzdevv.novacore.util.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rotating multiline player nameplates. Uses the player's visible custom name;
 * no TAB, ProtocolLib, or PlaceholderAPI dependency is required.
 */
public final class NametagModule implements Module {
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.builder().character('&').hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private final NovaCore plugin;
    private final Map<UUID, Component> originalNames = new ConcurrentHashMap<>();
    private FileConfiguration config;
    private int taskId = -1;
    private long rotation;
    private boolean enabled;

    public NametagModule(NovaCore plugin) { this.plugin = plugin; }

    @Override public String getName() { return "nametag"; }

    @Override public void onEnable() {
        File file = new File(plugin.getDataFolder(), "nametag.yml");
        if (!file.exists()) plugin.saveResource("nametag.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
        enabled = config.getBoolean("enabled", true);
        if (!enabled) return;
        for (Player player : Bukkit.getOnlinePlayers()) apply(player);
        long interval = Math.max(3L, Math.min(5L, config.getLong("rotation.interval-seconds", 4L)));
        taskId = Tasks.runTimer(plugin, () -> {
            rotation++;
            for (Player player : Bukkit.getOnlinePlayers()) apply(player);
        }, 1L, interval * 20L);
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler public void join(org.bukkit.event.player.PlayerJoinEvent event) {
                Tasks.runLater(plugin, () -> apply(event.getPlayer()), 2L);
            }
            @org.bukkit.event.EventHandler public void quit(org.bukkit.event.player.PlayerQuitEvent event) {
                restore(event.getPlayer());
            }
        }, plugin);
    }

    private void apply(Player player) {
        if (!player.isOnline()) return;
        originalNames.putIfAbsent(player.getUniqueId(), player.customName());
        String playerName = config.getString("name-format", "&f%player_name%").replace("%player_name%", player.getName());
        String line;
        if ((rotation & 1L) == 0L) {
            String money = config.getString("rotations.money-team",
                    "&#00FC00&l$ &fMoney &#00FC00%money% &8| &#00A4FC🪓 &fTeam &#00A4FC%team%");
            Team team = null;
            EconomyTeams teams = EconomyTeams.get();
            if (teams != null && teams.getTeams() != null) team = teams.getTeams().getByPlayer(player.getUniqueId());
            line = money.replace("%money%", CoreEconomy.formatMoney(CoreEconomy.balanceOf(player)))
                    .replace("%team%", team == null ? config.getString("no-team-text", "None") : team.getName());
        } else {
            String combat = config.getString("rotations.kills-deaths",
                    "&#FC0000🗡 &fKills &#FC0000%kills% &8| &#F97603☠ &fDeaths &#F97603%deaths%");
            line = combat.replace("%kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS)))
                    .replace("%deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS)));
        }
        // Newline creates the second line directly beneath the player's name.
        player.customName(LEGACY.deserialize(playerName + "\n" + line));
        player.setCustomNameVisible(true);
    }

    private void restore(Player player) {
        if (!originalNames.containsKey(player.getUniqueId())) return;
        Component original = originalNames.remove(player.getUniqueId());
        player.customName(original);
        player.setCustomNameVisible(original != null);
    }

    @Override public void reload() {
        if (taskId != -1) {
            Tasks.cancel(taskId);
            taskId = -1;
        }
        File file = new File(plugin.getDataFolder(), "nametag.yml");
        if (!file.exists()) plugin.saveResource("nametag.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
        enabled = config.getBoolean("enabled", true);
        if (!enabled) {
            for (Player player : Bukkit.getOnlinePlayers()) restore(player);
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) apply(player);
        long interval = Math.max(3L, Math.min(5L, config.getLong("rotation.interval-seconds", 4L)));
        taskId = Tasks.runTimer(plugin, () -> {
            rotation++;
            for (Player player : Bukkit.getOnlinePlayers()) apply(player);
        }, 1L, interval * 20L);
    }

    @Override public void onDisable() {
        if (taskId != -1) Tasks.cancel(taskId);
        for (Player player : Bukkit.getOnlinePlayers()) restore(player);
        originalNames.clear();
    }
}
