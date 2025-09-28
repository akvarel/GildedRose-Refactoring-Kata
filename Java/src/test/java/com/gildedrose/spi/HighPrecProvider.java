package com.gildedrose.spi;

import com.gildedrose.Item;
import com.gildedrose.ItemRuleProvider;
import com.gildedrose.ItemUpdater;

public class HighPrecProvider implements ItemRuleProvider {
    @Override
    public boolean supports(String itemName) { return "Double".equals(itemName); }
    @Override
    public ItemUpdater updater() {
        return new ItemUpdater() {
            @Override
            public void update(Item item) {
                item.quality = 20;
            }
        };
    }
    @Override
    public int precedence() { return 2; }
}
