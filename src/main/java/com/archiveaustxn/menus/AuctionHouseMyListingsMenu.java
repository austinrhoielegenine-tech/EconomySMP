package com.archiveaustxn.menus;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.managers.AuctionHouseManager;
import org.bukkit.entity.Player;

public final class AuctionHouseMyListingsMenu extends BaseMenu {

    private final int page;

    public AuctionHouseMyListingsMenu(
            EconomySMP plugin,
            int page,
            AuctionHouseManager.AuctionSort ignoredSort
    ) {
        super(plugin, plugin.getAuctionHouseManager().getMyListingsTitle(), 54);
        this.page = Math.max(1, page);
    }

    @Override
    public void build(Player player) {
    }

    @Override
    public void open(Player player) {
        new PlayerAuctionGui(plugin, page).open(player);
    }
}
