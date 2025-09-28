package com.gildedrose.simple;

import com.gildedrose.Item;

final class ItemClassifier {
    // Canonical item names used in classification.
    static final String NAME_AGED_BRIE = "Aged Brie";
    static final String NAME_BACKSTAGE = "Backstage passes to a TAFKAL80ETC concert";
    static final String NAME_SULFURAS = "Sulfuras, Hand of Ragnaros";
    static final String NAME_CONJURED = "Conjured Mana Cake";

    static ItemType classify(Item item) {
        String n = item.name;
        return switch (n) {
            case NAME_AGED_BRIE -> ItemType.AGED_BRIE;
            case NAME_BACKSTAGE -> ItemType.BACKSTAGE;
            case NAME_SULFURAS -> ItemType.SULFURAS;
            case NAME_CONJURED -> ItemType.CONJURED;
            default -> ItemType.DEFAULT;
        };
    }
}
