package com.archiveaustxn.commands;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.menus.MediaMenu;
import com.archiveaustxn.utils.CommandLabelUtils;
import com.archiveaustxn.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SocialCommand implements CommandExecutor {

    private final EconomySMP plugin;

    public SocialCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }
        if (!plugin.getConfigManager().isCommandEnabled("SOCIAL")) {
            player.sendMessage(ColorUtils.toComponent("&cSocial commands are currently disabled."));
            return true;
        }

        String normalizedLabel = CommandLabelUtils.normalizeLabel(label, cmd);
        if (normalizedLabel.equals("media")) {
            new MediaMenu(plugin).open(player);
            return true;
        }

        String key = switch (normalizedLabel) {
            case "discord" -> "SOCIAL.DISCORD";
            case "twitter" -> "SOCIAL.TWITTER";
            case "store"   -> "SOCIAL.STORE";
            default        -> null;
        };

        if (key != null) {
            List<String> lines = plugin.getConfigManager().getMessages().getStringList(key);
            for (String line : lines) player.sendMessage(ColorUtils.toComponent(line));
        } else {
            // /social - show all
            for (String section : List.of("SOCIAL.DISCORD", "SOCIAL.TWITTER", "SOCIAL.STORE")) {
                List<String> lines = plugin.getConfigManager().getMessages().getStringList(section);
                for (String line : lines) player.sendMessage(ColorUtils.toComponent(line));
            }
        }
        return true;
    }
}
