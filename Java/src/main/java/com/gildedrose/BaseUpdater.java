package com.gildedrose;

abstract class BaseUpdater implements ItemUpdater {
    protected void increaseQuality(Item item, int by) {
        item.quality = Quality.clamp(item.quality + by);
    }

    protected void decreaseQuality(Item item, int by) {
        item.quality = Quality.clamp(item.quality - by);
    }

    protected void decreaseSellIn(Item item) {
        item.sellIn -= 1;
    }
}
