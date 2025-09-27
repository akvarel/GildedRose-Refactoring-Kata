package com.gildedrose;

class BackstageUpdater extends BaseUpdater {
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
