package com.archiveaustxn.models;

import java.util.UUID;

public record IgnoreEntry(
        UUID ownerUuid,
        UUID ignoredUuid,
        String ignoredNameSnapshot,
        long createdAt
) {
}
