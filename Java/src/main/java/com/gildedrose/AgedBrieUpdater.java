package com.gildedrose;

/**
 * Aged Brie rule:
 * - Before sell date: quality +1
 * - Decrement sellIn by 1
 * - After sell date (sellIn < 0): improve twice as fast (an additional +1)
 * Invariants: quality is clamped to [0,50]. Name never changes.
 */
class AgedBrieUpdater extends BaseUpdater {
    @Override
    public void update(Item item) {
        increaseQuality(item, 1);
        decreaseSellIn(item);
        if (item.sellIn < 0) {
            increaseQuality(item, 1);
        }
    }
}
