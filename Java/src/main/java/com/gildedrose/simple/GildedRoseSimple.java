package com.gildedrose.simple;

import com.gildedrose.GildedRoseInterface;
import com.gildedrose.Item;

public class GildedRoseSimple implements GildedRoseInterface {
    private final ItemUpdaterFactory factory;
    Item[] items;

    public GildedRoseSimple(Item[] items, ItemUpdaterFactory factory) {
        this.items = items;
        this.factory = factory;
    }

    public void updateQuality() {
        for (Item item : items)
            factory.forItem(item).update(item);
    }

    public Item[] getItems() {
        return items;
    }
}
