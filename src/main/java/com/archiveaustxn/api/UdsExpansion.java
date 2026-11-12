package com.archiveaustxn.api;

import com.archiveaustxn.EconomySMP;
import org.jetbrains.annotations.NotNull;

public class UdsExpansion extends EconomyExpansion {

    public UdsExpansion(EconomySMP plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "uds";
    }
}
