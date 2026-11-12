package com.archiveaustxn.managers;

import com.archiveaustxn.EconomySMP;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RTPPriorityQueueTest {

    private Server originalServer;
    private Server mockServer;
    private org.bukkit.World mockWorld;

    @BeforeEach
    void setUp() throws Exception {
        originalServer = Bukkit.getServer();

        mockWorld = (org.bukkit.World) Proxy.newProxyInstance(
                org.bukkit.World.class.getClassLoader(),
                new Class<?>[]{org.bukkit.World.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) {
                        return "world";
                    }
                    if (method.getName().equals("getEnvironment")) {
                        return org.bukkit.World.Environment.NORMAL;
                    }
                    return null;
                }
        );

        mockServer = (Server) Proxy.newProxyInstance(
                Server.class.getClassLoader(),
                new Class<?>[]{Server.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getWorlds")) {
                        return List.of(mockWorld);
                    }
                    if (method.getName().equals("getWorld")) {
                        return mockWorld;
                    }
                    if (method.getName().equals("getLogger")) {
                        return java.util.logging.Logger.getLogger("Minecraft");
                    }
                    return null;
                }
        );

        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, mockServer);
    }

    @AfterEach
    void tearDown() throws Exception {
        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, originalServer);
    }

    private EconomySMP createMockPlugin(YamlConfiguration rtpConfig) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> newConstructor = reflectionFactory.newConstructorForSerialization(EconomySMP.class, objectConstructor);
        EconomySMP plugin = (EconomySMP) newConstructor.newInstance();

        ConfigManager configManager = new ConfigManager(plugin);
        Field rtpField = ConfigManager.class.getDeclaredField("rtp");
        rtpField.setAccessible(true);
        rtpField.set(configManager, rtpConfig);

        Field configField = ConfigManager.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(configManager, new YamlConfiguration());

        Field soundsField = ConfigManager.class.getDeclaredField("sounds");
        soundsField.setAccessible(true);
        soundsField.set(configManager, new YamlConfiguration());

        Field cmField = EconomySMP.class.getDeclaredField("configManager");
        cmField.setAccessible(true);
        cmField.set(plugin, configManager);

        FeatureManager featureManager = new FeatureManager(plugin);
        Field fmField = EconomySMP.class.getDeclaredField("featureManager");
        fmField.setAccessible(true);
        fmField.set(plugin, featureManager);

        TeleportManager teleportManager = new TeleportManager(plugin);
        Field tmField = EconomySMP.class.getDeclaredField("teleportManager");
        tmField.setAccessible(true);
        tmField.set(plugin, teleportManager);

        return plugin;
    }

    private Player createMockPlayer(UUID uuid, String name, Set<String> permissions) {
        final Player[] playerHolder = new Player[1];
        Player playerProxy = (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getUniqueId")) {
                        return uuid;
                    }
                    if (method.getName().equals("getName")) {
                        return name;
                    }
                    if (method.getName().equals("hasPermission")) {
                        String perm = (String) args[0];
                        return permissions.contains(perm.toLowerCase(Locale.ROOT));
                    }
                    if (method.getName().equals("getEffectivePermissions")) {
                        Set<PermissionAttachmentInfo> effective = new HashSet<>();
                        for (String perm : permissions) {
                            effective.add(new PermissionAttachmentInfo(playerHolder[0], perm, null, true));
                        }
                        return effective;
                    }
                    if (method.getName().equals("sendMessage")) {
                        return null;
                    }
                    if (method.getName().equals("isOnline")) {
                        return true;
                    }
                    return null;
                }
        );
        playerHolder[0] = playerProxy;
        return playerProxy;
    }

    @Test
    void testPriorityResolutionFromConfig() throws Exception {
        YamlConfiguration rtpConfig = new YamlConfiguration();
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.ENABLED", true);
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.DEFAULT-PRIORITY", 0);
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.PERMISSIONS.economysmp.rtp.priority.vip++", 30);
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.PERMISSIONS.economysmp.rtp.priority.vip+", 20);
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.PERMISSIONS.economysmp.rtp.priority.vip", 10);
        rtpConfig.set("WORLD-SETTINGS.world.MAX-RADIUS", 5000);

        EconomySMP plugin = createMockPlugin(rtpConfig);
        RTPManager rtpManager = new RTPManager(plugin);

        Player regular = createMockPlayer(UUID.randomUUID(), "Player1", Set.of());
        Player vip = createMockPlayer(UUID.randomUUID(), "PlayerVIP", Set.of("economysmp.rtp.priority.vip"));
        Player vipPlusPlus = createMockPlayer(UUID.randomUUID(), "PlayerVIPPlusPlus", Set.of("economysmp.rtp.priority.vip++"));
        Player customNumeric = createMockPlayer(UUID.randomUUID(), "PlayerNumeric", Set.of("economysmp.rtp.priority.50"));

        assertEquals(0, rtpManager.getPlayerPriority(regular));
        assertEquals(10, rtpManager.getPlayerPriority(vip));
        assertEquals(30, rtpManager.getPlayerPriority(vipPlusPlus));
        assertEquals(50, rtpManager.getPlayerPriority(customNumeric));
    }

    @Test
    void testPriorityQueueOrderingAndPosition() throws Exception {
        YamlConfiguration rtpConfig = new YamlConfiguration();
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.ENABLED", true);
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.DEFAULT-PRIORITY", 0);
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.PERMISSIONS.economysmp.rtp.priority.vip++", 30);
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.PERMISSIONS.economysmp.rtp.priority.vip+", 20);
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.PERMISSIONS.economysmp.rtp.priority.vip", 10);
        rtpConfig.set("WORLD-SETTINGS.world.MAX-RADIUS", 5000);
        rtpConfig.set("SETTINGS.PLAYERS-IN-RTP", 1); // 1 active slot

        EconomySMP plugin = createMockPlugin(rtpConfig);
        RTPManager rtpManager = new RTPManager(plugin);

        Player dummy = createMockPlayer(UUID.randomUUID(), "Dummy", Set.of());
        assertTrue(rtpManager.queueCommandTeleport(dummy, "world")); // Occupies active slot 1

        UUID regularId = UUID.randomUUID();
        UUID vipId = UUID.randomUUID();
        UUID vipPlusPlusId = UUID.randomUUID();

        Player regular = createMockPlayer(regularId, "Regular", Set.of());
        Player vip = createMockPlayer(vipId, "VIP", Set.of("economysmp.rtp.priority.vip"));
        Player vipPlusPlus = createMockPlayer(vipPlusPlusId, "VIP++", Set.of("economysmp.rtp.priority.vip++"));

        // Add regular first, then VIP, then VIP++
        assertTrue(rtpManager.queueCommandTeleport(regular, "world"));
        assertTrue(rtpManager.queueCommandTeleport(vip, "world"));
        assertTrue(rtpManager.queueCommandTeleport(vipPlusPlus, "world"));

        assertEquals(3, rtpManager.getQueueSize());
        assertTrue(rtpManager.isInQueue(regularId));
        assertTrue(rtpManager.isInQueue(vipId));
        assertTrue(rtpManager.isInQueue(vipPlusPlusId));

        // VIP++ should jump to position 1, VIP position 2, Regular position 3
        assertEquals(1, rtpManager.getQueuePosition(vipPlusPlusId));
        assertEquals(2, rtpManager.getQueuePosition(vipId));
        assertEquals(3, rtpManager.getQueuePosition(regularId));

        // Re-queuing an existing player should return false (already in queue)
        assertFalse(rtpManager.queueCommandTeleport(vip, "world"));
    }

    @Test
    void testPriorityQueueDisabled() throws Exception {
        YamlConfiguration rtpConfig = new YamlConfiguration();
        rtpConfig.set("SETTINGS.PRIORITY-QUEUE.ENABLED", false);
        rtpConfig.set("WORLD-SETTINGS.world.MAX-RADIUS", 5000);
        rtpConfig.set("SETTINGS.PLAYERS-IN-RTP", 1);

        EconomySMP plugin = createMockPlugin(rtpConfig);
        RTPManager rtpManager = new RTPManager(plugin);

        Player dummy = createMockPlayer(UUID.randomUUID(), "Dummy", Set.of());
        assertTrue(rtpManager.queueCommandTeleport(dummy, "world")); // Occupies active slot 1

        Player player = createMockPlayer(UUID.randomUUID(), "Player1", Set.of("economysmp.rtp.priority.vip++"));

        assertFalse(rtpManager.isPriorityQueueEnabled());
        // When queue disabled and slots full, request should fail immediately without adding to queue
        boolean result = rtpManager.queueCommandTeleport(player, "world");
        assertFalse(result);
        assertEquals(0, rtpManager.getQueueSize());
    }
}
