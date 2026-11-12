package com.archiveaustxn.commands;

import com.archiveaustxn.utils.PermissionUtils;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.menus.DuelClaimMenu;
import com.archiveaustxn.menus.DuelCreateMenu;
import com.archiveaustxn.menus.DuelQueueMenu;
import com.archiveaustxn.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public DuelCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("player only.");
            return true;
        }

        if (!plugin.getDuelManager().isEnabled() && (args.length == 0 || !"reload".equalsIgnoreCase(args[0]))) {
            plugin.getDuelManager().sendMessage(player, "DISABLED", "&cduels are currently disabled.");
            return true;
        }

        if (args.length == 0) {
            new DuelQueueMenu(plugin).open(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();
        if (subcommand.equals("claims")) {
            new DuelClaimMenu(plugin, 1).open(player);
            return true;
        }
        if (subcommand.equals("reload")) {
            if (!PermissionUtils.has(player, "economysmp.admin.duels")) {
                plugin.getDuelManager().sendMessage(player, "NO_PERMISSION_RELOAD", "&cyou do not have permission to reload duels.");
                return true;
            }
            plugin.getConfigManager().reloadDuels();
            plugin.getDuelManager().reload();
            plugin.getDuelManager().sendMessage(player, "RELOAD_SUCCESS", "&aduels config reloaded.");
            return true;
        }
        if (subcommand.equals("accept")) {
            plugin.getDuelManager().acceptChallenge(player, args.length > 1 ? args[1] : null);
            return true;
        }
        if (subcommand.equals("deny")) {
            plugin.getDuelManager().denyChallenge(player, args.length > 1 ? args[1] : null);
            return true;
        }

        Player target = plugin.getHideManager().findOnlinePlayer(player, args[0]);
        if (target == null) {
            plugin.getDuelManager().sendMessage(player, "PLAYER_NOT_ONLINE", "&cthat player is not online.");
            return true;
        }

        new DuelCreateMenu(plugin, target.getUniqueId()).open(player);
        return true;
    }
}
