package com.gildedrose;

/**
 * Base helpers for updater implementations.
 * Invariants enforced here:
 * - Quality is always clamped to [0,50] via Quality.clamp when increased/decreased.
 * - sellIn is decreased exactly once per day by calling decreaseSellIn().
 * Convention:
 * - Updaters should call these helpers instead of mutating Item fields directly.
 */
abstract class BaseUpdater implements ItemUpdater {
    protected void increaseQuality(Item item, int by) {
        item.quality = Quality.clamp(item.quality + by);
    }

    protected void decreaseQuality(Item item, int by) {
        item.quality = Quality.clamp(item.quality - by);
    }

    protected void decreaseSellIn(Item item) {
        item.sellIn -= 1;
    }
}
