package com.gildedrose.spi;

import com.gildedrose.Item;
import com.gildedrose.ItemRuleProvider;
import com.gildedrose.ItemUpdater;

/** precedence 0 should tie with built-ins and therefore not override them */
public class TieWithBuiltInBrieProvider implements ItemRuleProvider {
    @Override
    public boolean supports(String itemName) { return "Aged Brie".equals(itemName); }
    @Override
    public ItemUpdater updater() {
        return new ItemUpdater() {
            @Override
            public void update(Item item) {
                // Purposefully break expected Brie behavior: decrease quality
                item.quality = Math.max(0, item.quality - 1);
            }
        };
    }
    @Override
    public int precedence() { return 0; }
}
