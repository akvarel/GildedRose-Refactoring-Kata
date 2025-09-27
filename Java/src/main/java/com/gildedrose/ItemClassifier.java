package com.gildedrose;

final class ItemClassifier {
    static ItemType classify(Item item) {
        String n = item.name;
        if ("Aged Brie".equals(n)) return ItemType.AGED_BRIE;
        if ("Backstage passes to a TAFKAL80ETC concert".equals(n)) return ItemType.BACKSTAGE;
        if ("Sulfuras, Hand of Ragnaros".equals(n)) return ItemType.SULFURAS;
        if ("Conjured Mana Cake".equals(n)) return ItemType.CONJURED;
        return ItemType.DEFAULT;
    }
}
