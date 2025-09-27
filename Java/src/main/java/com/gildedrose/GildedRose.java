package com.gildedrose;

class GildedRose {
    private final ItemUpdaterFactory factory;
    Item[] items;

    public GildedRose(Item[] items, ItemUpdaterFactory factory) {
        this.items = items;
        this.factory = factory;
    }

    public void updateQuality() {
        for (Item item : items) factory.forItem(item).update(item);
    }
}
