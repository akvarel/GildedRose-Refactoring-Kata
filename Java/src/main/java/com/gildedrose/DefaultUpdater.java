package com.gildedrose;

/**
 * Updater for normal items (the default rules).
 * <p>
 * Rules:
 * <ul>
 *   <li>Quality decreases by 1 per day before the sell date.</li>
 *   <li>After the sell date has passed, quality decreases twice as fast (-2 per day).</li>
 *   <li>Quality never goes below 0; sellIn decreases by 1 per day.</li>
 * </ul>
 */
class DefaultUpdater extends BaseUpdater {
    /**
     * Applies the default rules: degrade quality by 1 per day (and an extra 1 after sell date), then decrease sellIn.
     */
    @Override
    public void update(Item item) {
        decreaseQuality(item, 1);
        decreaseSellIn(item);
        if (item.sellIn < 0) {
            decreaseQuality(item, 1);
        }
    }
}
