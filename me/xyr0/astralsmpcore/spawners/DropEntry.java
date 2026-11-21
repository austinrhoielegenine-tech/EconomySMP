package me.xyr0.astralsmpcore.spawners;

import org.bukkit.Material;

public record DropEntry(Material material, int min, int max, int chance) {
}
