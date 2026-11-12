package com.archiveaustxn.commands;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaCommandPermissionTest {

    @Test
    void mediaCommandIsRegisteredIndependentlyFromSocial() {
        YamlConfiguration pluginYaml = YamlConfiguration.loadConfiguration(new File("src/main/resources/plugin.yml"));

        assertTrue(pluginYaml.isConfigurationSection("commands.media"), "commands.media must exist in plugin.yml");
        assertEquals("economysmp.command.media", pluginYaml.getString("commands.media.permission"));
        assertEquals("/media", pluginYaml.getString("commands.media.usage"));

        assertTrue(pluginYaml.isConfigurationSection("commands.social"), "commands.social must exist in plugin.yml");
        assertEquals("economysmp.command.social", pluginYaml.getString("commands.social.permission"));
        assertFalse(pluginYaml.getStringList("commands.social.aliases").contains("media"),
                "media must not be an alias of social so its permissions can be managed independently");

        assertTrue(pluginYaml.getBoolean("permissions.economysmp.command.*.children.economysmp.command.media"),
                "economysmp.command.media must be a child of economysmp.command.*");
        assertTrue(pluginYaml.isConfigurationSection("permissions.economysmp.command.media"),
                "permissions.economysmp.command.media must be declared");
        assertEquals(Boolean.TRUE, pluginYaml.getBoolean("permissions.economysmp.command.media.default"));
    }
}