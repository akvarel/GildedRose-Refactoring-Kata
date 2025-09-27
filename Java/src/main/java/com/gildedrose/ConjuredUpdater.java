package com.gildedrose;

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
