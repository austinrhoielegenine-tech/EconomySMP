package com.archiveaustxn.commands;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.menus.RTPMenu;
import com.archiveaustxn.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public RTPCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Player only.");
            return true;
        }

        if (!plugin.getConfigManager().isCommandEnabled("RTP")) {
            player.sendMessage(ColorUtils.toComponent("&cRTP command is currently disabled."));
            return true;
        }

        if (!plugin.getRtpManager().isEnabled()) {
            player.sendMessage(ColorUtils.toComponent(
                    plugin.getConfigManager().getRtp().getString("MESSAGES.DISABLED", "&cRTP is disabled.")));
            return true;
        }

        if (args.length == 0) {
            new RTPMenu(plugin).open(player);
            return true;
        }

        plugin.getRtpManager().queueCommandTeleport(player, args[0]);
        return true;
    }
}
