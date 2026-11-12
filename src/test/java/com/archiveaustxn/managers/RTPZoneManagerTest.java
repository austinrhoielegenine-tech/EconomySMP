package com.archiveaustxn.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RTPZoneManagerTest {

    @Test
    void bundledConfigDefinesDefaultTitleFadeOut() {
        var file = com.archiveaustxn.BundledResources.path("config.yml").toFile();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        assertEquals(10, config.getInt("RTP-ZONE.TITLE-FADE-OUT-TICKS"));
    }

    @Test
    void titleFadeOutIsClampedToAtLeastOneTick() {
        assertEquals(1, RTPZoneManager.normalizeTitleFadeOutTicks(-10));
        assertEquals(1, RTPZoneManager.normalizeTitleFadeOutTicks(0));
        assertEquals(10, RTPZoneManager.normalizeTitleFadeOutTicks(10));
    }
}
