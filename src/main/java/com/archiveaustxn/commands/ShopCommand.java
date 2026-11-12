package com.archiveaustxn.commands;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.menus.ShopMenu;
import com.archiveaustxn.utils.ColorUtils;
import com.archiveaustxn.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public ShopCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            return reload(sender);
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.colorize(text(
                    "SHOP-GUI.MESSAGES.PLAYER-ONLY",
                    "&cOnly players can use this command."
            )));
            return true;
        }

        if (command.getName().equalsIgnoreCase("shardshop")) {
            new ShopMenu(plugin, "SHARD-MENU", 0).open(player);
            return true;
        }

        if (args.length == 0) {
            new ShopMenu(plugin).open(player);
            return true;
        }

        player.sendMessage(ColorUtils.toComponent(text(
                "SHOP-GUI.MESSAGES.USAGE",
                "&cUsage: /shop [reload]"
        )));
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (!PermissionUtils.has(sender, "economysmp.admin.shop")) {
            sender.sendMessage(ColorUtils.colorize(text(
                    "SHOP-GUI.MESSAGES.NO-RELOAD-PERMISSION",
                    "&cYou do not have permission to reload shop settings."
            )));
            return true;
        }

        plugin.getConfigManager().reloadShop();
        plugin.getConfigManager().reloadMenus();
        plugin.getConfigManager().reloadSounds();
        plugin.getShopManager().reload();
        sender.sendMessage(ColorUtils.colorize(text(
                "SHOP-GUI.MESSAGES.RELOADED",
                "&aShop configuration reloaded."
        )));
        return true;
    }

    private String text(String path, String fallback) {
        return plugin.getConfigManager().getShop().getString(path, fallback);
    }
}
