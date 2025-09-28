package com.gildedrose;

/**
 * Updater for "Aged Brie" items.
 * <p>
 * Rules:
 * <ul>
 *   <li>Quality increases by 1 per day before the sell date.</li>
 *   <li>After the sell date has passed, quality increases twice as fast (+2 per day).</li>
 *   <li>Quality is always capped at 50.</li>
 *   <li>SellIn decreases by 1 per day.</li>
 * </ul>
 */
class AgedBrieUpdater extends BaseUpdater {
    /**
     * Applies the Aged Brie rules: increase quality (double after sell date), then decrease sellIn.
     */
    @Override
    public void update(Item item) {
        increaseQuality(item, 1);
        decreaseSellIn(item);
        if (item.sellIn < 0) {
            // Original kata semantics: after sell date, the increase is doubled.
            increaseQuality(item, 1);
        }
    }
}
