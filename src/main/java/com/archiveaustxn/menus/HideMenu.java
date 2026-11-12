package com.archiveaustxn.menus;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.commands.HideCommand;
import com.archiveaustxn.managers.HideManager;
import com.archiveaustxn.models.HideState;
import com.archiveaustxn.utils.ItemUtils;
import com.archiveaustxn.utils.PermissionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class HideMenu extends BaseMenu {

    public HideMenu(EconomySMP plugin) {
        super(
                plugin,
                plugin.getConfigManager().getHide().getString("GUI.MAIN.TITLE", "&8Hide"),
                normalizeSize(plugin.getConfigManager().getHide().getInt("GUI.MAIN.SIZE", 27))
        );
    }

    @Override
    public void build(Player player) {
        clear();
        fill(Material.BLACK_STAINED_GLASS_PANE);
        HideManager manager = plugin.getHideManager();
        HideState state = manager.getState(player.getUniqueId());

        set(4, ItemUtils.createItem(
                Material.NAME_TAG,
                "&bHide status",
                state == null
                        ? List.of("&7Status: &cinactive")
                        : List.of(
                                "&7Status: &a" + state.mode().name(),
                                "&7Alias: &f" + state.alias(),
                                "&7Skin: &f" + (state.skinUsername().isBlank() ? "Original" : state.skinUsername())
                        )
        ));
        set(11, ItemUtils.createItem(
                Material.ENDER_EYE,
                "&bScramble",
                List.of("&7Generate a stable random identity.", "", "&eClick to activate.")
        ));
        set(13, ItemUtils.createItem(
                Material.PLAYER_HEAD,
                "&dDisguise",
                List.of("&7Choose a configured name and skin.", "", "&eClick to select.")
        ));
        set(15, ItemUtils.createItem(
                Material.RED_DYE,
                "&cRemove hide",
                List.of("&7Restore your real identity.", "", "&eClick to remove.")
        ));
        if (PermissionUtils.has(player, HideManager.ADMIN_PERMISSION)) {
            set(22, ItemUtils.createItem(
                    Material.SPYGLASS,
                    "&cHidden players",
                    List.of("&7Inspect and manage active hide states.", "", "&eClick to open.")
            ));
        }
    }

    @Override
    public void handleClick(int slot, Player player, ClickType clickType) {
        HideCommand command = new HideCommand(plugin);
        if (slot == 11) {
            player.closeInventory();
            command.sendResult(player, plugin.getHideManager().scramble(player));
        } else if (slot == 13) {
            new DisguiseAliasMenu(plugin, 0).open(player);
        } else if (slot == 15) {
            player.closeInventory();
            command.sendResult(player, plugin.getHideManager().remove(player, false));
        } else if (slot == 22 && PermissionUtils.has(player, HideManager.ADMIN_PERMISSION)) {
            new HideListMenu(plugin, 0).open(player);
        }
    }

    private static int normalizeSize(int configured) {
        int size = Math.max(9, Math.min(54, configured));
        return size - (size % 9);
    }
}
