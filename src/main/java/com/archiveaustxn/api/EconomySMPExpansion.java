package com.archiveaustxn.api;

import com.archiveaustxn.EconomySMP;
import org.jetbrains.annotations.NotNull;

public class EconomySMPExpansion extends EconomyExpansion {

    public EconomySMPExpansion(EconomySMP plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "economysmp";
    }
}
