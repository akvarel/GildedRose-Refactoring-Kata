package com.gildedrose;

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
