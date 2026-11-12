package com.archiveaustxn.commands;

import com.archiveaustxn.utils.PermissionUtils;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.menus.BillfordMenu;
import com.archiveaustxn.utils.ColorUtils;
import com.archiveaustxn.utils.SoundUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BillfordCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public BillfordCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Player only.");
            return true;
        }

        String permission = plugin.getConfigManager().getBillford()
                .getString("ACCESS.PERMISSION", "");
        if (!permission.isBlank() && !PermissionUtils.has(player, permission)) {
            player.sendMessage(ColorUtils.toComponent(
                    plugin.getConfigManager().getMessage(
                            "BILLFORD.NO-PERMISSION",
                            "{permission}",
                            permission
                    )
            ));
            return true;
        }

        SoundUtils.play(player, plugin.getConfigManager().getSound("BILLFORD.OPEN"));
        new BillfordMenu(plugin).open(player);
        return true;
    }
}
