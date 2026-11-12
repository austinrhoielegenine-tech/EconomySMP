package com.archiveaustxn.menus;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.managers.SpawnManager;

public class SpawnMenu extends TeleportAreaMenu {

    public SpawnMenu(EconomySMP plugin) {
        super(
                plugin,
                plugin.getConfigManager().getMenus().getString("SPAWN-MENU.TITLE", "&8Spawn areas"),
                plugin.getConfigManager().getMenus().getInt("SPAWN-MENU.SIZE", 54)
        );
    }

    @Override
    protected SpawnManager.AreaType getAreaType() {
        return SpawnManager.AreaType.SPAWN;
    }

    @Override
    protected String getMenuPath() {
        return "SPAWN-MENU";
    }

    @Override
    protected String getTeleportType() {
        return "SPAWN";
    }

    @Override
    protected String getEmptyTitle() {
        return "&cNo spawn areas";
    }

    @Override
    protected String getEmptyLore() {
        return "&7There are no valid cuboid-based spawn areas yet.";
    }
}
