package com.archiveaustxn.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EcseeConfigurationTest {

    @Test
    void bundledCommandPermissionAndSettingsArePresent() throws Exception {
        YamlConfiguration plugin = new YamlConfiguration();
        plugin.load(new File("src/main/resources/plugin.yml"));

        assertTrue(plugin.isConfigurationSection("commands.ecsee"));
        assertEquals("/ecsee <player>", plugin.getString("commands.ecsee.usage"));
        assertTrue(plugin.getBoolean(
                "permissions.economysmp.admin.children.economysmp.admin.ecsee"
        ));
        assertEquals(
                "op",
                plugin.getString("permissions.economysmp.admin.ecsee.default")
        );

        YamlConfiguration enderChest = new YamlConfiguration();
        enderChest.load(com.archiveaustxn.BundledResources.path("ender-chest.yml").toFile());

        assertTrue(enderChest.getBoolean("ENDER-CHEST.ECSEE.ENABLED"));
        assertEquals(
                "economysmp.admin.ecsee",
                enderChest.getString("ENDER-CHEST.ECSEE.PERMISSION")
        );
        assertTrue(enderChest.getLong("ENDER-CHEST.ECSEE.AUTO-REFRESH-TICKS") > 0L);
    }
}
