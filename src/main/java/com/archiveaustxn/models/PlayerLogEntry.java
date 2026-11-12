package com.archiveaustxn.models;

import java.util.UUID;

public record PlayerLogEntry(
        long id,
        UUID playerUuid,
        String playerName,
        String category,
        String logType,
        String details,
        long timestamp
) {}
