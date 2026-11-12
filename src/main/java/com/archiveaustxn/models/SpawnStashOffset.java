package com.archiveaustxn.models;

public record SpawnStashOffset(int x, int y, int z) {
    public static final SpawnStashOffset ZERO = new SpawnStashOffset(0, 0, 0);
}
