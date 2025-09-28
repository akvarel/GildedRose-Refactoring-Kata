package com.gildedrose;

/**
 * Base class for {@link ItemUpdater} implementations providing common helpers
 * with built-in quality clamping to the standard bounds [0, 50].
 */
abstract class BaseUpdater implements ItemUpdater {
    /**
     * Increases the item's quality by the given amount and clamps it to [0, 50].
     *
     * @param item the item to mutate
     * @param by   how much to increase quality by (non-negative)
     */
    protected void increaseQuality(Item item, int by) {
        item.quality = Quality.clamp(item.quality + by);
    }

    /**
     * Decreases the item's quality by the given amount and clamps it to [0, 50].
     *
     * @param item the item to mutate
     * @param by   how much to decrease quality by (non-negative)
     */
    protected void decreaseQuality(Item item, int by) {
        item.quality = Quality.clamp(item.quality - by);
    }

    /**
     * Decreases the item's {@code sellIn} by one day.
     *
     * @param item the item to mutate
     */
    protected void decreaseSellIn(Item item) {
        item.sellIn -= 1;
    }
}
