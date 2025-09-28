package com.gildedrose;

/**
 * Updater for "Conjured" items.
 * <p>
 * Rules:
 * <ul>
 *   <li>Quality degrades twice as fast as normal items (-2 per day).</li>
 *   <li>After the sell date has passed, it degrades twice as fast again (-4 per day total).</li>
 *   <li>Quality never goes below 0; sellIn decreases by 1 per day.</li>
 * </ul>
 */
class ConjuredUpdater extends BaseUpdater {
    /**
     * Applies the Conjured rules: degrade quality by 2 per day (and by another 2 after sell date), then decrease sellIn.
     */
    @Override
    public void update(Item item) {
        decreaseQuality(item, 2);
        decreaseSellIn(item);
        if (item.sellIn < 0) {
            decreaseQuality(item, 2);
        }
    }
}
