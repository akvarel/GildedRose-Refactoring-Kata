package com.gildedrose;

/**
 * Updater for "Backstage passes" items.
 * <p>
 * Rules:
 * <ul>
 *   <li>Quality increases by 1 per day normally.</li>
 *   <li>When there are 10 days or fewer to the concert, quality increases by an additional 1 (total +2).</li>
 *   <li>When there are 5 days or fewer, it increases by yet another 1 (total +3).</li>
 *   <li>After the concert (sellIn &lt; 0), quality drops to 0.</li>
 *   <li>Quality is capped at 50; sellIn decreases by 1 per day.</li>
 * </ul>
 */
class BackstageUpdater extends BaseUpdater {
    /**
     * Applies the tiered increase before the concert and resets quality to 0 after the concert.
     */
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
