package com.gildedrose;

/**
 * Carries metadata for an update batch and per-item update events.
 * Keep minimal to avoid coupling business logic to observability.
 */
record UpdateContext(long timestampNanos, long threadId, String correlationId) {
    static UpdateContext create(String correlationId) {
        return new UpdateContext(System.nanoTime(), Thread.currentThread().threadId(), correlationId);
    }
}
