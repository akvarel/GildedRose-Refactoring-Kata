package com.gildedrose;

/**
 * Backstage passes rule:
 * - Before concert: quality +1
 * - 10 days or fewer: additional +1 (total +2)
 * - 5 days or fewer: additional +1 (total +3)
 * - Decrement sellIn by 1
 * - After concert (sellIn < 0): quality drops to 0
 * Invariants: quality is clamped to [0,50] via helpers; name never changes.
 */
final class BackstageUpdater extends BaseUpdater {
    @Override
    public void update(Item item) {
        // Before concert
        increaseQuality(item, 1);
        if (item.sellIn <= 10) {
            increaseQuality(item, 1);
        }
        if (item.sellIn <= 5) {
            increaseQuality(item, 1);
        }
        decreaseSellIn(item);
        // After concert
        if (item.sellIn < 0) {
            item.quality = 0;
        }
    }
}
