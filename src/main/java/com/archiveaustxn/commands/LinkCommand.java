package com.archiveaustxn.commands;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.managers.PvpManager;
import com.archiveaustxn.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * {@code /link} - hands the player a one-time code to enter in Discord with {@code /link code:<code>}.
 *
 * <p>The code is the same short-lived code the ranked arena uses, so linking once covers every
 * Discord feature. The bot verifies the code and keeps the link on its own side; nothing here needs
 * to know a Discord id.</p>
 */
public class LinkCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public LinkCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can link a Discord account.");
            return true;
        }

        PvpManager pvp = plugin.getPvpManager();
        if (pvp == null || !pvp.isSyncEnabled()) {
            send(player, "&cDiscord linking is switched off.");
            return true;
        }

        String code = pvp.createSyncCode(player);
        if (code == null) {
            send(player, "&cCould not create a link code right now. Try again in a moment.");
            return true;
        }

        send(player, "&e&lDiscord Link");
        send(player, "&fYour link code: &e" + code);
        send(player, "&7Run &f/link code:" + code + " &7in our Discord to link your account. "
                + "The code expires soon and only works once.");
        return true;
    }

    private void send(Player player, String message) {
        player.sendMessage(ColorUtils.colorize(message, player));
    }
}
