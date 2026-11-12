package com.archiveaustxn.commands;

import com.archiveaustxn.EconomySMP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DrawCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public DrawCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("player only.");
            return true;
        }

        plugin.getDuelManager().requestDraw(player);
        return true;
    }
}
