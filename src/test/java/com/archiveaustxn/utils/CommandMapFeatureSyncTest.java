package com.archiveaustxn.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * UNREGISTER used to delete whatever sat on {@code /enderchest} and {@code /ec}, including another
 * plugin's command. These cases are the load orders that produced "Unknown command" on a server
 * that still had a replacement ender chest plugin enabled.
 */
class CommandMapFeatureSyncTest {

    private static final String PREFIX = "economysmp";

    @Test
    void yieldDoesNotStealAForeignCommandAlreadyOnTheAlias() {
        Command ours = command("enderchest", "ec");
        Command foreign = command("enderchest", "ec");
        Map<String, Command> map = new HashMap<>();
        map.put("ec", foreign);
        map.put("otherplugin:enderchest", foreign);
        map.put("otherplugin:ec", foreign);
        map.put("enderchest", ours);
        map.put("economysmp:enderchest", ours);
        map.put("economysmp:ec", ours);

        CommandMapFeatureSync.apply(ours, map, PREFIX, false);

        assertSame(foreign, map.get("ec"));
        assertSame(foreign, map.get("enderchest"));
        assertSame(foreign, map.get("otherplugin:enderchest"));
        assertNull(map.get("economysmp:enderchest"));
        assertNull(map.get("economysmp:ec"));
    }

    @Test
    void yieldHandsPlainLabelsBackToThePluginThatStillHasTheNamespacedCommand() {
        Command ours = command("enderchest", "ec");
        Command foreign = command("enderchest", "ec");
        Map<String, Command> map = new HashMap<>();
        map.put("enderchest", ours);
        map.put("ec", ours);
        map.put("economysmp:enderchest", ours);
        map.put("economysmp:ec", ours);
        map.put("otherplugin:enderchest", foreign);
        map.put("otherplugin:ec", foreign);

        CommandMapFeatureSync.apply(ours, map, PREFIX, false);

        assertSame(foreign, map.get("enderchest"));
        assertSame(foreign, map.get("ec"));
        assertSame(foreign, map.get("otherplugin:enderchest"));
        assertNull(map.get("economysmp:enderchest"));
        assertNull(map.get("economysmp:ec"));
    }

    @Test
    void yieldFillsAHoleWhenOnlyTheForeignNamespacedAliasRemains() {
        Command ours = command("enderchest", "ec");
        Command foreign = command("enderchest", "ec");
        Map<String, Command> map = new HashMap<>();
        map.put("economysmp:enderchest", ours);
        map.put("otherplugin:enderchest", foreign);

        CommandMapFeatureSync.apply(ours, map, PREFIX, false);

        assertSame(foreign, map.get("enderchest"));
        assertSame(foreign, map.get("ec"));
        assertNull(map.get("economysmp:enderchest"));
    }

    @Test
    void claimPutsThisPluginBackOnThePlainLabels() {
        Command ours = command("enderchest", "ec");
        Command foreign = command("enderchest", "ec");
        Map<String, Command> map = new HashMap<>();
        map.put("enderchest", foreign);
        map.put("ec", foreign);

        CommandMapFeatureSync.apply(ours, map, PREFIX, true);

        assertSame(ours, map.get("enderchest"));
        assertSame(ours, map.get("ec"));
        assertSame(ours, map.get("economysmp:enderchest"));
        assertSame(ours, map.get("economysmp:ec"));
    }

    private static Command command(String name, String... aliases) {
        Command command = new StubCommand(name);
        command.setAliases(List.of(aliases));
        return command;
    }

    private static final class StubCommand extends Command {
        private StubCommand(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            return true;
        }
    }
}
