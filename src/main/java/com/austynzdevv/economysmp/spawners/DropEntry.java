package com.austynzdevv.economysmp.spawners;

import org.bukkit.Material;

public record DropEntry(Material material, int min, int max, int chance) {
}
