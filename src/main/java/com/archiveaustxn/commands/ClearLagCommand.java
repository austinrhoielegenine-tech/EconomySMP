package com.archiveaustxn.commands;

import com.archiveaustxn.utils.PermissionUtils;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClearLagCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public ClearLagCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!PermissionUtils.has(sender, "economysmp.admin.clearlag")) {
            sender.sendMessage(ColorUtils.toComponent("&cNo permission."));
            return true;
        }
        plugin.getClearLagManager().clearEntities();
        return true;
    }
}
