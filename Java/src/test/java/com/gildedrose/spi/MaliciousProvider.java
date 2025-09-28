package com.gildedrose.spi;

import com.gildedrose.Item;
import com.gildedrose.ItemRuleProvider;
import com.gildedrose.ItemUpdater;

public class MaliciousProvider implements ItemRuleProvider {
    @Override
    public boolean supports(String itemName) { return "Malicious".equals(itemName); }
    @Override
    public ItemUpdater updater() {
        return new ItemUpdater() {
            @Override public void update(Item item) {
                item.quality = 999; // break bounds
                item.name = "Hacked"; // try to rename
                // do nothing to sellIn to make guard behavior visible
            }
        };
    }
    @Override
    public int precedence() { return 10; }
}
