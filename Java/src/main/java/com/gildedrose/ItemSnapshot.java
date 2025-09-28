package com.gildedrose;

/**
 * Immutable snapshot of an Item for tracing/observability purposes.
 * This does not replace Item; it is only used to safely capture before/after states
 * around in-place mutations performed by updaters.
 */
record ItemSnapshot(String name, int sellIn, int quality) {
    static ItemSnapshot of(Item item) {
        return new ItemSnapshot(item.name, item.sellIn, item.quality);
    }

    @Override
    public String toString() {
        return name + ", " + sellIn + ", " + quality;
    }
}
