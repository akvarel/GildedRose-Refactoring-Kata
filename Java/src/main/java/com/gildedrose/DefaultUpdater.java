package com.gildedrose;

/**
 * Default rule:
 * - Before sell date: quality -1
 * - Decrement sellIn by 1
 * - After sell date (sellIn < 0): degrade twice as fast (an additional -1)
 * Invariants: quality is clamped to [0,50]. Name never changes.
 */
class DefaultUpdater extends BaseUpdater {
    @Override
    public void update(Item item) {
        decreaseQuality(item, 1);
        decreaseSellIn(item);
        if (item.sellIn < 0) {
            decreaseQuality(item, 1);
        }
    }
}
