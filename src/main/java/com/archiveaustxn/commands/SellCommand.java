package com.archiveaustxn.commands;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.menus.SellAllConfirmMenu;
import com.archiveaustxn.menus.SellHistoryMenu;
import com.archiveaustxn.menus.SellMenu;
import com.archiveaustxn.menus.SellProgressMenu;
import com.archiveaustxn.menus.SellStatsAdminMenu;
import com.archiveaustxn.models.SellCategory;
import com.archiveaustxn.utils.CommandLabelUtils;
import com.archiveaustxn.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SellCommand implements CommandExecutor, TabCompleter {

    private final EconomySMP plugin;

    public SellCommand(EconomySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }

        String sub = CommandLabelUtils.normalizeLabel(label, command);

        if (sub.equals("topsell") || sub.equals("sellstats")) {
            return new SellStatsCommand(plugin).onCommand(sender, command, label, args);
        }

        if (args.length > 0 && (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("top"))) {
            return new SellStatsCommand(plugin).onCommand(sender, command, label, args);
        }

        switch (sub) {
            case "sell" -> new SellMenu(plugin).open(player);
            case "sellmulti", "sellmultiplier", "sellprogress" -> {
                SellCategory category = SellCategory.CROPS;
                if (args.length > 0) {
                    category = SellCategory.fromConfigKey(args[0]).orElse(SellCategory.CROPS);
                }
                new SellProgressMenu(plugin, category).open(player);
            }
            case "sellhand"    -> {
                double total = plugin.getShopManager().sellInventory(player, true);
                if (total <= 0) player.sendMessage(ColorUtils.toComponent(
                        plugin.getConfigManager().getMessage("WORTH.NO-SELLABLE")));
            }
            case "sellall"     -> new SellAllConfirmMenu(plugin).open(player);
            case "sellhistory" -> {
                if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
                    new SellStatsAdminMenu(plugin).open(player);
                } else {
                    new SellHistoryMenu(plugin).open(player);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && (alias.equalsIgnoreCase("sellmulti") || alias.equalsIgnoreCase("sellmultiplier") || alias.equalsIgnoreCase("sellprogress"))) {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase();
            for (SellCategory category : SellCategory.values()) {
                if (category.name().toLowerCase().startsWith(input) || category.getConfigKey().toLowerCase().startsWith(input)) {
                    completions.add(category.getConfigKey().toLowerCase());
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }
}

