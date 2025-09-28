package com.gildedrose.simple;

import com.gildedrose.Item;

import java.util.HashMap;
import java.util.Map;

public class ItemUpdaterFactory {
    private final Map<ItemType, ItemUpdater> byName = new HashMap<>();
    private final ItemUpdater defaultUpdater = new DefaultUpdater();

    public ItemUpdaterFactory() {
        byName.put(ItemType.AGED_BRIE, new AgedBrieUpdater());
        byName.put(ItemType.BACKSTAGE, new BackstageUpdater());
        byName.put(ItemType.SULFURAS, new SulfurasUpdater());
        byName.put(ItemType.CONJURED, new ConjuredUpdater());
    }

    ItemUpdater forItem(Item item) {
        return byName.getOrDefault(ItemClassifier.classify(item), defaultUpdater);
    }
}
