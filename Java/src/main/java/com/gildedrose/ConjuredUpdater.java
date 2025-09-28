package com.gildedrose;

/**
 * Conjured items rule:
 * - Before sell date: quality -2
 * - Decrement sellIn by 1
 * - After sell date (sellIn < 0): degrade twice as fast again (an additional -2, total -4)
 * Invariants: quality clamped to [0,50]; name never changes.
 */
class ConjuredUpdater extends BaseUpdater {
    @Override
    public void update(Item item) {
        decreaseQuality(item, 2);
        decreaseSellIn(item);
        if (item.sellIn < 0) {
            decreaseQuality(item, 2);
        }
    }
}
