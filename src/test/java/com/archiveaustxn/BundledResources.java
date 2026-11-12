package com.archiveaustxn;

import com.archiveaustxn.managers.ConfigManager;

import java.nio.file.Path;

/** Resolves bundled config files, which live under {@code src/main/resources/core/<folder>/}. */
public final class BundledResources {

    private BundledResources() {
    }

    public static Path path(String name) {
        return Path.of("src/main/resources", ConfigManager.bundledPath(name));
    }

    public static String classpath(String name) {
        return ConfigManager.bundledPath(name);
    }
}
