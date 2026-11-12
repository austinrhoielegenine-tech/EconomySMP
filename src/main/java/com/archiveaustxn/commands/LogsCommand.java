package com.archiveaustxn.commands;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.menus.PlayerLogsMenu;
import com.archiveaustxn.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LogsCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public LogsCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player viewer)) {
            sender.sendMessage("Player only.");
            return true;
        }

        if (!viewer.hasPermission("economysmp.admin.logs")) {
            viewer.sendMessage(ColorUtils.toComponent("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length != 1) {
            viewer.sendMessage(ColorUtils.toComponent("&cUsage: /logs <player>"));
            return true;
        }

        String input = args[0].trim();
        Player onlineTarget = findOnlineTarget(input);
        UUID targetUuid = onlineTarget == null
                ? plugin.getDatabaseManager().findPlayerUuidByUsername(input)
                : onlineTarget.getUniqueId();
        if (targetUuid == null) {
            viewer.sendMessage(ColorUtils.toComponent("&cPlayer not found."));
            return true;
        }

        String targetName = onlineTarget == null
                ? plugin.getDatabaseManager().getLastKnownUsername(targetUuid)
                : onlineTarget.getName();
        if (targetName == null || targetName.isBlank()) {
            targetName = input;
        }

        new PlayerLogsMenu(plugin, targetUuid, targetName).open(viewer);
        return true;
    }

    private Player findOnlineTarget(String username) {
        Player exact = Bukkit.getPlayerExact(username);
        if (exact != null) {
            return exact;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(username)) {
                return player;
            }
        }
        return null;
    }
}
