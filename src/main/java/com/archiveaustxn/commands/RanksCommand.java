package com.archiveaustxn.commands;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.menus.RanksMenu;
import com.archiveaustxn.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public RanksCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }
        if (!plugin.getConfigManager().isCommandEnabled("RANKS")) {
            player.sendMessage(ColorUtils.toComponent("&cRanks command is currently disabled."));
            return true;
        }

        RanksMenu menu = new RanksMenu(plugin);
        if (!menu.hasValidButtons()) {
            player.sendMessage(ColorUtils.toComponent("&cThe ranks menu has no usable buttons configured."));
            return true;
        }

        menu.open(player);
        return true;
    }
}
