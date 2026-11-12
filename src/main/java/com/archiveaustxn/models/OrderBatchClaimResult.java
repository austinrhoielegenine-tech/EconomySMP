package com.archiveaustxn.models;

public record OrderBatchClaimResult(
        int itemClaims,
        int refundClaims,
        int failedClaims,
        int itemAmount,
        double refundAmount
) {
}
