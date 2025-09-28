package com.gildedrose;

/**
 * Observer for per-item updates. Default is Noop.
 * This is optional and disabled by default in GildedRose.
 */
interface UpdateObserver {
    void onItemUpdated(ItemSnapshot before, ItemSnapshot after, String updaterType, UpdateContext ctx);

    static UpdateObserver noop() {
        return (before, after, updaterType, ctx) -> { /* no-op */ };
    }
}
