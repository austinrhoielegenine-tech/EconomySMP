package com.archiveaustxn.commands;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.utils.ColorUtils;
import com.archiveaustxn.utils.NightVisionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NightVisionCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public NightVisionCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }
        boolean enabled = NightVisionUtils.toggle(plugin, player);
        if (!enabled) {
            player.sendMessage(ColorUtils.toComponent("&7Night vision &cdisabled&7."));
        } else {
            player.sendMessage(ColorUtils.toComponent("&7Night vision &aenabled&7."));
        }
        return true;
    }
}
