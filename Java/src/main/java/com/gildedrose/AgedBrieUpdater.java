package com.gildedrose;

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
