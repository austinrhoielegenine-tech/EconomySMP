package com.archiveaustxn.commands;

import com.archiveaustxn.utils.PermissionUtils;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {

    private static final String PERMISSION = "economysmp.staff.chat.use";

    private final EconomySMP plugin;

    public StaffChatCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player && !PermissionUtils.has(player, PERMISSION)) {
            player.sendMessage(ColorUtils.toComponent(
                    plugin.getConfigManager().getMessageOrDefault("STAFFCHAT.NO_PERMISSION", "&cYou do not have permission to use staff chat.")
            ));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ColorUtils.toComponent(
                    plugin.getConfigManager().getMessageOrDefault("STAFFCHAT.USAGE", "&cUsage: /staffchat <message>")
            ));
            return true;
        }

        plugin.getNetworkStaffChatManager().sendStaffChat(sender, String.join(" ", args));
        return true;
    }
}
